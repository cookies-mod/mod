package codes.cookies.mod.api;

import codes.cookies.mod.config.categories.DevCategory;
import codes.cookies.mod.generated.BuildInfo;
import dev.morazzer.cookies.entities.misc.BackendVersion;
import dev.morazzer.cookies.entities.websocket.Packet;
import dev.morazzer.cookies.entities.websocket.packets.HandshakePacket;
import dev.morazzer.cookies.entities.websocket.packets.WrongProtocolVersionPacket;
import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.api.ws.WebsocketConnection;

import codes.cookies.mod.events.WebsocketEvent;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;

import java.io.IOException;
import java.util.List;

import java.util.UUID;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

import lombok.extern.slf4j.Slf4j;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import net.hypixel.modapi.HypixelModAPI;

import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.minecraft.text.Text;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

/**
 * Class to handle all important backend activities, like authentication, connecting and disconnecting. <br>
 */
@Slf4j
public class ApiManager {

	public static String USER_AGENT;
	static CloseableHttpClient CLIENT;
	private static boolean isAuthenticated;
	private static UUID lastAuthenticatedUser;
	@Getter
	private static String token;
	private static ScheduledFuture<?> authTask;

	/**
	 * Gets the formatted backend url.
	 *
	 * @param path The path relative to the api root.
	 * @return The formatted path.
	 */
	public static String getPath(String path) {
		final String apiUrl = getApiUrl();
		return apiUrl + path;
	}

	/**
	 * Initializes all listeners to correctly handle various situations that may require to connect/disconnect to/from
	 * the backend server.
	 */
	public static void initialize() {
		HypixelModAPI.getInstance().createHandler(ClientboundHelloPacket.class, ApiManager::onJoinHypixel);
		ClientPlayConnectionEvents.DISCONNECT.register(ApiManager::onDisconnectServer);
		Packet.onReceive(HandshakePacket.class, ApiManager::handleHandshake);
		Packet.onReceive(WrongProtocolVersionPacket.class, ApiManager::handleWrongProtocolVersion);
		USER_AGENT = "CookiesMod/%s (%s)".formatted(
				BuildInfo.version.toString(),
				SharedConstants.getGameVersion().getName());
		CookiesMod.getExecutorService().scheduleAtFixedRate(WebsocketConnection::sendPing, 1, 1, TimeUnit.MINUTES);
	}

	/**
	 * Called when the player connects to hypixel, this will automatically connect to the backend.
	 *
	 * @param clientboundHelloPacket The hello packet from hypixel.
	 */
	private static void onJoinHypixel(ClientboundHelloPacket clientboundHelloPacket) {
		connectApi();
	}

	/**
	 * Handles the case where the backend doesn't support the current protocol version, this may be caused by either
	 * an outdated backend or an outdated client.
	 *
	 * @param wrongProtocolVersionPacket The packet.
	 */
	private static void handleWrongProtocolVersion(WrongProtocolVersionPacket wrongProtocolVersionPacket) {
		CookiesUtils.sendFailedMessage(Text.translatable(TranslationKeys.BACKEND_WRONG_VERSION));
	}

	/**
	 * Handles the handshake, this confirms that both the client and the backend have the same entity version.
	 *
	 * @param packet The packet.
	 */
	private static void handleHandshake(HandshakePacket packet) {
		if (DevUtils.isDevEnvironment()) {
			CookiesUtils.sendSuccessMessage("Handshake completed");
		}
		WebsocketEvent.CONNECT.invoker().run();
	}

	/**
	 * Reconnects (and authenticates) to the api, this destroys the current socket connect.
	 */
	public static void reconnect() {
		disconnectApi();
		lastAuthenticatedUser = null;
		connectApi();
	}

	/**
	 * Called when the client disconnects from the server, this will end the current socket session.
	 *
	 * @param clientPlayNetworkHandler Ignored.
	 * @param minecraftClient          Ignored.
	 */
	private static void onDisconnectServer(
			ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
		disconnectApi();
	}

	/**
	 * Disconnects the socket and soft invalidates the current token, if the user doesn't change between server
	 * sessions the
	 * token will be reused.
	 */
	private static void disconnectApi() {
		if (WebsocketConnection.getInstance() != null) {
			WebsocketConnection.getInstance().cancelSchedule();
			WebsocketConnection.getInstance().disconnect();
		}
		if (!isAuthenticated) {
			return;
		}
		if (authTask != null) {
			authTask.cancel(true);
		}
		isAuthenticated = false;
		if (CLIENT != null) {
			try {
				CLIENT.close();
			} catch (IOException ignored) {
			}
		}
	}

