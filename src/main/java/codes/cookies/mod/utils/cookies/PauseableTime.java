package codes.cookies.mod.utils.cookies;

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

	public void unpause() {
		if (this.pausedAt == -1) {
			return;
		}
		this.pausedFor += System.currentTimeMillis() - this.pausedAt;
		this.pausedAt = -1;
	}

	public void pause() {
		if (this.isPaused()) {
			return;
		}
		this.pausedAt = System.currentTimeMillis();
	}

	public long getTimePassed() {
		if (this.pausedAt == -1) {
			return System.currentTimeMillis() - (this.startedAt + this.pausedFor);
		}
		return this.pausedAt - (this.startedAt + this.pausedFor);
	}

	public boolean isPaused() {
		return this.pausedAt != -1;
	}
}
