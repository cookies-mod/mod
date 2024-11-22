package codes.cookies.mod.utils.accessors;

import codes.cookies.mod.data.farming.GardenKeybindsData;

import net.minecraft.client.option.KeyBinding;

public interface KeyBindingAccessor {
	GardenKeybindsData.GardenKeyBindOverride cookies$getGardenKey();
	void cookies$setGardenKey(GardenKeybindsData.GardenKeyBindOverride key);

	static KeyBindingAccessor toAccessor(KeyBinding keyBinding) {
		return (KeyBindingAccessor) keyBinding;
	}
}
