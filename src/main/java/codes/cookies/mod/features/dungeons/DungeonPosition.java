package codes.cookies.mod.features.dungeons;

import com.mojang.datafixers.util.Function4;

import codes.cookies.mod.features.dungeons.map.DungeonMap;

import codes.cookies.mod.utils.maths.InterpolatedInteger;

import codes.cookies.mod.utils.maths.LinearInterpolatedInteger;

import java.util.function.BiFunction;

import java.util.function.Function;
import java.util.function.Supplier;

import java.util.function.ToIntFunction;

import lombok.Getter;

import net.minecraft.util.math.MathHelper;

import org.joml.Vector2i;

/**
 * A position that is within a dungeon, this always is stored as world space coordinates but can be converted to
 * either dungeon map coordinates or room map coordinates.
 */
public class DungeonPosition {
	private final InterpolatedInteger worldX;
	private final InterpolatedInteger worldY;
	@Getter
	private final DungeonInstance dungeonInstance;

	public DungeonPosition(int worldX, int worldY, DungeonInstance dungeonInstance) {
		this.worldX = new LinearInterpolatedInteger(100, worldX);
		this.worldY = new LinearInterpolatedInteger(100, worldY);
		this.dungeonInstance = dungeonInstance;
	}

	/**
	 * Wraps the function with a {@link ToIntFunction} to use it in streams.
	 *
	 * @param toInt The function.
	 * @return The {@link ToIntFunction}.
	 */
	public static ToIntFunction<DungeonPosition> toInt(Function<DungeonPosition, Integer> toInt) {
		return toInt::apply;
	}

	/**
	 * Returns a function that maps the position to the interpolated field value.
	 *
	 * @param mapper The field accessor.
	 * @return The function.
	 */
	public static Function<DungeonPosition, Integer> interpolate(BiFunction<DungeonPosition,
        Function<InterpolatedInteger, Integer>, Integer> mapper) {
		return value -> mapper.apply(value, InterpolatedInteger::getValue);
	}

	/**
	 * Returns a function that maps the position to the target (aka the not interpolated) field value.
	 *
	 * @param mapper The field accessor.
	 * @return The function.
	 */
	public static Function<DungeonPosition, Integer> target(BiFunction<DungeonPosition, Function<InterpolatedInteger,
        Integer>, Integer> mapper) {
		return value -> mapper.apply(value, InterpolatedInteger::getTarget);
	}

	/**
	 * Gets the interpolated value of the provided field.
	 *
	 * @param mapper The field to get.
	 * @return The interpolated value.
	 */
	public static int interpolated(Function<Function<InterpolatedInteger, Integer>, Integer> mapper) {
		return mapper.apply(InterpolatedInteger::getValue);
	}

	/**
	 * Gets the target value of the provided field.
	 *
	 * @param mapper The field to get.
	 * @return The target value.
	 */
	public static int target(Function<Function<InterpolatedInteger, Integer>, Integer> mapper) {
		return mapper.apply(InterpolatedInteger::getTarget);
	}

	/**
	 * Gets the item map x of this position.
	 *
	 * @param mapper The mapper.
	 * @return The map x.
	 */
	public int getMapX(Function<InterpolatedInteger, Integer> mapper) {
		return this.withCoordinates(
				this.map(mapper.apply(this.worldX),
						DungeonMap::getRoomsInX,
						Vector2i::x,
						this::mapWorldCoordinatesToMap),
				this::zero);
	}

	/**
	 * Gets the item map y of this position.
	 *
	 * @param mapper The mapper.
	 * @return The map y.
	 */
	public int getMapY(Function<InterpolatedInteger, Integer> mapper) {
		return this.withCoordinates(
				this.map(mapper.apply(this.worldY),
						DungeonMap::getRoomsInY,
						Vector2i::y,
						this::mapWorldCoordinatesToMap),
				this::zero);
	}

	/**
	 * Gets the world x of this position.
	 *
	 * @param mapper The mapper.
	 * @return The world x.
	 */
	public int getWorldX(Function<InterpolatedInteger, Integer> mapper) {
		return mapper.apply(this.worldX);
	}

	/**
	 * Gets the world y of this position.
	 *
	 * @param mapper The mapper.
	 * @return The world y.
	 */
	public int getWorldY(Function<InterpolatedInteger, Integer> mapper) {
		return mapper.apply(this.worldY);
	}

	/**
	 * Sets the world x of this position.
	 *
	 * @param worldX The world x.
	 */
	public void setWorldX(int worldX) {
		this.worldX.setTargetValue(worldX);
	}

	/**
	 * Sets the world y of this position.
	 *
	 * @param worldY The world y.
	 */
	public void setWorldY(int worldY) {
		this.worldY.setTargetValue(worldY);
	}

	/**
	 * Gets the room map x of this position.
	 *
	 * @param mapper The mapper.
	 * @return The room map x.
	 */
	public int getRoomMapX(Function<InterpolatedInteger, Integer> mapper) {
		return this.mapToRoomMap(mapper.apply(this.worldX));
	}

	/**
	 * Gets the room map y of this position.
	 *
	 * @param mapper The mapper.
	 * @return The room map y.
	 */
	public int getRoomMapY(Function<InterpolatedInteger, Integer> mapper) {
		return this.mapToRoomMap(mapper.apply(this.worldY));
	}

