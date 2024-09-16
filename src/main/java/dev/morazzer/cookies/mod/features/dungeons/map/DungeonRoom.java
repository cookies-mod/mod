package dev.morazzer.cookies.mod.features.dungeons.map;

import dev.morazzer.cookies.entities.websocket.packets.DungeonUpdateRoomSecrets;
import dev.morazzer.cookies.mod.config.categories.DungeonConfig;
import dev.morazzer.cookies.mod.features.dungeons.DungeonInstance;
import dev.morazzer.cookies.mod.features.dungeons.DungeonPosition;
import dev.morazzer.cookies.mod.features.dungeons.DungeonRoomData;

import dev.morazzer.cookies.mod.render.Renderable;

import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.maths.InterpolatedInteger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

/**
 * A dungeon room, this holds various information that is needed to render and display data associated with this room.
 */
@Getter
@Setter
public class DungeonRoom {
	/**
	 * A debug counter to display color, this isn't used without the debug flag enabled.
	 */
	private static int counter = 0;
	private final int debugRoomColor;

	private final DungeonInstance instance;
	private boolean isActive;
	private RoomType roomType;
	private DungeonRoomData data;
	private Checkmark checkmark;
	private DungeonRoom mergedWith;
	private Collection<DungeonPosition> position = new ArrayList<>();
	private int x;
	private int y;
	private int width;
	private int height;
	private int rotation;
	private String shape;
	private List<Renderable> waypoints = null;
	private int maxSecrets = -1;
	private int collectedSecrets = -1;

	public DungeonRoom(DungeonInstance instance, RoomType roomType) {
		this.debugRoomColor = counter++;
		this.instance = instance;
		this.roomType = roomType;
		this.checkmark = Checkmark.UNKNOWN;
	}

	/**
	 * Get the secret string of the current room.
	 *
	 * @return The secret string of the room.
	 */
	public String getSecretString() {
		if (this.maxSecrets == 0) {
			return null;
		}
		if (this.maxSecrets == -1 && this.collectedSecrets == -1) {
			return "?/?";
		} else if (this.maxSecrets == -1) {
			return this.collectedSecrets + "/?";
		} else if (this.collectedSecrets == -1) {
			return "?/" + this.maxSecrets;
		}
		return this.collectedSecrets + "/" + this.maxSecrets;
	}

	/**
	 * @return Whether the room a {@code (x,y)} is the same as the current room.
	 */
	public boolean isAt(int x, int y) {
		if (x < 0 && y < 0) {
			return false;
		}
		return this.equals(this.instance.getDungeonMap().getRoomAt(x, y));
	}

	/**
	 * Find the shape of the room, this is used for unknown rooms.
	 *
	 * @return The shape.
	 */
	public String findShape() {
		if (this.position.size() == 1) {
			return "1x1";
		} else if (this.position.size() == 2) {
			return "1x2";
		}

		final List<Integer> xList =
				this.position.stream().map(DungeonPosition.target(DungeonPosition::getRoomMapX)).distinct().toList();
		final List<Integer> yList =
				this.position.stream().map(DungeonPosition.target(DungeonPosition::getRoomMapY)).distinct().toList();

		int xSize = xList.size();
		int ySize = yList.size();

		if (xSize == 2 && ySize == 2 && this.position.size() == 4) {
			return "2x2";
		}
		if (Math.min(xSize, ySize) == 1) {
			final int max = Math.max(xSize, ySize);
			if (max == 3) {
				return "1x3";
			} else if (max == 4) {
				return "1x4";
			}
		}

		return "L";
	}

	/**
	 * Find and set the rotation of the room.
	 */
	public void findRotation() {
		this.rotation = this.calculateRotation();
	}

