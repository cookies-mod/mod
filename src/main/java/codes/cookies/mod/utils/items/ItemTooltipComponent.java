package codes.cookies.mod.utils.items;

import codes.cookies.mod.utils.RenderUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import org.joml.Matrix4f;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Allows for the displaying of items, in another items tooltip.
 */
public class ItemTooltipComponent implements TooltipComponent {

	private static final Identifier SLOT = Identifier.of("cookies-mod", "textures/gui/slot.png");
	private final Map<Integer, ItemStack> entries;
	private final int size;
	private final int amountInRow;
	private List<OrderedTextTooltipComponent> tooltip;

	@SuppressWarnings("MissingJavadoc")
	public ItemTooltipComponent(Map<Integer, ItemStack> entries) {
		this(entries, 9, Collections.emptyList());
	}

	public ItemTooltipComponent(Map<Integer, ItemStack> entries, int columnsInRow, List<Text> tooltip) {
		this.amountInRow = columnsInRow;
		final OptionalInt optionalMax = entries.keySet().stream().mapToInt(Integer::valueOf).max();
		if (optionalMax.isEmpty()) {
			this.entries = Collections.emptyMap();
			this.size = 0;
			return;
		}
		this.entries = entries;
		this.size = optionalMax.getAsInt() + 1;
		this.tooltip = tooltip.stream().map(Text::asOrderedText).map(OrderedTextTooltipComponent::new).toList();
	}

	public ItemTooltipComponent(Iterable<ItemStack> entries, int columnsInRow, List<Text> tooltip) {
		this(toMap(entries), columnsInRow, tooltip);
	}

	private static Map<Integer, ItemStack> toMap(Iterable<ItemStack> entries) {
		Map<Integer, ItemStack> map = new HashMap<>();
		int index = 0;
		for (ItemStack entry : entries) {
			map.put(index++, entry);
		}
		return map;
	}

	@Override
	public int getHeight(TextRenderer textRenderer) {
		return getRowsHeight() + 7 + getTotalTextHeight(textRenderer);
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return Math.max(getColumnsWidth(), getTextWidth(textRenderer));
	}

	@Override
	public void drawText(
			TextRenderer textRenderer,
			int x,
			int y,
			Matrix4f matrix,
			VertexConsumerProvider.Immediate vertexConsumers) {
		int newY = y;
		for (OrderedTextTooltipComponent orderedTextTooltipComponent : tooltip) {
			orderedTextTooltipComponent.drawText(textRenderer, x, newY, matrix, vertexConsumers);
			newY += orderedTextTooltipComponent.getHeight(textRenderer);
		}
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
		y = y + getTotalTextHeight(textRenderer);
		RenderUtils.renderBackgroundBox(context, x,y, this.getColumnsWidth(), this.getRowsHeight(), -1);

		for (int index = 0; index < this.size; index++) {
			int slotY = index / amountInRow;
			int slotX = index % amountInRow;
			this.drawSlot(slotX * 18 + 4 + 1 + x, slotY * 18 + 4 + 1 + y, index, context, textRenderer);
		}
	}

	private void drawSlot(int x, int y, int index, DrawContext context, TextRenderer textRenderer) {
		if (index >= this.size) {
			this.draw(context, x, y);
		} else {
			ItemStack itemStack = this.entries.getOrDefault(index, ItemStack.EMPTY);
			this.draw(context, x, y);

			final Integer data = ItemUtils.getData(itemStack, CookiesDataComponentTypes.ITEM_BACKGROUND_COLOR);
			if (data != null) {
				context.fill(x + 1, y + 1, x + 17, y + 17, data);
			}

			context.drawItem(itemStack, x + 1, y + 1, index);
			context.drawStackOverlay(textRenderer, itemStack, x + 1, y + 1);
		}
	}

	private void draw(DrawContext context, int x, int y) {
		context.drawTexture(RenderLayer::getGuiTextured, SLOT, x, y, 0, 0, 18, 18, 18, 18);
	}

	private int getColumnsWidth() {
		return this.getColumns() * 18 + 10;
	}

	public int getTextWidth(TextRenderer textRenderer) {
		return tooltip.stream().mapToInt(component -> component.getWidth(textRenderer)).max().orElse(0);
	}

	private int getRowsHeight() {
		return this.getRows() * 18 + 10;
	}

	public int getTotalTextHeight(TextRenderer textRenderer) {
		return tooltip.stream().mapToInt(value -> value.getHeight(textRenderer)).sum();
	}

	private int getRows() {
		return this.size / this.getColumns() + (this.size % this.getColumns() != 0 ? 1 : 0);
	}

	private int getColumns() {
		return Math.min(this.size, amountInRow);
	}
}
