package dev.morazzer.cookies.mod.features.cleanup.dungeon;

import dev.morazzer.cookies.mod.config.ConfigKeys;
import dev.morazzer.cookies.mod.utils.minecraft.LocationUtils;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

/**
 * Hides various different messages while inside dungeons.
 */
public class DungeonMessagesCleanup {

    @SuppressWarnings("MissingJavadoc")
    public DungeonMessagesCleanup() {
        ClientReceiveMessageEvents.ALLOW_GAME.register(this::shouldSend);
    }

    private boolean shouldSend(Text text, boolean b) {
        if (b) {
            return true;
        }
        if (LocationUtils.getRegion().island != LocationUtils.Island.CATACOMBS) {
            return true;
        }

        final String content = text.getString().trim();

        if (ConfigKeys.CLEANUP_HIDE_WATCHER_MESSAGE.get() && content.contains(":")) {
            return !content.startsWith("[BOSS] The Watcher: ");
        }

        if (ConfigKeys.CLEANUP_HIDE_POTION_EFFECTS.get()
            && content.equals(
            "Your active Potion Effects have been paused and stored. They will be restored when you leave Dungeons! " +
            "You are not allowed to use existing Potion Effects while in Dungeons.")) {
            return false;
        }

        if (ConfigKeys.CLEANUP_HIDE_CLASS_MESSAGES.get()) {
            if (content.startsWith("Your ") &&
                content.endsWith("stats are doubled because you are the only player using this class!")) {
                return false;
            } else if (content.startsWith("[Healer] ") || content.startsWith("[Berserk] ") ||
                       content.startsWith("[Mage] ") || content.startsWith("[Archer] ") ||
                       content.startsWith("[Tank] ")) {
                return false;
            }
        }

        if (ConfigKeys.CLEANUP_HIDE_BLESSING.get() && (content.startsWith("A Blessing of ") ||
                                                       (content.contains("has obtained Blessing of ") &&
                                                        content.endsWith("!")) ||
                                                       content.startsWith("DUNGEON BUFF! A Blessing of ") ||
                                                       content.startsWith("DUNGEON BUFF! You found a Blessing of ") ||
                                                       content.startsWith("Grants you ") ||
                                                       content.startsWith("Granted you "))) {
            return false;
        }

        if (ConfigKeys.CLEANUP_HIDE_SILVERFISH_MESSAGE.get()
            && content.equals("You cannot hit the silverfish while it's moving!")) {
            return false;
        }

        if (ConfigKeys.CLEANUP_HIDE_DUNGEON_KEY_MESSAGE.get() &&
            (content.equals("RIGHT CLICK on the BLOOD DOOR to open it. This key can only be used to open 1 door!")
             || content.equals("RIGHT CLICK on a WITHER door to open it. This key can only be used to open 1 door!"))) {
            return false;
        }

        return !(ConfigKeys.CLEANUP_HIDE_ULTIMATE_READY.get() &&
                 content.endsWith(" is ready to use! Press DROP to activate it!"));
    }

}
