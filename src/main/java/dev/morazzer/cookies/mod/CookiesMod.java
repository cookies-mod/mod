package dev.morazzer.cookies.mod;

import dev.morazzer.cookies.mod.commands.CookieCommand;
import dev.morazzer.cookies.mod.commands.OpenConfigCommand;
import dev.morazzer.cookies.mod.commands.ViewForgeRecipeCommand;
import dev.morazzer.cookies.mod.commands.dev.DevCommand;
import dev.morazzer.cookies.mod.commands.system.CommandManager;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.config.screen.ConfigScreen;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.events.EventLoader;
import dev.morazzer.cookies.mod.features.Features;
import dev.morazzer.cookies.mod.repository.Repository;
import dev.morazzer.cookies.mod.screen.ItemSearchScreen;
import dev.morazzer.cookies.mod.utils.UpdateChecker;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Main class of the mod, mainly initialization and loading of further components/features.
 */
public class CookiesMod implements ClientModInitializer {

    @Getter
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Opens the config screen.
     */
    public static void openConfig() {
        openScreen(new ConfigScreen(ConfigManager.getConfigReader()));
    }

    /**
     * Opens the provided screen.
     *
     * @param screen The screen to open.
     */
    public static void openScreen(Screen screen) {
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(screen));
    }

    @Override
    public void onInitializeClient() {
        CommandManager.initialize();
        ProfileStorage.register();
        Repository.loadRepository();
        EventLoader.load();
        Features.load();
        CommandManager.addCommands(new OpenConfigCommand(), new DevCommand(), new CookieCommand(), new ViewForgeRecipeCommand());
        UpdateChecker.init();
        this.registerKeyBindings();
    }

    private void registerKeyBindings() {
        final KeyBinding chestSearch = KeyBindingHelper.registerKeyBinding(new KeyBinding("cookies.mod.search",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "cookies.mod.keybinds"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (chestSearch.isPressed()) {
                openScreen(new ItemSearchScreen());
            }
        });
    }
}
