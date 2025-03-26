package codes.cookies.mod.commands.dev.debug;

import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.repository.constants.mining.ShaftCorpseLocations;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.json.JsonUtils;
import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.NotNull;

import net.minecraft.text.ClickEvent;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PrintCorpseLocationsDebugCommand extends ClientCommand {
	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return literal("print_corpse_locations").executes(run(this::printCorpseLocations));
	}

	private void printCorpseLocations() {
		final DataResult<JsonElement> elements = ShaftCorpseLocations.CachedShaftLocations.LIST_CODEC.encodeStart(
				JsonOps.INSTANCE,
				ShaftCorpseLocations.getCached());
		if (elements.isError()) {
			sendFailedMessage("Failed to serialize data: " +
					elements.error().map(DataResult.Error::message).orElse("<no message>"));
			return;
		}
		CookiesUtils.sendMessage(CookiesUtils.createPrefix(Constants.SUCCESS_COLOR)
				.append("Successfully serialized data :D")
				.styled(style -> style.withClickEvent(new ClickEvent.CopyToClipboard(
						JsonUtils.GSON.toJson(elements.getOrThrow())))));
	}
}
