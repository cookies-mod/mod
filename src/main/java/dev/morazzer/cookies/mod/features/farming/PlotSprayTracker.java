package dev.morazzer.cookies.mod.features.farming;

import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.features.farming.garden.Plot;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.skyblock.LocationUtils;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.player.UseItemCallback;

public class PlotSprayTracker {
	private static final String DEBUG = "SprayTracker";
	private static int lastPlotId;

	static void load() {
		UseItemCallback.EVENT.register(PlotSprayTracker::useItem);
		ClientReceiveMessageEvents.ALLOW_GAME.register(PlotSprayTracker::onChatMessage);
	}

	private static boolean onChatMessage(Text text, boolean overlay) {
		if (LocationUtils.Island.GARDEN.isActive()) {
			String literalText = CookiesUtils.stripColor(text.getString());
			if ("SPRAYONATOR! This will expire in 30m!".equals(literalText)) {
				ProfileStorage.getCurrentProfile().ifPresent(profile -> {
					DevUtils.log(DEBUG, "Setting " + lastPlotId + " to sprayed :3");
					profile.getPlotData().setSprayed(lastPlotId);
				});
			}
		}
		return true;
	}

	static TypedActionResult<ItemStack> useItem(PlayerEntity playerEntity, World world, Hand hand) {
		if (LocationUtils.Island.GARDEN.isActive()) {
			final ItemStack stackInHand = playerEntity.getStackInHand(hand);
			if ("SPRAYONATOR".equals(stackInHand.get(CookiesDataComponentTypes.SKYBLOCK_ID))) {
				lastPlotId = Plot.getCurrentPlot().toPlotId(playerEntity.getPos());
			} else {
				lastPlotId = -1;
			}
		} else {
			lastPlotId = -1;
		}
		return TypedActionResult.pass(ItemStack.EMPTY);
	}

}
