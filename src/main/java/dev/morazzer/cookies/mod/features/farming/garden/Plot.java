package dev.morazzer.cookies.mod.features.farming.garden;

	import java.util.Optional;

import dev.morazzer.cookies.mod.utils.dev.DevUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Helper class for detecting whether the play is on a plot and what plot he is on.
 */
@SuppressWarnings("MissingJavadoc")
public enum Plot {

	BARN,
	INNER_EDGE_PLOT,
	INNER_CORNER_PLOT,
	OUTER_EDGE_PLOT,
	OUTER_CORNER_PLOT,
	NONE;
	private static final Identifier SKIP_PLOT_CHECK = DevUtils.createIdentifier("plots/skip_check");

	/**
	 * Gets the plot the player currently is on.
	 *
	 * @return The plot.
	 */
	public static Plot getCurrentPlot() {
		return Optional.ofNullable(MinecraftClient.getInstance().player)
				.map(Entity::getPos)
				.map(Plot::getPlotFromRealCoordinate)
				.orElse(Plot.NONE);
	}

	/**
	 * Gets the plot from the real coordinates.
	 *
	 * @param position The coordinates.
	 * @return The plot.
	 */
	public static Plot getPlotFromRealCoordinate(Vec3d position) {
		return getPlotFromPlotCoordinate(
				(int) (changeToPlotCenter(Math.abs(position.x)) + 48) / 96,
				(int) (changeToPlotCenter(Math.abs(position.z)) + 48) / 96);
	}

	/**
	 * Gets the plot from the plot coordinates.
	 *
	 * @param x The x coordinate.
	 * @param z The y coordinate.
	 * @return The plot.
	 */
	public static Plot getPlotFromPlotCoordinate(int x, int z) {
		return getPlotFromAbsolutePlotCoordinate(Math.abs(x), Math.abs(z));
	}

	private static double changeToPlotCenter(double coordinate) {
		return coordinate - coordinate % 48;
	}

	private static Plot getPlotFromAbsolutePlotCoordinate(int absoluteX, int absoluteY) {
		if (absoluteX == 2 && absoluteY == 2) {
			return OUTER_CORNER_PLOT;
		} else if (absoluteX == 1 && absoluteY == 1) {
			return INNER_CORNER_PLOT;
		} else if (Math.min(absoluteX, absoluteY) == 0 && Math.max(absoluteX, absoluteY) == 1) {
			return INNER_EDGE_PLOT;
		} else if (absoluteX + absoluteY == 0) {
			return BARN;
		} else if (absoluteX <= 2 && absoluteY <= 2) {
			return OUTER_EDGE_PLOT;
		} else {
			return NONE;
		}
	}

	private static double changeToPlotCorner(double coordinate) {
		return coordinate - Math.abs(coordinate % 96);
	}

	/**
	 * Gets the center of the plot the location is on.
	 *
	 * @param position The location.
	 * @return The center of the plot.
	 */
	public Vec3d getPlotCenter(Vec3d position) {
		return new Vec3d(changeToPlotCorner(position.x + 240) - 192, 0, changeToPlotCorner(position.z + 240) - 192);
	}

	public int toPlotId(Vec3d position) {
		if (this == BARN || this == NONE) {
			return -1;
		}
		final Vec3d plotCenter = getPlotCenter(position);
		switch (this) {
			case INNER_EDGE_PLOT -> {
				if (plotCenter.z < 0) {
					return 1;
				} else if (plotCenter.x < -48) {
					return 2;
				} else if (plotCenter.x > 48) {
					return 3;
				} else {
					return 4;
				}
			}
			case INNER_CORNER_PLOT -> {
				if (plotCenter.z < 0) {
					if (plotCenter.x < 0) {
						return 5;
					}
					return 6;
				}
				if (plotCenter.x < 0) {
					return 7;
				}
				return 8;
			}
			case OUTER_EDGE_PLOT -> {
				if (plotCenter.x == 0 && plotCenter.z < 0) {
					return 9;
				} else if (plotCenter.z == 0 && plotCenter.x < 0) {
					return 10;
				} else if (plotCenter.z == 0 && plotCenter.x > 0) {
					return 11;
				} else if (plotCenter.x == 0 && plotCenter.z > 0) {
					return 12;
				}

				final boolean isXCloser = Math.min(Math.abs(plotCenter.x), Math.abs(plotCenter.z)) == Math.abs(plotCenter.x);

				if (plotCenter.z < 0 && plotCenter.x < 0) {
					if (isXCloser) {
						return 15;
					}
					return 13;
				} else if (plotCenter.x < 0 && plotCenter.z > 0) {
					if (isXCloser) {
						return 19;
					}
					return 17;
				} else if (plotCenter.x > 0 && plotCenter.z < 0) {
					if (isXCloser) {
						return 14;
					}
					return 16;
				} else if (plotCenter.x > 0 && plotCenter.z > 0) {
					if (isXCloser) {
						return 20;
					}
					return 18;
				}
			}
			case OUTER_CORNER_PLOT -> {
				if (plotCenter.z < 0 && plotCenter.x < 0) {
					return 21;
				} else if (plotCenter.z < 0 && plotCenter.x > 0) {
					return 22;
				} else if (plotCenter.z > 0 && plotCenter.x < 0) {
					return 23;
				} else if (plotCenter.z > 0 && plotCenter.x > 0) {
					return 24;
				}
			}
		}
		return -1;
	}

	/**
	 * @return Whether the plot is valid or not.
	 */
	public boolean isValidPlot() {
		return ordinal() != 5;
	}

	/**
	 * @return Whether the plot is an inner plot.
	 */
	public boolean isInnerCircle() {
		return (ordinal() == 1 || ordinal() == 2) || DevUtils.isEnabled(SKIP_PLOT_CHECK);
	}

	/**
	 * @return Whether the plot is an outer plot.
	 */
	public boolean isOuterCircle() {
		return (ordinal() == 3 || ordinal() == 4) || DevUtils.isEnabled(SKIP_PLOT_CHECK);
	}

	/**
	 * @return Whether the plot is the barn.
	 */
	public boolean isBarn() {
		return ordinal() == 0 || DevUtils.isEnabled(SKIP_PLOT_CHECK);
	}

	/**
	 * @return Whether the plot is on the edge.
	 */
	public boolean isEdge() {
		return (ordinal() == 1 || ordinal() == 3) || DevUtils.isEnabled(SKIP_PLOT_CHECK);
	}

	/**
	 * @return Whether the plot is a corner plot.
	 */
	public boolean isCorner() {
		return (ordinal() == 2 || ordinal() == 4) || DevUtils.isEnabled(SKIP_PLOT_CHECK);
	}
}
