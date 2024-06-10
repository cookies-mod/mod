package dev.morazzer.cookies.mod.config.system.editor;

import dev.morazzer.cookies.mod.config.system.options.FoldableOption;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

/**
 * Editor that describes a foldable.
 */
@Getter
public class FoldableEditor extends ConfigOptionEditor<Object, FoldableOption> {

    private final int foldableId;
    boolean active = true;

    @SuppressWarnings("MissingJavadoc")
    public FoldableEditor(FoldableOption option, int id) {
        super(option);
        this.foldableId = id;
    }

    @Override
    public void init() {
        this.active = false;
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        // TODO render
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
        if (mouseX >= 0 && mouseX < optionWidth
            && mouseY >= 0 && mouseY < getHeight()) {
            this.active = !this.active;
        }
        return super.mouseClicked(mouseX, mouseY, button, optionWidth);
    }

}
