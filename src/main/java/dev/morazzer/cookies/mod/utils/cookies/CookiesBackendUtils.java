package dev.morazzer.cookies.mod.utils.cookies;

import com.google.common.cache.CacheBuilder;

import com.google.common.cache.CacheLoader;

import com.google.common.cache.LoadingCache;
import dev.morazzer.cookies.entities.websocket.Packet;
import dev.morazzer.cookies.entities.websocket.packets.c2s.PlayersUseModRequestPacket;
import dev.morazzer.cookies.entities.websocket.packets.s2c.PlayersUseModResponsePacket;
import dev.morazzer.cookies.mod.api.ws.WebsocketConnection;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Various methods related to backend interactions and caching of backend responses.
 */
public class CookiesBackendUtils {

	static {
		Packet.onReceive(PlayersUseModResponsePacket.class, CookiesBackendUtils::updateCache);
	}


	private static void updateCache(PlayersUseModResponsePacket playersUseModResponsePacket) {
		for (Map.Entry<UUID, Boolean> uuidBooleanEntry : playersUseModResponsePacket.map.entrySet()) {
			PLAYER_USE_MOD_CACHE.put(uuidBooleanEntry.getKey(), uuidBooleanEntry.getValue());
		}
	}

	private static final LoadingCache<UUID, Boolean> PLAYER_USE_MOD_CACHE = CacheBuilder.newBuilder()
			.maximumSize(100)
			.expireAfterWrite(1, TimeUnit.HOURS)
			.build(CacheLoader.from(CookiesBackendUtils::loadUUID));

	/**
	 * Requests the mod usage status of all the uuids passed.
	 *
	 * @param uuids The uuids to check.
	 */
	public static void requestUUIDS(UUID... uuids) {
		WebsocketConnection.sendMessageAsync(new PlayersUseModRequestPacket(uuids));
	}

	private static Boolean loadUUID(UUID uuid) {
		requestUUIDS(uuid);
		return false;
	}

	/**
	 * @param uuid The uuid to look up.
	 * @return Whether said uuid is using the mod, if the uuid wasn't found in the cache it will be requested, until a
	 * response this will return false as we always assume that players aren't using the mod.
	 */
	public static boolean usesMod(UUID uuid) {
		return PLAYER_USE_MOD_CACHE.getUnchecked(uuid);
	}

}
