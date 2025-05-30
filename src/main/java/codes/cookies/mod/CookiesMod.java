package codes.cookies.mod;

import codes.cookies.mod.api.ApiManager;
import codes.cookies.mod.commands.CookieCommand;
import codes.cookies.mod.commands.OpenConfigCommand;
import codes.cookies.mod.commands.ViewForgeRecipeCommand;
import codes.cookies.mod.commands.WarpCommand;
import codes.cookies.mod.commands.dev.DevCommand;
import codes.cookies.mod.commands.system.CommandManager;
import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.CookiesConfig;
import codes.cookies.mod.data.cookiesmoddata.CookieDataManager;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.events.EventLoader;
import codes.cookies.mod.features.Features;
import codes.cookies.mod.features.farming.garden.keybinds.GardenKeybindPredicate;
import codes.cookies.mod.render.hud.HudEditScreen;
import codes.cookies.mod.render.hud.HudManager;
import codes.cookies.mod.repository.Repository;
import codes.cookies.mod.repository.constants.RepositoryConstants;
import codes.cookies.mod.screen.search.ItemSearchScreen;
import codes.cookies.mod.services.mining.CrystalStatusService;
import codes.cookies.mod.services.mining.powder.PowderService;
import codes.cookies.mod.utils.UpdateChecker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import codes.cookies.mod.utils.skyblock.MayorUtils;
import codes.cookies.mod.utils.skyblock.playerlist.PlayerListUtils;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import lombok.Getter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.hypixel.modapi.HypixelModAPI;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

/**
 * Main class of the mod, mainly initialization and loading of further components/features.
 */
public class CookiesMod implements ClientModInitializer {
	public static KeyBinding chestSearch;
	private static KeyBinding useGardenKeybinds;
	public static KeyBinding pasteCommandFromClipboard;
	@Getter
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    /**
     * Opens the config screen.
     */
    public static void openConfig() {
        openScreen(new ConfigScreen(MinecraftClient.getInstance().currentScreen, ConfigManager.CONFIGURATOR.getConfig(CookiesConfig.class)));
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
		PowderService.initialize();
		ConfigManager.load();
        CommandManager.initialize();
		CookieDataManager.load();
        ProfileStorage.register();
        Repository.loadRepository();
		HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
		MayorUtils.load();
        EventLoader.load();
		ApiManager.initialize();
        Features.load();
        CommandManager.addCommands(new OpenConfigCommand(), new DevCommand(), new CookieCommand(), new ViewForgeRecipeCommand());
        CommandManager.addCommands(RepositoryConstants.warps.getWarps().entrySet().stream().map(WarpCommand::new).toArray(WarpCommand[]::new));
        UpdateChecker.init();
		PlayerListUtils.init();
		HudManager.load();
		CrystalStatusService.register();
		this.registerKeyBindings();
		DevUtils.registerDebugs();
    }

	private void registerKeyBindings() {
		chestSearch = KeyBindingHelper.registerKeyBinding(new KeyBinding("cookies.mod.search",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_O,
				"cookies.mod.keybinds"));
		useGardenKeybinds = KeyBindingHelper.registerKeyBinding(new KeyBinding("cookies.mod.garden.keybind_switch",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				"cookies.mod.keybinds"));
		pasteCommandFromClipboard = KeyBindingHelper.registerKeyBinding(new KeyBinding("cookies.mod.paste_command",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"cookies.mod.keybinds"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (chestSearch.isPressed()) {
				openScreen(new ItemSearchScreen());
			}
			while (useGardenKeybinds.wasPressed() && LocationUtils.Island.GARDEN.isActive()) {
				GardenKeybindPredicate.keyBindToggle = !GardenKeybindPredicate.keyBindToggle;
				for (var keybind : KeyBinding.KEYS_BY_ID.values()) {
					keybind.setPressed(false);
					keybind.timesPressed = 0;
				}
				CookiesUtils.sendMessage(Text.translatable("cookies.mod.garden.keybinds." + (GardenKeybindPredicate.keyBindToggle ? "enabled" : "disabled")), false);
			}
			while (pasteCommandFromClipboard.wasPressed()) {
				var message = client.keyboard.getClipboard();

				if (message != null && message.startsWith("/")) {
					CookiesUtils.getPlayer().map(player -> player.networkHandler.sendCommand(message.substring(1)));
				}
			}
		});
	}

	public static void openHudScreen() {
		openScreen(new HudEditScreen());
	}
}
