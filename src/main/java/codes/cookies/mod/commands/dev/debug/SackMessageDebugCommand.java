package codes.cookies.mod.commands.dev.debug;

import codes.cookies.mod.features.misc.items.SackTrackerListener;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.cookies.CookiesUtils;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.jetbrains.annotations.NotNull;

/**
 * Command to debug the {@link SackTrackerListener} feature.
 * usage: /dev debug sackMessage (add|remove|both)
 */
public class SackMessageDebugCommand extends ClientCommand {
	@Override
	public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
		return super.literal("sackMessage")
				.then(super.literal("add").executes(super.run(this::add)))
				.then(super.literal("remove").executes(super.run(this::remove)))
				.then(super.literal("both").executes(super.run(this::both)));
	}

	private void add(CommandContext<FabricClientCommandSource> context) {
		final MutableText formatted = Text.literal("[Sacks] ").formatted(Formatting.GOLD);
		formatted.append(this.getAdd()).append(".");
		CookiesUtils.sendMessage(formatted);
	}

	private void remove(CommandContext<FabricClientCommandSource> context) {
		final MutableText formatted = Text.literal("[Sacks] ").formatted(Formatting.GOLD);
		formatted.append(this.getRemove()).append(".");
		CookiesUtils.sendMessage(formatted);
	}

	private void both(CommandContext<FabricClientCommandSource> context) {
		final MutableText formatted = Text.literal("[Sacks] ").formatted(Formatting.GOLD);
		formatted.append(this.getAdd())
				.append(Text.literal(", ").formatted(Formatting.GRAY))
				.append(this.getRemove())
				.append(".");
		CookiesUtils.sendMessage(formatted);
	}

	private Text getRemove() {
		MutableText remove = Text.literal("remove").formatted(Formatting.RED);


		remove.styled(style -> style.withHoverEvent(new HoverEvent.ShowText(
				Text.empty()
						.append("Removed items:\n")
						.append(this.getText("diamond", -2))
						.append(this.getText("emerald", -20)))));
		return remove;
	}

	private Text getAdd() {
		MutableText add = Text.literal("add").formatted(Formatting.GREEN);

		add.styled(style -> style.withHoverEvent(new HoverEvent.ShowText(
				Text.empty()
						.append("Added items:\n")
						.append(this.getText("titanium_ore", 2))
						.append(this.getText("enchanted_titanium", 20)))));
		return add;
	}

	private Text getText(String id, int count) {
		StringBuilder stringBuilder = new StringBuilder();
		final RepositoryItem repositoryItem = RepositoryItem.of(id);
		stringBuilder.append(count > 0 ? "+" : "").append(count);
		stringBuilder.append(" ").append(repositoryItem.getName());
		stringBuilder.append(" (Debug Sack)\n");

		return Text.literal(stringBuilder.toString());
	}
}
