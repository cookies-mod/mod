package codes.cookies.mod.config.system.editor;

import java.util.List;
import java.util.Locale;

import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.utils.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.WidgetTooltipPositioner;
import net.minecraft.text.OrderedText;

/**
 * Generic option editor.
 *
 * @param <T> Type of the value.
 * @param <O> Type of the option.
 */
public abstract class ConfigOptionEditor<T, O extends Option<T, O>> {

	protected final O option;
	private boolean isDragging;
	@Getter
	@Setter
	private int x = 0, y = 0;

	/**
	 * Creates a new option editor.
	 *
	 * @param option The option the editor belongs to.
	 */
	public ConfigOptionEditor(@NotNull O option) {
		this.option = option;
	}

	/**
	 * Called whenever the screen is resized or the config menu is opened.
	 */
	public void init() {
	}

	/**
	 * Renders the editor.
	 *
	 * @param drawContext The current draw context.
	 * @param mouseX      The current x position of the mouse.
	 * @param mouseY      The current y position of the mouse.
	 * @param tickDelta   The time difference between the last and the current tick.
	 * @param optionWidth The width the option has to be rendered at.
	 */
	public void render(
			@NotNull DrawContext drawContext,
			int mouseX,
			int mouseY,
			float tickDelta,
			int optionWidth
	) {
		RenderUtils.renderBox(drawContext, 0, 0, optionWidth, this.getHeight(optionWidth));
	}

	/**
	 * Gets the height of the option.
	 *
	 * @param optionWidth The width of the option.
	 * @return The height of the option.
	 */
	@Contract(pure = true)
	public int getHeight(int optionWidth) {
		return this.getHeight();
	}

	/**
	 * Gets the height of the option.
	 *
	 * @return The height of the option.
	 */
	@Contract(pure = true)
	public int getHeight() {
		return 45;
	}

	/**
	 * Checks whether the option matches a user-defined search parameter.
	 *
	 * @param search The search parameter.
	 * @return Whether the option matches the parameter.
	 */
	public boolean doesMatchSearch(@NotNull final String search) {
		return this.option.getName().getString().toLowerCase(Locale.ROOT).contains(search)
				|| this.option.getDescription().getString().toLowerCase(Locale.ROOT).contains(search)
				|| this.option.getTags()
				.stream()
				.anyMatch(key -> key.contains(search));
	}

	/**
	 * Called whenever a key is pressed.
	 *
	 * @param keyCode   The key code.
	 * @param scanCode  The scan code.
	 * @param modifiers The modifiers for the key.
	 * @return If the action was consumed.
	 */
	public boolean keyPressed(
			final int keyCode,
			final int scanCode,
			final int modifiers
	) {
		return false;
	}

	/**
	 * Called whenever a mouse button was clicked.
	 *
	 * @param mouseX      The current x position of the mouse.
	 * @param mouseY      The current y position of the mouse.
	 * @param button      The button that was clicked.
	 * @param optionWidth The width the option is rendered at.
	 * @return If the action was consumed.
	 */
	public boolean mouseClicked(
			double mouseX,
			double mouseY,
			int button,
			int optionWidth
	) {
		return false;
	}

	/**
	 * Called whenever a mouse button was released.
	 *
	 * @param mouseX The current x position of the mouse.
	 * @param mouseY The current y position of the mouse.
	 * @param button The button that was clicked.
	 * @return If the action was consumed.
	 */
	public boolean mouseReleased(
			double mouseX,
			double mouseY,
			int button
	) {
		return false;
	}

	/**
	 * Called whenever the mouse is dragged while having a button clicked.
	 *
	 * @param mouseX      The current x position of the mouse.
	 * @param mouseY      The current y position of the mouse.
	 * @param button      The button that is clicked.
	 * @param deltaX      The difference on the x-axis between the last call and now.
	 * @param deltaY      The difference on the y-axis between the last call and now.
	 * @param optionWidth The width the option is rendered at.
	 * @return If the action was consumed.
	 */
	public boolean mouseDragged(
			double mouseX,
			double mouseY,
			int button,
			double deltaX,
			double deltaY,
			int optionWidth
	) {
		return false;
	}

	/**
	 * Whether there is currently an element that is being dragged in the editor.
	 *
	 * @return Whether there is a dragged element.
	 */
	public boolean isDragging() {
		return this.isDragging;
	}

	/**
	 * Change the current dragging state for the editor.
	 *
	 * @param dragging If there is something being dragged.
	 */
	public void setDragging(boolean dragging) {
		this.isDragging = dragging;
	}

	/**
	 * Called whenever the mouse wheel is scrolled.
	 *
	 * @param mouseX           The current x position of the mouse.
	 * @param mouseY           The current y position of the mouse.
	 * @param horizontalAmount The amount on the x-axis that has been scrolled.
	 * @param verticalAmount   The amount on the y-axis that has been scrolled.
	 */
	@SuppressWarnings("EmptyMethod")
	public void mouseScrolled(
			double mouseX,
			double mouseY,
			double horizontalAmount,
			double verticalAmount
	) {
	}

	/**
	 * Called whenever a key was released.
	 *
	 * @param keyCode   The key code.
	 * @param scanCode  The scan code.
	 * @param modifiers The modifiers for the key.
	 */
	@SuppressWarnings("EmptyMethod")
	public void keyReleased(
			int keyCode,
			int scanCode,
			int modifiers
	) {
	}

	/**
	 * Called whenever a character was typed.
	 *
	 * @param character The character that was typed.
	 * @param modifiers The modifiers that apply to the character.
	 */
	public boolean charTyped(
			char character,
			int modifiers
	) {
		return false;
	}

	/**
	 * Called to render the overlay for the current editor.
	 *
	 * @param drawContext The current draw context.
	 * @param mouseX      The current x position of the mouse.
	 * @param mouseY      The current y position of the mouse.
	 * @param tickDelta   The time difference between the last and the current tick.
	 * @param optionWidth The width the option has to be rendered at.
	 */
	public void renderOverlay(
			DrawContext drawContext,
			int mouseX,
			int mouseY,
			float tickDelta,
			int optionWidth
	) {
		if (mouseX > 2 && mouseX < this.getTextRenderer().getWidth(this.option.getName()) + 2
				&& mouseY > 0 && mouseY < 16) {
			final List<OrderedText> orderedTexts = MinecraftClient.getInstance().textRenderer.wrapLines(
					this.option.getDescription(),
					optionWidth * 2);
			drawContext.drawTooltip(this.getTextRenderer(), orderedTexts,
					new WidgetTooltipPositioner(
							ScreenRect.empty()), mouseX, mouseY);
		}
	}

	/**
	 * Gets the {@linkplain TextRenderer} instance.
	 *
	 * @return The {@linkplain TextRenderer}.
	 */
	@Contract(pure = true)
	protected TextRenderer getTextRenderer() {
		return MinecraftClient.getInstance().textRenderer;
	}

}
