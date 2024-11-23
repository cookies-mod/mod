package codes.cookies.mod.features.mining;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import codes.cookies.mod.config.categories.mining.powder.PowderTrackerHudFoldable;
import codes.cookies.mod.data.mining.PowderType;
import codes.cookies.mod.render.hud.HudManager;
import codes.cookies.mod.render.hud.elements.MultiLineTextHudElement;
import codes.cookies.mod.render.hud.internal.HudEditAction;
import codes.cookies.mod.services.mining.powder.PowderEntry;
import codes.cookies.mod.services.mining.powder.PowderService;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.maths.InterpolatedInteger;
import codes.cookies.mod.utils.maths.LinearInterpolatedInteger;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class PowderHud extends MultiLineTextHudElement {

	InterpolatedInteger powderAmount = new LinearInterpolatedInteger(1000, 0);

	public PowderHud() {
		super(Identifier.of("cookies", "powder_hud"));
		HudManager.register(this);
	}

	@Override
	public boolean shouldRender() {
		if (!getConfig().enableHud.getValue()) {
			return this.hudEditAction == HudEditAction.SHOW_ALL;
		}

		if (this.hudEditAction == HudEditAction.ALL_ENABLED || this.hudEditAction == HudEditAction.SHOW_ALL) {
			return true;
		}

		return LocationUtils.Island.DWARVEN_MINES.isActive() || LocationUtils.Island.MINESHAFT.isActive() || LocationUtils.Island.CRYSTAL_HOLLOWS.isActive();
	}

	@Override
	public int getWidth() {
		return Math.max(100, lastWidth);
	}

	private PowderTrackerHudFoldable getConfig() {
		return PowderTrackerHudFoldable.getConfig();
	}

	@Override
	protected List<Text> getText() {
		List<Text> text = new ArrayList<>();

		PowderService.getCurrentlyActivePowderType().ifPresentOrElse(
				type -> {
					powderAmount.tick();
					final PowderEntry powderEntry = PowderService.getPowderEntry(type);
					powderAmount.setTargetValue((int) powderEntry.getProjectedAmountPerHour());
					text.add(type.getText());
					text.add(Text.literal("§7Powder/h: ")
							.append(Text.literal(DecimalFormat.getIntegerInstance()
											.format(this.powderAmount.getValue()))
									.formatted(type.getFormatting())
							));
					text.add(Text.literal("§7Gained: ")
							.append(Text.literal(DecimalFormat.getIntegerInstance().format(powderEntry.getGained()))
									.formatted(type.getFormatting())));
					if (getConfig().showExtraData.getValue()) {
						if (type == PowderType.GEMSTONE) {
							text.add(Text.literal("§7Chests/min:§e %.2f §8(%s)".formatted(powderEntry.getOtherPerMinute(), powderEntry.getOtherTotal())));
						} else if (type == PowderType.GLACITE) {
							text.add(Text.literal("§7Shafts/min:§e %.2f §8(%s)".formatted(powderEntry.getOtherPerMinute(), powderEntry.getOtherTotal())));
						}
					}
					text.add(Text.literal("§7Active for §e" + CookiesUtils.formattedMs(powderEntry.getMillisecondsActive()) + (powderEntry.isPaused() ? " §c(Paused)" : "")));

				}, () -> {
					if (this.hudEditAction != HudEditAction.NONE) {
						text.add(getName());
					}
				});

		return text;
	}

	@Override
	public int getMaxRows() {
		return 5;
	}

	@Override
	public Text getName() {
		return Text.literal("Powder Tracker").formatted(Formatting.DARK_GREEN);
	}
}
