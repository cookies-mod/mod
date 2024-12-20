package codes.cookies.mod.features.farming;

import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.features.farming.garden.Plot;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

	static ActionResult useItem(PlayerEntity playerEntity, World world, Hand hand) {
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
		return ActionResult.PASS;
	}

}
