package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.DraggableListOption;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to render a draggable list (this is currently not implemented).
 */
public class DraggableListEditor extends ConfigOptionEditor<List<String>, DraggableListOption> {

    @SuppressWarnings("MissingJavadoc")
    public DraggableListEditor(DraggableListOption option) {
        super(option);
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        drawContext.fill(0, 0, optionWidth, this.getHeight(), 0xFFFFFF00);
    }

}
