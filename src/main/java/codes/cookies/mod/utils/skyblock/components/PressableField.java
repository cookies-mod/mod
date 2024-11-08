package codes.cookies.mod.utils.skyblock.components;

import codes.cookies.mod.utils.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

@Setter
@Getter
public class PressableField extends PressableWidget {

    private boolean shouldRender = true;
    private int color;
    private Runnable runnable;

    public PressableField(int x, int y, int width, int height, int color) {
        super(x, y, width, height, Text.empty());
        this.color = color;
        this.runnable = () -> {};
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.shouldRender) {
            return;
        }
        RenderUtils.renderBackgroundBox(context, this.getX(), this.getY(), this.getWidth(), this.getHeight(), color);
    }

    @Override
    public void onPress() {
        runnable.run();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
