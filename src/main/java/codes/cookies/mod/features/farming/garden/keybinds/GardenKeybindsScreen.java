package codes.cookies.mod.features.farming.garden.keybinds;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

import codes.cookies.mod.translations.TranslationKeys;


public class GardenKeybindsScreen extends KeybindsScreen implements TranslationKeys {
	public GardenKeybindsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions);
		this.title = Text.translatable(CONFIG_FARMING_KEYBIND_MENU_TITLE);
		gameOptions.write();
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
		super.close();
	}
}
