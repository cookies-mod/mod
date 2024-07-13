package dev.morazzer.cookies.mod.utils.items;

import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Allows for the displaying of items, in another items tooltip.
 */
public class ItemTooltipComponent implements TooltipComponent {

    private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("container/bundle/background");
    private static final Identifier SLOT = Identifier.ofVanilla("container/bundle/slot");
    private final Map<Integer, ItemStack> entries;
    private final int size;

    @SuppressWarnings("MissingJavadoc")
    public ItemTooltipComponent(Map<Integer, ItemStack> entries) {
        final OptionalInt optionalMax = entries.keySet().stream().mapToInt(Integer::valueOf).max();
        if (optionalMax.isEmpty()) {
            this.entries = Collections.emptyMap();
            this.size = 0;
            return;
        }
        this.entries = entries;
        this.size = optionalMax.getAsInt() + 1;
    }

    @Override
    public int getHeight() {
        return getRowsHeight() + 7;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return getColumnsWidth();
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        context.drawGuiTexture(BACKGROUND_TEXTURE, x, y, this.getColumnsWidth(), this.getRowsHeight());

        for (int index = 0; index < this.size; index++) {
            int slotY = index / 9;
            int slotX = index % 9;
            this.drawSlot(slotX * 18 + 1 + x, slotY * 18 + 1 + y, index, context, textRenderer);
        }
    }

    private void drawSlot(
        int x, int y, int index, DrawContext context, TextRenderer textRenderer) {
        if (index >= this.size) {
            this.draw(context, x, y);
        } else {
            ItemStack itemStack = this.entries.getOrDefault(index, ItemStack.EMPTY);
            this.draw(context, x, y);
            context.drawItem(itemStack, x + 1, y + 1, index);
            context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);
        }
    }

    private void draw(DrawContext context, int x, int y) {
        context.drawGuiTexture(SLOT, x, y, 0, 18, 20);
    }

    private int getColumnsWidth() {
        return this.getColumns() * 18 + 2;
    }

    private int getRowsHeight() {
        return this.getRows() * 18 + 4;
    }

    private int getRows() {
        return this.size / this.getColumns() + (this.size % this.getColumns() != 0 ? 1 : 0);
    }

    private int getColumns() {
        return Math.min(this.size, 9);
    }
}
