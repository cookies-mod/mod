package codes.cookies.mod.config.categories.mining.powder;

public enum TimeoutTime {

	ONE_MIN(1), TWO_MINE(2), FIVE_MIN(5), TEN_MIN(10), TWENTY_MIN(20);

	private final int timeInMinutes;

	TimeoutTime(int timeInMinutes) {
		this.timeInMinutes = timeInMinutes;
	}

	public long getTimeInMilliseconds() {
		return timeInMinutes * 60 * 1000L;
	}

}
