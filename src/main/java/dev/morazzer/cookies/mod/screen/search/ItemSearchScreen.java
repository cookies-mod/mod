package dev.morazzer.cookies.mod.screen.search;

import dev.morazzer.cookies.mod.config.screen.TabConstants;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemCompound;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.generated.utils.ItemAccessor;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.screen.ScrollbarScreen;
import dev.morazzer.cookies.mod.services.ItemSearchService;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.CookiesUtils;
import dev.morazzer.cookies.mod.utils.TextUtils;
import dev.morazzer.cookies.mod.utils.accessors.InventoryScreenAccessor;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;
import dev.morazzer.cookies.mod.utils.minecraft.SoundUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * The screen for the item search feature.
 */
public class ItemSearchScreen extends ScrollbarScreen implements InventoryScreenAccessor, TabConstants {
    private static final Identifier ITEM_SEARCH_BACKGROUND = Identifier.of("cookies-mod", "textures/gui/search.png");

    private static final int ITEM_CELL = 18;
    private static final int ITEM_ROW = 14;

    private static final int BACKGROUND_WIDTH = 281;
    private static final int BACKGROUND_HEIGHT = 185;

    private static final int SCROLLBAR_OFFSET_X = 261;
    private static final int SCROLLBAR_OFFSET_Y = 17;
    private static final int SCROLLBAR_HEIGHT = 163;
    private final List<Disabled> disableds = new ArrayList<>();
    private final ArrayList<ItemCompound> items = new ArrayList<>();
    private final List<ItemCompound> finalItems = new ArrayList<>();
    private final TextFieldWidget searchField;
    private int x;
    private int y;
    private String lastSearch = "";
    private ItemSourceCategories itemSourceCategories = ItemSourceCategories.ALL;
    private ItemSearchCategories itemSearchCategories = ItemSearchCategories.ALL;

