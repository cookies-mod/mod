package dev.morazzer.cookies.mod.features.misc.utils;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.events.profile.ProfileSwapEvent;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeResult;
import dev.morazzer.cookies.mod.utils.ColorUtils;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.DevUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import lombok.Getter;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Helper to display a recipe (and its respective sub recipes) in game and also show the ingredients plus their respective amount of items that are required.
 */
public class CraftHelper {
    /**
     * Logging key for the {@link DevUtils#log(String, Object, Object...)} method.
     */
    public static final String LOGGING_KEY = "crafthelper";
    private static final Identifier DEBUG_INFO = DevUtils.createIdentifier("craft_helper/debug");

    private static CraftHelper instance;
    private final NumberFormat numberFormat = new DecimalFormat();
    @Getter
    private RepositoryItem selectedItem;
    private RecipeCalculationResult calculation;
    private int yOffset = 0;
    private long lastCalculation = System.currentTimeMillis();
    private List<OrderedText> tooltip = new ArrayList<>();

    @SuppressWarnings("MissingJavadoc")
    public CraftHelper() {
        instance = this;
        ProfileSwapEvent.AFTER_SET_NO_UUID.register(this::profileSwap);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof HandledScreen<?>)) {
                return;
            }

            if (!ConfigManager.getConfig().helpersConfig.craftHelper.getValue()) {
                return;
            }

            ScreenEvents.afterRender(screen).register(this::afterRender);
        });
        setSelectedItem(RepositoryItem.of("terminator"));
    }

    /**
     * Sets the currently selected item that is displayed and invokes a recalculation.
     *
     * @param item The item to use.
     */
    public static void setSelectedItem(RepositoryItem item) {
        if (item == null) {
            instance.calculation = null;
            instance.selectedItem = null;
            ProfileStorage.getCurrentProfile().ifPresent(data -> data.setSelectedCraftHelperItem(null));
            instance.recalculate();
            return;
        }

        final Optional<Recipe> first = item.getRecipes().stream().findFirst();
        if (first.isEmpty()) {
            instance.calculation = null;
            instance.selectedItem = null;
            instance.recalculate();
            return;
        }
        instance.selectedItem = item;
        instance.calculation = RecipeCalculator.calculate(item.getRecipes().stream().findFirst().get());
        ProfileStorage.getCurrentProfile().ifPresent(data -> data.setSelectedCraftHelperItem(item.getInternalId()));
        instance.recalculate();
    }

    private static int getAmount(EvaluationContext context, int max, StackCountContext stackCountContext) {
        int amount = 0;

        if (context.parent != null) {
            amount = getAmountThroughParents(context, max, stackCountContext);
        }

        amount += stackCountContext.take(context.recipeResult.getRepositoryItem(), max - amount);

        return Math.min(amount, max);
    }

    private static int getAmountThroughParents(EvaluationContext context, int max,
                                               StackCountContext stackCountContext) {
        int amount = 0;
        amount += (stackCountContext.integers.peek() *
                   (context.recipeResult.getAmount() / context.parent.recipeResult().getAmount()));
        return Math.min(amount, max);
    }

    private void profileSwap() {
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            return;
        }
        final ProfileData profileData = currentProfile.get();
        if (profileData.getSelectedCraftHelperItem() != null &&
            !profileData.getSelectedCraftHelperItem().isEmpty()) {
            String item = profileData.getSelectedCraftHelperItem();
            final RepositoryItem repositoryItem = RepositoryItem.of(item);
            if (repositoryItem != null) {
                DevUtils.log(LOGGING_KEY, "Swapping craft item to %s", item);
                setSelectedItem(repositoryItem);
            }
        } else {
            DevUtils.log(LOGGING_KEY, "Craft item of profile empty, setting to null.");
            setSelectedItem(null);
        }
    }

    private void afterRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;

        if (this.lastCalculation + 10000 < System.currentTimeMillis()) {
            this.recalculate();
        }
        if (this.calculation == null || this.tooltip.isEmpty()) {
            return;
        }

        drawContext.drawTooltip(
            MinecraftClient.getInstance().textRenderer,
            this.tooltip,
            HoveredTooltipPositioner.INSTANCE,
            handledScreen.x + handledScreen.backgroundWidth * 2 + 64,
            handledScreen.y + handledScreen.backgroundHeight / 2 - yOffset
        );
    }

    private void recalculate() {
        if (this.calculation == null) {
            return;
        }

        long start = System.nanoTime();
        List<OrderedText> tooltip = new ArrayList<>();
        append("", tooltip, calculation, 0, new EvaluationContext(calculation, null), new StackCountContext());
        DevUtils.runIf(DEBUG_INFO, () ->
            tooltip.add(
                Text.literal(("Time to calculate: %sμs").formatted((System.nanoTime() - start) / 1000))
                    .asOrderedText()));
        this.tooltip = tooltip;
        this.yOffset = (this.tooltip.size() * 9) / 2;
        this.lastCalculation = System.currentTimeMillis();
    }

    private boolean append(String prefix, List<OrderedText> text, RecipeResult<?> calculationResult, int depth,
                           EvaluationContext parent, StackCountContext stackCountContext) {
        EvaluationContext context = new EvaluationContext(calculationResult, parent);
        final int amount = getAmount(context, calculationResult.getAmount(), stackCountContext);
        final int amountThroughParents =
            getAmountThroughParents(context, calculationResult.getAmount(), stackCountContext);
        DevUtils.runIf(DEBUG_INFO,
            () -> text.add(
                Text.literal("Amount: %s, through parents: %s, required: %s"
                    .formatted(amount, amountThroughParents, calculationResult.getAmount())).asOrderedText()
            )
        );
        if (amountThroughParents == calculationResult.getAmount()) {
            DevUtils.runIf(DEBUG_INFO, () -> text.add(Text.literal("Parent full, skipping").asOrderedText()));
            return true;
        }

        if (calculationResult instanceof RecipeCalculationResult subResult) {
            int index = text.size();

            boolean childrenFinished = true;
            stackCountContext.integers.push(amount);
            for (int i = 0; i < subResult.getRequired().size(); i++) {
                RecipeResult recipeResult = subResult.getRequired().get(i);
                String newPrefix;
                boolean isLast = i + 1 == subResult.getRequired().size();
                if (prefix.isEmpty()) {
                    newPrefix = isLast ? "└ " : "├ ";
                } else {
                    newPrefix = prefix.replace("├", "│").replace("└", "  ") + (isLast ? "└ " : "├ ");
                }

                childrenFinished &= append(newPrefix, text, recipeResult, depth + 1, context, stackCountContext);
            }
            stackCountContext.integers.pop();

            childrenFinished = childrenFinished && !subResult.getRequired().isEmpty();

            text.add(index,
                this.formatted(
                    prefix,
                    calculationResult.getId(),
                    calculationResult.getRepositoryItem(),
                    calculationResult.getAmount() - amountThroughParents,
                    amount - amountThroughParents,
                    childrenFinished, depth
                ).asOrderedText());

            return childrenFinished;
        } else {
            text.add(
                this.formatted(
                    prefix,
                    calculationResult.getId(),
                    calculationResult.getRepositoryItem(),
                    calculationResult.getAmount() - amountThroughParents,
                    amount - amountThroughParents,
                    false,
                    depth).asOrderedText()
            );
        }

        return amount == calculationResult.getAmount();
    }

    private Text formatted(String prefix, String id, RepositoryItem repositoryItem, int amount, int amountOfItem,
                           boolean childrenFinished, int depth) {
        final MutableText literal = Text.empty().append(Text.literal(prefix).formatted(Formatting.DARK_GRAY));

        final double percentage = (double) amountOfItem / amount;
        final int color =
            ColorUtils.calculateBetween(Formatting.RED.getColorValue(), Formatting.GREEN.getColorValue(), percentage);


        if (percentage == 1) {
            if (depth == 0) {
                literal.append(Text.literal(Constants.Emojis.FLAG_FILLED).formatted(Formatting.GREEN));
            } else {
                literal.append(Text.literal(Constants.Emojis.YES).formatted(Formatting.GREEN, Formatting.BOLD));
            }
        } else {
            if (childrenFinished) {
                if (depth == 0) {
                    literal.append(Text.literal(Constants.Emojis.FLAG_FILLED).formatted(Formatting.YELLOW));
                } else {
                    literal.append(Text.literal(Constants.Emojis.WARNING).formatted(Formatting.YELLOW));
                }
            } else {
                if (depth == 0) {
                    literal.append(Text.literal(Constants.Emojis.FLAG_EMPTY).formatted(Formatting.RED));
                } else {
                    literal.append(Text.literal(Constants.Emojis.NO).formatted(Formatting.RED, Formatting.BOLD));
                }
            }
        }

        literal.append(" ");
        final String formatted = "%s/%s".formatted(numberFormat.format(amountOfItem), numberFormat.format(amount));
        literal.append(Text.literal(formatted).withColor(color));
        literal.append(" ");
        if (repositoryItem != null) {
            literal.append(repositoryItem.getFormattedName());
        } else {
            literal.append(id);
        }

        return literal;
    }

    private record EvaluationContext(RecipeResult<?> recipeResult, EvaluationContext parent) {
    }

    private static class StackCountContext {
        Map<RepositoryItem, Long> itemMap = new HashMap<>();
        Stack<Integer> integers = new Stack<>();

        public StackCountContext() {
            integers.push(0);
        }

        public int take(RepositoryItem id, int max) {
            if (id == null) {
                return 0;
            }

            if (!itemMap.containsKey(id)) {
                int count = 0;
                final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
                if (currentProfile.isPresent()) {
                    final ProfileData profileData = currentProfile.get();
                    count += profileData.getSackTracker().getItems().getOrDefault(id, 0);
                }
                itemMap.put(id, (long) count);
            }

            long l = itemMap.getOrDefault(id, 0L);
            int used = (int) (l >> 32);
            int total = (int) l;
            int available = total - used;
            if (max >= available) {
                itemMap.put(id, ((long) total << 32) | total);
                return available;
            }

            used += max;
            l = l & 0xFFFFFFFFL;
            l |= (long) used << 32;
            itemMap.put(id, l);
            return max;
        }
    }

}
