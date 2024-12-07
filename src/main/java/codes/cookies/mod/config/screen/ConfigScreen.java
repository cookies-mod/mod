package codes.cookies.mod.config.screen;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.CookiesConfig;
import codes.cookies.mod.config.system.Row;
import codes.cookies.mod.config.system.editor.ConfigOptionEditor;
import codes.cookies.mod.config.system.editor.FoldableEditor;
import codes.cookies.mod.config.system.parsed.ConfigReader;
import codes.cookies.mod.config.system.parsed.ProcessedCategory;
import codes.cookies.mod.config.system.parsed.ProcessedOption;
import codes.cookies.mod.utils.RenderUtils;
import codes.cookies.mod.screen.ScrollbarScreen;
import codes.cookies.mod.utils.maths.MathUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Config screen to visualize and change the {@linkplain CookiesConfig}.
 */
public class ConfigScreen extends ScrollbarScreen implements InventoryConfigScreenConstants, TabConstants {


    private final static int SCROLL_HEIGHT = 163;
    private final ConfigReader configReader;
    private final List<ProcessedOption<?, ?>> hiddenOptions = new LinkedList<>();
    private final LinkedList<ProcessedCategory> visibleCategories = new LinkedList<>();
    private final LinkedList<ProcessedCategory> allCategories = new LinkedList<>();
    private final ConcurrentHashMap<Integer, Integer> activeFoldables = new ConcurrentHashMap<>();
    private final int optionsViewport = 160;
    int x;
    int y;
    private ProcessedCategory selectedCategory;
    private TextFieldWidget searchField;
    private int optionsLeft;
    private int optionsTop;
    private int optionsRight;
    private int optionsBottom;
    private int optionDefaultWidth;
    private int innerPadding;
    private int optionsAllSize;

    /**
     * Creates a new option screen.
     *
     * @param configReader The config reader.
     */
    public ConfigScreen(final ConfigReader configReader) {
        super(Text.empty(), SCROLL_HEIGHT);
        this.configReader = configReader;
        this.allCategories.addAll(configReader.getCategories());
        this.visibleCategories.addAll(this.allCategories);
    }

    @Override
    public void render(final DrawContext drawContext, final int mouseX, final int mouseY, final float tickDelta) {
        this.renderBackground(drawContext, mouseX, mouseY, tickDelta);
        super.renderScrollbar(drawContext);
        if (this.selectedCategory == null && !this.visibleCategories.isEmpty()) {
            this.setSelectedCategory(this.visibleCategories.getFirst());
        }

        RenderUtils.renderFilledBox(drawContext,
            this.optionsLeft - 1,
            this.optionsTop - 1,
            this.optionsRight + 1,
            this.optionsBottom + 1);

        this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> {
            final ConfigOptionEditor<?, ?> editor = processedOption.getEditor();

            drawContext.enableScissor(this.optionsLeft - 1,
                this.optionsTop - 1,
                this.optionsRight + 1,
                this.optionsBottom + 1);
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(positionX, positionY, 1);

            final int localMouseX = mouseX - positionX;
            final int localMouseY = mouseY - positionY;
            editor.render(drawContext, localMouseX, localMouseY, tickDelta, optionWidth);
            drawContext.disableScissor();
            drawContext.getMatrices().pop();
        });


