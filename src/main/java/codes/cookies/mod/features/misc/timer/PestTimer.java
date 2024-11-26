package codes.cookies.mod.features.misc.timer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.sub.EquipmentData;
import codes.cookies.mod.data.profile.sub.PlotData;
import codes.cookies.mod.features.farming.garden.PestTimerHud;
import codes.cookies.mod.features.farming.garden.Plot;
import codes.cookies.mod.render.hud.HudManager;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import codes.cookies.mod.utils.skyblock.MayorUtils;
import lombok.Getter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Getter
public class PestTimer extends Timer {
	public static final SbEntityToast.ImageData DATA = new SbEntityToast.ImageData(
			20,
			20,
			5,
			7,
			Identifier.of("cookies-mod", "textures/mobs/beetle.png"));
	long lastPestSpawnedTime = -1;
	private String debug;

	public PestTimer() {
		super(ConfigManager.getConfig().farmingConfig.pestFoldable, "pest");
		HudManager.register(new PestTimerHud(this));
	}

	@Override
	boolean showNotification() {
		return LocationUtils.Island.GARDEN.isActive();
	}

	public Optional<String> getDebug() {
		if (!isDebug()) {
			return Optional.empty();
		}
		return Optional.ofNullable(debug);
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
		StringBuilder stringBuilder;
		if (isDebug()) {
			stringBuilder = new StringBuilder();
		} else {
			stringBuilder = null;
		}
		double baseTime = getBaseTime();
		final List<ItemStack> itemStacks = ProfileStorage.getCurrentProfile()
				.map(ProfileData::getEquipmentData)
				.map(EquipmentData::getValue)
				.orElseGet(Collections::emptyList);
		if (stringBuilder != null) {
			stringBuilder.append("§m          §r\n");
		}
		for (ItemStack itemStack : itemStacks) {
			boolean foundAny = false;
			if ("PEST_VEST".equalsIgnoreCase(itemStack.get(CookiesDataComponentTypes.SKYBLOCK_ID))) {
				baseTime *= 0.8;
				if (stringBuilder != null) {
					stringBuilder.append("Found pest vest! (20%)\n");
				}
				foundAny = true;
			} else if ("PESTHUNTERS_GLOVES".equalsIgnoreCase(itemStack.get(CookiesDataComponentTypes.SKYBLOCK_ID))) {
				baseTime *= 0.99;
				if (stringBuilder != null) {
					stringBuilder.append("Found pest hunter gloves! (1%)\n");
				}
				foundAny = true;
			}
			if ("squeaky".equalsIgnoreCase(itemStack.get(CookiesDataComponentTypes.MODIFIER))) {
				baseTime *= 0.99;
				if (stringBuilder != null) {
					stringBuilder.append("Found squeaky modifier! (1%)\n");
				}
				foundAny = true;
			}

			if (foundAny) {
				if (stringBuilder != null) {
					stringBuilder.append("§m          §r\n");
				}
			}
		}

		final double sprayedPlotReduction;
		if (MayorUtils.isPerkActive("pest_eradicator")) {
			if (stringBuilder != null) {
				stringBuilder.append("Finnegan perk (50%)");
			}
			sprayedPlotReduction = 0.25;
		} else {
			sprayedPlotReduction = 0.5;
		}
		final var value = ConfigManager.getConfig().farmingConfig.pestFoldable.timerType.getValue();
		switch (value) {
			case FIRST -> {
				final boolean apply = ProfileStorage.getCurrentProfile()
						.map(ProfileData::getPlotData)
						.map(PlotData::isAnySprayed)
						.orElse(false);
				if (apply) {
					if (stringBuilder != null) {
						stringBuilder.append("One plot sprayed (50%)\n");
					}
					baseTime *= sprayedPlotReduction;
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
						if (stringBuilder != null) {
							stringBuilder.append("Current plot sprayed (50%)\n");
						}
						baseTime *= sprayedPlotReduction;
					}
				}
			}
		}
		if (stringBuilder != null) {
			stringBuilder.append("§m          §r\n");
			this.debug = stringBuilder.toString();
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
	public int getTime() {
		int timeDelta = (int) ((System.currentTimeMillis() - lastPestSpawnedTime) / 1000);
		return getTimeBetweenPests() - timeDelta;
	}

	@Override
	SbEntityToast.ImageData getData() {
		return DATA;
	}
}
