package codes.cookies.mod.utils.skyblock;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

/**
 * Utility class to work with skyblock time.
 */
@Getter
public class SkyblockDateTime {
	/**
	 * Creates the skyblock time of a real world instant.
	 * @param instant The instant.
	 */
	public SkyblockDateTime(Instant instant) {
		this.instant = instant;
		this.skyblockInstant = this.instant.minus(Duration.ofSeconds(SKYBLOCK_EPOCH.getEpochSecond()));
	}

	/**
	 * Creates the skyblock time based on a skyblock instant.
	 * @param instant The skyblock isntant.
	 * @return The skyblock time.
	 */
	public static SkyblockDateTime ofSkyblockInstant(Instant instant) {
		return new SkyblockDateTime(instant.plus(Duration.ofSeconds(SKYBLOCK_EPOCH.getEpochSecond())));
	}

	/**
	 * @return The current skyblock time.
	 */
	public static SkyblockDateTime now() {
		return new SkyblockDateTime(Instant.now());
	}

	private final static Instant SKYBLOCK_EPOCH = Instant.ofEpochMilli(1_560_275_700_000L);

	private final Instant instant;
	private final Instant skyblockInstant;

	private static final long SB_HOUR = 50;
	private static final long SB_DAY = SB_HOUR * 24;
	private static final long SB_MONTH = SB_DAY * 31;
	private static final long SB_SEASON = SB_MONTH * 3;
	private static final long SB_YEAR = SB_SEASON * 4;

	private long getSkyblockInstant() {
		return this.skyblockInstant.getEpochSecond();
	}

	private int getElapsedSkyblockMinutes() {
		return (int) Math.floor((this.getSkyblockInstant() / 0.83));
	}

	private int getCurrentSkyblockMinute() {
		return (int) Math.floor((this.getSkyblockInstant() % 50) / 0.83);
	}

	private int getElapsedSkyblockHours() {
		return (int) Math.floor((this.getSkyblockInstant() / 50f));
	}

	private int getCurrentSkyblockHour() {
		return (int) Math.floor((this.getSkyblockInstant() % (float) SB_DAY) / SB_HOUR);
	}

	private int getElapsedSkyblockDays() {
		return (int) Math.floor(this.getSkyblockInstant() / (float) SB_DAY);
	}

	private int getCurrentSkyblockDay() {
		return (int) Math.floor((this.getSkyblockInstant() % (float) SB_MONTH) / SB_DAY) + 1;
	}

	private int getElapsedSkyblockMonths() {
		return (int) Math.floor(this.getSkyblockInstant() / (float) SB_MONTH);
	}

	private int getCurrentSkyblockMonth() {
		return (int) Math.floor((this.getSkyblockInstant() % (float) SB_YEAR) / SB_MONTH) + 1;
	}

	private int getSkyblockYear() {
		return (int) Math.floor(this.getSkyblockInstant() / (float) SB_YEAR) + 1;
	}

	public boolean isInFuture() {
		SkyblockDateTime now = now();
		return now.getElapsedSkyblockDays() < this.getElapsedSkyblockDays();
	}

	public boolean isCurrentDay() {
		SkyblockDateTime now = now();
		return now.getElapsedSkyblockDays() == this.getElapsedSkyblockDays();
	}

	public boolean isInPast() {
		SkyblockDateTime now = now();
		return now.getElapsedSkyblockDays() > this.getElapsedSkyblockDays();
	}

	@Override
	public String toString() {
		return "%s:%S %s/%s/%s".formatted(
				getCurrentSkyblockHour(),
				getCurrentSkyblockMinute(),
				getCurrentSkyblockDay(),
				getCurrentSkyblockMonth(),
				getSkyblockYear()
		);
	}

	public String toStringWithEvents() {
		StringBuilder stringBuilder = new StringBuilder();
		for (SkyblockEvents value : SkyblockEvents.values()) {
			if (this.isActive(value)) {
				stringBuilder.append(value).append(", ");
			}
		}
		String suffix = "";
		if (!stringBuilder.isEmpty()) {
			suffix = "[%s]".formatted(stringBuilder.substring(0, stringBuilder.length() - 2));
		}
		return "%s %s".formatted(
				toString(),
				suffix
		);
	}

