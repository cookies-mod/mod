package dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.morazzer.cookies.mod.CookiesMod;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.CraftHelperInstance;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.CraftHelperManager;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.CraftHelperPlacement;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.utils.RenderUtils;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.minecraft.SoundUtils;
import dev.morazzer.cookies.mod.utils.minecraft.TextBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public final class HeadingPart extends CraftHelperComponent {

	private static final Identifier SLOT = Identifier.ofVanilla("container/bundle/slot");
	private final RepositoryItem repositoryItem;
	private final CraftHelperText formattedName;
	private final int nameWidth;
	private final ItemStack stack;
	private final CraftHelperInstance instance;
	private final CraftHelperText close;
	private final TooltipFieldPart tooltipField;
	private final CraftHelperText amountText;

	public HeadingPart(
			RepositoryItem repositoryItem, Text formattedName, int nameWidth, ItemStack stack,
			CraftHelperInstance instance
	) {
		this.repositoryItem = repositoryItem;
		this.nameWidth = nameWidth;
		this.stack = stack;
		this.instance = instance;
		this.formattedName = CraftHelperText.of(createHeading());
		this.close = CraftHelperText.of(Text.empty().append(createMove()).append(" ").append(createClose()));
		this.amountText = CraftHelperText.of(createAmount());
		this.tooltipField = new TooltipFieldPart(5,5, 18, 18);
		this.tooltipField.setHoverText(getLore());
	}

	public HeadingPart(RepositoryItem repositoryItem, CraftHelperInstance craftHelperInstance) {
		this(
				repositoryItem,
				repositoryItem.getFormattedName(),
				MinecraftClient.getInstance().textRenderer.getWidth(repositoryItem.getFormattedName()),
				repositoryItem.constructItemStack(),
				craftHelperInstance);
	}

	private void pressedClose() {
		SoundUtils.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
		CraftHelperManager.remove();
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
		super.drawItems(textRenderer, x, y, context);
		RenderUtils.renderBackgroundBox(context, x, y, 26, 26);


		context.drawGuiTexture(SLOT, x +4, y + 4, 0, 18, 20);
		context.getMatrices().push();
		context.getMatrices().translate(0, 0, -100);
		context.drawItem(stack, x + 5, y + 5, (int) System.currentTimeMillis());
		context.drawItemInSlot(textRenderer, stack, x + 1, y + 1);
		context.getMatrices().pop();

		int remainingWidth = width - 34;
		int nameDelta = remainingWidth - nameWidth;
		this.formattedName.setX(26 + nameDelta / 2);
		this.formattedName.setY(2);
		context.drawTextWithShadow(
				textRenderer,
				formattedName.text(),
				x + this.formattedName.x(),
				y + this.formattedName.y(),
				Colors.WHITE);


		int amountWidth = textRenderer.getWidth(this.amountText.text());
		int amountDelta = remainingWidth - amountWidth;
		this.amountText.setX(26 + amountDelta / 2);
		this.amountText.setY(13);
		context.drawTextWithShadow(
				textRenderer,
				this.amountText.text(),
				x + this.amountText.x(),
				y + this.amountText.y(),
				Colors.WHITE);

		this.close.setX(width - 4 - textRenderer.getWidth(this.close.text()));
		this.close.setY(1);
		context.drawTextWithShadow(
				textRenderer,
				this.close.text(),
				x + this.close.x(),
				y + this.close.y(),
				Colors.RED);
	}


	@Override
	public int getHeight() {
		return 30;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 60 + textRenderer.getWidth(formattedName.text()) + textRenderer.getWidth(close.text());
	}

	public RepositoryItem repositoryItem() {
		return repositoryItem;
	}

	public Text formattedName() {
		return formattedName.text();
	}

	public int nameWidth() {
		return nameWidth;
	}

	public ItemStack stack() {
		return stack;
	}

	public CraftHelperInstance instance() {
		return instance;
	}

	private List<Text> getLore() {
		final List<Text> lore = new ArrayList<>(repositoryItem.getLore());
		lore.addFirst(repositoryItem.getFormattedName());
		return lore;
	}

	private Text createHeading() {
		return new TextBuilder(repositoryItem.getFormattedName()).onHover(getLore()).build();
	}

	private Text createMove() {
		return new TextBuilder(Constants.Emojis.MOVE)
				.setRunnable(this::move)
				.onHover(List.of(Text.literal("Click to move").formatted(Formatting.AQUA)))
				.formatted(Formatting.AQUA).build();
	}

	private void move() {
		SoundUtils.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
		CookiesMod.openScreen(new CraftHelperPlacement());
	}

	private Text createClose() {
		return new TextBuilder(Constants.Emojis.NO).formatted(Formatting.RED, Formatting.BOLD)
				.setRunnable(this::pressedClose)
				.onHover(List.of(Text.literal("Close").formatted(Formatting.RED)))
				.build();
	}

	private Text createAmount() {
		Text add = new TextBuilder(" + ").formatted(Formatting.GREEN)
				.onHover(List.of(
						Text.literal("Increases the target amount").formatted(Formatting.GRAY),
						Text.empty(),
						Text.literal("Click to increase by 1.").formatted(Formatting.YELLOW),
						Text.literal("Shift-click to increase by 10.").formatted(Formatting.YELLOW)))
				.setRunnable(this::increase).build();
		Text remove = new TextBuilder(" - ").formatted(Formatting.RED)
				.onHover(List.of(
						Text.literal("Decreases the target amount").formatted(Formatting.GRAY),
						Text.empty(),
						Text.literal("Click to decrease by 1.").formatted(Formatting.YELLOW),
						Text.literal("Shift-click to decrease by 10.").formatted(Formatting.YELLOW)))
				.setRunnable(this::decrease).build();

		return Text.empty()
				.append(remove)
				.append(" ")
				.append(Text.literal(String.valueOf(instance.getAmount())).formatted(Formatting.UNDERLINE))
				.append(" ")
				.append(add);
	}

	private void decrease() {
		int newAmount;
		if (Screen.hasShiftDown()) {
			newAmount = Math.max(1, this.instance.getAmount() - 10);
		} else {
			newAmount = Math.max(1, this.instance.getAmount() - 1);
		}
		SoundUtils.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
		instance.setAmount(newAmount);
		instance.recalculate();
	}

	private void increase() {
		int newAmount;
		if (Screen.hasShiftDown()) {
			newAmount = this.instance.getAmount() + 10;
		} else {
			newAmount = this.instance.getAmount() + 1;
		}
		SoundUtils.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
		instance.setAmount(newAmount);
		instance.recalculate();
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (HeadingPart) obj;
		return Objects.equals(this.repositoryItem, that.repositoryItem) &&
				Objects.equals(this.formattedName, that.formattedName) &&
				this.nameWidth == that.nameWidth &&
				Objects.equals(this.stack, that.stack) &&
				Objects.equals(this.instance, that.instance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(repositoryItem, formattedName, nameWidth, stack, instance);
	}

	@Override
	public String toString() {
		return "HeadingPart[" +
				"repositoryItem=" + repositoryItem + ", " +
				"formattedName=" + formattedName + ", " +
				"nameWidth=" + nameWidth + ", " +
				"stack=" + stack + ", " +
				"instance=" + instance + ']';
	}

	@Override
	public List<CraftHelperComponentPart> getTextParts() {
		return List.of(formattedName, close, amountText, tooltipField);
	}
}
