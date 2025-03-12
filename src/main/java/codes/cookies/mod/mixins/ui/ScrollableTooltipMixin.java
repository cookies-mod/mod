package codes.cookies.mod.mixins.ui;

import codes.cookies.mod.config.categories.MiscCategory;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import codes.cookies.mod.utils.accessors.FocusedSlotAccessor;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.items.types.ScrollableDataComponentTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The real implementation of the scrollable tooltip.
 */
@Mixin(HandledScreen.class)
public class ScrollableTooltipMixin implements FocusedSlotAccessor {

    @Unique
    private static final Identifier COOKIES$DEBUG = DevUtils.createIdentifier("scrollable_tooltip_debug");

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @WrapOperation(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void drawTooltip(DrawContext instance, TextRenderer textRenderer, List<Text> text,
                             Optional<TooltipData> data, int x, int y, Operation<Void> original) {
        if (!MiscCategory.enableScrollableTooltips) {
            original.call(instance, textRenderer, text, data, x, y);
            return;
        }

        Integer first =
            ItemUtils.getData(this.focusedSlot.getStack(), ScrollableDataComponentTypes.TOOLTIP_OFFSET_FIRST);
        Integer last = ItemUtils.getData(this.focusedSlot.getStack(), ScrollableDataComponentTypes.TOOLTIP_OFFSET_LAST);
        Integer vertical =
            ItemUtils.getData(this.focusedSlot.getStack(), ScrollableDataComponentTypes.TOOLTIP_OFFSET_VERTICAL);
        Integer horizontal =
            ItemUtils.getData(this.focusedSlot.getStack(), ScrollableDataComponentTypes.TOOLTIP_OFFSET_HORIZONTAL);

        if (first == null && last == null && vertical == null && horizontal == null) {
            original.call(instance, textRenderer, text, data, x, y);
            return;
        }

        if (first != null || last != null) {
            final int a = Math.min(text.size(), Math.max(Objects.requireNonNullElse(first, 0), 0));
            final int b = Math.min(Math.max(text.size() + Objects.requireNonNullElse(last, 0), a), text.size());

            if (b <= a) {
                text = text.subList(0, 0);
            } else if (a < text.size()) {
                text = text.subList(a, b);
            }

            if (DevUtils.isEnabled(COOKIES$DEBUG)) {
                text.add(Text.literal("First: %s".formatted(a)));
                text.add(Text.literal("Last: %s".formatted(b)));
            }
        }

        if (DevUtils.isEnabled(COOKIES$DEBUG)) {
            text.add(Text.literal("Vertical: %s".formatted(vertical)));
            text.add(Text.literal("Horizontal: %s".formatted(horizontal)));
        }

        original.call(instance,
            textRenderer,
            text,
            data,
            x + Objects.requireNonNullElse(horizontal, 0),
            y + Objects.requireNonNullElse(vertical, 0));
    }

    @Override
    public Slot cookies$getFocusedSlot() {
        return this.focusedSlot;
    }
}
