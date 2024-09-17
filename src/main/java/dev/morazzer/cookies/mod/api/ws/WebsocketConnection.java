package dev.morazzer.cookies.mod.api.ws;

import dev.morazzer.cookies.entities.websocket.Packet;
import dev.morazzer.cookies.entities.websocket.Side;

import dev.morazzer.cookies.entities.websocket.packets.HandshakePacket;
import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.api.ApiManager;

import dev.morazzer.cookies.mod.events.WebsocketEvent;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Client to connect to the backend server, used to relay information between client and server, and also between
 * client and client.
 * <br>
 * The authentication is the same as with the backend api.
 */
public class WebsocketConnection implements WebSocket.Listener {

	@Getter
	private static WebsocketConnection instance;
	public boolean isConnected;
	private WebSocket webSocket;
	int tries = 1;
	private ScheduledFuture<?> schedule;
	private ByteArrayOutputStream current = new ByteArrayOutputStream();
	private CompletableFuture<?> future = new CompletableFuture<>();

	/**
	 * Creates a new websocket connection and terminates the old one.
	 *
	 * @return The websocket.
	 */
	public static WebsocketConnection create() {
		if (instance != null) {
			instance.disconnect();
		}
		instance = new WebsocketConnection();
		return instance;
	}

	/**
	 * Connects to the backend server. This will automatically retry if the mod is not able to connect for whatever
	 * reason.
	 */
	public void connect() {
		this.disconnect();
		try (final HttpClient build = HttpClient.newBuilder()
				.followRedirects(HttpClient.Redirect.NORMAL)
				.version(HttpClient.Version.HTTP_2)
				.build()) {
			final String scheme = URI.create(ApiManager.getApiUrl()).getScheme();

			final String websocketScheme;
			if (scheme.endsWith("s")) {
				websocketScheme = "wss";
			} else {
				websocketScheme = "ws";
			}

			final String s = ApiManager.getPath("websocket").replaceFirst(scheme + "://", websocketScheme + "://");
			this.webSocket = build.newWebSocketBuilder()
					.header("Authorization", "Bearer " + ApiManager.getToken())
					.buildAsync(URI.create(s), this)
					.join();
			sendMessage(new HandshakePacket());
			this.cancelSchedule();
		} catch (Exception e) {
			CookiesUtils.sendFailedMessage("Backend connection lost, try reconnecting! (in %ss)".formatted(
					this.tries * 5));
			this.reconnect((this.tries++ * 5));
		}
	}

	@Override
	public void onOpen(WebSocket webSocket) {
		this.isConnected = true;
		this.tries = 1;
		this.current = new ByteArrayOutputStream();
		if (DevUtils.isDevEnvironment()) {
			CookiesUtils.sendMessage("Successfully connected to backend server!");
		}
		WebSocket.Listener.super.onOpen(webSocket);
	}

	@Override
	public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
		if (this.webSocket != webSocket) {
			return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
		}
		WebsocketEvent.DISCONNECT.invoker().run();
		this.isConnected = false;
		System.out.println(reason + " " + statusCode);
		if (statusCode != WebSocket.NORMAL_CLOSURE) {
			CookiesUtils.sendFailedMessage("Backend connection lost, try reconnecting! (in %ss)".formatted(
					this.tries * 5));
			this.reconnect((this.tries++ * 5));
			return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
		}
		return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
	}

	public static void sendPing() {
		if (getInstance() == null) {
			return;
		}
		getInstance().webSocket.sendPing(ByteBuffer.wrap("ping".getBytes()));
	}

	/**
	 * Schedules a reconnect after the given time.
	 *
	 * @param time The time in seconds to wait before attempting to reconnect.
	 */
	private void reconnect(int time) {
		this.cancelSchedule();
		this.schedule = CookiesMod.getExecutorService().schedule(this::connect, time, TimeUnit.SECONDS);
	}

	public void cancelSchedule() {
		if (this.schedule != null) {
			this.schedule.cancel(true);
			this.schedule = null;
		}
	}

	@SneakyThrows
	@Override
	public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
		byte[] bytes = new byte[data.remaining()];
		data.get(bytes);
		this.current.writeBytes(bytes);
		webSocket.request(1);

		if (last) {
			this.handleMessage(this.current.toByteArray());
			this.current.reset();
			this.future.complete(null);
			CompletableFuture<?> future = this.future;
			this.future = new CompletableFuture<>();
			return future;
		}
		return this.future;
	}

	/**
	 * Handles a message from the backend, this is invoked once one message was completely received.
	 *
	 * @param byteArray The message in form of a byte array.
	 * @throws IOException If the packet is malformed.
	 */
	private void handleMessage(byte[] byteArray) throws IOException {
		Side.PACKETS.deserializeAndSend(byteArray);
	}

	/**
	 * Sends a message to the backend asynchronously so that it doesn't block the thread.
	 *
	 * @param packet The packet to send.
	 */
	public static void sendMessageAsync(Packet<?> packet) {
		if (getInstance() == null) {
			return;
		}
		getInstance().sendMessageAsyncInternal(packet);
	}
	/**
	 * Sends a message to the backend server.
	 *
	 * @param packet The packet to send.
	 */
	public static void sendMessage(Packet<?> packet) {
		if (getInstance() == null) {
			return;
		}
		getInstance().sendMessageInternal(packet);
	}

	private void sendMessageAsyncInternal(Packet<?> packet) {
		CookiesMod.getExecutorService().execute(() -> this.sendMessageInternal(packet));
	}

	private void sendMessageInternal(Packet<?> packet) {
		final byte[] serialize = Side.PACKETS.serializeUnknown(packet);
		this.webSocket.sendBinary(ByteBuffer.wrap(serialize), true);
	}

	/**
	 * Disconnects from the backend server without attempting a reconnect.
	 */
	public void disconnect() {
		if (this.webSocket != null && !this.webSocket.isOutputClosed()) {
			this.webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Disconnected");
			this.webSocket = null;
		}
	}
}
