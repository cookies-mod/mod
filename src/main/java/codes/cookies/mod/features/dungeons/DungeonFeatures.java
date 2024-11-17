package codes.cookies.mod.features.dungeons;

import codes.cookies.mod.features.dungeons.map.DungeonMapHud;
import codes.cookies.mod.render.hud.HudManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.events.ChatMessageEvents;
import codes.cookies.mod.features.dungeons.map.DungeonType;
import codes.cookies.mod.features.dungeons.solver.terminals.ChangeAllToSameColorTerminalSolver;
import codes.cookies.mod.features.dungeons.solver.terminals.CorrectAllThePanesTerminalSolver;
import codes.cookies.mod.features.dungeons.solver.terminals.SelectAllColorsTerminalSolver;
import codes.cookies.mod.features.dungeons.solver.terminals.ClickInOrderTerminalSolver;
import codes.cookies.mod.features.dungeons.solver.terminals.StartsWithTerminalSolver;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.maths.RomanNumerals;

import codes.cookies.mod.utils.skyblock.PartyUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import lombok.Getter;

import net.minecraft.util.Identifier;

/**
 * Main entrypoint for all dungeon related functionality, this also is responsible for dungeon session handling.
 */
public class DungeonFeatures {
	private static final Identifier SEND_DEBUG_MESSAGES = DevUtils.createIdentifier("dungeons/send_debug_messages");
	private DungeonInstance currentInstance = null;
	private final Cache<String, DungeonInstance> cache = CacheBuilder.newBuilder()
			.maximumSize(10)
			.expireAfterWrite(45, TimeUnit.MINUTES)
			.removalListener(this::removeInstance)
			.build();
	private long dungeonInstanceCreatorTime = -1;
	private CompletableFuture<String> dungeonInstanceCreator;

	/**
	 * Called when a dungeon instance is removed from the cache.
	 *
	 * @param listener The information about the removed instance.
	 */
	private void removeInstance(RemovalNotification<String, DungeonInstance> listener) {
		sendDebugMessage("Destroying dungeon instance for server " + listener.getKey());
		Optional.ofNullable(listener.getValue()).ifPresent(DungeonInstance::destroy);
	}

	@Getter
	private static DungeonFeatures instance;

	/**
	 * Initializes all dungeon features and registers all listeners that are required to correctly operate the dungeon
	 * map.
	 */
	public DungeonFeatures() {
		if (instance != null) {
			throw new IllegalStateException("DungeonFeatures has already been initialized");
		}
		instance = this;
		ChatMessageEvents.register(
				this::onDungeonJoin,
				"cookies-regex:-+\\n(\\[.*?] )?[a-zA-Z0-9_]{1,16} entered (.*?), (Floor (.*?)|Entrance)!\n.*");
		DungeonListeners.initialize();
		new SelectAllColorsTerminalSolver();
		new StartsWithTerminalSolver();
		new ClickInOrderTerminalSolver();
		new CorrectAllThePanesTerminalSolver();
		new ChangeAllToSameColorTerminalSolver();
		SpiritLeapOverlay.init(this);
		HudManager.register(DungeonMapHud.getInstance());
	}

	/**
	 * Notifies the mod of a dungeon join.
	 *
	 * @param string The chat message.
	 */
	private void onDungeonJoin(String string) {
		if (!DungeonConfig.getInstance().useDungeonFeatures.getValue()) {
			return;
		}
		final String entered = string.split("entered ")[1].split("\n")[0];
		final String[] split = entered.split(",");
		final String type = split[0];
		final DungeonType dungeonType = DungeonType.of(type);
		final int floorLevel;
		if (split[1].contains("Entrance")) {
			floorLevel = 0;
		} else {
			final String floor = split[1].trim().substring(6).replace("!", "");
			floorLevel = RomanNumerals.romanToArabic(floor);
		}

		this.awaitDungeonCreation(dungeonType, floorLevel);
	}

	/**
	 * Creates a future that awaits the creation of the dungeon.
	 *
	 * @param dungeonType The type of the dungeon that was joined.
	 * @param floorLevel  The floor that was joined.
	 */
	private void awaitDungeonCreation(DungeonType dungeonType, int floorLevel) {
		sendDebugMessage("Awaiting dungeon creation " + dungeonType.name().toLowerCase() + " " + floorLevel);
		this.dungeonInstanceCreator = new CompletableFuture<>();
		this.dungeonInstanceCreatorTime = System.currentTimeMillis();
		PartyUtils.request();
		this.dungeonInstanceCreator.whenComplete(this.createDungeon(dungeonType, floorLevel));
	}

	/**
	 * Creates a dungeon creation callback.
	 *
	 * @param dungeonType The dungeon type.
	 * @param floorLevel  The floor.
	 * @return The dungeon creation callback.
	 */
	private BiConsumer<? super String, ? super Throwable> createDungeon(DungeonType dungeonType, int floorLevel) {
		return (string, throwable) -> {
			final DungeonInstance dungeonInstance = new DungeonInstance(dungeonType, floorLevel, string);
			if (this.currentInstance != null) {
				this.currentInstance.unload();
			}
			this.setInstance(dungeonInstance);
		};
	}


	/**
	 * Sets the current instance to the provided value.
	 *
	 * @param dungeonInstance The instance.
	 */
	private void setInstance(DungeonInstance dungeonInstance) {
		this.currentInstance = dungeonInstance;
		if (this.currentInstance != null) {
			this.currentInstance.load();
			this.cache.put(this.currentInstance.serverId(), this.currentInstance);
		}
	}

	/**
	 * Starts the dungeon with the server id.
	 *
	 * @param serverId The server id.
	 */
	public void startDungeon(String serverId) {
		this.exitDungeon();
		if (this.dungeonInstanceCreatorTime + 1000 * 20 < System.currentTimeMillis()) {
			this.setInstance(this.cache.getIfPresent(serverId));
		}
		if (this.currentInstance != null) {
			sendDebugMessage("Restored dungeon session " + this.currentInstance.serverId());
		}
		if (this.currentInstance == null && this.dungeonInstanceCreator != null) {
			this.dungeonInstanceCreator.complete(serverId);
			this.dungeonInstanceCreator = null;
			this.dungeonInstanceCreatorTime = -1;
		}
	}

	/**
	 * Notifies the mod of the player leaving the dungeon, this will cause the session to be cached and unloaded.
	 */
	public void exitDungeon() {
		if (this.currentInstance != null) {
			this.currentInstance.unload();
			sendDebugMessage("Removed dungeon session " + this.currentInstance.serverId());

		}
		this.currentInstance = null;
	}

	/**
	 * Gets the current dungeon instance.
	 *
	 * @return The instance.
	 */
	public Optional<DungeonInstance> getCurrentInstance() {
		if (this.currentInstance != null && this.currentInstance.getPlayer() == null) {
			this.startDungeon("");
			return Optional.empty();
		}
		return Optional.ofNullable(this.currentInstance);
	}

	/**
	 * Sends a debug message if debug is enabled, else just ignore it.
	 * @param message The message to send.
	 */
	public static void sendDebugMessage(String message) {
		if (!DevUtils.isEnabled(SEND_DEBUG_MESSAGES)) {
			return;
		}
		CookiesUtils.sendInformation(message);
	}
}
