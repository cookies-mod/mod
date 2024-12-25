package codes.cookies.mod.events;

import codes.cookies.mod.events.profile.ProfileSwapEvent;
import codes.cookies.mod.utils.dev.DevUtils;
import java.util.UUID;
import net.minecraft.text.Text;

/**
 * Listener to look for certain chat messages.
 */
public class ChatListener {

    /**
     * The last skyblock profile id recoded.
     */
    public static UUID lastProfileId;

    /**
     * Looks for any incoming profile id or server switch messages.
     *
     * @param text    The message.
     * @param overlay If the message is in the overlay.
     */
    public static void lookForProfileIdMessage(Text text, boolean overlay) {
        if (overlay) {
            return;
        }
        if (text.getString().matches("Profile ID: .*")) {
            final UUID uuid = UUID.fromString(text.getString().substring(12).trim());

            if (uuid.equals(lastProfileId)) {
                return;
            }
            DevUtils.log(
                "profileSwitch",
                "Found new profile id was %s is %s",
                lastProfileId,
                text.getString().substring(12).trim()
                        );

            ProfileSwapEvent.EVENT_NO_UUID.invoker().run();
            if (lastProfileId != null) {
                ProfileSwapEvent.EVENT.invoker().swapProfile(lastProfileId, uuid);
            }
            final UUID previous = lastProfileId;
            lastProfileId = uuid;

            ProfileSwapEvent.AFTER_SET_NO_UUID.invoker().run();
            if (lastProfileId != null) {
                ProfileSwapEvent.AFTER_SET.invoker().swapProfile(previous, uuid);
            }
        }
    }
}
