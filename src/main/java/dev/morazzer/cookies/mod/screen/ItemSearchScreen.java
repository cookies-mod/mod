package dev.morazzer.cookies.mod.screen;

import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.services.ItemSearchService;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;
import dev.morazzer.cookies.mod.utils.minecraft.SoundUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * The screen for the item search feature.
 */
public class ItemSearchScreen extends Screen {
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/creative_inventory/scroller");
    private static final Identifier ITEM_SEARCH_BACKGROUND = Identifier.of("cookies-mod", "textures/gui/search.png");
    private static final int BACKGROUND_WIDTH = 281;
    private static final int BACKGROUND_HEIGHT = 185;
    private static final int SCROLLBAR_OFFSET_X = 262;
    private static final int SCROLLBAR_OFFSET_Y = 18;
    private static final int SCROLLBAR_HEIGHT = 163;
    private static final int SCROLLBAR_HEIGHT_EFFECTIVE = 148;
    private static final int SCROLLBAR_WIDTH = 14;
    private static final int ITEM_CELL = 18;
    private static final int ITEM_ROW = 14;
    private final Map<RepositoryItem, List<ItemSearchService.Context>> contextsMap = new HashMap<>();
    private final List<ItemSearchService.Context> contexts = new ArrayList<>();
    private final List<ItemSearchService.Context> visibleContexts = new ArrayList<>();
    private final List<ItemSearchService.IslandItems> items;
    private final TextFieldWidget searchField;
    private int x;
    private int y;
    private int scroll = 0;
    private int maxScroll = 0;
    private float scrollStep = 0;
    private boolean isScrolling = false;
    private boolean isInDetailsView = false;
    private BiConsumer<List<Text>, ItemSearchService.Context> tooltipAppender = this::appendDefaultTooltips;

    /**
     * Creates a new item search screen.
     */
    public ItemSearchScreen() {
        super(Text.literal("Item Search"));
        this.searchField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 0, 18, Text.of(""));
        final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
        if (currentProfile.isEmpty()) {
            this.items = Collections.emptyList();
            return;
        }

