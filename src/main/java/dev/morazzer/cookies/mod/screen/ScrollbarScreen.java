package dev.morazzer.cookies.mod.screen;

import dev.morazzer.cookies.mod.utils.maths.MathUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Common code for screens with scrollbars.
 */
public abstract class ScrollbarScreen extends CookiesScreen {
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/creative_inventory/scroller");
    private static final int SCROLLBAR_WIDTH = 14;
    protected int scroll;
    private int scrollbarHeight;
    private int effectiveScrollbarHeight;
    private int scrollbarX;
    private int scrollbarY;
    private int maxScroll = 0;
    private float scrollStep = 0;
    private boolean isScrolling;

    protected ScrollbarScreen(Text title, int scrollbarHeight) {
        super(title);
        this.scrollbarHeight = scrollbarHeight;
        this.updateScrollbar(0, 0, 0);
    }

    /**
     * Sets the new position and height for the scrollbar.
     * @param height The height of the scrollbar.
     * @param x The left coordinate.
     * @param y The top coordinate.
     */
    public void updateScrollbar(int height, int x, int y) {
        this.scrollbarHeight = height;
        this.effectiveScrollbarHeight = this.scrollbarHeight - 15;
        this.scrollbarX = x;
        this.scrollbarY = y;
        this.updateScroll(this.maxScroll);
    }

    /**
     * Sets the new maximum scroll value.
     * @param maxScroll The max scroll value.
     */
    public void updateScroll(int maxScroll) {
        this.maxScroll = maxScroll;
        this.scrollStep = (float) this.effectiveScrollbarHeight / Math.max(this.maxScroll, 1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isInBound((int) mouseX,
            (int) mouseY,
            this.scrollbarX,
            this.scrollbarY,
            SCROLLBAR_WIDTH,
            this.scrollbarHeight)) {
            this.isScrolling = true;
            return true;
        }
        this.isScrolling = false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isScrolling) {
            int start = this.scrollbarY;
            int end = start + this.scrollbarHeight;
            this.scroll = (int) (((mouseY - start - 7.5f) / (end - start - 15.0f)) * this.maxScroll);
            this.scroll = MathUtils.clamp(this.scroll, 0, this.maxScroll);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /**
     * Updates the scroll amount.
     * @param verticalAmount The scroll amount.
     */
    public void updateScrollbar(double verticalAmount) {
        this.scroll = MathUtils.clamp((int) (this.scroll - verticalAmount), 0, this.maxScroll);
    }

    protected void renderScrollbar(DrawContext drawContext) {
        int scrollBarX = this.scrollbarX;
        int scrollBarY = this.scrollbarY;
        drawContext.drawGuiTexture(
				RenderLayer::getGuiTextured, SCROLLER_TEXTURE,
            scrollBarX,
            MathUtils.clamp(scrollBarY + (int) (this.scroll * this.scrollStep),
                scrollBarY,
                scrollBarY + this.effectiveScrollbarHeight),
            SCROLLBAR_WIDTH,
            15);
    }
}
