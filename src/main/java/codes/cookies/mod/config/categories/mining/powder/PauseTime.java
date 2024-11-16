package codes.cookies.mod.config.categories.mining.powder;

public enum PauseTime {

	TEN_SEC(10), THIRTY_SEC(30), ONE_MIN(60), TWO_MIN(120), FIVE_MIN(300), TEN_MIN(600);

	private final int timeInSec;

	PauseTime(int timeInSec) {
		this.timeInSec = timeInSec;
	}

	public long getTimeInMilliseconds() {
		return timeInSec * 1000L;
	}
}
