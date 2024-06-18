package dev.morazzer.cookies.mod.features.misc.items;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.generated.utils.ItemAccessor;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.items.Value;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Feature to display stats about a specific item in its lore/tooltip.
 */
public class ItemStats {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd' 'hh:mm")
        .withZone(ZoneId.systemDefault());

    @SuppressWarnings("MissingJavadoc")
    public static void register() {
        ItemTooltipCallback.EVENT.register(ItemStats::modifyTooltip);
    }

    private static void modifyTooltip(
        final ItemStack itemStack,
        final Item.TooltipContext tooltipContext,
        final TooltipType tooltipType,
        final List<Text> texts
    ) {
        final Value<String> stringValue = ItemAccessor.skyblockId(itemStack);
        if (stringValue.getAsOptional().isEmpty()) {
            return;
        }

        final List<Text> list = new ArrayList<>();
        final RepositoryItem repositoryItem = ItemUtils.getData(itemStack, CookiesDataComponentTypes.REPOSITORY_ITEM);

        final Instant time;
        if (
            ConfigManager.getConfig().miscConfig.showItemCreationDate.getValue()
            && (time = ItemUtils.getData(itemStack, CookiesDataComponentTypes.TIMESTAMP)) != null
        ) {
            list.add(Text.literal("Obtained: %s".formatted(dateTimeFormatter.format(time))).formatted(
                Formatting.LIGHT_PURPLE));
        }

        if (
            ConfigManager.getConfig().miscConfig.showItemDonatedToMuseum.getValue()
            && repositoryItem != null && repositoryItem.isMuseumable()
        ) {
            Boolean donatedMuseum = ItemUtils.getData(itemStack, CookiesDataComponentTypes.DONATED_MUSEUM);
            if (donatedMuseum == null) {
                donatedMuseum = false;
            }
            list.add(
                Text.literal("Museum: ")
                    .formatted(Formatting.LIGHT_PURPLE)
                    .append(
                        Text.literal(donatedMuseum ? Constants.Emojis.YES : Constants.Emojis.NO)
                            .formatted(donatedMuseum ? Formatting.GREEN : Formatting.RED, Formatting.BOLD)
                    ).append(
                        Text.literal(donatedMuseum ? " Yes" : " No")
                            .formatted(donatedMuseum ? Formatting.GREEN : Formatting.RED)
                    )
            );
        }

        if (
            ConfigManager.getConfig().miscConfig.showItemNpcValue.getValue()
            && repositoryItem != null && repositoryItem.getValue() > 0
        ) {
            addValue(itemStack, repositoryItem.getValue(), list);
        }

        if (
            ConfigManager.getConfig().miscConfig.showItemNpcValue.getValue()
            && repositoryItem != null && repositoryItem.getMotesValue() > 0
        ) {
            addValue(itemStack, repositoryItem.getMotesValue(), list);
        }

        if (!list.isEmpty()) {
            texts.add(Text.empty());
        }

        texts.addAll(list);
    }

    private static void addValue(final ItemStack itemStack, final double value, List<Text> list) {
        final MutableText formatted = Text.literal("Coins Price: ").formatted(Formatting.LIGHT_PURPLE);
        formatted.append(Text.literal(String.valueOf(value * itemStack.getCount())).formatted(Formatting.GOLD));
        if (itemStack.getCount() > 1) {
            formatted.append(Text.literal(" (%s)".formatted(value)).formatted(Formatting.DARK_GRAY));
        }
        list.add(formatted);
    }

}
