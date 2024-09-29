package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Count context for forge items.
 */
public class ForgeCountContext {

	public List<Long> startTimes = new LinkedList<>();
	public Long lastStartTimeUsed;
	public int used;

	/**
	 * Gets the amount of available items.
	 */
	public int getAvailable() {
		return this.startTimes.size() - this.used;
	}

	/**
	 * Removes the amount of items from the available items.
	 * @param amount The amount to remove.
	 */
	public void take(int amount) {
		if (this.used == this.startTimes.size()) {
			this.lastStartTimeUsed = null;
			return;
		}

		this.used = Math.min(this.used + amount, this.startTimes.size());
		this.lastStartTimeUsed = this.startTimes.get(this.used - 1);
	}

	/**
	 * Adds a start time to the items.
	 * @param time The time to add.
	 */
	public void addStartTime(long time) {
		this.startTimes.add(time);
		this.startTimes.sort(Long::compareTo);
	}

	/**
	 * Gets the last used start time.
	 * @return The last used start time.
	 */
	public long getLastTimeStarted() {
		return this.lastStartTimeUsed == null ? -1 : this.lastStartTimeUsed;
	}

}
