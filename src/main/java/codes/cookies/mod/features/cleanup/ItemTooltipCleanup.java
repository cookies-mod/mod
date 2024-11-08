package codes.cookies.mod.features.cleanup;

import codes.cookies.mod.config.ConfigKeys;
import codes.cookies.mod.events.ItemLoreEvent;
import codes.cookies.mod.utils.SkyblockUtils;
import java.util.Iterator;
import java.util.List;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

/**
 * Cleanup for all types of item tooltips.
 */
public class ItemTooltipCleanup {

    @SuppressWarnings("MissingJavadoc")
    public ItemTooltipCleanup() {
        ItemLoreEvent.EVENT.register(this::modifyLore);
    }

    private void modifyLore(List<MutableText> texts) {
        if (!SkyblockUtils.isCurrentlyInSkyblock()) {
            return;
        }
        boolean remove = false;
        boolean hasFinishedStats = false;
        boolean pet = false;
        boolean hadEnchantments = false;

        final Iterator<MutableText> iterator = texts.iterator();
        while (iterator.hasNext()) {
            boolean removeCurrent = remove;
            final MutableText text = iterator.next();
            final String content = text.getString().trim();

            if ((content.endsWith("Pet") || content.endsWith("Mount"))
                || ((content.contains("Pet") || content.contains("Mount")) && content.endsWith("Skin"))
                || content.equals("All Skills")) {
                pet = true;
            }

            if (!hasFinishedStats) {
                final Iterator<Text> siblings = text.getSiblings().iterator();
                while (siblings.hasNext()) {
                    final Text next = siblings.next();
                    final String trim = next.getString().trim();
                    final TextColor color = next.getStyle().getColor();
                    if (color == null) {
                        continue;
                    }
                    //The used constants always have a color value.
                    //noinspection DataFlowIssue
                    if (trim.startsWith("(") && trim.endsWith(")") &&
                        ((ConfigKeys.CLEANUP_REMOVE_DUNGEON_STATS.get() &&
                          next.getStyle().getColor().getRgb() == Formatting.DARK_GRAY.getColorValue()) ||
                         (ConfigKeys.CLEANUP_REMOVE_REFORGE_STATS.get() &&
                          next.getStyle().getColor().getRgb() == Formatting.BLUE.getColorValue()) ||
                         (ConfigKeys.CLEANUP_REMOVE_HPB_STATS.get() &&
                          next.getStyle().getColor().getRgb() == Formatting.YELLOW.getColorValue()) ||
                         (ConfigKeys.CLEANUP_REMOVE_GEMSTONE_STATS.get() &&
                          next.getStyle().getColor().getRgb() == Formatting.LIGHT_PURPLE.getColorValue()))) {
                        siblings.remove();
                    }
                }
            }

            if (ConfigKeys.CLEANUP_REMOVE_GEAR_SCORE.get() && content.startsWith("Gear Score: ")) {
                removeCurrent = true;
            } else if (content.isBlank()) {
                if (ConfigKeys.CLEANUP_REMOVE_BLANK.get()) {
                    removeCurrent = true;
                }
                remove = false;
                hasFinishedStats = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_FULL_SET_BONUS.get() && content.startsWith("Full Set Bonus: ")) {
                remove = true;
                removeCurrent = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_GEMSTONE_LINE.get() && content.startsWith("[") &&
                       content.endsWith("]")) {
                removeCurrent = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_MAX_LEVEL.get() && pet && content.equals("MAX LEVEL")) {
                removeCurrent = true;
                remove = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_ACTIONS.get() && pet && content.equals("Left-click to summon!")) {
                removeCurrent = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_ACTIONS.get() && pet &&
                       content.equals("Right-click to convert to an item!")) {
                removeCurrent = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_ACTIONS.get() && pet && content.equals("Click to despawn!")) {
                removeCurrent = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_HELD_ITEM.get() && pet && content.startsWith("Held Item: ")) {
                removeCurrent = true;
                remove = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_ABILITY.get() &&
                       (content.endsWith("RIGHT CLICK") || content.endsWith("LEFT CLICK") ||
                        content.equals("Scroll Abilities:"))) {
                removeCurrent = true;
                remove = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_PIECE_BONUS.get() && content.startsWith("Piece Bonus: ")) {
                removeCurrent = true;
                remove = true;
            } else if (ConfigKeys.CLEANUP_REMOVE_RUNES.get() && content.startsWith("◆") && content.contains("Rune")) {
                removeCurrent = true;
                remove = true;
            }

            if (ConfigKeys.CLEANUP_REMOVE_ENCHANTS.get() && !text.getSiblings().isEmpty() && !hadEnchantments) {
                final Text first = text.getSiblings().getFirst();
                if (first.getStyle().getColor() != null) {
                    final int rgb = first.getStyle().getColor().getRgb();

                    //The used constants always have a color value.
                    //noinspection DataFlowIssue
                    if ((rgb == Formatting.LIGHT_PURPLE.getColorValue() && iterator.hasNext())||
                        (rgb == Formatting.BLUE.getColorValue() && !first.getStyle().isBold())) {
                        hadEnchantments = true;
                        removeCurrent = true;
                        remove = true;
                    }
                }
            }

            if (ConfigKeys.CLEANUP_REMOVE_REFORGE.get() && !text.getSiblings().isEmpty()) {
                final Text first = text.getSiblings().getFirst();
                if (first.getStyle().getColor() != null) {
                    final int rgb = first.getStyle().getColor().getRgb();

                    //The used constants always have a color value.
                    //noinspection DataFlowIssue
                    if (rgb == Formatting.BLUE.getColorValue() && content.endsWith("Bonus")) {
                        removeCurrent = true;
                        remove = true;
                    }
                }
            }

            if (ConfigKeys.CLEANUP_REMOVE_SOULBOUND.get() &&
                (content.equals("* Co-op Soulbound *") || content.equals("* Soulbound *"))) {
                removeCurrent = true;
            }

            if (removeCurrent) {
                iterator.remove();
            }
        }

        if (!texts.isEmpty()) {
            while (!texts.isEmpty() && texts.getLast().getString().isBlank()) {
                texts.removeLast();
            }
        }
    }

}
