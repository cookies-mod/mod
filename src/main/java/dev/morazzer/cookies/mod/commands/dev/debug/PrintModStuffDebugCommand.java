package dev.morazzer.cookies.mod.commands.dev.debug;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.morazzer.cookies.mod.commands.system.ClientCommand;
import dev.morazzer.cookies.mod.features.misc.timer.SbEntityToast;
import dev.morazzer.cookies.mod.repository.constants.RepositoryConstants;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;
import dev.morazzer.cookies.mod.utils.minecraft.SoundUtils;

import net.minecraft.sound.SoundEvents;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PrintModStuffDebugCommand extends ClientCommand {

	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return literal("print_mod_locations").executes(run(this::printModLocations));
	}

	private void printModLocations() {
		SoundUtils.playSound(SoundEvents.BLOCK_BELL_USE);
		MinecraftClient.getInstance()
				.getToastManager()
				.add(new SbEntityToast(Identifier.of("cookies-mod", "textures/mobs/primal_fear.png"),
						() -> Text.literal("Test"),
						10000));

		final DataResult<JsonElement> elements =
				BlockPos.CODEC.listOf().encodeStart(JsonOps.INSTANCE, RepositoryConstants.modLocations);
		if (elements.isError()) {
			sendFailedMessage("Failed to serialize data: " +
					elements.error().map(DataResult.Error::message).orElse("<no message>"));
			return;
		}
		CookiesUtils.sendMessage(CookiesUtils.createPrefix(Constants.SUCCESS_COLOR)
				.append("Successfully serialized data :D")
				.styled(style -> style.withClickEvent(new ClickEvent(
						ClickEvent.Action.COPY_TO_CLIPBOARD,
						JsonUtils.GSON.toJson(elements.getOrThrow())))));
	}
}
