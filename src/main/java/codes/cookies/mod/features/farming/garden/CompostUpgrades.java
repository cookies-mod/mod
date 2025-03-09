package codes.cookies.mod.features.farming.garden;

import codes.cookies.mod.config.categories.FarmingCategory;
import codes.cookies.mod.events.ItemLoreEvent;
import codes.cookies.mod.repository.Ingredient;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.constants.ComposterUpgrades;
import codes.cookies.mod.repository.constants.RepositoryConstants;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.maths.RomanNumerals;
import codes.cookies.mod.utils.skyblock.LocationUtils;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Displays total compost upgrade cost.
 */
public class CompostUpgrades {
    boolean isScreenOpen = false;

    @SuppressWarnings("MissingJavadoc")
    public CompostUpgrades() {
        ScreenEvents.BEFORE_INIT.register(this::openScreen);
        ItemLoreEvent.EVENT_ITEM.register(this::update);
    }

    private void openScreen(MinecraftClient minecraftClient, Screen screen, int i, int i1) {
        if (!SkyblockUtils.isCurrentlyInSkyblock()) {
            return;
        }
        if (!LocationUtils.Island.GARDEN.isActive()) {
            return;
        }
        if (!FarmingCategory.showCompostPriceBreakdown) {
            return;
        }
        if (!(screen instanceof HandledScreen<?>)) {
            return;
        }
        if (!screen.getTitle().getString().equals("Composter Upgrades")) {
            return;
        }
        if (RepositoryConstants.composterUpgrades == null) {
            return;
        }
        this.isScreenOpen = true;
        ScreenEvents.remove(screen).register(s -> this.isScreenOpen = false);
    }

    private void update(ItemStack itemStack, List<MutableText> mutableTexts) {
        if (!this.isScreenOpen) {
            return;
        }
        if (!Plot.getCurrentPlot().isBarn()) {
            return;
        }
        if (RepositoryConstants.composterUpgrades == null) {
            return;
        }
        if (ItemUtils.getData(itemStack, DataComponentTypes.CUSTOM_NAME) == null) {
            return;
        }
        if (!FarmingCategory.showCompostPriceBreakdown) {
            return;
        }

        LinkedList<MutableText> list = new LinkedList<>();

        String name = itemStack.getName().getString();
        if (!name.startsWith("Composter Speed")
            && !name.startsWith("Multi Drop")
            && !name.startsWith("Fuel Cap")
            && !name.startsWith("Organic Matter Cap")
            && !name.startsWith("Cost Reduction")) {
            return;
        }
        String upgradeName = name.replaceAll(
            "(Composter Speed|Multi Drop|Fuel Cap|Organic Matter Cap|Cost Reduction).*",
            "$1"
        );
        String lastPart = name.substring(upgradeName.length()).trim();
        int currentLevel;
        if (lastPart.isEmpty()) {
            currentLevel = 0;
        } else {
            currentLevel = RomanNumerals.romanToArabic(lastPart.trim());
        }

        String maxAmount;
        List<ComposterUpgrades.CompostUpgrade> upgrades;
        switch (upgradeName.trim()) {
            case "Composter Speed" -> {
                maxAmount = "500%";
                upgrades = RepositoryConstants.composterUpgrades.getSpeed();
            }
            case "Multi Drop" -> {
                maxAmount = "75%";
                upgrades = RepositoryConstants.composterUpgrades.getMultiDrop();
            }
            case "Fuel Cap" -> {
                maxAmount = "850,000";
                upgrades = RepositoryConstants.composterUpgrades.getFuelCap();
            }
            case "Organic Matter Cap" -> {
                maxAmount = "540,000";
                upgrades = RepositoryConstants.composterUpgrades.getOrganicMatterCap();
            }
            case "Cost Reduction" -> {
                maxAmount = "25%";
                upgrades = RepositoryConstants.composterUpgrades.getCostReduction();
            }
            default -> {
                return;
            }
        }

        for (MutableText text : mutableTexts) {
            list.add(text);
            if (text.getString().startsWith("Next Tier: ")) {
                list.add(Text.translatable(TranslationKeys.COMPOST_UPGRADE_MAX_TIER).append(": ").formatted(Formatting.GRAY)
                             .append(Text.literal(maxAmount).formatted(Formatting.GREEN)));
            } else if (text.getString().startsWith("+")) {
                list.add(Text.empty());
                list.add(Text.translatable(TranslationKeys.COMPOST_UPGRADE_REMAINING_COST).append(": ").formatted(Formatting.GRAY));
                List<ComposterUpgrades.CompostUpgrade> subList = upgrades.subList(Math.min(
                    currentLevel,
                    upgrades.size()
                                                                                          ), upgrades.size());
                List<Ingredient> subListIngredients = subList.stream()
                                                             .flatMap(compostUpgrade -> compostUpgrade.cost().stream())
                                                             .toList();


                Stream<Ingredient> stream = Ingredient.mergeToList(subListIngredients).stream();
                switch (FarmingCategory.sortOrder) {
                    case DESCENDING -> stream = stream.sorted(Comparator.comparingInt(Ingredient::getAmount)
                                                                        .reversed());
                    case ASCENDING -> stream = stream.sorted(Comparator.comparingInt(Ingredient::getAmount));
                }

                stream.forEach(ingredient -> {
                    MutableText entry = Text.literal("  ");
                    RepositoryItem item = RepositoryItem.of(ingredient.getId());
                    if (item == null) {
                        list.add(Text.translatable(TranslationKeys.ITEM_NOT_FOUND, ingredient.getId()));
                        return;
                    }
                    entry.append(item.getFormattedName());
                    entry.append(Text.literal(" x").append(String.valueOf(ingredient.getAmount()))
                                     .formatted(Formatting.DARK_GRAY));
                    list.add(entry);
                });
                int sum = subList.stream().mapToInt(ComposterUpgrades.CompostUpgrade::copper).sum();
                list.add(Text.literal("  ")
                             .append(Text.literal(String.valueOf(sum)).append(" Copper").formatted(Formatting.RED)));
                break;
            }
        }

        mutableTexts.clear();
        mutableTexts.addAll(list);
    }

}
