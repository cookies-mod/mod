package codes.cookies.mod.commands;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.screen.search.ItemSearchScreen;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

/**
 * Various commands intended to be used by the player
 */
public class CookieCommand extends ClientCommand {
	private static final String COORDINATE_FORMAT = "x: %s, y: %s, z: %s";


	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return literal("cookie").executes(run(CookiesMod::openConfig))
				.then(literal("config").executes(run(CookiesMod::openConfig)))
				.then(create(literal("sendCoords"), this::sendCoords))
				.then(create(literal("search"), this::searchScreen))
				.then(create(literal("hud"), this::editHud));
	}

	private LiteralArgumentBuilder<FabricClientCommandSource> editHud(LiteralArgumentBuilder<FabricClientCommandSource> command) {
		return command.executes(run(CookiesMod::openHudScreen));
	}

	private LiteralArgumentBuilder<FabricClientCommandSource> sendCoords(LiteralArgumentBuilder<FabricClientCommandSource> command) {
		return command.executes(run(context -> {
			final ClientPlayerEntity player = MinecraftClient.getInstance().player;

			String message = COORDINATE_FORMAT.formatted((int) player.getX(), (int) player.getY(), (int) player.getZ());
			MinecraftClient.getInstance().player.networkHandler.sendChatMessage(message);
		}));
	}

	private LiteralArgumentBuilder<FabricClientCommandSource> searchScreen(LiteralArgumentBuilder<FabricClientCommandSource> command) {
		return command.executes(run(() -> CookiesMod.openScreen(new ItemSearchScreen())));
	}
}
