package dev.morazzer.cookies.mod.screen.search;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.data.profile.items.Item;
import dev.morazzer.cookies.mod.data.profile.items.ItemCompound;
import dev.morazzer.cookies.mod.data.profile.items.sources.StorageItemSource;
import dev.morazzer.cookies.mod.data.profile.sub.StorageData;
import dev.morazzer.cookies.mod.services.ItemSearchService;
import dev.morazzer.cookies.mod.utils.TextUtils;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.skyblock.InventoryUtils;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ClientSideInventory;

import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.util.function.Consumer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.lwjgl.glfw.GLFW;

public class InspectItemScreen extends ClientSideInventory {

	private static final Position LEFT_ARROW = new Position(5, 0);
	private static final Position RIGHT_ARROW = new Position(5, 8);
	private static final int ROWS = 6;
	private final ItemSearchScreen itemSearchScreen;

	public InspectItemScreen(ItemCompound compound, ItemSearchScreen itemSearchScreen) {
		super(compound.itemStack().getName(), ROWS);
		this.itemSearchScreen = itemSearchScreen;
		super.initPagination(compound.items()
				.stream()
				.sorted(Comparator.<Item<?>>comparingInt(item -> item.source().ordinal())
						.thenComparingInt(this::ordered))
				.map(this::modifyItem)
				.toList(), new Position(1, 1), new Position(4, 7), null);

		this.inventoryContents.fill(new ItemBuilder(Items.BLACK_STAINED_GLASS_PANE).hideAdditionalTooltips()
				.hideTooltips()
				.build());

		this.inventoryContents.set(new Position(5, 4),
				new ItemBuilder(Items.ARROW).setName("Back to overview")
						.setClickRunnable(InventoryUtils.wrapWithSound(this::backToOverview))
						.build());
		super.drawBackground = false;
	}

	private void backToOverview() {
		this.itemSearchScreen.updateInventory();
		CookiesMod.openScreen(this.itemSearchScreen);
	}

	private int ordered(Item<?> item) {
		return switch (item.source()) {
			case STORAGE -> {
				final StorageItemSource.Context data = (StorageItemSource.Context) item.data();
				yield (data.location() == StorageData.StorageLocation.BACKPACK ? 9 : 0) + data.page() + 1;
			}
			case INVENTORY -> ((int) item.data());
			default -> 0;
		};
	}

	private ItemStack modifyItem(Item<?> item) {
		final ItemStack copy = item.itemStack().copy();

		final LoreComponent loreComponent = copy.get(DataComponentTypes.LORE);
		List<Text> lore;
		if (loreComponent != null) {
			lore = new ArrayList<>(loreComponent.lines());
		} else {
			lore = new ArrayList<>();
		}

		lore.add(Text.empty());

		final Object data = item.data();

		final ItemCompound.CompoundType compoundType = ItemCompound.CompoundType.of(item.source(), data);
		ItemSearchService.appendMultiTooltip(compoundType, data, lore);
		lore.add(Text.translatable(SCREEN_ITEM_SEARCH_REMOVE_FROM_CACHE).formatted(Formatting.RED));
		copy.set(CookiesDataComponentTypes.CUSTOM_LORE, lore);

		copy.set(CookiesDataComponentTypes.ITEM_CLICK_CONSUMER,
				InventoryUtils.wrapWithSound(this.getItemClickConsumer(item, copy)));

		return copy;
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.itemSearchScreen.render(context, 0, 0, delta);
		this.renderInGameBackground(context);
		super.applyBlur(delta);
		super.renderBackground(context, mouseX, mouseY, delta);
	}

	private Consumer<Integer> getItemClickConsumer(Item<?> item, ItemStack copy) {
		return button -> this.onItemClick(item, copy, button);
	}

	private void onItemClick(Item<?> item, ItemStack copy, Integer button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			item.source().getItemSource().remove(item);
			this.pagination.removeItem(copy);
		} else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			ItemSearchService.performAction(
					ItemCompound.CompoundType.of(item.source(), item.data()),
					item.data(),
					item);
			this.close();
		}
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	protected void paginationUpdate() {
		super.paginationUpdate();
		if (this.pagination.hasPreviousPage()) {
			this.inventoryContents.set(LEFT_ARROW,
					new ItemBuilder(Items.ARROW).setName(TextUtils.translatable(PAGE_PREVIOUS, Formatting.GREEN))
							.setLore(Text.translatable(PAGE_WITH_NUMBER, this.pagination.getCurrentPage() - 1)
									.formatted(Formatting.YELLOW))
							.setClickRunnable(InventoryUtils.wrapWithSound(this.pagination::previousPage))
							.build());
		} else {
			this.inventoryContents.set(LEFT_ARROW, outline);
		}
		if (this.pagination.hasNextPage()) {
			this.inventoryContents.set(RIGHT_ARROW,
					new ItemBuilder(Items.ARROW).setName(TextUtils.translatable(PAGE_NEXT, Formatting.GREEN))
							.setLore(Text.translatable(PAGE_WITH_NUMBER, this.pagination.getCurrentPage() + 1)
									.formatted(Formatting.YELLOW))
							.setClickRunnable(InventoryUtils.wrapWithSound(this.pagination::nextPage))
							.build());
		} else {
			this.inventoryContents.set(RIGHT_ARROW, outline);
		}

		this.inventoryTitle = Text.translatable(SCREEN_ITEM_SEARCH_OVERVIEW_TITLE,
						this.pagination.getCurrentPage(),
						this.pagination.getMaxPage())
				.formatted(Formatting.DARK_GRAY);
	}
}