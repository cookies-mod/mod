package dev.morazzer.cookies.mod.utils.maths;

import lombok.Getter;
import lombok.Setter;

/**
 * An interpolated integer.
 */
public sealed abstract class InterpolatedInteger permits LinearInterpolatedInteger, SigmoidInterpolatedInteger {

	protected final long timeToTarget;
	protected long timeSpend;
	@Setter
	@Getter
	protected int value;
	protected int targetValue;
	protected int startValue;
	protected long lastMillis;
	protected boolean hasReachedTarget;

	/**
	 * Creates an interpolated integer.
	 *
	 * @param timeToTarget The time to reach the target value.
	 * @param startValue   The start value.
	 */
	public InterpolatedInteger(long timeToTarget, int startValue) {
		this.timeToTarget = timeToTarget;
		this.startValue = startValue;
		this.value = startValue;
		this.hasReachedTarget = true;
	}

	/**
	 * Sets the target value.
	 *
	 * @param targetValue The target value.
	 */
	public void setTargetValue(int targetValue) {
		this.setTargetValue(targetValue, false);
	}

	/**
	 * Sets the target value.
	 *
	 * @param targetValue The new target.
	 * @param force       If the target should be set even if its same.
	 */
	public void setTargetValue(int targetValue, boolean force) {
		if (!force && targetValue == this.targetValue) {
			return;
		}
		this.startValue = this.value;
		this.targetValue = targetValue;
		this.timeSpend = 0;
		this.lastMillis = System.currentTimeMillis();
		this.hasReachedTarget = false;
	}

	public long lastMillis() {
		return this.lastMillis;
	}

	/**
	 * Called to tick the integer.
	 * @param deltaTime The time difference between this and the last tick.
	 */
	public abstract void tick(long deltaTime);

	/**
	 * @return Whether the integer has reached its target or not.
	 */
	public boolean hasReachedTarget() {
		return this.hasReachedTarget;
	}

	/**
	 * @return The target value.
	 */
	public int getTarget() {
		return this.targetValue;
	}

	/**
	 * Ticks the integer, the time between this and the previous tick is calculated automatically.
	 */
	public void tick() {
		if (this.hasReachedTarget()) {
			return;
		}
		this.tick(System.currentTimeMillis() - this.lastMillis());
	}

}
