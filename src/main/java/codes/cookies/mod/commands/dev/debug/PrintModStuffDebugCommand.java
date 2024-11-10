package codes.cookies.mod.commands.dev.debug;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.repository.constants.RepositoryConstants;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.json.JsonUtils;
import org.jetbrains.annotations.NotNull;

import net.minecraft.text.ClickEvent;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PrintModStuffDebugCommand extends ClientCommand {

	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return literal("print_mod_locations").executes(run(this::printModLocations));
	}

	private void printModLocations() {
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
