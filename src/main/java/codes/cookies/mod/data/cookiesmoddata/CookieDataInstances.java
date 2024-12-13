package codes.cookies.mod.data.cookiesmoddata;

import codes.cookies.mod.data.farming.GardenKeybindsData;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public final class CookieDataInstances {
	public static final GardenKeybindsData gardenKeybindsData = register(new GardenKeybindsData());

	/**
	 * Registers the data and in all required positions.
	 * @param data The data to register.
	 * @return The data.
	 * @param <T> The type of the data.
	 */
	private static <T extends CookiesModData> T register(T data) {
		if (data instanceof ClientInitializationCallback callback) {
			ClientLifecycleEvents.CLIENT_STARTED.register(callback::gameInitialized);
		}

		return data;
	}
}
