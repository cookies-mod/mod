package codes.cookies.mod.config.system.editor;

import codes.cookies.mod.config.system.options.KeybindingOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Editor to create a keybinding in the config.
 */
public class KeybindingEditor extends ConfigOptionEditor<InputUtil.Key, KeybindingOption> {

    private static final Identifier BUTTON = Identifier.of("cookiesmod", "gui/config/button.png");
    private boolean currentlyEditingKey;

    @SuppressWarnings("MissingJavadoc")
    public KeybindingEditor(KeybindingOption option) {
        super(option);
    }

    @Override
    public void render(@NotNull DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int optionWidth) {
        super.render(drawContext, mouseX, mouseY, tickDelta, optionWidth);
        // TODO render
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.currentlyEditingKey) {
            this.option.setValue(InputUtil.fromKeyCode(keyCode, scanCode));
            this.currentlyEditingKey = false;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int optionWidth) {
        if (button != -1 && this.currentlyEditingKey) {
            String translationKey = switch (button) {
                case 0 -> "key.mouse.left";
                case 1 -> "key.mouse.right";
                case 2 -> "key.mouse.middle";
                default -> "key.mouse.%s".formatted(button);
            };
            this.option.setValue(InputUtil.fromTranslationKey(translationKey));
            this.currentlyEditingKey = false;
            return true;
        }

        if ((button == 0)
            && (mouseX > ((optionWidth / 6f) - 24))
            && (mouseX < ((optionWidth / 6f) + 24))
            && (mouseY > (this.getHeight() - 21))
            && (mouseY < (this.getHeight() - 5))
        ) {
            this.currentlyEditingKey = true;
            return false;
        }
        this.currentlyEditingKey = false;

        return super.mouseClicked(mouseX, mouseY, button, optionWidth);
    }

}
