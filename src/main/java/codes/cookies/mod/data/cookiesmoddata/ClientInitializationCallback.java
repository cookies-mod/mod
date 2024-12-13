package codes.cookies.mod.data.cookiesmoddata;

import net.minecraft.client.MinecraftClient;

/**
 * Can be used to add special methods that will be called upon client initialization to an instance of {@link CookiesModData}
 */
public interface ClientInitializationCallback {
	default void gameInitialized() {}

	default void gameInitialized(MinecraftClient minecraftClient) {
		gameInitialized();
	}
}
