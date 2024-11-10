package codes.cookies.mod.features.dungeons;

import java.util.OptionalInt;
import java.util.UUID;

import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.utils.Result;
import codes.cookies.mod.utils.cookies.CookiesBackendUtils;
import codes.cookies.mod.utils.maths.InterpolatedInteger;
import codes.cookies.mod.utils.maths.LinearInterpolatedInteger;
import codes.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Items;

/**
 * A player that is partaking in a dungeon session. To check whether it is the client use {@link #isSelf()}, to see
 * if the user has the mod use {@link #isUsingMod()}.
 */
@Getter
@Setter
public class DungeonPlayer {

	private UUID uuid;
	@Nullable
	private AbstractClientPlayerEntity player;

	private String name;
	private int classLevel;
	private InterpolatedInteger rotation = new LinearInterpolatedInteger(50, 0);
	private String dungeonClass;
	private PlayerListEntry playerListEntry;
	private DungeonInstance dungeonInstance;
	private DungeonPosition position;
	private boolean skip;
	private long lastLocalUpdate;
	private long lastSocketUpdate;

	/**
	 * Creates a new dungeon player and tries to find the uuid of the player.
	 *
	 * @param dungeonInstance The instance.
	 * @param name            The name of the player.
	 * @param clazz           The selected dungeon class.
	 * @param clazzLevel      The class level.
	 */
	public DungeonPlayer(DungeonInstance dungeonInstance, String name, String clazz, String clazzLevel) {
		this.dungeonInstance = dungeonInstance;
		this.name = name;
		this.findUuid();
		this.dungeonClass = clazz;
		this.setClassLevel(clazzLevel);
		this.position = new DungeonPosition(0, 0, dungeonInstance);
	}

	/**
	 * Find the player entity and the uuid that corresponds with this player.
	 */
	private void findUuid() {
		final MinecraftClient instance = MinecraftClient.getInstance();
		if (instance == null || instance.world == null) {
			return;
		}
		final ClientWorld world = instance.world;

		for (AbstractClientPlayerEntity player : world.getPlayers()) {
			if (player.getName() != null && player.getName().getString().equals(this.name)) {
				this.uuid = player.getUuid();
				this.player = player;
				return;
			}
		}
	}

	/**
	 * Sets the class level of the current player.
	 *
	 * @param classLevel The class leve.
	 */
	private void setClassLevel(String classLevel) {
		int level;
		try {
			level = Integer.parseInt(classLevel);
		} catch (NumberFormatException e) {
			level = 0;
		}
		this.classLevel = level;
	}

	/**
	 * Gets the not interpolated world x of the player.
	 */
	public int getX() {
		return DungeonPosition.target(this.position::getWorldX);
	}

	/**
	 * Gets the not interpolated world y (in 3d space z) of the player.
	 */
	public int getY() {
		return DungeonPosition.target(this.position::getWorldY);
	}

	public Result<ItemBuilder, String> getItem() {
		return getItem(true);
	}

