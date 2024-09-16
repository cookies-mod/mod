package dev.morazzer.cookies.mod.commands.dev;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;

import dev.morazzer.cookies.mod.features.dungeons.DungeonFeatures;
import dev.morazzer.cookies.mod.features.dungeons.DungeonInstance;
import dev.morazzer.cookies.mod.features.dungeons.DungeonRoomData;

import dev.morazzer.cookies.mod.features.dungeons.map.DungeonRoom;

import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;

import java.io.IOException;
import java.util.Optional;

import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class ModifyDungeonRoom extends ClientCommand {

	private DungeonInstance getCurrentRoom() {
		return Optional.ofNullable(DungeonFeatures.getInstance()).map(DungeonFeatures::getCurrentInstance).orElse(null);
	}

	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return super.literal("dungeonroom")
				.then(super.literal("save").executes(super.run(this::save)))
				.then(super.literal("name")
						.then(super.argument("name", StringArgumentType.word())
								.executes(this.runRoomAware(this::setName))));
	}

	private void setName(CommandContext<FabricClientCommandSource> context, DungeonRoom currentRoom) {
		if (currentRoom.getData() == null) {
			super.sendFailedMessage("Current room data is null?? :c");
			return;
		}
		currentRoom.getData().setName(StringArgumentType.getString(context, "name"));
		currentRoom.getData().wasUpdated = true;
		super.sendSuccessMessage("Updated name");
	}

	private void save() {
		try {
			DungeonRoomData.save();
			super.sendSuccessMessage("Successfully saved data!");
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
			super.sendFailedMessage("Failed to save data :c");
		}
	}

	public Command<FabricClientCommandSource> runRoomAware(
			BiConsumer<CommandContext<FabricClientCommandSource>, DungeonRoom> consumer) {
		return super.run(context -> {
			final DungeonInstance instance = this.getCurrentRoom();
			if (instance == null) {
				super.sendFailedMessage("Not currently in a dungeon!");
				return;
			}
			final Vector2i playerMapPosition = instance.getDungeonMap().getPlayerRoomMapPosition();
			final DungeonRoom roomAt = instance.getDungeonMap().getRoomAt(playerMapPosition.x, playerMapPosition.y);
			if (roomAt == null) {
				super.sendFailedMessage("Can't find current room :c");
				return;
			}
			consumer.accept(context, roomAt);
		});
	}

}
