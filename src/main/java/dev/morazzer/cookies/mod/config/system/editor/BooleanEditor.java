package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.utils.minecraft.SoundUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to display a single boolean value switch.
 */
public class BooleanEditor extends ConfigOptionEditor<Boolean, BooleanOption> {

    private static final ItemStack ACTIVATED = new ItemStack(Items.GREEN_WOOL);
    private static final ItemStack DEACTIVATED = new ItemStack(Items.RED_WOOL);

    @SuppressWarnings("MissingJavadoc")
    public BooleanEditor(final BooleanOption option) {
        super(option);
    }

	@Override
    public void render(final @NotNull DrawContext drawContext,
                       final int mouseX,
                       final int mouseY,
                       final float tickDelta,
                       final int optionWidth) {
        super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
        drawContext.drawText(this.getTextRenderer(), this.option.getName(), 2,
            this.getHeight(optionWidth) / 2 - this.getTextRenderer().fontHeight / 2, 0xFFFFFFFF, true);

        drawContext.drawItemWithoutEntity(
            this.option.getValue() ? ACTIVATED : DEACTIVATED,
            optionWidth - 20,
            this.getHeight() - 17
        );
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {

        if (mouseX > optionWidth - 20 && mouseX < optionWidth - 4
            && mouseY > this.getHeight() - 17 && mouseY < this.getHeight() - 1) {
            this.option.setValue(!this.option.getValue());
            SoundUtils.playSound(SoundEvents.BLOCK_LEVER_CLICK, 10, 1);

        }

        return false;
    }

}