        this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> {
            final ConfigOptionEditor<?, ?> editor = processedOption.getEditor();

            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(positionX, positionY, 20);

            final int localMouseX = mouseX - positionX;
            final int localMouseY = mouseY - positionY;
            final int finalLocalMouseX =
                ((mouseY >= this.optionsTop) && (mouseY < this.optionsBottom)) ? localMouseX : -1;

            editor.renderOverlay(drawContext, finalLocalMouseX, localMouseY, tickDelta, optionWidth);
            drawContext.getMatrices().pop();
        });

        this.searchField.render(drawContext, mouseX, mouseY, tickDelta);

        if (this.selectedCategory != null) {
            RenderUtils.renderTextWithMaxWidth(drawContext,
                this.selectedCategory.getName(),
                70,
                this.x + 6,
                this.y + 6,
                -1,
                true);
        }

        if ((mouseY < this.y || mouseY > this.y + 186)) {
            for (final ProcessedCategory allCategory : this.allCategories) {
                final int tabX = this.x + this.getTabX(allCategory);
                final int tabY = this.y + this.getTabY(allCategory);
                if (isInBound(mouseX, mouseY, tabX, tabY, 26, 32)) {
                    if (this.selectedCategory == allCategory) {
                        drawContext.drawTooltip(this.textRenderer, allCategory.getDescription(), mouseX, mouseY);
                    } else {
                        drawContext.drawTooltip(this.textRenderer, allCategory.getName(), mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == InputUtil.GLFW_KEY_DOWN) {
            this.mouseScrolled(this.optionsLeft + 1, this.optionsTop + 1, 0, -1);
        } else if (keyCode == InputUtil.GLFW_KEY_UP) {
            this.mouseScrolled(this.optionsLeft + 1, this.optionsTop + 1, 0, 1);
        }
        if (keyCode == InputUtil.GLFW_KEY_ENTER && this.searchField.active) {
            this.searchField.active = false;
            this.searchField.setFocused(false);
            return true;
        }
        if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
		AtomicBoolean hasBeenConsumed = new AtomicBoolean(false);
        this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> hasBeenConsumed.set(hasBeenConsumed.get() || processedOption.getEditor()
            .keyPressed(keyCode, scanCode, modifiers)));
		if (hasBeenConsumed.get()) {
			return true;
		}
		if (!searchField.isFocused() && MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
			this.close();
			return true;
		}
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        ConfigManager.saveConfig(true, "config-screen");
        super.close();
    }

    @Override
    protected void init() {
        this.searchField = new TextFieldWidget(this.textRenderer, 0, 0, 0, 18, Text.of(""));
        this.searchField.setVisible(true);
        this.searchField.setDrawsBackground(false);
        this.setSelectedCategory(this.allCategories.peekFirst());
        this.executeForEach((processedOption, positionX, positionY, optionWidth) -> processedOption.getEditor().init(),
            true);
        this.resize(MinecraftClient.getInstance(), this.width, this.height);
        this.searchField.setChangedListener(text -> {
            this.repopulateActiveFoldables();
            this.updateSearchResults();
            this.setSearchBarWidth();
            this.repopulateHiddenOptions();
            this.recalculateOptionBarSize();
        });
    }

    @Override
    public void renderBackground(final DrawContext drawContext, final int i, final int j, final float f) {
        for (final ProcessedCategory category : this.configReader.getCategories()) {
            if (this.selectedCategory == category) {
                continue;
            }
            this.renderTabIcon(drawContext, category);
        }

        drawContext.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, this.x, this.y, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 256, 256);

        if (this.selectedCategory != null) {
            this.renderTabIcon(drawContext, this.selectedCategory);
        }
    }

    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final int scaleFactor = (int) MinecraftClient.getInstance().getWindow().getScaleFactor();

        this.x = (width - BACKGROUND_WIDTH) / 2;
        this.y = (height - BACKGROUND_HEIGHT) / 2;

        final int adjustmentFactor = Math.max(2, scaleFactor);

        this.innerPadding = 20 / adjustmentFactor;

        this.optionsTop = this.y + 18;
        this.optionsLeft = this.x + 9;
        this.optionsRight = this.optionsLeft + 160;
        this.optionsBottom = this.optionsTop + 160;

        this.optionDefaultWidth = this.optionsRight - this.optionsLeft + 1;
        this.updateScrollbar(SCROLL_HEIGHT, this.x + 174, this.y + 16);

        this.recalculateOptionBarSize();
        this.setSearchBarWidth();
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (isInBound((int) mouseX,
            (int) mouseY,
            this.searchField.getX() - 3,
            this.searchField.getY(),
            this.searchField.getWidth(),
            this.searchField.getHeight()) && button == 0) {
            this.searchField.setFocused(true);
            this.searchField.active = true;
            return true;
        }

        this.searchField.setFocused(false);
        this.searchField.active = false;

        if ((mouseY < this.y || mouseY > this.y + 186)) {
            for (final ProcessedCategory allCategory : this.allCategories) {
                final int tabX = this.x + this.getTabX(allCategory);
                final int tabY = this.y + this.getTabY(allCategory);
                if (isInBound((int) mouseX, (int) mouseY, tabX, tabY, 26, 32) && button == 0) {
                    this.setSelectedCategory(allCategory);
                    return true;
                }
            }
        }

        if (mouseY > this.optionsTop && mouseY < this.optionsBottom && mouseX > this.optionsLeft &&
            mouseX < this.optionsRight) {
            final AtomicBoolean consumed = new AtomicBoolean(false);
            this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> {
                if (consumed.get()) {
                    return;
                }
                consumed.set(processedOption.getEditor()
                    .mouseClicked(mouseX - positionX, mouseY - positionY, button, optionWidth));
            });
        }

        this.repopulateActiveFoldables();
        this.updateSearchResults();
        this.setSearchBarWidth();
        this.repopulateHiddenOptions();
        this.recalculateOptionBarSize();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
        final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (mouseY > this.optionsTop && mouseY < this.optionsBottom && mouseX > this.optionsLeft &&
            mouseX < this.optionsRight) {
            this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> processedOption.getEditor()
                .mouseDragged(mouseX - positionX, mouseY - positionY, button, deltaX, deltaY, optionWidth));
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /**
     * Executes an operation for all options that are in the current category that are visible and not hidden.
     *
     * @param executor The executor to be called.
     */
    private void executeForEachVisibleNotHidden(final ProcessedOptionExecutor executor) {
        if (this.selectedCategory == null) {
            return;
        }
        int optionsY = -this.scroll;
        for (final ProcessedOption<?, ?> processedOption : this.selectedCategory.getProcessedOptions()) {
            final int optionWidth = this.getOptionSize(processedOption);
            if (optionWidth == -1) {
                continue;
            }
            final ConfigOptionEditor<?, ?> editor = processedOption.getEditor();
            if (editor == null) {
                continue;
            }
            if (this.hiddenOptions.contains(processedOption)) {
                continue;
            }
            if (!processedOption.getOption().isActive()) {
                continue;
            }

            final int finalX = this.optionsLeft - 1 + (((this.optionsRight - this.optionsLeft) - optionWidth) / 2);
            final int finalY = this.optionsTop + optionsY + 1;

            if (((finalY + editor.getHeight(optionWidth)) > (this.optionsTop + 1)) &&
                (finalY < (this.optionsBottom - 1))) {
                executor.execute(processedOption, finalX, finalY, optionWidth + 1);
            }

            optionsY += editor.getHeight(optionWidth);
        }
    }

    /**
     * Gets the size that an option has to be rendered in.
     *
     * @param processedOption The option to get the size for.
     * @return The size the option has to be rendered in.
     */
    private int getOptionSize(final ProcessedOption<?, ?> processedOption) {
        if (processedOption.getFoldable() >= 0) {
            if (!this.activeFoldables.containsKey(processedOption.getFoldable())) {
                return -1;
            }

            final int foldableDepth = this.activeFoldables.get(processedOption.getFoldable());
            return this.optionDefaultWidth - (this.innerPadding * (foldableDepth + 1));
        }

        return this.optionDefaultWidth;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> processedOption.getEditor()
            .mouseReleased(mouseX - positionX, mouseY - positionY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(
        final double mouseX, final double mouseY, final double horizontalAmount, final double verticalAmount) {
        this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> processedOption.getEditor()
            .mouseScrolled(mouseX - positionX, mouseY - positionY, horizontalAmount, verticalAmount));
        if ((mouseY > this.optionsTop) && (mouseY < this.optionsBottom) && (mouseX > this.optionsLeft) &&
            (mouseX < this.optionsRight)) {
            this.updateScrollbar(verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> processedOption.getEditor()
            .keyReleased(keyCode, scanCode, modifiers));
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(final char character, final int modifiers) {
        if (this.searchField.charTyped(character, modifiers)) {
            return true;
        }
		AtomicBoolean consumed = new AtomicBoolean(false);
        this.executeForEachVisibleNotHidden((processedOption, positionX, positionY, optionWidth) -> consumed.set(consumed.get() || processedOption.getEditor()
            .charTyped(character, modifiers)));
		if (consumed.get()) {
			return true;
		}
        return super.charTyped(character, modifiers);
    }

    private void renderTabIcon(@NotNull final DrawContext drawContext, @NotNull final ProcessedCategory category) {
        final boolean active = this.selectedCategory == category;
        final boolean bottom = category.getRow() == Row.BOTTOM;
        final int column = category.getColumn();

        final int tabX = this.x + this.getTabX(category);
        final int tabY = this.y + this.getTabY(category);

        final Identifier[] identifiers =
            bottom ? (active ? TAB_BOTTOM_SELECTED_TEXTURES : TAB_BOTTOM_UNSELECTED_TEXTURES) :
                (active ? TAB_TOP_SELECTED_TEXTURES : TAB_TOP_UNSELECTED_TEXTURES);


        drawContext.drawGuiTexture(RenderLayer::getGuiTextured, identifiers[MathUtils.clamp(column, 0, identifiers.length - 1)], tabX, tabY, 26, 32);

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0, 0, 100);

        final int offset = bottom ? -1 : 1;
        final ItemStack itemStack = category.getItemStack();

        final int itemX = tabX + 5;
        final int itemY = tabY + 8 + offset;

        drawContext.drawItem(itemStack, itemX, itemY);
        drawContext.drawStackOverlay(this.textRenderer, itemStack, itemX, itemY);
        drawContext.getMatrices().pop();
    }

    int getTabX(@NotNull final ProcessedCategory category) {
        final int column = category.getColumn();
        if (category.isSpecial()) {
            return BACKGROUND_WIDTH - ITEM_TAB_WIDTH * (7 - column) + 1;
        }
        return column * ITEM_TAB_WIDTH;
    }

    int getTabY(@NotNull final ProcessedCategory category) {
        return -(((category.getRow() == Row.BOTTOM) ? -(BACKGROUND_HEIGHT - 13) : 28));
    }

    /**
     * Repopulates the active foldable list to reflect the current state.
     */
    private void repopulateActiveFoldables() {
        this.activeFoldables.clear();
        if (this.selectedCategory == null) {
            return;
        }
        for (final ProcessedOption<?, ?> processedOption : this.selectedCategory.getProcessedOptions()) {
            if (processedOption.getFoldable() >= 0) {
                if (!this.activeFoldables.containsKey(processedOption.getFoldable())) {
                    continue;
                }
            }
            final ConfigOptionEditor<?, ?> editor = processedOption.getEditor();
            if (editor == null) {
                continue;
            }
            if (editor instanceof final FoldableEditor foldableEditor) {
                if (foldableEditor.isActive() || !this.searchField.getText().isEmpty()) {
                    int depth = 0;
                    if (processedOption.getFoldable() >= 0) {
                        depth = this.activeFoldables.get(processedOption.getFoldable()) + 1;
                    }
                    this.activeFoldables.put(foldableEditor.getFoldableId(), depth);
                }
            }
        }
    }

    /**
     * Recalculates all variables used to display the option scrollbar.
     */
    private void recalculateOptionBarSize() {

        if (this.selectedCategory == null) {
            this.optionsAllSize = this.optionsViewport - 10;
            return;
        }

        this.optionsAllSize = 0;
        for (final ProcessedOption<?, ?> processedOption : this.selectedCategory.getProcessedOptions()) {
            if ((processedOption.getFoldable() >= 0 &&
                 !this.activeFoldables.containsKey(processedOption.getFoldable())) ||
                this.hiddenOptions.contains(processedOption)) {
                continue;
            }
            if (!processedOption.getOption().isActive()) {
                continue;
            }
            final int optionWidth = this.getOptionSize(processedOption);
            this.optionsAllSize += processedOption.getEditor().getHeight(optionWidth);
        }

        int size = this.optionsAllSize - this.optionsViewport;
        this.scroll = Math.clamp(this.scroll, 0, Math.max(0, size));
        this.updateScroll(size);
    }

    /**
     * Sets the search bar width to a newly calculated value.
     */
    private void setSearchBarWidth() {
        this.searchField.setWidth(90);
        this.searchField.setPosition(this.x + 82, this.y + 6);
        this.searchField.setHeight(12);
    }

    /**
     * Updates the current search results to include/exclude new changes.
     */
    private void updateSearchResults() {
        final String search = this.searchField.getText();
        this.visibleCategories.clear();
        this.visibleCategories.addAll(this.allCategories);
        if (search.isEmpty()) {
            return;
        }
        this.visibleCategories.removeIf(Predicate.<ProcessedCategory>not(processedCategory ->
                processedCategory.getName().getString().contains(search) ||
                processedCategory.getDescription().getString().contains(search) || processedCategory.getProcessedOptions()
                    .stream()
                    .anyMatch(option -> option.getEditor().doesMatchSearch(search.toLowerCase(Locale.ROOT))))
            .and(Predicate.not(ProcessedCategory::isSearch)));
        if (this.visibleCategories.isEmpty()) {
            return;
        }
        if (this.selectedCategory != null && !this.visibleCategories.contains(this.selectedCategory)) {
            this.setSelectedCategory(this.visibleCategories.peekFirst());
        }
    }

    /**
     * Changes the selected category.
     *
     * @param processedCategory The new category.
     */
    private void setSelectedCategory(final ProcessedCategory processedCategory) {
        this.selectedCategory = processedCategory;
        if (this.selectedCategory != null) {
            this.selectedCategory.getProcessedOptions().forEach(processedOption -> processedOption.getEditor().init());
        }

        this.repopulateHiddenOptions();
        this.repopulateActiveFoldables();
        this.recalculateOptionBarSize();
        this.updateSearchResults();
    }

    /**
     * Repopulates a list of all currently hidden options, which have been excluded through the search.
     */
    private void repopulateHiddenOptions() {
        final String search = this.searchField.getText();
        this.hiddenOptions.clear();
        if (search.isEmpty()) {
            return;
        }
        final List<Integer> matchedFoldables = new ArrayList<>();
        if (this.selectedCategory != null) {
            for (final ProcessedOption<?, ?> processedOption : this.selectedCategory.getProcessedOptions()) {
                if (!processedOption.getOption().isActive()) {
                    this.hiddenOptions.add(processedOption);
                    continue;
                }
                if (!processedOption.getEditor().doesMatchSearch(search.toLowerCase(Locale.ROOT)) &&
                    !matchedFoldables.contains(processedOption.getFoldable())) {
                    this.hiddenOptions.add(processedOption);
                } else if (processedOption.getEditor() instanceof final FoldableEditor editor) {
                    matchedFoldables.add(editor.getFoldableId());
                }
                if (processedOption.getParent() != null && !this.hiddenOptions.contains(processedOption)) {
                    this.hiddenOptions.remove(processedOption.getParent());
                }
            }
        }
    }

    /**
     * Executes an operation for all options that are in the current category.
     *
     * @param executor The executor to be called.
     * @param all      If all or only, the visible options should be used.
     */
    private void executeForEach(
        final ProcessedOptionExecutor executor, @SuppressWarnings("SameParameterValue") final boolean all) {
        if (this.selectedCategory == null) {
            return;
        }
        int optionsY = -this.scroll;
        for (final ProcessedOption<?, ?> processedOption : this.selectedCategory.getProcessedOptions()) {
            final int optionWidth = this.getOptionSize(processedOption);
            if (optionWidth == -1) {
                continue;
            }
            final ConfigOptionEditor<?, ?> editor = processedOption.getEditor();
            if (editor == null) {
                continue;
            }

            final int finalX = (this.optionsLeft + this.optionsRight - optionWidth) / 2 - 5;
            final int finalY = this.optionsTop + 5 + optionsY;

            if (all ||
                ((finalY + editor.getHeight(optionWidth) > this.optionsTop + 1) && (finalY < this.optionsBottom - 1))) {
                executor.execute(processedOption, finalX, finalY, optionWidth);
            }


            optionsY += editor.getHeight(optionWidth) + 5;
        }
    }

    @FunctionalInterface
    private interface ProcessedOptionExecutor {

        /**
         * Called for a specific option to provide more generic values.
         *
         * @param processedOption The option.
         * @param positionX       The x position of the option.
         * @param positionY       The y position of the option.
         * @param optionWidth     The width the option has to be rendered in.
         */
        void execute(ProcessedOption<?, ?> processedOption, int positionX, int positionY, int optionWidth);

    }


}
