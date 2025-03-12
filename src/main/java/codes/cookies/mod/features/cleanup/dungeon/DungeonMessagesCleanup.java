package codes.cookies.mod.features.cleanup.dungeon;

import codes.cookies.mod.config.categories.CleanupCategory;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;

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
        if (!SkyblockUtils.isCurrentlyInSkyblock()) {
            return true;
        }
        if (LocationUtils.getRegion().island != LocationUtils.Island.CATACOMBS) {
            return true;
        }

        final String content = text.getString().trim();

        if (CleanupCategory.hideWatcherMessages && content.contains(":")) {
            return !content.startsWith("[BOSS] The Watcher: ");
        }

        if (CleanupCategory.hidPotionEffectMessage && content.equals(
            "Your active Potion Effects have been paused and stored. They will be restored when you leave Dungeons! " +
            "You are not allowed to use existing Potion Effects while in Dungeons.")) {
            return false;
        }

        if (CleanupCategory.hideClassMessages) {
            if (content.startsWith("Your ") &&
                content.endsWith("stats are doubled because you are the only player using this class!")) {
                return false;
            } else if (content.startsWith("[Healer] ") || content.startsWith("[Berserk] ") ||
                       content.startsWith("[Mage] ") || content.startsWith("[Archer] ") ||
                       content.startsWith("[Tank] ")) {
                return false;
            }
        }

        if (CleanupCategory.hideBlessingMessages && (content.startsWith("A Blessing of ") ||
                                                       (content.contains("has obtained Blessing of ") &&
                                                        content.endsWith("!")) ||
                                                       content.startsWith("DUNGEON BUFF! A Blessing of ") ||
                                                       content.startsWith("DUNGEON BUFF! You found a Blessing of ") ||
                                                       content.startsWith("Grants you ") ||
                                                       content.startsWith("Granted you "))) {
            return false;
        }

        if (CleanupCategory.hideSilverfishMessage
            && content.equals("You cannot hit the silverfish while it's moving!")) {
            return false;
        }

        if (CleanupCategory.hideDungeonKeyMessage &&
            (content.equals("RIGHT CLICK on the BLOOD DOOR to open it. This key can only be used to open 1 door!")
             || content.equals("RIGHT CLICK on a WITHER door to open it. This key can only be used to open 1 door!"))) {
            return false;
        }

        return !(CleanupCategory.hideUltimateReady &&
                 content.endsWith(" is ready to use! Press DROP to activate it!"));
    }

}
