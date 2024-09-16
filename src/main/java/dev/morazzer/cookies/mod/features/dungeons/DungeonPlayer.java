package dev.morazzer.cookies.mod.features.dungeons;

import dev.morazzer.cookies.mod.utils.cookies.CookiesBackendUtils;
import dev.morazzer.cookies.mod.utils.maths.InterpolatedInteger;
import dev.morazzer.cookies.mod.utils.maths.LinearInterpolatedInteger;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;

/**
 * A player that is partaking in a dungeon session. To check whether it is the client use {@link #isSelf()}, to see
 * if the user has the mod use {@link #isUsingMod()}.
 */
@Getter
@Setter
public class DungeonPlayer {

	private UUID uuid;
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
	 * @param dungeonInstance The instance.
	 * @param name The name of the player.
	 * @param clazz The selected dungeon class.
	 * @param clazzLevel The class level.
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

	/**
	 * @return Whether the user has been updated locally in the past 500ms,
	 */
	public boolean wasRecentlyUpdateLocal() {
		return System.currentTimeMillis() - this.lastLocalUpdate < 500;
	}

	/**
	 * @return Whether the user has been updated through the socket in the past 500ms,
	 */
	public boolean wasRecentlyUpdatedSocket() {
		return System.currentTimeMillis() - this.lastSocketUpdate < 500;
	}

	/**
	 * @return Whether the user has been updated by either the client or the socket in the past 500ms.
	 */
	public boolean wasRecentlyUpdated() {
		return this.wasRecentlyUpdatedSocket() || this.wasRecentlyUpdateLocal();
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

	/**
	 * @return Whether the player is the user or not.
	 */
	public boolean isSelf() {
		return this.player instanceof ClientPlayerEntity;
	}
}
