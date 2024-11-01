package dev.morazzer.cookies.mod.config.categories;

import javax.swing.*;

import dev.morazzer.cookies.mod.config.data.RancherSpeedConfig;
import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Foldable;
import dev.morazzer.cookies.mod.config.system.Parent;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.EnumCycleOption;
import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;

import dev.morazzer.cookies.mod.features.misc.timer.NotificationManager;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import org.apache.commons.lang3.StringUtils;

/**
 * Config that contains all farming related settings.
 */
@SuppressWarnings({"MissingJavadoc", "unused"})
public class FarmingConfig extends Category {

	public BooleanOption showPlotPriceBreakdown = new BooleanOption(CONFIG_FARMING_SHOW_PLOT_PRICE_BREAKDOWN, false);

	public BooleanOption yawPitchDisplay = new BooleanOption(CONFIG_FARMING_YAW_PITCH_DISPLAY, false);

	public PestFoldable pestFoldable = new PestFoldable();

	@Parent
	public TextDisplayOption ranchers = new TextDisplayOption(CONFIG_FARMING_CATEGORIES_RANCHERS);

	public BooleanOption showRancherSpeed = new BooleanOption(CONFIG_FARMING_SHOW_RANCHER_SPEED, false);

	public BooleanOption showRancherOptimalSpeeds = new BooleanOption(CONFIG_FARMING_SHOW_RANCHER_OPTIMAL_SPEED, false);

	public RancherSpeedConfig rancherSpeed = new RancherSpeedConfig();

	@Parent
	public TextDisplayOption compostText = new TextDisplayOption(CONFIG_FARMING_CATEGORIES_COMPOST);

	public BooleanOption showCompostPriceBreakdown =
			new BooleanOption(CONFIG_FARMING_SHOW_COMPOST_PRICE_BREAKDOWN, false);

	public EnumCycleOption<SortOrder> compostSortOrder = new EnumCycleOption<>(
			CONFIG_FARMING_COMPOST_SORT_ORDER,
			SortOrder.ASCENDING).withSupplier(value -> Text.translatable(switch (value) {
		case UNSORTED -> CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_UNSORTED;
		case ASCENDING -> CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_ASCENDING;
		case DESCENDING -> CONFIG_FARMING_COMPOST_SORT_ORDER_VALUES_DESCENDING;
	})).onlyIf(showCompostPriceBreakdown);

	//@Parent
	//public TextDisplayOption visitorText = new TextDisplayOption(CONFIG_FARMING_CATEGORIES_VISITOR);

	//public BooleanOption visitorMaterialHelper = new BooleanOption(CONFIG_FARMING_VISITOR_MATERIAL_HELPER, false);

	@Parent
	public TextDisplayOption jacobsText = new TextDisplayOption(CONFIG_FARMING_CATEGORIES_JACOBS);

	public BooleanOption highlightUnclaimedJacobContests =
			new BooleanOption(CONFIG_FARMING_HIGHLIGHT_UNCLAIMED_JACOB_CONTENTS, false);

	public FarmingConfig() {
		super(new ItemStack(Items.WHEAT), CONFIG_FARMING);
	}

	@Override
	public Row getRow() {
		return Row.TOP;
	}

	@Override
	public int getColumn() {
		return 1;
	}

	public static class PestFoldable extends Foldable implements MiscConfig.NotificationFoldable.TimerConfig {
		public EnumCycleOption<TimerTreatment> timerType = new EnumCycleOption<>(CONFIG_MISC_NOTIFICATIONS_PEST_ORDER, TimerTreatment.FIRST)
				.withSupplier(value -> Text.literal(StringUtils.capitalize(value.name().toLowerCase())));

		public BooleanOption enabled = new BooleanOption(CONFIG_MISC_NOTIFICATIONS_ENABLED, true);
		public EnumCycleOption<NotificationManager.NotificationType> type = new EnumCycleOption<>(CONFIG_MISC_NOTIFICATIONS_TYPE, NotificationManager.NotificationType.TOAST);
		public BooleanOption enableSound = new BooleanOption(CONFIG_MISC_NOTIFICATION_SOUND, true);

		@Override
		public boolean enabled() {
			return enabled.getValue();
		}

		@Override
		public NotificationManager.NotificationType notificationType() {
			return type.getValue();
		}

		@Override
		public boolean enableSound() {
			return enableSound.getValue();
		}

		@Override
		public String getName() {
			return CONFIG_MISC_NOTIFICATIONS_PEST;
		}

		public enum TimerTreatment {
			FIRST, CURRENT, LATEST
		}
	}
}
