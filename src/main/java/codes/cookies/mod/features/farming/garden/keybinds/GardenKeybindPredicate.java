package codes.cookies.mod.features.farming.garden.keybinds;

import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import lombok.Getter;

import net.minecraft.client.MinecraftClient;

import java.util.function.Supplier;

@Getter
public enum GardenKeybindPredicate {
	OnGarden("On Garden", LocationUtils.Island.GARDEN::isActive),
	IfKeybindEnabled("If Keybind Pressed", () -> false),
	HoldingFarmingTool("Holding Farming Tool", () -> {
		if(!OnGarden.shouldBeEnabled.get()) {
			return false;
		}
		var player = MinecraftClient.getInstance().player;
		if(player == null) {
			return false;
		}

		var mainHandStack = player.getMainHandStack();
		if(mainHandStack == null || mainHandStack.isEmpty()) {
			return false;
		}

		var skyblockId = mainHandStack.get(CookiesDataComponentTypes.SKYBLOCK_ID);

		if(skyblockId == null) {
			return false;
		}
		var skyBlockIdLower = skyblockId.toLowerCase();

		if(skyBlockIdLower.contains("hoe") || skyBlockIdLower.contains("chopper") || skyBlockIdLower.contains("dicer")) {
			return true;
		}
		if(skyBlockIdLower.contains("daedalus")) {
			return true;
		}

		var enchantments = mainHandStack.get(CookiesDataComponentTypes.ENCHANTMENTS);
		if(enchantments != null && enchantments.containsKey("cultivating")) {
			return true;
		}

		return false;
	}),
	;

	public static boolean keyBindToggle = false;

	static {
		IfKeybindEnabled.shouldBeEnabled = () -> keyBindToggle && OnGarden.shouldBeEnabled.get();
	}


	private final String name;
	private Supplier<Boolean> shouldBeEnabled;

	GardenKeybindPredicate(String name, Supplier<Boolean> shouldBeEnabled) {
		this.name = name;
		this.shouldBeEnabled = shouldBeEnabled;
	}

}
