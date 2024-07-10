package dev.morazzer.cookies.mod.features.misc.utils;

import com.google.common.collect.Lists;
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
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.items.AbsoluteTooltipPositioner;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import lombok.Getter;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

/**
 * Helper to display a recipe (and its respective sub recipes) in game and also show the ingredients plus their
 * respective amount of items that are required.
 */
public class CraftHelper {
    /**
     * Logging key for the {@link DevUtils#log(String, Object, Object...)} method.
     */
    public static final String LOGGING_KEY = "crafthelper";
    private static final Identifier DEBUG_INFO = DevUtils.createIdentifier("craft_helper/debug");
    private static final Identifier DEBUG_HITBOX = DevUtils.createIdentifier("craft_helper/hitbox");
    private static CraftHelper instance;
    private final int buttonWidthHeight = 8;
    @Getter
    private RepositoryItem selectedItem;
    private RecipeCalculationResult calculation;
    private int yOffset = 0;
    private List<OrderedText> tooltip = new ArrayList<>();
    private int buttonX;
    private int buttonY;
    private int width = 0;
    private int scrolled = 1;

    @SuppressWarnings("MissingJavadoc")
    public CraftHelper() {
        instance = this;
        ProfileSwapEvent.AFTER_SET_NO_UUID.register(this::profileSwap);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof HandledScreen<?>)) {
                return;
            }
            if (!SkyblockUtils.isCurrentlyInSkyblock()) {
                return;
            }
            if (!ConfigManager.getConfig().helpersConfig.craftHelper.getValue()) {
                return;
            }

            this.recalculate();
            ScreenEvents.afterRender(screen).register(this::afterRender);
            ScreenMouseEvents.beforeMouseClick(screen).register(this::clicked);
            ScreenMouseEvents.beforeMouseScroll(screen).register(this::beforeScroll);
        });
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
        instance.calculation = RecipeCalculator.calculate(item);
        ProfileStorage.getCurrentProfile().ifPresent(data -> data.setSelectedCraftHelperItem(item.getInternalId()));
        instance.recalculate();
        instance.scrolled = 1;
    }

    private static int getAmount(EvaluationContext context, int max, StackCountContext stackCountContext) {
        int amount = 0;

        if (context.parent != null) {
            amount = getAmountThroughParents(context, max, stackCountContext);
        }

        amount += stackCountContext.take(context.recipeResult.getRepositoryItem(), max - amount);

        return Math.min(amount, max);
    }

    private static int getAmountThroughParents(
        EvaluationContext context,
        int max,
        StackCountContext stackCountContext) {
        int amount = 0;
        amount += (stackCountContext.integers.peek() *
                   (context.recipeResult.getAmount() / context.parent.recipeResult().getAmount()));
        return Math.min(amount, max);
    }

    @SuppressWarnings("MissingJavadoc")
    public static boolean append(
        String prefix,
        List<MutableText> text,
        RecipeResult<?> calculationResult,
        int depth,
        EvaluationContext parent,
        StackCountContext stackCountContext,
        Formatter formatter) {
        EvaluationContext context = new EvaluationContext(calculationResult, parent);
        final int amount = getAmount(context, calculationResult.getAmount(), stackCountContext);
        final int amountThroughParents = getAmountThroughParents(
            context,
            calculationResult.getAmount(),
            stackCountContext);
        DevUtils.runIf(
            DEBUG_INFO,
            () -> text.add(Text.literal("Amount: %s, through parents: %s, required: %s".formatted(
                amount,
                amountThroughParents,
                calculationResult.getAmount()))));
        if (amountThroughParents == calculationResult.getAmount()) {
            DevUtils.runIf(DEBUG_INFO, () -> text.add(Text.literal("Parent full, skipping")));
            return true;
        }

        if (calculationResult instanceof RecipeCalculationResult subResult) {
            int index = text.size();

            boolean childrenFinished = true;
            stackCountContext.integers.push(amount);
            if (amount == calculationResult.getAmount()) {
                DevUtils.runIf(DEBUG_INFO, () -> text.add(Text.literal("Skipping childs, parent full")));
            }
            for (int i = 0; i < subResult.getRequired().size(); i++) {
                if (amount == calculationResult.getAmount()) {
                    continue;
                }
                RecipeResult<?> recipeResult = subResult.getRequired().get(i);
                String newPrefix;
                boolean isLast = i + 1 == subResult.getRequired().size();
                if (prefix.isEmpty()) {
                    newPrefix = isLast ? "└ " : "├ ";
                } else {
                    newPrefix = prefix.replace("├", "│").replace("└", "  ") + (isLast ? "└ " : "├ ");
                }

                childrenFinished &= append(
                    newPrefix,
                    text,
                    recipeResult,
                    depth + 1,
                    context,
                    stackCountContext,
                    formatter);
            }
            stackCountContext.integers.pop();

            childrenFinished = childrenFinished && !subResult.getRequired().isEmpty();

            text.add(index, formatter.format(
                prefix,
                calculationResult.getId(),
                calculationResult.getRepositoryItem(),
                calculationResult.getAmount() - amountThroughParents,
                amount - amountThroughParents,
                childrenFinished,
                depth));

            return childrenFinished;
        } else {
            text.add(formatter.format(
                prefix,
                calculationResult.getId(),
                calculationResult.getRepositoryItem(),
                calculationResult.getAmount() - amountThroughParents,
                amount - amountThroughParents,
                false,
                depth));
        }

        return amount == calculationResult.getAmount();
    }

    private MutableText formatted(
        String prefix,
        String id,
        RepositoryItem repositoryItem,
        int amount,
        int amountOfItem,
        boolean childrenFinished,
        int depth) {
        final MutableText literal = Text.empty().append(Text.literal(prefix).formatted(Formatting.DARK_GRAY));

        final double percentage = (double) amountOfItem / amount;
        //noinspection DataFlowIssue
        final int color = ColorUtils.calculateBetween(
            Formatting.RED.getColorValue(),
            Formatting.GREEN.getColorValue(),
            percentage);


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
        final String formatted = "%s/%s".formatted(
            MathUtils.NUMBER_FORMAT.format(amountOfItem),
            MathUtils.NUMBER_FORMAT.format(amount));
        literal.append(Text.literal(formatted).withColor(color));
        literal.append(" ");
        if (repositoryItem != null) {
            literal.append(repositoryItem.getFormattedName());
        } else {
            literal.append(id);
        }

        return literal;
    }

    private void beforeScroll(
        Screen screen,
        double mouseX,
        double mouseY,
        double horizontalScroll,
        double verticalScroll) {
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;

        if (!this.shouldRender()) {
            return;
        }

        if (this.tooltip.size() < 30) {
            return;
        }

        final int x = this.calculateX(handledScreen);
        final int y = this.calculateY(handledScreen);

        if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + 270) {
            this.scrolled = (int) Math.max(1, Math.min(this.scrolled - verticalScroll, this.tooltip.size() - 29));
        }
    }

    private void clicked(Screen screen, double mouseX, double mouseY, int button) {
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;

        if (!this.shouldRender()) {
            return;
        }

        final int x = this.calculateX(handledScreen);
        final int y = this.calculateY(handledScreen);

        final int buttonX = x + this.buttonX;
        final int buttonY = y + this.buttonY;

        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidthHeight && mouseY >= buttonY &&
            mouseY <= buttonY + buttonWidthHeight) {
            setSelectedItem(null);
        }
    }

    private void afterRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;

        if (!this.shouldRender()) {
            return;
        }

        final int x = this.calculateX(handledScreen);
        final int y = this.calculateY(handledScreen);

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0, 0, -100);
        final int size = this.tooltip.size();
        final List<OrderedText> tooltip;
        if (this.tooltip.size() > 30) {
            tooltip = new ArrayList<>(this.tooltip.subList(
                Math.min(size - 29, this.scrolled),
                Math.min(29 + this.scrolled, size)));
            tooltip.addFirst(this.tooltip.getFirst());
        } else {
            tooltip = this.tooltip;
        }

        drawContext.drawTooltip(
            MinecraftClient.getInstance().textRenderer,
            tooltip,
            AbsoluteTooltipPositioner.INSTANCE,
            x,
            y);


        if (DevUtils.isEnabled(DEBUG_HITBOX)) {
            drawContext.getMatrices().translate(0, 0, 1000);
            drawContext.drawBorder(
                x + this.buttonX,
                y + this.buttonY,
                this.buttonWidthHeight,
                this.buttonWidthHeight,
                -1);
        }
        drawContext.getMatrices().pop();

    }

    private int calculateX(HandledScreen<?> handledScreen) {
        return handledScreen.x + handledScreen.backgroundWidth + 2;
    }

    private int calculateY(HandledScreen<?> handledScreen) {
        return handledScreen.y + handledScreen.backgroundHeight / 2 - yOffset;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean shouldRender() {
        return this.calculation != null && !this.tooltip.isEmpty();
    }

    private void profileSwap() {
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            return;
        }
        final ProfileData profileData = currentProfile.get();
        if (profileData.getSelectedCraftHelperItem() != null && !profileData.getSelectedCraftHelperItem().isEmpty()) {
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

    private void recalculate() {
        if (this.calculation == null) {
            return;
        }

        long start = System.nanoTime();
        List<MutableText> tooltip = new ArrayList<>();
        append(
            "",
            tooltip,
            calculation,
            0,
            new EvaluationContext(calculation, null),
            new StackCountContext(),
            this::formatted);

        if (!tooltip.isEmpty()) {
            this.addClose(tooltip);
        }

        DevUtils.runIf(
            DEBUG_INFO,
            () -> tooltip.add(Text.literal(("Time to calculate: %sμs").formatted((System.nanoTime() - start) / 1000))));
        this.tooltip = Lists.transform(tooltip, Text::asOrderedText);
        this.yOffset = (Math.min(this.tooltip.size(), 30) * 9) / 2;
    }

    private void addClose(List<MutableText> tooltip) {
        int maxSize = 0;
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        for (MutableText orderedText : tooltip) {
            int size = textRenderer.getWidth(orderedText);
            if (size > maxSize) {
                maxSize = size;
            }
        }

        final MutableText orderedText = tooltip.getFirst();
        int size = textRenderer.getWidth(orderedText);
        if (size != maxSize) {
            int spaceSize = textRenderer.getWidth(" ");
            int delta = maxSize - size;
            orderedText.append(StringUtils.leftPad("", delta / spaceSize - 1));
        }

        this.width = maxSize + (size == maxSize ? 5 : 0);

        orderedText.append(Text.literal(Constants.Emojis.NO).formatted(Formatting.RED, Formatting.BOLD));

        this.buttonX = textRenderer.getWidth(orderedText) + 3;
        this.buttonY = -12;
    }

    public interface Formatter {
        MutableText format(
            String prefix,
            String id,
            RepositoryItem repositoryItem,
            int amount,
            int amountOfItem,
            boolean childrenFinished,
            int depth);
    }

    @SuppressWarnings("MissingJavadoc")
    public record EvaluationContext(RecipeResult<?> recipeResult, EvaluationContext parent) {
    }

    @SuppressWarnings("MissingJavadoc")
    public static class StackCountContext {
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