	private static void connectApi() {}

	/**
	 * Connects to the backend api, if the user is still the same as the last time this was invoked, then the token
	 * will be reused, if not a new token will be requested.
	 */
	private static void connectApi_() {
		if (!DevCategory.connectToBackend.get()) {
			return;
		}
		if (isAuthenticated) {
			return;
		}
		if (lastAuthenticatedUser != null &&
			lastAuthenticatedUser.equals(MinecraftClient.getInstance().getSession().getUuidOrNull())) {
			CookiesMod.getExecutorService().submit(ApiManager::finishAuth);
			return;
		}
		ApiAuthProcess.setup();
		CookiesMod.getExecutorService().submit(ApiAuthProcess::start);
		ApiAuthProcess.getCurrent().whenComplete(ApiManager::completeAuthentication);
		if (authTask != null) {
			authTask.cancel(false);
		}
		authTask = null;
	}

	/**
	 * Completes the authentication by either retrieving the token or handling a potential auth error.
	 *
	 * @param apiToken  The token.
	 * @param throwable A potential error.
	 */
	private static void completeAuthentication(String apiToken, Throwable throwable) {
		if (throwable != null) {
			if (throwable instanceof ApiAuthProcess.AuthException authException) {
				handleApiAuthError(authException);
			} else {
				log.error("Failed to authenticate", throwable);
				handleUnknown();
			}
			return;
		}
		lastAuthenticatedUser = MinecraftClient.getInstance().getSession().getUuidOrNull();
		token = apiToken;
		finishAuth();
	}

	/**
	 * Finishes the auth process by settings the api to authenticated and connecting to the backend server.
	 */
	private static void finishAuth() {
		if (CLIENT != null) {
			try {
				CLIENT.close();
			} catch (IOException ignored) {
			}
		}
		if (DevUtils.isDevEnvironment()) {
			CookiesUtils.sendSuccessMessage("Successfully connected to backend!");
		}
		isAuthenticated = true;
		CLIENT = HttpClientBuilder.create()
				.setUserAgent(USER_AGENT)
				.disableAuthCaching()
				.setDefaultHeaders(List.of(new BasicHeader("Authorization", "Bearer " + token)))
				.build();
		WebsocketConnection.create().connect();
	}

	/**
	 * Handles an unknown error which will cause the mod to retry connecting in two minutes.
	 */
	private static void handleUnknown() {
		scheduleAuthentication(2);
		CookiesUtils.sendFailedMessage(
				"Unable to connect to backend due to an unknown issue, trying again in two minutes.");
	}

	/**
	 * Schedules the mod to try reauthenticate in x minutes.
	 *
	 * @param minutes The minutes to wait.
	 */
	public static void scheduleAuthentication(int minutes) {
		if (authTask != null) {
			authTask.cancel(false);
		}
		authTask = CookiesMod.getExecutorService().schedule(ApiManager::connectApi, minutes, TimeUnit.MINUTES);
	}

	/**
	 * Handles an api error, based on the error reason the mod will wait a different amount of time before retrying.
	 *
	 * @param throwable The api error.
	 */
	private static void handleApiAuthError(ApiAuthProcess.AuthException throwable) {
		final ApiAuthProcess.AuthException.Reason reason = throwable.getReason();
		if (reason == ApiAuthProcess.AuthException.Reason.FAILED_TO_CONNECT) {
			scheduleAuthentication(5);
			CookiesUtils.sendFailedMessage("Failed to connect to backend, trying again in five minutes.");
			return;
		} else if (reason == ApiAuthProcess.AuthException.Reason.INTERNAL_ERROR) {
			scheduleAuthentication(1);
			CookiesUtils.sendFailedMessage(
					"Failed to connect to backed due to an internal error, trying again in one minute.");
			return;
		}
		log.error("Failed to authenticate", throwable);
		handleUnknown();
	}

	/**
	 * Gets the api url with a trailing `/` and the version suffix (if enabled in the config).
	 *
	 * @return The api url.
	 */
	public static String getApiUrl() {
		final String value = DevCategory.backendUrl;
		final String apiUrl;

		if (value.lastIndexOf("/") == value.length() - 1) {
			apiUrl = value;
		} else {
			apiUrl = value + "/";
		}

		if (DevCategory.useVersionSuffix) {
			return apiUrl + BackendVersion.CURRENT_VERSION_STRING + "/";
		}
		return apiUrl;
	}

}