	/**
	 * Calculate the rotation of the room based on a few criteria.
	 *
	 * @return The rotation of the room.
	 */
	public int calculateRotation() {
		if (this.roomType == RoomType.FAIRY) {
			return -1;
		}

		if (this.shape.equals("2x2")) {
			return 0;
		}

		final List<Integer> xList =
				this.position.stream().map(DungeonPosition.target(DungeonPosition::getRoomMapX)).distinct().toList();
		final List<Integer> yList =
				this.position.stream().map(DungeonPosition.target(DungeonPosition::getRoomMapY)).distinct().toList();

		int xSize = xList.size();
		int ySize = yList.size();

		switch (this.shape) {
			case "1x2", "1x3", "1x4" -> {
				if (xSize == 1) {
					return 0;
				} else if (ySize == 1) {
					return 1;
				}
			}
		}

		if (this.shape.equals("L")) {
			final DungeonPosition dungeonPosition = this.position.stream().filter(pos1 -> {
				int x1 = DungeonPosition.target(pos1::getRoomMapX);
				int y1 = DungeonPosition.target(pos1::getRoomMapY);
				return this.position.stream().filter(pos2 -> {
					int x2 = DungeonPosition.target(pos2::getRoomMapX);
					int y2 = DungeonPosition.target(pos2::getRoomMapY);

					return (x1 == x2 && (y1 + 1 == y2 || y1 - 1 == y2)) || (y1 == y2 && (x1 + 1 == x2 || x1 - 1 == x2));
				}).count() == 2;
			}).findFirst().orElse(null);
			if (dungeonPosition == null) {
				return -1;
			}

			int cornerX = DungeonPosition.target(dungeonPosition::getRoomMapX);
			int cornerY = DungeonPosition.target(dungeonPosition::getRoomMapY);

			int minX = xList.stream().min(Integer::compareTo).orElse(0);
			int maxX = xList.stream().max(Integer::compareTo).orElse(0);
			int minY = yList.stream().min(Integer::compareTo).orElse(0);
			int maxY = yList.stream().max(Integer::compareTo).orElse(0);

			if (cornerX == minX && cornerY == maxY) {
				return 2;
			} else if (cornerX == maxX && cornerY == maxY) {
				return 3;
			} else if (cornerX == maxX && cornerY == minY) {
				return 0;
			} else if (cornerX == minX && cornerY == minY) {
				return 1;
			}
		}

		if (this.shape.equals("1x1")) {
			DungeonPosition position = this.position.stream().findFirst().orElse(null);
			if (position == null) {
				return -1;
			}
			int x = DungeonPosition.target(position::getRoomMapX);
			int y = DungeonPosition.target(position::getRoomMapY);

			DungeonDoor top = null, left = null, bottom = null, right = null;
			int hits = 0;

			for (DungeonDoor door : this.instance.getDungeonMap().getDoors()) {
				//	System.out.printf("Door at (%s:%s)%n", door.x(), door.y());
				if (door.x() == x && door.y() == y) {
					if (door.left() && left == null) {
						left = door;
						hits++;
					} else if (bottom == null) {
						bottom = door;
						hits++;
					}
				} else if (door.x() == x && door.y() == y - 1 && !door.left() && top == null) {
					top = door;
					hits++;
				} else if (door.x() == x + 1 && door.y() == y && door.left() && right == null) {
					right = door;
					hits++;
				}
			}

			return switch (hits) {
				case 4 -> 1;
				case 3 -> {
					if (left != null) {
						yield 3;
					} else if (right != null) {
						yield 1;
					} else if (bottom != null) {
						yield 2;
					}
					yield 0;
				}
				case 2 -> {
					if (top != null && bottom != null) {
						yield 2;
					} else if (left != null && right != null) {
						yield 1;
					} else if (left != null && bottom != null) {
						yield 1;
					} else if (top != null && right != null) {
						yield 3;
					} else if (top != null && left != null) {
						yield 2;
					} else if (right != null && bottom != null) {
						yield 0;
					}
					yield -1;
				}
				case 1 -> {
					if (left != null) {
						yield 0;
					} else if (right != null) {
						yield 2;
					} else if (bottom != null) {
						yield 3;
					} else {
						yield 1;
					}
				}
				default -> -1;
			};
		}
		return -1;
	}

	/**
	 * Whether the room can have secrets or not.
	 */
	public boolean canHaveSecrets() {
		return switch (this.roomType) {
			case NORMAL, TRAP -> true;
			default -> false;
		};
	}

	/**
	 * Updates the secrets of the room.
	 *
	 * @param s The secret line.
	 * @param x The x of the room.
	 * @param y The y of the room.
	 */
	public void updateSecrets(String s, int x, int y) {
		if (!this.canHaveSecrets()) {
			return;
		}

		final int collected, max;
		if (s == null) {
			collected = Math.max(0, this.collectedSecrets);
			max = Math.max(0, this.maxSecrets);
		} else {
			final String[] split = s.split("/");
			collected = Integer.parseInt(split[0]);
			max = Integer.parseInt(split[1]);
		}
		int newMax = Math.max(this.maxSecrets, max);

		if (this.maxSecrets != newMax || this.collectedSecrets != collected) {
			this.updateSecrets(x, y, collected, newMax);
		}

		this.maxSecrets = max;
		this.collectedSecrets = collected;
	}

	/**
	 * Sends a message to the other clients including information about secrets in the current room.
	 */
	private void updateSecrets(int x, int y, int collected, int newMax) {
		this.instance.send(new DungeonUpdateRoomSecrets(x, y, collected, newMax));
	}

