package dev.morazzer.cookies.mod.features.farming.garden;

import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import java.util.Optional;
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

    /**
     * Gets the center of the plot the location is on.
     *
     * @param position The location.
     * @return The center of the plot.
     */
    public Vec3d getPlotCenter(Vec3d position) {
        return new Vec3d(changeToPlotCorner(position.x + 240) - 192, 0, changeToPlotCorner(position.z + 240) - 192);
    }

    private static double changeToPlotCorner(double coordinate) {
        return coordinate - Math.abs(coordinate % 96);
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
