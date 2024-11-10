package codes.cookies.mod.features.misc.timer;

import java.util.Collections;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.sub.EquipmentData;
import codes.cookies.mod.data.profile.sub.PlotData;
import codes.cookies.mod.features.farming.garden.Plot;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import codes.cookies.mod.utils.skyblock.MayorUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class PestTimer extends Timer {
	public static final SbEntityToast.ImageData DATA = new SbEntityToast.ImageData(
			20,
			20,
			5,
			7,
			Identifier.of("cookies-mod", "textures/mobs/beetle.png"));
	long lastPestSpawnedTime = -1;

	public PestTimer() {
		super(ConfigManager.getConfig().farmingConfig.pestFoldable, "pest");
	}

	@Override
	boolean showNotification() {
		return LocationUtils.Island.GARDEN.isActive();
	}

	@Override
	void onChatMessage(String message) {
		if (!LocationUtils.Island.GARDEN.isActive()) {
			return;
		}
		if (message.startsWith("GROSS! A Pest has appeared in ")
				|| message.startsWith("EWW! 2 Pests have spawned in")
				|| (message.startsWith("YUCK! ") && message.contains("Pests have spawned in"))) {
			lastPestSpawnedTime = System.currentTimeMillis();
			hasBeenAlerted = false;
		}
	}

	int getBaseTime() {
		Text footer = MinecraftClient.getInstance().inGameHud.getPlayerListHud().footer;
		if (footer == null) {
			return 300; // no pest repellent active
		}

		final String literalFooter = footer.getString();
		if (literalFooter.contains("Pest Repellent II")) {
			return 1200; // max pest repellent active
		} else if (literalFooter.contains("Pest Repellent I")) {
			return 600; // normal pest repellent active
		}

		return 300; // Again no pest repellent active
	}

	int getTimeBetweenPests() {
		double baseTime = getBaseTime();
		if (ProfileStorage.getCurrentProfile()
				.map(ProfileData::getEquipmentData)
				.map(EquipmentData::getValue)
				.orElseGet(Collections::emptyList)
				.stream()
				.anyMatch(stack -> "PEST_VEST".equalsIgnoreCase(stack.get(CookiesDataComponentTypes.SKYBLOCK_ID)))) {
			baseTime *= 0.8;
		}
		if (MayorUtils.isPerkActive("pest_eradicator")) {
			baseTime *= 0.5;
		}
		final var value = ConfigManager.getConfig().farmingConfig.pestFoldable.timerType.getValue();
		switch (value) {
			case FIRST -> {
				final boolean apply = ProfileStorage.getCurrentProfile()
						.map(ProfileData::getPlotData)
						.map(PlotData::isAnySprayed)
						.orElse(false);
				if (apply) {
					baseTime *= 0.5;
				}
			}
			case CURRENT -> {
				int currentPlot = CookiesUtils.getPlayer().map(PlayerEntity::getPos)
						.map(Plot.getCurrentPlot()::toPlotId)
						.orElse(-1);
				if (currentPlot != -1) {
					boolean applyCooldown = ProfileStorage.getCurrentProfile()
							.map(ProfileData::getPlotData)
							.map(plotData -> plotData.isPlotSprayed(currentPlot))
							.orElse(false);
					if (applyCooldown) {
						baseTime *= 0.5;
					}
				}
			}
		}
		return (int) baseTime;
	}

	@Override
	Text getNotificationMessage() {
		if (getTime() < 0) {
			return Text.literal("You can spawn pests!");
		}
		return Text.literal("Pests can spawn in " + getTime() + "s!");
	}

	@Override
	Text getChatMessage() {
		if (getTime() < 0) {
			CookiesUtils.createPrefix(43520)
					.append("Pests can spawn!");
		}
		return CookiesUtils.createPrefix(43520)
				.append("Pests can spawn soon! ")
				.append(Text.literal("(10s)").formatted(Formatting.DARK_GRAY));
	}

	@Override
	int getTime() {
		int timeDelta = (int) ((System.currentTimeMillis() - lastPestSpawnedTime) / 1000);
		return getTimeBetweenPests() - timeDelta;
	}

	@Override
	SbEntityToast.ImageData getData() {
		return DATA;
	}
}
