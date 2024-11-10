package codes.cookies.mod.commands.dev.debug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.commands.system.ClientCommand;
import codes.cookies.mod.features.misc.utils.crafthelper.CraftHelperPlacement;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

public class OpenCraftHelperPlacementScreen extends ClientCommand {

    @Override
    public @NotNull LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("openCraftHelperPlacementScreen").executes(run(this::openScreen));
    }

    private void openScreen() {
        CookiesMod.openScreen(new CraftHelperPlacement());
    }
}
