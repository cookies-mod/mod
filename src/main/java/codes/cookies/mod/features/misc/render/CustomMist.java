package codes.cookies.mod.features.misc.render;

import codes.cookies.mod.config.categories.mining.CustomMistColor;
import codes.cookies.mod.config.categories.mining.MiningConfig;
import codes.cookies.mod.events.locations.IslandChangeEvent;

import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class CustomMist {

	private boolean isInDwarvenMines;
	private boolean isOptionToggled;
	public volatile CustomMistColor replacement;
	private static CustomMist INSTANCE;

	public CustomMist() {
		IslandChangeEvent.EVENT.register((previous, current) -> this.isInDwarvenMines = current == LocationUtils.Island.DWARVEN_MINES);
		final MiningConfig instance = MiningConfig.getInstance();
		instance.enableCustomMist.withCallback(this::toggle);
		instance.mistColor.withCallback(this::changeColor);
		this.replacement =  instance.mistColor.getValue();
		this.isOptionToggled = instance.enableCustomMist.getValue();
		INSTANCE = this;
	}

	private void changeColor(CustomMistColor previous, CustomMistColor current) {
		this.replacement = current;

		if (this.isActive()) {
			this.recalculateIfInMines();
		}
	}

	private void toggle(boolean oldValue, boolean newValue) {
		this.isOptionToggled = newValue;
		this.recalculateIfInMines();
	}

	private void recalculateIfInMines() {
		if (!this.isInDwarvenMines) {
			return;
		}

		final ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null) {
			return;
		}

		world.scheduleChunkRenders(-71, 0, 0, 250, 80, 160);
	}

	public static boolean isIsActive() {
		return INSTANCE.isActive();
	}

	private boolean isActive() {
		return this.isOptionToggled && this.isInDwarvenMines;
	}

	public static CustomMistColor getReplacement() {
		return INSTANCE.replacement;
	}
}