    public ItemSearchScreen() {
        super(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH), 163);
        this.searchField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 0, 18, Text.of(""));


        this.addAllItems();
    }

    private void addAllItems() {
        this.buildItemIndex();
        this.finalItems.addAll(this.items);
        this.updateMaxScroll();
    }

    private void buildItemIndex() {
        this.items.clear();
        ItemSources.getItems(this.itemSourceCategories.getSources()).forEach(this::addItem);
        this.items.removeIf(Predicate.not(itemCompound -> this.itemSearchCategories.getItemPredicate()
            .test(ItemAccessor.repositoryItemOrNull(itemCompound.itemStack()))));
        this.items.sort((Comparator.comparingInt(ItemCompound::amount)).reversed());
    }

    private void updateMaxScroll() {
        this.updateScroll(Math.max(0, this.finalItems.size() / ITEM_ROW - 8));
    }

    private void addItem(Item<?> item) {
        final RepositoryItem repositoryItem = ItemAccessor.repositoryItemOrNull(item.itemStack());
        if (repositoryItem == null) {
            return;
        }

        final ItemCompound compound = this.items.stream()
            .filter(itemCompound -> ItemSearchService.isSame(itemCompound.itemStack(), item.itemStack()))
            .findFirst()
            .orElse(new ItemCompound(item));
        compound.add(item);
        this.items.remove(compound);
        this.items.add(compound);
    }

    @Override
    public int cookies$getBackgroundWidth() {
        return BACKGROUND_WIDTH;
    }

    @Override
    public int cookies$getBackgroundHeight() {
        return BACKGROUND_HEIGHT;
    }

    @Override
    public int cookies$getX() {
        return this.x;
    }

    @Override
    public int cookies$getY() {
        return this.y;
    }

    @Override
    public List<Disabled> cookies$getDisabled() {
        return this.disableds;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTopTabs(context, false);
        this.renderBottomTabs(context, false);

        context.drawTexture(ITEM_SEARCH_BACKGROUND,
            this.x,
            this.y,
            0,
            0,
            BACKGROUND_WIDTH,
            BACKGROUND_HEIGHT,
            BACKGROUND_WIDTH,
            BACKGROUND_HEIGHT);

        context.drawText(MinecraftClient.getInstance().textRenderer,
            "Item Search",
            this.x + 6,
            this.y + 6,
            0xFF555555,
            false);

        renderScrollbar(context);
        this.drawItems(context, mouseX, mouseY);
        this.searchField.render(context, mouseX, mouseY, delta);
        this.renderTopTabs(context, true);
        this.renderBottomTabs(context, true);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputUtil.GLFW_KEY_ENTER && this.searchField.active) {
            this.searchField.active = false;
            this.searchField.setFocused(false);
            return true;
        }
        if (this.searchField.keyPressed(keyCode, scanCode, modifiers) || this.searchField.isActive()) {
            return true;
        }
        if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        super.init();
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            this.close();
            CookiesUtils.sendFailedMessage("No currently active profile");
            return;
        }
        this.resize(MinecraftClient.getInstance(),
            MinecraftClient.getInstance().getWindow().getScaledWidth(),
            MinecraftClient.getInstance().getWindow().getScaledHeight());
        this.searchField.setVisible(true);
        this.searchField.setDrawsBackground(false);
        this.searchField.setChangedListener(this::updateSearch);
        this.searchField.setWidth(90);
        this.searchField.setHeight(12);
    }

    private void updateSearch(String s) {
        this.finalItems.clear();
        this.finalItems.addAll(this.items);
        lastSearch = s;
        this.finalItems.removeIf(Predicate.not(this.matches(s)));
        this.updateMaxScroll();
        this.scroll = 0;
    }

    private Predicate<? super ItemCompound> matches(String s) {
        return item -> item.itemStack()
            .getName()
            .getString()
            .toLowerCase(Locale.ROOT)
            .contains(s.toLowerCase(Locale.ROOT));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.x = width / 2 - BACKGROUND_WIDTH / 2;
        this.y = height / 2 - BACKGROUND_HEIGHT / 2;
        this.searchField.setPosition(this.x + 169, this.y + 6);
        this.updateScrollbar(SCROLLBAR_HEIGHT, this.x + SCROLLBAR_OFFSET_X, this.y + SCROLLBAR_OFFSET_Y);
        this.height = height;
        this.width = width;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isInBound((int) mouseX,
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

        if (isInBound((int) mouseX, (int) mouseY, this.x + 6, this.y + 17, 252, 162)) {
            double localX = mouseX - (this.x + 6);
            double localY = mouseY - (this.y + 17);

            int slotX = (int) (localX / ITEM_CELL);
            int slotY = (int) (localY / ITEM_CELL);

            final int offset = ITEM_ROW * this.scroll;
            int index = slotY * ITEM_ROW + slotX + offset;
            if (index < this.finalItems.size()) {
                final ItemCompound compound = this.finalItems.get(index);
                this.clickedSlot(compound, button);
                return true;
            }
        }

        if (this.mouseClickedTop(mouseX, mouseY, button) || this.mouseClickedBottom(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isInBound((int) mouseX, (int) mouseY, this.x, this.y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT)) {
            updateScrollbar(verticalAmount);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.searchField.isFocused() && this.searchField.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    private void clickedSlot(ItemCompound compound, int button) {
        if (button == 0) {
            ItemSearchService.highlight(compound);
            close();
            SoundUtils.playSound(SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON, 2, 1);
        }
    }

    private void updateInventory() {
        this.buildItemIndex();
        this.updateSearch(this.lastSearch);
        this.updateMaxScroll();
    }

    private boolean mouseClickedBottom(double mouseX, double mouseY, int button) {
        for (int i = 0; i < ItemSourceCategories.VALUES.length; i++) {
            if (this.isInBound((int) mouseX,
                (int) mouseY,
                this.x + this.getTabX(i),
                this.y + this.getTabY(false),
                ITEM_TAB_WIDTH,
                ITEM_TAB_HEIGHT)) {
                this.itemSourceCategories = ItemSourceCategories.VALUES[i];
                this.updateInventory();
                SoundUtils.playSound(SoundEvents.BLOCK_LEVER_CLICK, 2, 1);
                return true;
            }
        }
        return false;
    }

    private boolean mouseClickedTop(double mouseX, double mouseY, int button) {
        for (int i = 0; i < ItemSearchCategories.VALUES.length; i++) {
            if (this.isInBound((int) mouseX,
                (int) mouseY,
                this.x + this.getTabX(i),
                this.y + this.getTabY(true),
                ITEM_TAB_WIDTH,
                ITEM_TAB_HEIGHT)) {
                this.itemSearchCategories = ItemSearchCategories.VALUES[i];
                this.updateInventory();
                SoundUtils.playSound(SoundEvents.BLOCK_LEVER_CLICK, 2, 1);
                return true;
            }
        }

        return false;
    }

    private void drawItems(DrawContext context, int mouseX, int mouseY) {
        int index = 0;
        final int offset = ITEM_ROW * this.scroll;
        for (int i = ITEM_ROW * this.scroll; i < this.finalItems.size(); i++) {
            ItemCompound itemContext = this.finalItems.get(i);
            int slotX = ((i - offset) % ITEM_ROW) * ITEM_CELL + this.x + 7;
            int slotY = ((i - offset) / ITEM_ROW) * ITEM_CELL + this.y + 18;

            context.drawItem(itemContext.itemStack(), slotX, slotY);
            context.getMatrices().push();
            context.getMatrices().scale(0.5f, 0.5f, 1f);
            context.getMatrices().translate(15, 15, 0);
            context.drawItemInSlot(MinecraftClient.getInstance().textRenderer,
                itemContext.itemStack(),
                (int) (slotX / 0.5f),
                (int) (slotY / 0.5f),
                NumberFormat.getCompactNumberInstance(Locale.ENGLISH, NumberFormat.Style.SHORT).format(itemContext.amount()));
            context.getMatrices().pop();
            if (mouseX > slotX && mouseY > slotY && mouseX < slotX + 16 && mouseY < slotY + 16) {
                HandledScreen.drawSlotHighlight(context, slotX, slotY, 100);
                final List<Text> tooltipFromItem =
                    Screen.getTooltipFromItem(MinecraftClient.getInstance(), itemContext.itemStack());
                this.appendTooltip(tooltipFromItem, itemContext);

                context.drawTooltip(MinecraftClient.getInstance().textRenderer,
                    tooltipFromItem,
                    Optional.empty(),
                    mouseX,
                    mouseY);
            }
            if (++index >= 9 * ITEM_ROW) {
                break;
            }
        }
    }

    private void renderTopTabs(DrawContext context, boolean onlyActive) {
        for (int i = 0; i < ItemSearchCategories.VALUES.length; i++) {
            final ItemSearchCategories value = ItemSearchCategories.VALUES[i];
            final boolean isActive = value == this.itemSearchCategories;
            if (onlyActive && !isActive) {
                continue;
            }
            this.renderTab(context, true, isActive, i, value.getDisplay(), value.getName());
        }
    }

    private void renderBottomTabs(DrawContext context, boolean onlyActive) {
        for (int i = 0; i < ItemSourceCategories.VALUES.length; i++) {
            ItemSourceCategories value = ItemSourceCategories.VALUES[i];
            final boolean isActive = value == this.itemSourceCategories;
            if (onlyActive && !isActive) {
                continue;
            }
            this.renderTab(context, false, isActive, i, value.getDisplay(), value.getName());
        }
    }

    private void appendTooltip(List<Text> tooltip, ItemCompound itemCompound) {
        tooltip.add(Text.empty());

        int storage = 0, sacks = 0, chests = 0;

        for (Item<?> item : itemCompound.items()) {
            if (!this.itemSourceCategories.has(item.source())) {
                continue;
            }
            switch (item.source()) {
                case STORAGE -> storage += item.amount();
                case SACKS -> sacks += item.amount();
                case CHESTS -> chests += item.amount();
            }
        }

        if (this.itemSourceCategories.has(ItemSources.STORAGE) && storage > 0) {
            tooltip.add(ItemSources.STORAGE.getName().copy().formatted(Formatting.GRAY).append(formattedText(storage)));
        }
        if (this.itemSourceCategories.has(ItemSources.SACKS) && sacks > 0) {
            tooltip.add(ItemSources.SACKS.getName().copy().formatted(Formatting.GRAY).append(formattedText(sacks)));
        }
        if (this.itemSourceCategories.has(ItemSources.CHESTS) && chests > 0) {
            tooltip.add(ItemSources.CHESTS.getName().copy().formatted(Formatting.GRAY).append(formattedText(chests)));
        }
        if (this.itemSourceCategories.getSources().length > 1 &&
            ((storage != 0 && (sacks != 0 || chests != 0)) || (sacks != 0 && chests != 0))) {
            tooltip.add(TextUtils.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_TOTAL, Formatting.GRAY)
                .append(formattedText(storage + sacks + chests)));
        }

        tooltip.add(Text.empty());
        tooltip.add(Text.translatable(TranslationKeys.SCREEN_ITEM_SEARCH_CLICK_TO_HIGHLIGHT)
            .formatted(Formatting.YELLOW));
        //tooltip.add(Text.literal("Right-click to view chests!").formatted(Formatting.YELLOW));
        //tooltip.add(Text.literal("Shift right-click to remove!").formatted(Formatting.RED));
    }

    private void renderTab(
        DrawContext context, boolean top, boolean active, int index, ItemStack itemStack, Text name) {
        final Identifier[] identifiers = top ? (active ? TAB_TOP_SELECTED_TEXTURES : TAB_TOP_UNSELECTED_TEXTURES) :
            (active ? TAB_BOTTOM_SELECTED_TEXTURES : TAB_BOTTOM_UNSELECTED_TEXTURES);
        int tabX = this.x + getTabX(index);
        int tabY = this.y + getTabY(top);

        context.drawGuiTexture(identifiers[MathUtils.clamp(index, 0, identifiers.length - 1)], tabX, tabY, 26, 32);

        final int offset = top ? 1 : -1;

        final int itemX = tabX + 5;
        final int itemY = tabY + 8 + offset;

        context.drawItem(itemStack, itemX, itemY);
        context.drawItemInSlot(this.textRenderer, itemStack, itemX, itemY);
    }

    private MutableText formattedText(int amount) {
        return Text.literal(": ")
            .append(NumberFormat.getIntegerInstance(Locale.ENGLISH).format(amount))
            .formatted(Formatting.YELLOW);
    }

    int getTabX(int index) {
        return index * ITEM_TAB_WIDTH;
    }

    int getTabY(boolean top) {
        return -(((top) ? 28 : -(BACKGROUND_HEIGHT - 4)));
    }
}
