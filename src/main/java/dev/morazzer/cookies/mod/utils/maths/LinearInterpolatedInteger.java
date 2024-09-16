package dev.morazzer.cookies.mod.utils.maths;

/**
 * Linear interpolated integer.
 */
@SuppressWarnings("unused")
public non-sealed class LinearInterpolatedInteger extends InterpolatedInteger {

    /**
     * Creates a linear interpolated integer.
     *
     * @param timeToTarget The time to reach the target value.
     * @param startValue   The start value.
     */
    public LinearInterpolatedInteger(long timeToTarget, int startValue) {
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

        this.value = (int) ((1 - time) * this.startValue + time * this.targetValue);
        this.lastMillis = System.currentTimeMillis();
    }


}
