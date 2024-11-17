package codes.cookies.mod.utils.skyblock.inventories;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.RenderUtils;
import codes.cookies.mod.utils.accessors.InventoryScreenAccessor;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import codes.cookies.mod.utils.items.ItemUtils;
import lombok.AccessLevel;
import lombok.Getter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Allows for creation of "inventories" that do exactly look like normal ones.
 * They actually just act like screens though.
 */
public class ClientSideInventory extends Screen implements InventoryScreenAccessor, TranslationKeys {

	protected static final ItemStack outline =
			new ItemBuilder(Items.BLACK_STAINED_GLASS_PANE).hideAdditionalTooltips().hideTooltips().build();
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
	private static final int BACKGROUND_WIDTH = 176;
	protected final InventoryContents inventoryContents;
	private final int rows;
	private final List<Disabled> disableds = new ArrayList<>();
	private final int backgroundHeight;
	@Getter
	private final Slot[] slots;
	private final Slot[] playerInventorySlots;
	protected boolean drawBackground = true;
	protected Pagination pagination;
	protected Text inventoryTitle;
	@Getter(AccessLevel.PROTECTED)
	private int x;
	@Getter(AccessLevel.PROTECTED)
	private int y;

	public ClientSideInventory(Text title, int rows) {
		super(title);
		this.inventoryTitle = this.title;
		this.rows = rows;
		this.backgroundHeight = 114 + this.rows * 18;
		this.slots = new Slot[this.rows * 9];
		this.playerInventorySlots = new Slot[36];
		for (int index = 0; index < this.slots.length; index++) {
			int slotX = index % 9;
			int slotY = index / 9;
			this.slots[index] = new Slot(slotX * 18, slotY * 18);
		}

		final PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
		for (int i = 0; i < playerInventorySlots.length; i++) {
			final int slotX = i % 9;
			final int tempY = (i / 9) - 1;
			final int slotY = tempY < 0 ? tempY + 4 : tempY;
			final Slot slot = new Slot(slotX * 18, slotY * 18 + (tempY < 0 ? 4 : 0) + this.rows * 18 + 13);
			slot.itemStack = inventory.getStack(i);
			playerInventorySlots[i] = slot;
		}
		this.inventoryContents = new InventoryContents(this.rows, this);
	}

	/**
	 * Initializes pagination with the provided values.
	 */
	public void initPagination(List<ItemStack> items, Position from, Position to, List<ItemStack> first) {
		this.pagination = new Pagination(items, from, to, first);
	}

	public InventoryContents getContents() {
		return this.inventoryContents;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		if (this.inventoryTitle != null) {
			context.drawText(this.textRenderer, this.inventoryTitle, this.x + 8, this.y + 6, -1, false);
		}
		context.getMatrices().push();
		int offsetX = this.x + 8;
		int offsetY = this.y + 18;
		context.getMatrices().translate(offsetX, offsetY, 0);
		for (Slot slot : this.slots) {
			this.renderSlot(context, slot, mouseX - offsetX, mouseY - offsetY);
		}

		for (Slot playerInventorySlot : this.playerInventorySlots) {
			this.renderSlot(context, playerInventorySlot, mouseX - offsetX, mouseY - offsetY);
		}
		context.getMatrices().pop();
	}

	@Override
	protected void init() {
		super.init();
		this.x = (this.width - BACKGROUND_WIDTH) / 2;
		this.y = (this.height - this.backgroundHeight) / 2;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.pagination != null && this.pagination.isDirty()) {
			this.paginationUpdate();
			this.pagination.setItems(this.inventoryContents);
		}
	}

	protected void paginationUpdate() {
		this.inventoryContents.fillRectangle(this.pagination.getFrom(), this.pagination.getTo(), ItemStack.EMPTY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
			this.close();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.drawBackground) {
			renderInGameBackground(context);
		}
		int i = (this.width - BACKGROUND_WIDTH) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(
				RenderLayer::getGuiTextured,
				TEXTURE,
				i,
				j,
				0.0F,
				0.0F,
				BACKGROUND_WIDTH,
				this.rows * 18 + 17,
				256,
				256);
		context.drawTexture(
				RenderLayer::getGuiTextured,
				TEXTURE,
				i,
				j + this.rows * 18 + 17,
				0.0F,
				126.0F,
				BACKGROUND_WIDTH,
				96,
				256,
				256);
	}

	private void renderSlot(DrawContext context, Slot slot, int mouseX, int mouseY) {
		if (slot.itemStack == null || slot.itemStack.isEmpty()) {
			return;
		}
		int slotX = slot.x;
		int slotY = slot.y;
		context.drawItem(slot.itemStack, slotX, slotY);
		context.drawStackOverlay(textRenderer, slot.itemStack, slotX, slotY);

		if (mouseX > slotX && mouseX < slotX + 18 && mouseY > slotY && mouseY < slotY + 18) {
			RenderUtils.drawSlotHighlightBack(context, slotX, slotY);
			context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, slot.itemStack, mouseX, mouseY);
			RenderUtils.drawSlotHighlightFront(context, slotX, slotY);
		}
	}

	@Override
	public int cookies$getBackgroundWidth() {
		return BACKGROUND_WIDTH;
	}

	@Override
	public int cookies$getBackgroundHeight() {
		return this.backgroundHeight;
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int offsetX = this.x + 8;
		int offsetY = this.y + 18;
		double localX = mouseX - offsetX;
		double localY = mouseY - offsetY;
		if (localX > this.slots[0].x && localX < this.slots[8].x + 18 && localY > this.slots[0].y &&
			localY < this.slots[this.slots.length - 1].y + 18) {
			int slotX = (int) Math.floor(localX / 18);
			int slotY = (int) Math.floor(localY / 18);

			int index = slotX + slotY * 9;
			if (index < 0 || index >= this.slots.length) {
				return super.mouseClicked(mouseX, mouseY, button);
			}

			final Slot slot = this.slots[index];
			if (slot != null) {
				this.executeClick(slot, button);
			}
		} else if (localX > this.playerInventorySlots[9].x && localX < this.playerInventorySlots[8].x + 18 &&
				   localY > this.playerInventorySlots[9].y && localY < this.playerInventorySlots[0].y + 18) {
			int slotX = (int) Math.floor(localX / 18);
			int slotY = (int) Math.floor((localY - playerInventorySlots[9].y) / 18);

			int index = slotX + slotY * 9;
			if (index < 0 || index >= this.playerInventorySlots.length) {
				return super.mouseClicked(mouseX, mouseY, button);
			}

			final Slot slot = this.playerInventorySlots[index];
			if (slot != null) {
				this.executeClick(slot, button);
			}
		}


		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void executeClick(Slot slot, int button) {
		final ItemStack itemStack = slot.itemStack;
		if (itemStack == null || itemStack.isEmpty()) {
			return;
		}

		final Consumer<Integer> consumer = ItemUtils.getData(itemStack, CookiesDataComponentTypes.ITEM_CLICK_CONSUMER);
		if (consumer != null) {
			consumer.accept(button);
		}
		final Runnable runnable = ItemUtils.getData(itemStack, CookiesDataComponentTypes.ITEM_CLICK_RUNNABLE);
		if (runnable != null) {
			runnable.run();
		}
	}

	/**
	 * Sets the slot to the provided item.
	 */
	public void setSlot(int row, int column, ItemStack itemStack) {
		this.slots[row * 9 + column].itemStack = Objects.requireNonNullElse(itemStack, ItemStack.EMPTY);
	}

	private static class Slot {
		private final int x;
		private final int y;
		private ItemStack itemStack;

		public Slot(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

}
