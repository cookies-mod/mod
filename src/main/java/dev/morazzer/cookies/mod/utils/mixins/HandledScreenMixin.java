package dev.morazzer.cookies.mod.utils.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.properties.Property;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.accessors.InventoryScreenAccessor;
import dev.morazzer.cookies.mod.utils.accessors.SlotAccessor;
import dev.morazzer.cookies.mod.utils.dev.DevInventoryUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.exceptions.ExceptionHandler;
import dev.morazzer.cookies.mod.utils.items.CookiesDataComponentTypes;
import dev.morazzer.cookies.mod.utils.items.ItemTooltipComponent;
import dev.morazzer.cookies.mod.utils.items.ItemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Allows for saving of screens/inventories.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements InventoryScreenAccessor {

	@Unique
	private static final Identifier ALLOW_SCREEN_SAVING = DevUtils.createIdentifier("save_handled_screens");
	@Unique
	private static final Identifier ITEM_DEBUG_MODE = DevUtils.createIdentifier("item_debug_mode");
	@Shadow
	@Nullable
	protected Slot focusedSlot;

	@Shadow
	public int backgroundHeight;

	@Shadow
	public int backgroundWidth;

	@Shadow
	public int x;

	@Shadow
	public int y;
	@Unique
	private final List<Disabled> cookies$disabled = new ArrayList<>();

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	@SuppressWarnings("MissingJavadoc")
	public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (DevUtils.isEnabled(ALLOW_SCREEN_SAVING) && keyCode == 83) {
			try {
				final Path path =
						DevInventoryUtils.saveInventory((HandledScreen<? extends ScreenHandler>) (Object) this);
				CookiesUtils.sendMessage(CookiesUtils.createPrefix()
						.append("Saved inventory to file %s".formatted(path.getFileName()))
						.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
								path.getFileName().toString().split("\\.")[0]))));
				cir.setReturnValue(true);
			} catch (IOException ioException) {
				CookiesUtils.sendFailedMessage("Failed to writing inventory file");
				ExceptionHandler.handleException(ioException);
			} catch (Exception exception) {
				ExceptionHandler.handleException(exception);
			}
			return;
		}
		if (this.focusedSlot != null && DevUtils.isEnabled(ITEM_DEBUG_MODE)) {
			final Slot focusedSlot = this.focusedSlot;
			final ItemStack stack = focusedSlot.getStack();
			if (stack.isEmpty()) {
				return;
			}
			if (keyCode == InputUtil.GLFW_KEY_E) {
				MinecraftClient.getInstance().keyboard.setClipboard(stack.get(CookiesDataComponentTypes.SKYBLOCK_ID));
				CookiesUtils.sendSuccessMessage("Copied skyblock id!");
				cir.setReturnValue(true);
			} else if (keyCode == InputUtil.GLFW_KEY_S && stack.isOf(Items.PLAYER_HEAD)) {
				final ProfileComponent skin = stack.get(DataComponentTypes.PROFILE);
				if (skin == null) {
					CookiesUtils.sendFailedMessage("Item does not have a skin attached!");
					cir.setReturnValue(true);
					return;
				}
				skin.gameProfile().getProperties().forEach((string, property) -> {
					System.out.printf("%s - %s%n", string, property.value());
				});
				final Collection<Property> textures = skin.gameProfile().getProperties().get("textures");
				for (Property texture : textures) {
					MinecraftClient.getInstance().keyboard.setClipboard(texture.value());
					CookiesUtils.sendSuccessMessage("Copied skin!");
					cir.setReturnValue(true);
					return;
				}
			} else if (keyCode == InputUtil.GLFW_KEY_N) {
				MinecraftClient.getInstance().keyboard.setClipboard(stack.getName().getString());
				cir.setReturnValue(true);
			}
		}
	}

	@WrapOperation(
			method = "drawMouseoverTooltip", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;" +
					 "Ljava/util/List;Ljava/util/Optional;II)V"
	)
	)
	public void drawTooltip(
			DrawContext instance,
			TextRenderer textRenderer,
			List<Text> text,
			Optional<TooltipData> data,
			int x,
			int y,
			Operation<Void> original) {
		if (focusedSlot == null) {
			return;
		}
		final ItemStack stack = this.focusedSlot.getStack();
		final ItemTooltipComponent loreItems = ItemUtils.getData(stack, CookiesDataComponentTypes.LORE_ITEMS);

		if (loreItems == null && !DevUtils.isEnabled(ITEM_DEBUG_MODE)) {
			original.call(instance, textRenderer, text, data, x, y);
			return;
		}
		List<TooltipComponent> list =
				text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Util.toArrayList());
		if (loreItems != null) {
			list.add(list.isEmpty() ? 0 : 1, loreItems);
			data.ifPresent(data2 -> list.add(1, TooltipComponent.of(data2)));
		}
		if (DevUtils.isEnabled(ITEM_DEBUG_MODE)) {
			final String skyblockId = stack.get(CookiesDataComponentTypes.SKYBLOCK_ID);
			list.add(new OrderedTextTooltipComponent(Text.empty().asOrderedText()));
			if (skyblockId != null) {
				list.add(new OrderedTextTooltipComponent(Text.literal("[E] ")
						.append(Text.literal(skyblockId).formatted(Formatting.GRAY))
						.formatted(Formatting.GOLD)
						.asOrderedText()));
			}
			if (stack.isOf(Items.PLAYER_HEAD) && stack.get(DataComponentTypes.PROFILE) != null) {
				list.add(new OrderedTextTooltipComponent(Text.literal("[S] ")
						.append(Text.literal("To copy skin").formatted(Formatting.GRAY))
						.formatted(Formatting.GOLD)
						.asOrderedText()));
			}
			list.add(new OrderedTextTooltipComponent(Text.literal("[N] ")
					.append(Text.literal("To copy name").formatted(Formatting.GRAY))
					.formatted(Formatting.GOLD)
					.asOrderedText()));
		}
		final TooltipComponent tooltipComponent = list.removeLast();
		if (tooltipComponent instanceof OrderedTextTooltipComponent orderedTextTooltipComponent) {
			final int width = orderedTextTooltipComponent.getWidth(textRenderer);
			if (width != 0) {
				list.add(tooltipComponent);
			}
		}
		instance.drawTooltip(textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE);
	}

	@Inject(
			method = "mouseClicked",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"),
			cancellable = true
	)
	private void cancelMouseClick(
			double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir, @Local Slot slot) {
		if (slot == null) {
			return;
		}
		if (SlotAccessor.getOnClick(slot) != null) {
			SlotAccessor.getOnClick(slot).accept(button);
			cir.setReturnValue(true);
			return;
		}
		if (SlotAccessor.getRunnable(slot) != null) {
			SlotAccessor.getRunnable(slot).run();
			cir.setReturnValue(true);
			return;
		}
		if (SlotAccessor.getItem(slot) != null) {
			cir.setReturnValue(true);
		}
		if (SlotAccessor.getOnItemClickRunnable(slot) != null) {
			SlotAccessor.getOnItemClickRunnable(slot).run();
		}
	}

	@ModifyArgs(
			method = "drawSlot", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;" +
					 "Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"
	)
	)
	private void drawItem$drawItemInSlot(Args args) {
		final ItemStack itemStack = args.get(1);
		String text;
		if ((text = ItemUtils.getData(itemStack, CookiesDataComponentTypes.CUSTOM_SLOT_TEXT)) != null) {
			args.set(4, text);
		}
	}

	@Override
	public int cookies$getBackgroundWidth() {
		return this.backgroundWidth;
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
		return this.cookies$disabled;
	}
}
