package dev.morazzer.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.morazzer.cookies.entities.websocket.packets.DungeonUpdateRoomIdPacket;
import dev.morazzer.cookies.entities.websocket.packets.TestServerPacket;
import dev.morazzer.cookies.mod.api.ws.WebsocketConnection;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import org.jetbrains.annotations.NotNull;

/**
 * Command used to debug the backend api, this will only work in a dev environment since it may cause weird behaviour in production.
 */
public class ApiDebug extends ClientCommand {
	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return super.literal("api")
				.requires(super::ensureDevEnvironment)
				.then(
						super.literal("sendDebug").executes(super.run(this::sendApiDebug))
				).then(
						super.literal("setRoomStuff").executes(super.run(this::setRoomStuff))
				);
	}

	private void setRoomStuff() {
		WebsocketConnection.sendMessage(new DungeonUpdateRoomIdPacket(2, 3, "asdf"));
		super.sendSuccessMessage("Sent websocket message!");
	}

	private void sendApiDebug() {
		WebsocketConnection.sendMessage(new TestServerPacket("test abc"));
	}
}