        this.items = new ArrayList<>();
        this.index();
    }

    private void index() {
        this.items.clear();
        this.items.addAll(ItemSearchService.getAllItems());

        this.contextsMap.clear();
        this.items.forEach(this::addItems);
        this.contexts.clear();
        contextsMap.forEach((repositoryItem, context) -> this.contexts.addAll(context));
        this.contexts.sort(Comparator.<ItemSearchService.Context>comparingInt(o -> o.stack().getCount()).reversed());
        this.visibleContexts.clear();
        this.visibleContexts.addAll(contexts);
        this.calculateMaxScroll();
    }

    private void addItems(ItemSearchService.IslandItems islandItems) {
        for (ItemStack stack : islandItems.stacks()) {
            final RepositoryItem data = ItemUtils.getData(stack, CookiesDataComponentTypes.REPOSITORY_ITEM);
            if (data == null) {
                continue;
            }

            final List<ItemSearchService.Context> existingContexts =
                contextsMap.computeIfAbsent(data, repositoryItem -> new ArrayList<>());

            boolean found = false;
            for (ItemSearchService.Context context : existingContexts) {
                if (ItemSearchService.isSame(stack, context.stack())) {
                    boolean added = false;
                    for (ItemSearchService.Block block : context.blocks()) {
                        if (block.blocks().equals(islandItems.blockPos())) {
                            block.count().addAndGet(stack.getCount());
                            added = true;
                        }
                    }
                    if (!added) {
                        context.blocks()
                            .add(new ItemSearchService.Block(islandItems.blockPos(),
                                new AtomicInteger(stack.getCount())));
                    }
                    context.stack().setCount(context.stack().getCount() + stack.getCount());
                    found = true;
                }
            }

            if (found) {
                continue;
            }

            final Set<ItemSearchService.Block> blocks = new HashSet<>();
            blocks.add(new ItemSearchService.Block(islandItems.blockPos(), new AtomicInteger(stack.getCount())));

            final ItemStack copy = stack.copy();
            copy.remove(CookiesDataComponentTypes.DONATED_MUSEUM);
            copy.remove(CookiesDataComponentTypes.TIMESTAMP);
            existingContexts.add(new ItemSearchService.Context(copy, blocks));
        }
    }

    private void calculateMaxScroll() {
        this.maxScroll = Math.max(0, this.visibleContexts.size() / ITEM_ROW - 8);
        this.scrollStep = (float) SCROLLBAR_HEIGHT_EFFECTIVE / Math.max(this.maxScroll, 1);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

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
            "Chest Search",
            this.x + 6,
            this.y + 6,
            0xFF555555,
            false);

        int scrollBarX = this.x + SCROLLBAR_OFFSET_X - 1;
        int scrollBarY = this.y + SCROLLBAR_OFFSET_Y - 1;
        context.drawGuiTexture(SCROLLER_TEXTURE,
            scrollBarX,
            MathUtils.clamp(scrollBarY + (int) (this.scroll * this.scrollStep),
                scrollBarY,
                scrollBarY + SCROLLBAR_HEIGHT_EFFECTIVE),
            SCROLLBAR_WIDTH,
            15);

        this.drawItems(context, mouseX, mouseY);
        this.searchField.render(context, mouseX, mouseY, delta);
    }

    private void drawItems(DrawContext context, int mouseX, int mouseY) {
        int index = 0;
        final int offset = ITEM_ROW * this.getScroll();
        for (int i = ITEM_ROW * this.getScroll(); i < this.visibleContexts.size(); i++) {
            ItemSearchService.Context itemContext = this.visibleContexts.get(i);
            int slotX = ((i - offset) % ITEM_ROW) * ITEM_CELL + this.x + 7;
            int slotY = ((i - offset) / ITEM_ROW) * ITEM_CELL + this.y + 18;

            context.drawItem(itemContext.stack(), slotX, slotY);
            context.getMatrices().push();
            context.getMatrices().scale(0.9f, 0.9f, 1f);
            context.getMatrices().translate(1.2, 1.2, 0);
            context.drawItemInSlot(MinecraftClient.getInstance().textRenderer,
                itemContext.stack(),
                (int) (slotX / 0.9f),
                (int) (slotY / 0.9f),
                NumberFormat.getCompactNumberInstance().format(itemContext.stack().getCount()));
            context.getMatrices().pop();
            if (mouseX > slotX && mouseY > slotY && mouseX < slotX + 16 && mouseY < slotY + 16) {
                HandledScreen.drawSlotHighlight(context, slotX, slotY, 100);
                final List<Text> tooltipFromItem =
                    Screen.getTooltipFromItem(MinecraftClient.getInstance(), itemContext.stack());
                this.tooltipAppender.accept(tooltipFromItem, itemContext);

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

    private int getScroll() {
        return Math.min(this.maxScroll, scroll);
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
            if (this.isInDetailsView) {
                this.index();
                this.isInDetailsView = false;
                return true;
            }
            this.close();
            return true;
        }
        if (keyCode == InputUtil.GLFW_KEY_ESCAPE && this.isInDetailsView) {
            this.index();
            this.isInDetailsView = false;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        super.init();
        this.resize(MinecraftClient.getInstance(),
            MinecraftClient.getInstance().getWindow().getScaledWidth(),
            MinecraftClient.getInstance().getWindow().getScaledHeight());
        this.searchField.setVisible(true);
        this.searchField.setDrawsBackground(false);
        this.searchField.setChangedListener(this::updateSearch);
        this.searchField.setWidth(90);
        this.searchField.setHeight(12);
    }

    private void updateSearch(String search) {
        this.visibleContexts.clear();
        this.visibleContexts.addAll(this.contexts);
        if (search.isBlank()) {
            return;
        }
        this.visibleContexts.removeIf(context -> doesMatch(context, search));
        this.calculateMaxScroll();
    }

    private boolean doesMatch(ItemSearchService.Context context, String search) {
        final ItemStack stack = context.stack();
        if (stack.getName().getString().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
            return false;
        }

        final LoreComponent data = ItemUtils.getData(stack, DataComponentTypes.LORE);
        if (data == null || data.lines().isEmpty()) {
            return true;
        }

        for (Text text : data.lines()) {
            if (text.getString()
                .replaceAll("(?i)§[a-f0-9klmnor]", "")
                .toLowerCase(Locale.ROOT)
                .contains(search.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.x = width / 2 - BACKGROUND_WIDTH / 2;
        this.y = height / 2 - BACKGROUND_HEIGHT / 2;
        this.searchField.setPosition(this.x + 169, this.y + 6);
        this.calculateMaxScroll();
    }

    private void appendDefaultTooltips(List<Text> tooltip, ItemSearchService.Context context) {
        tooltip.add(Text.empty());
        final MutableText literal =
            Text.literal(NumberFormat.getIntegerInstance(Locale.ENGLISH).format(context.stack().getCount()));
        tooltip.add(Text.literal("Stored: ").append(literal.formatted(Formatting.YELLOW)).formatted(Formatting.GRAY));
        int chests = context.blocks().size();
        final MutableText chestsText = Text.literal(NumberFormat.getIntegerInstance(Locale.ENGLISH).format(chests));
        tooltip.add(Text.literal("Chests: ")
            .append(chestsText.formatted(Formatting.YELLOW))
            .formatted(Formatting.GRAY));

        tooltip.add(Text.empty());
        tooltip.add(Text.literal("Left-click to highlight all chests!").formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("Right-click to view chests!").formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("Shift right-click to remove!").formatted(Formatting.RED));
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

        if (isInBound((int) mouseX,
            (int) mouseY,
            this.x + SCROLLBAR_OFFSET_X,
            this.y + SCROLLBAR_OFFSET_Y,
            SCROLLBAR_WIDTH,
            SCROLLBAR_HEIGHT)) {
            this.isScrolling = true;
            return true;
        }

        if (isInBound((int) mouseX, (int) mouseY, this.x + 6, this.y + 17, 252, 162)) {
            double localX = mouseX - (this.x + 6);
            double localY = mouseY - (this.y + 17);

            int slotX = (int) (localX / ITEM_CELL);
            int slotY = (int) (localY / ITEM_CELL);

            final int offset = ITEM_ROW * this.getScroll();
            int index = slotY * ITEM_ROW + slotX + offset;
            if (index < this.visibleContexts.size()) {
                final ItemSearchService.Context context = this.visibleContexts.get(index);
                this.clickedSlot(context, button);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isScrolling) {
            int start = this.y + SCROLLBAR_OFFSET_Y;
            int end = start + SCROLLBAR_HEIGHT;
            this.scroll = (int) (((mouseY - start - 7.5f) / (end - start - 15.0f)) * this.maxScroll);
            this.scroll = MathUtils.clamp(this.scroll, 0, this.maxScroll);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX > this.x && mouseY > this.y && mouseX < this.x + BACKGROUND_WIDTH &&
            mouseY < this.y + BACKGROUND_HEIGHT) {
            this.scroll = MathUtils.clamp((int) (this.scroll - verticalAmount), 0, this.maxScroll);
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

    private boolean isInBound(
        final int x, final int y, final int regionX, final int regionY, final int regionWidth, final int regionHeight) {
        return (x >= regionX && x <= regionX + regionWidth) && (y >= regionY && y <= regionY + regionHeight);
    }

    private void clickedSlot(ItemSearchService.Context context, int button) {
        if (button == 0) {
            ItemSearchService.highlight(context);
            close();
            SoundUtils.playSound(SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON, 2, 1);
        } else if (button == 1) {
            if (Screen.hasShiftDown()) {
                this.removeChest(context);
                this.index();
                SoundUtils.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2, 1);
            }
            if (!this.isInDetailsView) {
                this.showDetails(context);
                SoundUtils.playSound(SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON, 2, 1);
            }
        }
    }

    private void removeChest(ItemSearchService.Context context) {
        context.blocks()
            .stream()
            .map(ItemSearchService.Block::blocks)
            .map(ItemSearchService.BiBlockPosKey::first)
            .forEach(ItemSearchService::chestBreak);
    }

    private void showDetails(ItemSearchService.Context context) {
        this.isInDetailsView = true;
        List<ItemSearchService.Context> newList = new ArrayList<>();
        for (ItemSearchService.Block block : context.blocks()) {
            final ItemStack itemStack = context.stack().copyWithCount(block.count().get());
            itemStack.remove(CookiesDataComponentTypes.DONATED_MUSEUM);
            itemStack.remove(CookiesDataComponentTypes.TIMESTAMP);
            newList.add(new ItemSearchService.Context(itemStack, Set.of(block)));
        }
        this.visibleContexts.clear();
        this.visibleContexts.addAll(newList);
        this.calculateMaxScroll();
        this.tooltipAppender = this::appendChestTooltips;
    }

    private void appendChestTooltips(List<Text> tooltip, ItemSearchService.Context stack) {
        tooltip.add(Text.empty());
        final ItemSearchService.Block block = stack.blocks().stream().findFirst().orElseThrow();
        final MutableText formatted = Text.literal("Chest location: ").formatted(Formatting.YELLOW);
        tooltip.add(formatted.append(getFormattedCoordinates(block.blocks().first())));
        tooltip.add(Text.empty());
        tooltip.add(Text.literal("Left-click to highlight chest!").formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("Shift right-click to remove!").formatted(Formatting.RED));
    }

    private Text getFormattedCoordinates(BlockPos pos) {
        return Text.literal("x: %s, y: %s, z: %s".formatted(pos.getX(), pos.getY(), pos.getZ()))
            .formatted(Formatting.GRAY);
    }

}
