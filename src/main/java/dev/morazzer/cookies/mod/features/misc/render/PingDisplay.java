package dev.morazzer.cookies.mod.features.misc.render;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.utils.ColorUtils;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.TextUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * Displays the ping in the action bar.
 */
public interface PingDisplay {

    static void load() {
        ClientReceiveMessageEvents.MODIFY_GAME.register(PingDisplay::modifyGame);
    }

    private static Text modifyGame(Text text, boolean overlay) {
        if (!overlay) {
            return text;
        }

        if (!ConfigManager.getConfig().miscConfig.showPing.getValue()) {
            return text;
        }
        final long lastPing = SkyblockUtils.getLastPing();
        final int color = ColorUtils.calculateBetween(Constants.SUCCESS_COLOR,
            Constants.FAIL_COLOR,
            MathHelper.clamp(lastPing / 500F, 0, 1));
        return text.copy().append(TextUtils.literal("     %sms".formatted(lastPing), color));
    }

}