	public Result<ItemBuilder, String> getItem(boolean useSkullsIfPossible) {
		if (player != null && useSkullsIfPossible) {
			if (isSelf()) {
				return Result.success(new ItemBuilder(Items.PLAYER_HEAD).set(
						DataComponentTypes.PROFILE,
						new ProfileComponent(MinecraftClient.getInstance().getGameProfile())));
			}
			return Result.success(new ItemBuilder(Items.PLAYER_HEAD).set(DataComponentTypes.PROFILE,
					new ProfileComponent(player.getGameProfile())));
		}
		if (dungeonClass == null) {
			return Result.error("Neither player nor class found");
		}

		final char[] charArray = dungeonClass.toCharArray();
		if (charArray.length == 0) {
			return Result.error("Neither player nor class found");
		}

		final ItemBuilder itemBuilder = new ItemBuilder(Items.PLAYER_HEAD);

		switch (dungeonClass.toLowerCase().toCharArray()[0]) {
			case 't' -> itemBuilder.setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE1MjYzMmE5YzhmN2JkZTk0NzE5MDY0MjM0OTIwZGVkNDg2MTRlMmJkOGJjOTFhZmU3ZmZjMmNkOGE0NmYxOSJ9fX0=");
			case 'm' -> itemBuilder.setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI5YzQ5ZmZkZjRjZTRjNmY1ZjA0OWVmYzhjYTBlMDhiOWI1YmJmM2M2YTg3ODNkZmFhY2NhZDc3ZGZjOTk3YSJ9fX0=");
			case 'h' -> itemBuilder.setSkin(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgzMjM2NjM5NjA3MDM2YzFiYTM5MWMyYjQ2YTljN2IwZWZkNzYwYzhiZmEyOTk2YTYwNTU1ODJiNGRhNSJ9fX0=");
			case 'b' -> itemBuilder.setSkin(
					"ewogICJ0aW1lc3RhbXAiIDogMTY5MjI5ODIyMjY4MywKICAicHJvZmlsZUlkIiA6ICI4NzE3ZGFhNmM3OTU0NzE2YmJlYWQ0MDRkYzg0NDQzZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTa3VsbDAwMDAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTUyMjg2NzcyMTJiZTQzZWFhZDIzZDQ3ZWQ4NDNlMTVmYjFlNjgzODQ1OTRjMDliNThiMjNmODI0MjdlNTQ5YSIKICAgIH0KICB9Cn0=");
			case 'a' -> itemBuilder.setSkin(
					"ewogICJ0aW1lc3RhbXAiIDogMTY5NzYyNzM1MDg1NSwKICAicHJvZmlsZUlkIiA6ICIxMzEzZGFmMDc2OGQ0YmQ5Yjc1ODJkMGI1NWUwZGQxNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJMZW50aWNjaGllIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2U5YzU4OGE4YjYyYWZmZDc0NjQzZTBkNjY0MWJkYTNhMDc4NGEzMDRjNTc5YzA2N2ZhNDk1ZTJjNDNlYzk0NjIiCiAgICB9CiAgfQp9");
			default -> {
				return Result.error("Unknown class: " + dungeonClass);
			}
		}
		return Result.success(itemBuilder);
	}

	/**
	 * @return Whether the player is the user or not.
	 */
	public boolean isSelf() {
		return this.player instanceof ClientPlayerEntity;
	}

	public OptionalInt getColor() {
		if (dungeonClass == null || dungeonClass.isEmpty()) {
			return OptionalInt.empty();
		}
		return switch (dungeonClass.toLowerCase().toCharArray()[0]) {
			case 'h' -> OptionalInt.of(DungeonConfig.getInstance().classColorFoldable.healer.getValue().getRGB());
			case 'm' -> OptionalInt.of(DungeonConfig.getInstance().classColorFoldable.mage.getValue().getRGB());
			case 'b' -> OptionalInt.of(DungeonConfig.getInstance().classColorFoldable.bers.getValue().getRGB());
			case 'a' -> OptionalInt.of(DungeonConfig.getInstance().classColorFoldable.arch.getValue().getRGB());
			case 't' -> OptionalInt.of(DungeonConfig.getInstance().classColorFoldable.tank.getValue().getRGB());
			default -> OptionalInt.empty();
		};
	}

	/**
	 * Updates the position based on socket data.
	 *
	 * @param x The x position.
	 * @param y The y position.
	 */
	public void updatePositionSocket(int x, int y) {
		if (this.wasRecentlyUpdateLocal()) {
			this.lastSocketUpdate = System.currentTimeMillis();
			return;
		}
		this.setPosition(x, y);
		this.lastSocketUpdate = System.currentTimeMillis();
	}

	/**
	 * @return Whether the user has been updated locally in the past 500ms,
	 */
	public boolean wasRecentlyUpdateLocal() {
		return System.currentTimeMillis() - this.lastLocalUpdate < 500;
	}

	/**
	 * Updates the position.
	 *
	 * @param x The x position.
	 * @param y The y position.
	 */
	public void setPosition(int x, int y) {
		this.position.setWorldX(x);
		this.position.setWorldY(y);
	}

	/**
	 * Updates the position based on local data.
	 *
	 * @param x The x position.
	 * @param y The y position.
	 */
	public void updatePositionLocal(int x, int y) {
		this.setPosition(x, y);
		this.lastLocalUpdate = System.currentTimeMillis();
	}

	/**
	 * Updates the position based on the map decorations, this will not execute if another data source is available.
	 *
	 * @param x The x position.
	 * @param y The y position.
	 */
	public void setPosition(byte x, byte y) {
		if (this.wasRecentlyUpdated()) {
			return;
		}
		this.position.setMapX((x + 128) / 2);
		this.position.setMapY((y + 128) / 2);
	}

	/**
	 * @return Whether the user has been updated by either the client or the socket in the past 500ms.
	 */
	public boolean wasRecentlyUpdated() {
		return this.wasRecentlyUpdatedSocket() || this.wasRecentlyUpdateLocal();
	}

	/**
	 * @return Whether the user has been updated through the socket in the past 500ms,
	 */
	public boolean wasRecentlyUpdatedSocket() {
		return System.currentTimeMillis() - this.lastSocketUpdate < 500;
	}

	/**
	 * Update the rotation based on socket data.
	 *
	 * @param rotation The rotation.
	 */
	public void updateRotationSocket(int rotation) {
		if (this.wasRecentlyUpdateLocal()) {
			this.lastSocketUpdate = System.currentTimeMillis();
			return;
		}
		this.setRotationWithWrap(rotation);
		this.lastSocketUpdate = System.currentTimeMillis();
	}

	/**
	 * Set the rotation and wrap it.
	 *
	 * @param newRotation The new rotation.,
	 */
	private void setRotationWithWrap(int newRotation) {
		int rotation = newRotation - this.rotation.getValue();
		if (rotation > 180) {
			this.rotation.setValue(this.rotation.getValue() + 360);
		} else if (rotation < -180) {
			this.rotation.setValue(this.rotation.getValue() - 360);
		}
		this.rotation.setTargetValue(newRotation);
	}

	/**
	 * Update the rotation based on local data.
	 *
	 * @param rotation The rotation.
	 */
	public void updateRotationLocal(float rotation) {
		int newRotation = (int) ((rotation) % 360);
		this.setRotationWithWrap(newRotation);
		this.lastLocalUpdate = System.currentTimeMillis();
	}

	/**
	 * Sets the rotation based on map decoration, this will not be used if another data source is available.
	 *
	 * @param rotation The rotation.
	 */
	public void setRotation(byte rotation) {
		if (this.wasRecentlyUpdated()) {
			return;
		}
		final int i = Math.round((rotation * (360 / 16f)) % 360);
		this.setRotationWithWrap(i);
	}

	/**
	 * Updates the class of the current player, or mark as dead/skip if no class is found.
	 *
	 * @param name       The name of the player.
	 * @param clazz      The class of the player.
	 * @param clazzLevel The class level of the player.
	 */
	public void update(String name, String clazz, String clazzLevel) {
		this.name = name;
		this.findUuid();
		switch (clazz) {
			case "EMPTY", "DEAD" -> this.skip = true;
			default -> {
				this.skip = false;
				this.dungeonClass = clazz;
				this.setClassLevel(clazzLevel);
			}
		}
	}

	/**
	 * Ticks both the position and the rotation.
	 */
	public void tick() {
		this.position.tick();
		this.rotation.tick();
	}

	/**
	 * @return Gets the interpolated x position of the player.
	 */
	public int getInterpolatedX() {
		return DungeonPosition.interpolated(this.position::getMapX);
	}

	/**
	 * @return Gets the interpolated y position of the player.
	 */
	public int getInterpolatedY() {
		return DungeonPosition.interpolated(this.position::getMapY);
	}

	/**
	 * @return Whether the player is using the mod or not.
	 */
	public boolean isUsingMod() {
		if (this.uuid == null) {
			return false;
		}
		return CookiesBackendUtils.usesMod(this.uuid);
	}
}
