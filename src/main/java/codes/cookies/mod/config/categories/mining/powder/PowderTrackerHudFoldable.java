package codes.cookies.mod.config.categories.mining.powder;

import codes.cookies.mod.config.categories.mining.MiningConfig;
import codes.cookies.mod.config.system.Foldable;
import codes.cookies.mod.config.system.HudSetting;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.EnumCycleOption;
import codes.cookies.mod.features.mining.PowderHud;

public class PowderTrackerHudFoldable extends Foldable {

	public static PowderTrackerHudFoldable getConfig() {
		return MiningConfig.getInstance().powderTrackerHud;
	}

	@HudSetting(PowderHud.class)
	public BooleanOption enableHud = new BooleanOption(CONFIG_MINING_POWDER_TRACKER_ENABLED, true);
	public BooleanOption showMs = new BooleanOption(CONFIG_MINING_POWDER_TRACKER_SHOW_MS, false);
	@HudSetting(PowderHud.class)
	public BooleanOption pauseTimer = new BooleanOption(CONFIG_MINING_POWDER_TRACKER_PAUSE, true);
	@HudSetting(PowderHud.class)
	public EnumCycleOption<PauseTime> pauseAfter = new EnumCycleOption<>(CONFIG_MINING_POWDER_TRACKER_PAUSE_VALUE, PauseTime.TEN_SEC);
	@HudSetting(PowderHud.class)
	public BooleanOption showExtraData = new BooleanOption(CONFIG_MINING_POWDER_TRACKER_EXTRA_DATA, true);
	@HudSetting(PowderHud.class)
	public EnumCycleOption<ShaftTrackingType> trackingType = new EnumCycleOption<>(CONFIG_MINING_POWDER_TRACKER_SHAFT_TRACKING, ShaftTrackingType.ENTER);
	@HudSetting(PowderHud.class)
	public EnumCycleOption<TimeoutTime> timeoutTime = new EnumCycleOption<>(CONFIG_MINING_POWDER_TRACKER_TIMEOUT, TimeoutTime.TEN_MIN);

	{
		// registering here because order of registration equals call order
		pauseAfter.onlyIf(pauseTimer).onlyIf(enableHud);
		trackingType.onlyIf(showExtraData).onlyIf(enableHud);

		pauseTimer.onlyIf(enableHud);
		timeoutTime.onlyIf(enableHud);
		showExtraData.onlyIf(enableHud);
	}
	public long getTimeoutTime() {
		return Math.max(pauseAfter.getValue().getTimeInMilliseconds(), timeoutTime.getValue().getTimeInMilliseconds());
	}

	public long getPauseTime() {
		return pauseAfter.getValue().getTimeInMilliseconds();
	}

	@Override
	public String getName() {
		return CONFIG_MINING_POWDER_TRACKER;
	}
}