	/**
	 * Finds the minimum coordinates in worlds space.
	 */
	private void findMin() {
		for (DungeonPosition dungeonPosition : this.position) {
			if (this.x > DungeonPosition.target(dungeonPosition::getWorldX)) {
				this.x = DungeonPosition.target(dungeonPosition::getWorldX);
			}
			if (this.y > DungeonPosition.target(dungeonPosition::getWorldY)) {
				this.y = DungeonPosition.target(dungeonPosition::getWorldY);
			}
		}
	}

	/**
	 * Gets the max value for the given field.
	 *
	 * @param mapper A mapper.
	 * @return The max value
	 */
	private int getMax(BiFunction<DungeonPosition, Function<InterpolatedInteger, Integer>, Integer> mapper) {
		return this.position.stream()
				.max(Comparator.comparingInt(DungeonPosition.toInt(DungeonPosition.target(mapper))))
				.map(DungeonPosition.target(mapper))
				.orElse(0);
	}

	/**
	 * Gets the min value for the given field.
	 *
	 * @param mapper A mapper.
	 * @return The min value
	 */
	private int getMin(BiFunction<DungeonPosition, Function<InterpolatedInteger, Integer>, Integer> mapper) {
		return this.position.stream()
				.min(Comparator.comparingInt(DungeonPosition.toInt(DungeonPosition.target(mapper))))
				.map(DungeonPosition.target(mapper))
				.orElse(0);
	}

	/**
	 * Find the size of the current room.
	 */
	public void findSize() {
		int maxX = this.getMax(DungeonPosition::getWorldX);
		int minX = this.getMin(DungeonPosition::getWorldX);
		int maxY = this.getMax(DungeonPosition::getWorldY);
		int minY = this.getMin(DungeonPosition::getWorldY);

		this.width = 30 + 32 * (maxX - minX);
		this.height = 30 + 32 * (maxY - minY);
	}

	/**
	 * Add the position to the rooms positions, if not already in there.
	 *
	 * @param dungeonPosition The dungeon position to add.
	 */
	public void addPosition(DungeonPosition dungeonPosition) {
		if (this.position.contains(dungeonPosition)) {
			return;
		}
		this.position.add(dungeonPosition);
		this.shape = this.findShape();
		this.findRotation();
		this.findMin();
		this.findSize();
	}

	/**
	 * Set the room type.
	 *
	 * @param roomType The room type.
	 */
	public void setRoomType(RoomType roomType) {
		if ((this.roomType == null || this.roomType == RoomType.UNKNOWN) && roomType != RoomType.UNKNOWN) {
			this.setCheckmark(Checkmark.OPENED);
		}
		this.roomType = roomType;
		if (roomType == RoomType.UNKNOWN) {
			this.setCheckmark(Checkmark.UNKNOWN);
		}
	}

	/**
	 * Set the checkmark of the room.
	 *
	 * @param checkmark The checkmark.
	 */
	void setCheckmark(Checkmark checkmark) {
		if (checkmark == Checkmark.UNKNOWN && this.roomType != RoomType.UNKNOWN) {
			return;
		}
		if (checkmark == Checkmark.FAILED && this.roomType != RoomType.PUZZLE) {
			return;
		}
		this.checkmark = checkmark;
	}


	/**
	 * Set the room data.
	 *
	 * @param data The data.
	 */
	public void setData(DungeonRoomData data) {
		this.data = data;
		this.maxSecrets = data.secrets();
	}

	/**
	 * @return The text color of the room, this may be different based on room type.
	 */
	public int getRoomTextColor() {
		if (!DungeonConfig.getInstance().showRoomStatusAsTextColor.getValue()) {
			return -1;
		}

		if (this.roomType == RoomType.PUZZLE) {
			return switch (this.checkmark) {
				case DONE -> Constants.SUCCESS_COLOR;
				case FAILED -> Constants.FAIL_COLOR;
				default -> {
					if (this.data == null) {
						final int rgb = 0xAAAAAA;
						yield (0xFF000000 | rgb);
					}
					yield -1;
				}
			};
		}
		return switch (this.checkmark) {
			case OPENED -> this.roomType == RoomType.TRAP ? -1 : Constants.FAIL_COLOR;
			case DONE -> Constants.SUCCESS_COLOR;
			case CLEARED -> {
				if (this.canHaveSecrets() && this.maxSecrets != -1 && this.collectedSecrets >= this.maxSecrets) {
					yield Constants.SUCCESS_COLOR;
				}
				yield -1;
			}
			default -> -1;
		};
	}
}
