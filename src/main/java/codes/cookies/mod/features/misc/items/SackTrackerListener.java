package codes.cookies.mod.features.misc.items;

import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.sub.SackTracker;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

/**
 * Listener to message to sacks messages.
 */
public class SackTrackerListener {
    private static final String LOGGER_KEY = "SackTrackerListener";

    private static final Pattern pattern = Pattern.compile("^ *([+-][\\d,]+)(.+?)\\(.*\\) *$", Pattern.MULTILINE);

    @SuppressWarnings("MissingJavadoc")
    public SackTrackerListener() {
        ClientReceiveMessageEvents.GAME.register(this::receiveMessage);
    }

    private void receiveMessage(Text text, boolean b) {
        if (!SkyblockUtils.isCurrentlyInSkyblock()) {
            return;
        }
        if (b) {
            return;
        }

        final String content = text.getString();
        if (!content.startsWith("[Sacks]")) {
            return;
        }

        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            DevUtils.log(LOGGER_KEY, "No active profile");
            return;
        }

        HoverEvent add = null;
        HoverEvent remove = null;
        for (Text sibling : text.getSiblings()) {
            final HoverEvent hoverEvent = sibling.getStyle().getHoverEvent();
            if (hoverEvent == null || !hoverEvent.getAction().equals(HoverEvent.Action.SHOW_TEXT)) {
                continue;
            }

            final Text value = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
            if (value == null) {
                return;
            }

            final String eventContent = value.getString();

            if (eventContent.contains("Added items:") && add == null) {
                add = hoverEvent;
            } else if (eventContent.contains("Removed items:") && remove == null) {
                remove = hoverEvent;
            }
        }

        DevUtils.log(LOGGER_KEY, "Add event: %s", add == null ? "empty" : "found");
        if (add != null) {
            parse(add, currentProfile.get().getSackTracker());
        }

        DevUtils.log(LOGGER_KEY, "Remove event: %s", remove == null ? "empty" : "found");
        if (remove != null) {
            parse(remove, currentProfile.get().getSackTracker());
        }
    }

    private void parse(HoverEvent hoverEvent, SackTracker sackTracker) {
        final Text value = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
        if (value != null) {
            final String content = value.getString();
            final Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                final int count;
                try {
                    count = Integer.parseInt(matcher.group(1).replaceAll("[^+\\-\\d]", ""));
                } catch (NumberFormatException e) {
                    continue;
                }
                final String name = matcher.group(2).trim();

                final Optional<RepositoryItem> ofName = RepositoryItem.ofName(name);
                if (ofName.isEmpty()) {
                    DevUtils.log(LOGGER_KEY, "No such item: %s", name);
                    continue;
                }

                final RepositoryItem repositoryItem = ofName.get();
                sackTracker.modify(repositoryItem, count);
                DevUtils.log(LOGGER_KEY, "Modified item %s, delta: %d", repositoryItem.getInternalId(), count);
            }
        }
    }
}
