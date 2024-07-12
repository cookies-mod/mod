package dev.morazzer.cookies.mod;

import dev.morazzer.cookies.mod.commands.CookieCommand;
import dev.morazzer.cookies.mod.commands.OpenConfigCommand;
import dev.morazzer.cookies.mod.commands.dev.DevCommand;
import dev.morazzer.cookies.mod.commands.system.CommandManager;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.config.screen.ConfigScreen;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.events.EventLoader;
import dev.morazzer.cookies.mod.features.Features;
import dev.morazzer.cookies.mod.repository.Repository;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

/**
 * Main class of the mod, mainly initialization and loading of further components/features.
 */
public class CookiesMod implements ClientModInitializer {

    @Getter
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onInitializeClient() {
        CommandManager.initialize();
        ProfileStorage.register();
        Repository.loadRepository();
        EventLoader.load();
        Features.load();
        CommandManager.addCommands(new OpenConfigCommand(), new DevCommand(), new CookieCommand());
    }

    public static void openConfig() {
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new ConfigScreen(ConfigManager.getConfigReader())));
    }
}
