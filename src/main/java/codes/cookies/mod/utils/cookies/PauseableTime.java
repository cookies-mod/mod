package codes.cookies.mod.utils.cookies;

/**
 * Represents a timer that can be paused.
 */
public class PauseableTime {
	private final long startedAt;
	private long pausedAt;
	private long pausedFor = 0;

	public PauseableTime(boolean start) {
		this.startedAt = System.currentTimeMillis();
		this.pausedAt = System.currentTimeMillis();
		if (start) {
			this.unpause();
		}
	}

	/**
	 * Unpauses the time if paused, else do nothing.
	 */
	public void unpause() {
		if (this.pausedAt == -1) {
			return;
		}
		this.pausedFor += System.currentTimeMillis() - this.pausedAt;
		this.pausedAt = -1;
	}

	/**
	 * Pauses the timer, if it isn't already paused.
	 */
	public void pause() {
		if (this.isPaused()) {
			return;
		}
		this.pausedAt = System.currentTimeMillis();
	}

	/**
	 * @return The time the timer was active for in total.
	 */
	public long getTimePassed() {
		if (this.pausedAt == -1) {
			return System.currentTimeMillis() - (this.startedAt + this.pausedFor);
		}
		return this.pausedAt - (this.startedAt + this.pausedFor);
	}

	/**
	 * @return Whether the timer currently is paused.
	 */
	public boolean isPaused() {
		return this.pausedAt != -1;
	}
}
