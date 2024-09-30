package dev.morazzer.cookies.mod.utils.skyblock;

import dev.morazzer.cookies.mod.events.IslandChangeEvent;
import dev.morazzer.cookies.mod.events.profile.ServerSwapEvent;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.minecraft.ScoreboardUtils;
import dev.morazzer.mods.cookies.generated.Regions;

import java.util.List;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hypixel.data.type.GameType;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

import net.minecraft.util.Identifier;

/**
 * Utils to get the current location of the player.
 */
public class LocationUtils {
	private static Regions region;
	private static Island island;
	private static long lastUpdated = -1;
	private static boolean isInSkyblock;
	private static String serverName;
	private static final Identifier SEND_ISLAND_DEBUG = DevUtils.createIdentifier("island_debug");

	/**
	 * @return The current server name.
	 */
	public static String getLastServer() {
		return serverName;
	}

	/**
	 * Registers the packet listener to stay informed about the player location.
	 */
	public static void register() {
		HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class,
            LocationUtils::handleLocationUpdate);
	}

	/**
	 * @return Whether the player is in skyblock or not.
	 */
	public static boolean isInSkyblock() {
		return isInSkyblock;
	}

	/**
	 * Called whenever the player switches location.
	 *
	 * @param clientboundLocationPacket The packet send by hypixel.
	 */
	private static void handleLocationUpdate(ClientboundLocationPacket clientboundLocationPacket) {
		serverName = clientboundLocationPacket.getServerName();

		final Optional<ServerType> serverType = clientboundLocationPacket.getServerType();
		if (serverType.isEmpty() || (!(serverType.get() instanceof GameType game) || game != GameType.SKYBLOCK)) {
			isInSkyblock = false;
			setIsland(Island.NONE);
			return;
		}
		isInSkyblock = true;
		final Optional<String> mode = clientboundLocationPacket.getMode();
		if (mode.isEmpty()) {
			setIsland(Island.UNKNOWN);
		} else {
			setIsland(Island.parse(mode.get()));
		}
		if (DevUtils.isEnabled(SEND_ISLAND_DEBUG)) {
			CookiesUtils.sendMessage(serverName + " " + island);
		}
		ServerSwapEvent.SERVER_SWAP.invoker().onServerSwap();
		ServerSwapEvent.SERVER_SWAP_ID.invoker().accept(serverName);
	}

	/**
	 * Sets the island the player currently is on.
	 */
	private static void setIsland(Island island) {
		final Island previous = LocationUtils.island;
		LocationUtils.island = island;
		IslandChangeEvent.EVENT.invoker().onIslandChange(previous, island);
	}

	/**
	 * Gets the current region the player is in.
	 *
	 * @return The region.
	 */
	public static Regions getRegion() {
		if (lastUpdated + 1000 > System.currentTimeMillis()) {
			return region;
		}

		final List<String> lines = ScoreboardUtils.getLines().reversed();
		String location = null;
		String icon = "";
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("⏣ ") || line.startsWith("ф ")) {
				icon = line.trim().substring(0, 1).trim();
				location = line.trim().substring(1).trim();
				break;
			}
		}

		if (location == null) {
			lastUpdated = System.currentTimeMillis();
			LocationUtils.region = Regions.UNKNOWN;
			return region;
		}

		for (Regions value : Regions.values()) {
			if (value.icon != null && !icon.equals(value.icon)) {
				continue;
			}
			if ((value.regex && location.matches(value.scoreboard)) || location.equals(value.scoreboard)) {
				lastUpdated = System.currentTimeMillis();
				LocationUtils.region = value;
				return region;
			}
		}

		lastUpdated = System.currentTimeMillis();
		LocationUtils.region = Regions.UNKNOWN;
		return region;
	}

	@SuppressWarnings("MissingJavadoc")
	@RequiredArgsConstructor
	@Getter
	public enum Island {
		PRIVATE_ISLAND("dynamic"),
		GARDEN("garden"),
		HUB("hub"),
		THE_BARN("farming_1"),
		MUSHROOM_DESERT("farming_1"),
		THE_PARK("foraging_1"),
		SPIDERS_DEN("combat_1"),
		THE_END("combat_3"),
		CRIMSON_ISLE("crimson_isle"),
		GOLD_MINE("mining_1"),
		DEEP_CAVERNS("mining_2"),
		DWARVEN_MINES("mining_3"),
		CRYSTAL_HOLLOWS("crystal_hollows"),
		WINTER_ISLAND("winter"),
		DUNGEON_HUB("dungeon_hub"),
		RIFT("rift"),
		CATACOMBS("dungeon"),
		UNKNOWN("*"),
		NONE("");

		private final String apiName;

		private static final Island[] VALUES = values();
		private static final Identifier SKIP_ISLAND_CHECK = DevUtils.createIdentifier("islands/skip_check");

		public static Island parse(String apiName) {
			for (Island value : VALUES) {
				if (value.getApiName().equals(apiName)) {
					return value;
				}
			}
			return UNKNOWN;
		}

		public boolean isActive() {
			if (DevUtils.isEnabled(SKIP_ISLAND_CHECK)) {
				return true;
			}

			if (this == Island.THE_BARN || this == Island.MUSHROOM_DESERT) {
				return LocationUtils.island == THE_BARN || LocationUtils.island == MUSHROOM_DESERT;
			}

			return LocationUtils.island == this;
		}
	}

}
