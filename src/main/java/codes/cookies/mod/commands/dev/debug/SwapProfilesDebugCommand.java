package codes.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import java.util.UUID;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Debug command to swap profiles without having to swap them.
 * <br>
 * usage: /dev debug swap {@literal <uuid>}
 */
public class SwapProfilesDebugCommand extends ClientCommand {
    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return super.literal("swap").then(
				super.argument("uuid", UuidArgumentType.uuid()).executes(super.run(this::swapProfile))
        );
    }

    private void swapProfile(CommandContext<FabricClientCommandSource> context) {
        final UUID uuid = context.getArgument("uuid", UUID.class);
        CookiesUtils.sendMessage(Text.literal("Profile ID: " + uuid.toString()), false);
		super.sendSuccessMessage("Switched to profile " + uuid);
    }
}
