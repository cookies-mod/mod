package codes.cookies.mod.features.farming.garden;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.features.misc.timer.PestTimer;
import codes.cookies.mod.render.hud.elements.TextHudElement;
import codes.cookies.mod.render.hud.internal.HudEditAction;
import codes.cookies.mod.utils.TextUtils;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Hud for the {@link PestTimer}
 */
public class PestTimerHud extends TextHudElement {
	private final Text defaultText = Text.literal("Pests in ").formatted(Formatting.DARK_GREEN);
	private final Text canSpawnText = Text.literal("Pests can spawn!").formatted(Formatting.DARK_GREEN);
	private final PestTimer timer;

	public PestTimerHud(PestTimer timer) {
		super(Identifier.of("cookies", "pest_timer_hud"));
		this.timer = timer;
	}

	@Override
	protected Text getText() {
		if (this.timer.getLastPestSpawnedTime() == -1) {
			return defaultText.copy().append(TextUtils.literal("<unknown>", Formatting.RED));
		}
		final int time = this.timer.getTime();
		if (time <= 0) {
			return this.canSpawnText;
		}

		return this.defaultText.copy()
				.append(Text.literal(CookiesUtils.formattedMs(time * 1000L)).formatted(Formatting.YELLOW));
	}

	@Override
	protected Text getEditText() {
		return defaultText.copy().append(TextUtils.literal("<unknown>", Formatting.RED));
	}

	@Override
	public boolean shouldRender() {
		if (this.hudEditAction == HudEditAction.SHOW_ALL) {
			return true;
		}
		if (!this.isEnabled()) {
			return false;
		}
		if (this.hudEditAction == HudEditAction.ALL_ENABLED) {
			return true;
		}

		return this.isOnIsland();
	}

	private boolean isEnabled() {
		return ConfigManager.getConfig().farmingConfig.pestFoldable.enableHud.getValue()
				&& ConfigManager.getConfig().farmingConfig.pestFoldable.enabled.getValue();
	}

	private boolean isOnIsland() {
		return LocationUtils.Island.GARDEN.isActive();
	}

	@Override
	protected Text getDisplayText() {
		if (this.isEnabled() && this.isOnIsland()) {
			return this.getText();
		}
		return this.getEditText();
	}

	@Override
	public Text getName() {
		return Text.literal("Pest Timer").formatted(Formatting.DARK_GREEN);
	}
}
