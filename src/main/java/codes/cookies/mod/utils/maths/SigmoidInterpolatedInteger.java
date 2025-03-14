package codes.cookies.mod.utils.maths;

public final class SigmoidInterpolatedInteger extends InterpolatedInteger {

	/**
	 * Creates a sigmoid interpolated integer.
	 *
	 * @param timeToTarget The time to reach the target value.
	 * @param startValue   The start value.
	 */
	public SigmoidInterpolatedInteger(long timeToTarget, int startValue) {
		super(timeToTarget, startValue);
	}

	@Override
	public void tick(long deltaTime) {
		if (this.hasReachedTarget) {
			return;
		}
		this.timeSpend += deltaTime;
		float time = (float) this.timeSpend / (float) this.timeToTarget;
		if (time > 1) {
			this.hasReachedTarget = true;
			this.value = this.targetValue;
			return;
		}
		float progress = MathUtils.sigmoidZeroOne(time);

		this.value = (int) ((1 - progress) * this.startValue + progress * this.targetValue);
		this.lastMillis = System.currentTimeMillis();
	}
}
