package codes.cookies.mod.services.mining.powder;

import codes.cookies.mod.utils.cookies.PauseableTime;

public final class PowderEntry {
	private long powderGained;
	private PauseableTime pauseableTime;
	private long lastUpdate;
	private int other;

	public PowderEntry() {
		this.powderGained = 0;
		this.pauseableTime = new PauseableTime(false);
		this.lastUpdate = -1;
	}

	public void reset() {
		this.powderGained = 0;
		this.pauseableTime = new PauseableTime(false);
		this.lastUpdate = -1;
		this.other = 0;
	}

	public void updateOther(int delta) {
		this.other += delta;
		this.lastUpdate = System.currentTimeMillis();
	}

	public void update(int delta) {
		if (delta < 0) {
			return;
		}//if (this.lastUpdate + PowderTrackerHudFoldable.getConfig().getTimeoutTime() < System.currentTimeMillis()) {//this.reset();//}

		powderGained += delta;
		this.lastUpdate = System.currentTimeMillis();
		this.unpause();
	}

	public void unpause() {
		pauseableTime.unpause();
	}

	public void pause() {
		pauseableTime.pause();
	}

	public void pauseIfInactive() {

	}

	public long getGained() {
		this.pauseIfInactive();
		return this.powderGained;
	}

	public int getMillisecondsActive() {
		this.pauseIfInactive();
		//f (!PowderTrackerHudFoldable.getConfig().showMs.getValue()) {
		//	return (int) ((this.pauseableTime.getTimePassed() / 1000) * 1000);
		//
		return (int) (this.pauseableTime.getTimePassed());
	}

	public double getGainesPerMinute() {
		return this.getGained() / (this.pauseableTime.getTimePassed() / 60000f);
	}

	public double getProjectedAmountPerHour() {
		return getGainesPerMinute() * 60;
	}

	public boolean isPaused() {
		return this.pauseableTime.isPaused();
	}

	public double getOtherPerMinute() {
		return this.other / (this.pauseableTime.getTimePassed() / 60000f);
	}

	public int getOtherTotal() {
		return this.other;
	}
}