	/**
	 * Sets the position to be equal to the room map x.
	 *
	 * @param x The room map x.
	 */
	public void setRoomMapX(int x) {
		this.worldX.setTargetValue(this.mapRoomMapToWorld(x));
	}

	/**
	 * Sets the position to be equal to the room map y.
	 *
	 * @param y The room map y.
	 */
	public void setRoomMapY(int y) {
		this.worldY.setTargetValue(this.mapRoomMapToWorld(y));
	}

	/**
	 * Sets the position to be equal to the item map x.
	 *
	 * @param mapX The item map x.
	 */
	public void setMapX(int mapX) {
		this.worldX.setTargetValue(this.withCoordinates(this.map(mapX,
				DungeonMap::getRoomsInX,
				Vector2i::x,
				this::mapMapCoordinatesToWorld), this::zero));
	}

	/**
	 * Sets the position to be equal to the item map y.
	 *
	 * @param mapY The item map y.
	 */
	public void setMapY(int mapY) {
		this.worldY.setTargetValue(this.withCoordinates(this.map(mapY,
				DungeonMap::getRoomsInY,
				Vector2i::y,
				this::mapMapCoordinatesToWorld), this::zero));
	}

	/**
	 * Ticks the coordinates.
	 */
	public void tick() {
		this.worldX.tick();
		this.worldY.tick();
	}

	/**
	 * Gets the dungeon map this position is associated with.
	 *
	 * @return The dungeon map.
	 */
	private DungeonMap getDungeonMap() {
		return this.dungeonInstance.getDungeonMap();
	}

	/**
	 * Maps the room map space to the world space.
	 *
	 * @param value The room map space coordinate.
	 * @return The world space.
	 */
	private int mapRoomMapToWorld(int value) {
		return value * 32 - 200;
	}

	/**
	 * Maps the world space to the room map space.
	 *
	 * @param value The world space coordinate.
	 * @return The room map space.
	 */
	private int mapToRoomMap(int value) {
		return Math.round((value + 200f) / 32 * 2) / 2;
	}

	/**
	 * @return Provider for default return value if no dungeon instance was provided.
	 */
	private int zero() {
		return 0;
	}

	/**
	 * Map world space coordinates to item map space coordinates.
	 *
	 * @param value       The value to map.
	 * @param topLeft     The top left value.
	 * @param bottomRight The bottom right value.
	 * @param rooms       The amount of rooms in the mapped axis.
	 * @return The mapped item map coordinate.
	 */
	private int mapWorldCoordinatesToMap(int value, int topLeft, int bottomRight, int rooms) {
		return (int) MathHelper.clampedMap(value, -200, -200 + (rooms * 32), topLeft, bottomRight);
	}

	/**
	 * Map item map space coordinates to world space coordinates.
	 *
	 * @param value       The value to map.
	 * @param topLeft     The top left value.
	 * @param bottomRight The bottom right value.
	 * @param rooms       The amount of rooms in the mapped axis.
	 * @return The mapped world space coordinates.
	 */
	private int mapMapCoordinatesToWorld(int value, int topLeft, int bottomRight, int rooms) {
		return (int) MathHelper.clampedMap(value, topLeft, bottomRight, -200, -200 + (rooms * 32));
	}

	/**
	 * Helper function to deduplicate code.
	 *
	 * @param value    The value to map.
	 * @param rooms    The provided to get the rooms in the mapped axis.
	 * @param mapper   The mapper to extract the correct coordinate.
	 * @param function The mapper to map between current and target space.
	 * @param <T>      The type of number (only used as int atm, but has support for more).
	 * @return The mapped number.
	 */
	private <T> BiFunction<Vector2i, Vector2i, T> map(
			T value, Function<DungeonMap, T> rooms, Function<Vector2i, T> mapper, Function4<T, T, T, T, T> function) {
		return (topLeft, topRight) -> function.apply(value,
				mapper.apply(topLeft),
				mapper.apply(topRight),
				rooms.apply(this.getDungeonMap()));
	}

	/**
	 * Runs the current operation with the coordinates.
	 *
	 * @param consumer     The consumer to run with the coordinates.
	 * @param defaultValue The default value if no coordinates where found.
	 * @param <T>          The type of number.
	 * @return The mapped value.
	 */
	private <T> T withCoordinates(BiFunction<Vector2i, Vector2i, T> consumer, Supplier<T> defaultValue) {
		final Vector2i topLeft = this.getTopLeft();
		final Vector2i bottomRight = this.getBottomRight();
		if (topLeft == null || bottomRight == null) {
			return defaultValue.get();
		}
		return consumer.apply(topLeft, bottomRight);
	}

	/**
	 * Gets the top left coordinate of the item map.
	 *
	 * @return The top left coordinate.
	 */
	private Vector2i getTopLeft() {
		if (this.dungeonInstance == null) {
			return null;
		}
		return this.dungeonInstance.getDungeonMap().getTopLeftPixel();
	}

	/**
	 * Gets the bottom right coordinate of the item map.
	 *
	 * @return The bottom right coordinate.
	 */
	private Vector2i getBottomRight() {
		if (this.dungeonInstance == null) {
			return null;
		}
		return this.dungeonInstance.getDungeonMap().getBottomRightPixel();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DungeonPosition other) {
			return this.worldX == other.worldX && this.worldY == other.worldY;
		}
		return super.equals(obj);
	}
}