	public SkyblockDateTime withYear(int year) {
		return new SkyblockDateTime(Instant.ofEpochSecond((this.getInstant()
				.getEpochSecond() - ((this.getSkyblockYear()) * SB_YEAR)) + (year * SB_YEAR)));
	}

	public SkyblockDateTime withMonth(int month) {
		return new SkyblockDateTime(Instant.ofEpochSecond((this.getInstant()
				.getEpochSecond() - ((this.getCurrentSkyblockMonth() - 1) * SB_MONTH)) + (month * SB_MONTH)));
	}

	public SkyblockDateTime withDay(int day) {
		return new SkyblockDateTime(Instant.ofEpochSecond((this.getInstant()
				.getEpochSecond() - ((this.getCurrentSkyblockDay() - 1) * SB_DAY)) + (day * SB_DAY)));
	}

	public SkyblockDateTime withMinute(int minute) {
		return new SkyblockDateTime(Instant.ofEpochMilli((long) (this.getInstant()
				.getEpochSecond() - this.getInstant().getEpochSecond() % 50 + minute * 0.83) * 1000));
	}

	public SkyblockDateTime withMinuteZero() {
		return new SkyblockDateTime(Instant.ofEpochSecond(this.getInstant()
				.getEpochSecond() - this.getInstant().getEpochSecond() % 50));
	}

	public SkyblockDateTime withHour(int hour) {
		return new SkyblockDateTime(Instant.ofEpochSecond(((this.getInstant()
				.getEpochSecond() - (this.getInstant().getEpochSecond() % SB_DAY)) + (hour * SB_HOUR))));
	}

	private SkyblockDateTime with(int minute, int hour, int day, int month, int year) {
		return SkyblockDateTime.ofSkyblockInstant(Instant.ofEpochMilli((long) ((minute * 0.83 + hour * SB_HOUR + day * SB_DAY + month * SB_MONTH + year * SB_YEAR) * 1000)));
	}

	/**
	 * Checks whether a provided even is currently active.
	 * @param events The event to check.
	 * @return Whether it is active.
	 */
	public boolean isActive(SkyblockEvents events) {
		return switch (events) {
			case FARMING_CONTEST -> (getElapsedSkyblockDays() % 3) == 1;
			case DARK_AUCTION -> (getElapsedSkyblockDays() % 3) == 0;
			case STAR_CULT -> (getCurrentSkyblockDay() != 0) && ((getCurrentSkyblockDay() % 7) == 0);
			case NEW_YEAR -> (getCurrentSkyblockMonth() == 12) && (getCurrentSkyblockDay() >= 29);
			case TRAVELING_ZOO -> ((getCurrentSkyblockMonth() == 4) && (getCurrentSkyblockDay() <= 3))
					|| ((getCurrentSkyblockMonth() == 10) && (getCurrentSkyblockDay() <= 3));
			case SPOOKY_FESTIVAL -> getCurrentSkyblockMonth() == 8 && getCurrentSkyblockDay() >= 29;
			case WINTER_ISLAND -> getCurrentSkyblockMonth() == 12;
			case JERRY_WORKSHOP -> getCurrentSkyblockMonth() == 12 && getCurrentSkyblockDay() >= 24
					&& getCurrentSkyblockDay() <= 26;
			case ELECTION_START -> this.getCurrentSkyblockMonth() == 6 && this.getCurrentSkyblockDay() == 27;
			case ELECTION_CLOSE -> this.getCurrentSkyblockMonth() == 3 && this.getCurrentSkyblockDay() == 27;
			case ELECTION_OPEN ->
					(this.getCurrentSkyblockMonth() < 3) || ((this.getCurrentSkyblockMonth() == 3) && (this.getCurrentSkyblockDay() < 27))
							|| (this.getCurrentSkyblockMonth() > 6) || ((this.getCurrentSkyblockMonth() == 6) && (this.getCurrentSkyblockDay() >= 27));
			default -> false;
		};
	}

