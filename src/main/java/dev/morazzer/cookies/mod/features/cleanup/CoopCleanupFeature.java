package dev.morazzer.cookies.mod.features.cleanup;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.events.ItemLoreEvent;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * Cleanup for the coop collections item.
 */
public class CoopCleanupFeature {

    @SuppressWarnings("MissingJavadoc")
    public CoopCleanupFeature() {
        ItemLoreEvent.EVENT.register(this::modifyLore);
    }

    private void modifyLore(List<Text> texts) {
        if (texts.size() < 2) {
            return;
        }

        String first = texts.getFirst().getString();
        String second = texts.get(1).getString();
        if (!(first + " " + second).contains("Collection progress and rewards!")) {
            return;
        }

        switch (ConfigManager.getConfig().cleanupConfig.coopCleanupOption.getValue()) {
            case ALL -> this.removeAll(texts);
            case EMPTY -> this.removeEmpty(texts);
            case OTHER -> this.removeOther(texts);
        }
    }

    private void removeOther(List<Text> texts) {
        boolean remove = false;
        final String username = MinecraftClient.getInstance().getSession().getUsername();
        final Iterator<Text> iterator = texts.iterator();
        while (iterator.hasNext()) {
            Text text = iterator.next();

            if (text.getString().isBlank()) {
                remove = false;
            }

            if (remove) {
                if (text.getString().contains(username)) {
                    continue;
                }
                iterator.remove();
            }

            if (text.getString().startsWith("Co-op Contributions:")) {
                remove = true;
            }
        }
    }

    private void removeEmpty(List<Text> texts) {
        texts.removeIf(text -> text.getString().trim().endsWith(": 0"));
    }

    private void removeAll(List<Text> texts) {
        boolean remove = false;
        final Iterator<Text> iterator = texts.iterator();
        while (iterator.hasNext()) {
            Text text = iterator.next();

            if (text.getString().startsWith("Co-op Contributions:")) {
                remove = true;
            } else if (text.getString().startsWith("Total Collected:")) {
                remove = true;
            }
            if (remove) {
                iterator.remove();
            }

            if (text.getString().isBlank()) {
                remove = false;
            }
        }
    }

}
