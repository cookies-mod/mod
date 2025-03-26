package codes.cookies.mod.utils.skyblock;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.api.ApiManager;
import codes.cookies.mod.utils.cookies.CookiesBackendUtils;

import java.util.Set;
import java.util.UUID;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import lombok.Getter;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket;

/**
 * Utils related to party interactions.
 */
public class PartyUtils {

	@Getter
	private static boolean isInParty;
	@Getter
	private static UUID partyLeader;
	@Getter
	private static Set<UUID> partyMembers;
	private static long lastReceived;

	/**
	 * Registers the listener to track party information.
	 */
	public static void register() {
		HypixelModAPI.getInstance().createHandler(ClientboundPartyInfoPacket.class, PartyUtils::handle);
		HypixelModAPI.getInstance().createHandler(ClientboundHelloPacket.class, packet -> request());
		CookiesMod.getExecutorService().scheduleAtFixedRate(() -> {
			if (LocationUtils.isInSkyblock()) {
				request();
			}
		}, 0, 10, java.util.concurrent.TimeUnit.SECONDS);
	}

	/**
	 * Requests the current party information.
	 */
	public static void request() {
		if (System.currentTimeMillis() - lastReceived < 2000) {
			return;
		}
		HypixelModAPI.getInstance().sendPacket(new ServerboundPartyInfoPacket());
	}

	/**
	 * Updates the information based on the provided packet.
	 *
	 * @param clientboundPartyInfoPacket The packet send by hypixel.
	 */
	private static void handle(ClientboundPartyInfoPacket clientboundPartyInfoPacket) {
		lastReceived = System.currentTimeMillis();
		isInParty = clientboundPartyInfoPacket.isInParty();
		if (isInParty) {
			partyLeader = clientboundPartyInfoPacket.getLeader().orElse(null);
			partyMembers = clientboundPartyInfoPacket.getMemberMap().keySet();
			CookiesBackendUtils.requestUUIDS(partyMembers.toArray(UUID[]::new));
		} else {
			partyLeader = null;
			partyMembers = null;
		}
	}

}