	/**
	 * Gets the next skyblock time the provided event starts at.
	 * @param skyblockEvents The event.
	 * @return The time it happens at.
	 */
	public SkyblockDateTime getNext(SkyblockEvents skyblockEvents) {
		return switch (skyblockEvents) {
			case FARMING_CONTEST -> this.with(
					0,
					0,
					(this.getCurrentSkyblockDay() + 3) - ((this.getCurrentSkyblockDay() % 3)) - 1,
					getCurrentSkyblockMonth() - 1,
					getSkyblockYear() - 1
			);
			case DARK_AUCTION -> this.with(
					0,
					0,
					(this.getCurrentSkyblockDay() + 3) - ((this.getCurrentSkyblockDay() % 3)) - 2,
					getCurrentSkyblockMonth() - 1,
					getSkyblockYear() - 1
			);
			case STAR_CULT -> {
				if (this.getCurrentSkyblockDay() < 27) {
					yield this.with(
							0,
							0,
							this.getCurrentSkyblockDay() - (this.getCurrentSkyblockDay() % 7) + 6,
							getCurrentSkyblockMonth() - 1,
							getSkyblockYear() - 1
					);
				} else {
					yield this.with(
							0,
							0,
							6,
							getCurrentSkyblockMonth(),
							getSkyblockYear() - 1
					);
				}
			}
			case NEW_YEAR -> {
				if (this.isActive(SkyblockEvents.NEW_YEAR)) {
					yield this.with(0, 0, 28, 11, this.getSkyblockYear());
				} else {
					yield this.with(0, 0, 28, 11, this.getSkyblockYear() - 1);
				}
			}
			case TRAVELING_ZOO -> {
				if (this.getCurrentSkyblockMonth() == 10 && this.getCurrentSkyblockDay() > 3
						|| this.getCurrentSkyblockMonth() > 10 || this.getCurrentSkyblockMonth() < 3) {
					yield this.with(0, 0, 0, 2, this.getSkyblockYear());
				} else {
					yield this.with(0, 0, 0, 9, this.getSkyblockYear());
				}
			}
			case WINTER_ISLAND -> {
				if (getCurrentSkyblockMonth() == 12) {
					yield this.with(0, 0, 0, 11, this.getSkyblockYear());
				} else {
					yield this.with(0, 0, 0, 11, this.getSkyblockYear() - 1);
				}
			}
			case ELECTION_CLOSE -> {
				if (this.getCurrentSkyblockMonth() < 3 || this.getCurrentSkyblockMonth() == 3 && getCurrentSkyblockDay() < 27) {
					yield this.with(0, 0, 26, 2, this.getSkyblockYear() - 1);
				} else {
					yield this.with(0, 0, 26, 2, this.getSkyblockYear());
				}
			}
			case ELECTION_START, ELECTION_OPEN -> {
				if (this.getCurrentSkyblockMonth() < 6 || this.getCurrentSkyblockMonth() == 6 && this.getCurrentSkyblockDay() < 27) {
					yield this.with(0, 0, 26, 5, this.getSkyblockYear() - 1);
				} else {
					yield this.with(0, 0, 26, 5, this.getSkyblockYear());
				}
			}
			case JERRY_WORKSHOP -> {
				if (this.getCurrentSkyblockMonth() == 12 && this.getCurrentSkyblockDay() >= 24) {
					yield this.with(0, 0, 23, 11, this.getSkyblockYear());
				} else {
					yield this.with(0, 0, 23, 11, this.getSkyblockYear() - 1);
				}
			}
			case SPOOKY_FESTIVAL -> {
				if (this.getCurrentSkyblockMonth() > 8 || this.getCurrentSkyblockMonth() == 8 && this.getCurrentSkyblockDay() >= 29) {
					yield this.with(0, 0, 28, 7, this.getSkyblockYear());
				} else {
					yield this.with(0, 0, 28, 7, this.getSkyblockYear() - 1);
				}
			}
			default -> this;
		};
	}

	/**
	 * A list of all (supported) skyblock events.
	 */
	public enum SkyblockEvents {
		FARMING_CONTEST,
		TRAVELING_ZOO,
		ELECTION_START,
		SPOOKY_FESTIVAL,
		ELECTION_OPEN,
		ELECTION_CLOSE,
		THUNDER,
		RAIN,
		JERRY_WORKSHOP,
		WINTER_ISLAND,
		DARK_AUCTION,
		NEW_YEAR,
		STAR_CULT
	}
}
