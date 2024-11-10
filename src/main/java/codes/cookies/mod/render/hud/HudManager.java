package codes.cookies.mod.render.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codes.cookies.mod.render.hud.elements.HudElement;

import codes.cookies.mod.render.hud.internal.HudPosition;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.util.Identifier;

public class HudManager {

	static final List<HudElement> elements = new ArrayList<>();
	private static final Map<Identifier, HudPosition> value = new HashMap<>();

	public static void load() {
		HudRenderCallback.EVENT.register(HudManager::render);
		for (HudElement element : elements) {
			final HudPosition hudPosition = value.get(element.getIdentifier());
			if (hudPosition == null) {
				continue;
			}
			element.load(hudPosition);
		}
	}

	private static boolean cancelRendering() {
		return MinecraftClient.getInstance().currentScreen instanceof HudEditScreen;
	}

	public static void register(HudElement element) {
		elements.add(element);
	}

	private static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		if (cancelRendering()) {
			return;
		}
		elements.forEach(hudElement -> HudManager.render(drawContext, renderTickCounter, hudElement));
	}

	private static void render(DrawContext drawContext, RenderTickCounter renderTickCounter, HudElement hudElement) {
		int elementX = hudElement.getX();
		int elementY = hudElement.getY();

		drawContext.cm$withMatrix(stack -> {
			stack.translate(elementX, elementY, 0);
			stack.scale(hudElement.getScale(), hudElement.getScale(), 0);
			hudElement.renderChecks(
					drawContext,
					getTextRenderer(),
					renderTickCounter.getTickDelta(true));
		});
	}

	private static TextRenderer getTextRenderer() {
		return MinecraftClient.getInstance().textRenderer;
	}

	public static void applyAll(Map<Identifier, HudPosition> value) {
		HudManager.value.putAll(value);
	}

	public static Map<Identifier, HudPosition> getSettings() {
		Map<Identifier, HudPosition> value = new HashMap<>();
		for (HudElement element : elements) {
			value.put(element.getIdentifier(), element.getPosition());
		}
		return value;
	}
}
