package dev.morazzer.cookies.mod.api;

import com.mojang.authlib.exceptions.AuthenticationException;

import dev.morazzer.cookies.entities.request.AuthRequest;

import dev.morazzer.cookies.entities.response.AuthResponse;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Helper to authenticate against the backend api. <br>
 * The auth process follows four steps: <br>
 * <ol>
 *     <li>We send a request to the mojang session servers and send them a random uuid that acts as our
 *     identifier.</li>
 *     <li>We send that same uuid and the user name to the backend server.</li>
 *     <li>The backend server requests the identifier from the session servers to see if it is valid.</li>
 *     <li>We retrieve the token from the backend server, this token is valid for 24h (as long as a
 *     minecraft session) and will be used from now on.</li>
 * </ol>
 * <br>
 * <h3>Disclaimer</h3><br>
 * The request to the minecraft session servers is directed completely by the client. In normal environments (like
 * when you connect to a server), the client and the server both create a so called "shared secret" that is a hash in
 * base 16. Since we use a random uuid in string form as our identifier it can never be a valid shared-secret and
 * can therefore never be used to connect to any server. <br>
 * <br>
 * To learn more about the minecraft auth process you can read the page on
 * <a href="https://wiki.vg/Protocol_Encryption#Authentication">wiki.vg</a>.
 */
public class ApiAuthProcess {
	@Getter
	private static CompletableFuture<String> current;

	/**
	 * Sets up a new auth process.
	 */
	public static void setup() {
		current = new CompletableFuture<>();
	}

	/**
	 * Starts the auth process by authenticating against minecraft session servers and requesting the token from the
	 * backend server.
	 */
	public static void start() {
		final Session session = MinecraftClient.getInstance().getSession();
		UUID uuid = UUID.randomUUID();
		try {
			MinecraftClient.getInstance()
					.getSessionService()
					.joinServer(session.getUuidOrNull(), session.getAccessToken(), uuid.toString());
		} catch (AuthenticationException e) {
			throw new RuntimeException(e);
		}

		try (CloseableHttpClient client = HttpClientBuilder.create().setUserAgent(ApiManager.USER_AGENT).build()) {
			HttpPost httpPost = new HttpPost(ApiManager.getPath("login"));
			httpPost.setEntity(createLoginEntity(uuid, session.getUsername()));
			httpPost.setHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
			httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
			try {
				final HttpResponse execute = client.execute(httpPost);
				processResponse(execute);
			} catch (IOException e) {
				current.completeExceptionally(new AuthException(AuthException.Reason.FAILED_TO_CONNECT));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Processes the response from the backend server, and schedules a retry if any exception/unexcepted problems
	 * occurred.
	 *
	 * @param execute The response from the backend server.
	 */
	private static void processResponse(HttpResponse execute) {
		if (execute.getStatusLine().getStatusCode() != 200) {
			current.completeExceptionally(new AuthException(AuthException.Reason.OTHER));
			return;
		}
		HttpEntity entity = execute.getEntity();
		if (entity == null) {
			current.completeExceptionally(new AuthException(AuthException.Reason.FAILED_TO_CONNECT));
			return;
		}
		try {
			final byte[] bytes = entity.getContent().readAllBytes();
			final AuthResponse authResponse =
					JsonUtils.CLEAN_GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), AuthResponse.class);

			if (authResponse == null) {
				current.completeExceptionally(new AuthException(AuthException.Reason.FAILED_TO_CONNECT));
				return;
			}

			current.complete(authResponse.token());
		} catch (IOException e) {
			current.completeExceptionally(new AuthException(AuthException.Reason.INTERNAL_ERROR));
		}
	}

	/**
	 * Creates the entity that is sent to the backend server.
	 *
	 * @param uuid     The "identifier" used to authenticate the player.
	 * @param username The username of the player.
	 * @return The entity.
	 */
	private static HttpEntity createLoginEntity(UUID uuid, String username) {
		AuthRequest authRequest = new AuthRequest(uuid.toString(), username);
		return new StringEntity(JsonUtils.CLEAN_GSON.toJson(authRequest), ContentType.APPLICATION_JSON);
	}

	/**
	 * An exception to represent a failure during the auth process.
	 */
	@RequiredArgsConstructor
	@Getter
	static class AuthException extends Exception {
		private final Reason reason;

		/**
		 * The supported reasons that may have caused the failure.
		 */
		enum Reason {
			FAILED_TO_CONNECT,
			UNAUTHORIZED,
			INTERNAL_ERROR,
			OTHER
		}

	}
}
