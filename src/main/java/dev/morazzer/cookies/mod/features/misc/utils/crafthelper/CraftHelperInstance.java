package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips.CraftHelperComponent;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips.CraftHelperComponentPart;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips.CraftHelperText;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips.HeadingPart;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips.SpacerComponent;
import dev.morazzer.cookies.mod.features.misc.utils.crafthelper.tooltips.TooltipFieldPart;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import dev.morazzer.cookies.mod.screen.CookiesScreen;
import dev.morazzer.cookies.mod.utils.accessors.ClickEventAccessor;
import dev.morazzer.cookies.mod.utils.accessors.HoverEventAccessor;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.dynamic.Codecs;

@Getter
@Setter
public class CraftHelperInstance {

	public static final Codec<CraftHelperInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					RepositoryItem.ID_CODEC.fieldOf("id").forGetter(CraftHelperInstance::getRepositoryItem),
					Codecs.NON_NEGATIVE_INT.fieldOf("amount").forGetter(CraftHelperInstance::getAmount),
					Codec.STRING.listOf().fieldOf("collapsed").forGetter(CraftHelperInstance::getCollapsed))
			.apply(instance, CraftHelperInstance::new));
	public static final CraftHelperInstance EMPTY = new CraftHelperInstance(RepositoryItem.EMPTY, 0, new ArrayList<>());

	final RepositoryItem repositoryItem;
	final List<String> collapsed;
	private final CraftHelperFormatter formatter;
	int amount;
	HeadingPart headingPart;
	List<CraftHelperComponent> componentList = new ArrayList<>();
	int width = 0;
	int height;
	int lastX;
	int lastY;
	int maxScroll = 0;
	boolean hasCalculated = false;
	int scroll = 0;

	public CraftHelperInstance(RepositoryItem repositoryItem, int amount, List<String> collapsed) {
		this.repositoryItem = repositoryItem;
		this.amount = amount;
		this.collapsed = new ArrayList<>(collapsed);
		this.formatter = new CraftHelperFormatter(this);
	}

	public void recalculate() {
		componentList.clear();
		final var calculate = RecipeCalculator.calculate(repositoryItem);
		if (calculate.isError()) {
			CraftHelperManager.remove();
			return;
		}
		final RecipeCalculationResult recipeCalculationResult = calculate.getResult().orElse(null).multiply(amount);
		if (recipeCalculationResult == null) {
			CraftHelperManager.remove();
			return;
		}
		componentList.clear();
		headingPart = new HeadingPart(repositoryItem, this);
		componentList.addAll(this.formatter.format(recipeCalculationResult, this));
		final ArrayList<CraftHelperComponent> list = new ArrayList<>(this.componentList);
		list.addFirst(headingPart);
		width = list.stream()
				.mapToInt(component -> component.getWidth(MinecraftClient.getInstance().textRenderer))
				.max().orElse(0) + 10;
		height = list.stream()
				.filter(Predicate.not(CraftHelperComponent::isHidden))
				.limit(31)
				.mapToInt(component -> component.getHeight(MinecraftClient.getInstance().textRenderer))
				.sum();
		this.maxScroll = (int) Math.max(list.stream()
				.filter(Predicate.not(CraftHelperComponent::isHidden))
				.count() - 30, 0);
		hasCalculated = true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CraftHelperInstance other) {
			return this.repositoryItem.equals(other.repositoryItem) && this.amount == other.amount;
		}
		return false;
	}

	public int getOffset() {
		return 0;
	}

	public List<CraftHelperComponent> getVisibleComponentList() {
		this.maxScroll = (int) Math.max(componentList.stream()
				.filter(Predicate.not(CraftHelperComponent::isHidden))
				.count() - 30, 0);

		final List<CraftHelperComponent> list = this.componentList.stream()
				.filter(Predicate.not(CraftHelperComponent::isHidden))
				.skip(Math.min(this.scroll, this.maxScroll))
				.limit(30)
				.collect(Collectors.toList());
		list.addFirst(getHeadingPart());
		return list;
	}

	public void render(DrawContext drawContext, int x, int y, int mouseX, int mouseY, float tickDelta) {
		if (!hasCalculated) {
			return;
		}
		lastX = x;
		lastY = y - height / 2;
		final List<CraftHelperComponent> list = getVisibleComponentList();

		final ArrayList<TooltipComponent> craftHelperComponents = new ArrayList<>(list);
		craftHelperComponents.addFirst(new SpacerComponent(0, width));
		drawContext.drawTooltip(
				MinecraftClient.getInstance().textRenderer,
				craftHelperComponents,
				lastX,
				lastY,
				CraftHelperTooltipPositioner.INSTANCE,
				null);

		final Optional<Pair<CraftHelperComponentPart, Integer>> textUnder = getTextUnder(mouseX, mouseY, list);
		if (textUnder.isPresent()) {
			drawContext.getMatrices().translate(0, 0, 100);
			this.renderOverlay(
					textUnder.get().getLeft(),
					textUnder.get().getRight(),
					mouseX + x,
					mouseY + y,
					drawContext);
		}
	}

	private void renderOverlay(CraftHelperComponentPart part, int textX, int x, int y, DrawContext drawContext) {
		if (part instanceof TooltipFieldPart tooltipFieldPart) {
			drawContext.drawTooltip(
					MinecraftClient.getInstance().textRenderer,
					tooltipFieldPart.getHoverText().stream().map(Text::asOrderedText).toList(),
					HoveredTooltipPositioner.INSTANCE,
					x,
					y);
		} else if (part instanceof CraftHelperText text) {
			final Style styleAt = MinecraftClient.getInstance().textRenderer.getTextHandler()
					.getStyleAt(text.text(), textX);
			if (styleAt == null) {
				return;
			}
			if (styleAt.getHoverEvent() == null) {
				return;
			}

			final Optional<List<Text>> hoverText = HoverEventAccessor.getText(styleAt.getHoverEvent());
			hoverText.ifPresent(texts -> drawContext.drawTooltip(
					MinecraftClient.getInstance().textRenderer,
					texts.stream().map(Text::asOrderedText).toList(),
					HoveredTooltipPositioner.INSTANCE,
					x,
					y));
		}
	}

	public Optional<Pair<CraftHelperComponentPart, Integer>> getTextUnder(
			double mouseX,
			double rawMouseY,
			List<CraftHelperComponent> components
	) {
		double mouseY = rawMouseY + this.height / 2f;


		int heightOffset = 0;
		for (CraftHelperComponent craftHelperComponent : components) {
			final int height = craftHelperComponent.getHeight(MinecraftClient.getInstance().textRenderer);
			if (CookiesScreen.isInBound(
					(int) (mouseX),
					(int) (mouseY),
					0,
					heightOffset,
					width,
					height)) {
				return this.clicked(mouseX, mouseY - heightOffset, craftHelperComponent);
			}
			heightOffset += height;
		}
		return Optional.empty();
	}

	public boolean onMouseClicked(double mouseX, double mouseY, int button) {
		if (!hasCalculated) {
			return false;
		}
		final Optional<Pair<CraftHelperComponentPart, Integer>> textUnder = getTextUnder(
				mouseX,
				mouseY,
				getVisibleComponentList());
		if (textUnder.isEmpty()) {
			return false;
		}
		final CraftHelperComponentPart left = textUnder.get().getLeft();
		if (!(left instanceof CraftHelperText clickedText)) {
			return false;
		}
		final int x = textUnder.get().getRight();

		final Style styleAt = MinecraftClient.getInstance().textRenderer.getTextHandler()
				.getStyleAt(clickedText.text(), x);
		if (styleAt == null) {
			return false;
		}
		if (styleAt.getClickEvent() != null) {
			final Optional<Runnable> runnable = ClickEventAccessor.getRunnable(styleAt.getClickEvent());
			if (runnable.isPresent()) {
				runnable.ifPresent(Runnable::run);
				return true;
			}
		}

		return false;
	}

	private Optional<Pair<CraftHelperComponentPart, Integer>> clicked(
			double mouseX,
			double mouseY,
			CraftHelperComponent craftHelperComponent
	) {
		for (CraftHelperComponentPart textPart : craftHelperComponent.getTextParts()) {
			if (CookiesScreen.isInBound(
					(int) mouseX,
					(int) mouseY,
					textPart.x(),
					textPart.y(),
					textPart.width(),
					textPart.height())) {
				return Optional.of(new Pair<>(textPart, (int) mouseX - textPart.x()));
			}
		}
		return Optional.empty();
	}

	public boolean onMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (!hasCalculated) {
			return false;
		}
		if (CookiesScreen.isInBound((int) mouseX, (int) mouseY + height / 2, 0, 0, width, height)) {
			this.scroll = Math.clamp(this.scroll - ((int) verticalAmount), 0, this.maxScroll);
			return true;
		}
		return false;
	}

	public boolean onKeyPressed(int key, int scancode, int modifiers) {
		return false;
	}

	public boolean onCharTyped(char chr, int modifiers) {
		return false;
	}

	public boolean onKeyReleased(int key, int scancode, int modifiers) {
		return false;
	}

	public void setCollapsed(String path) {
		this.collapsed.add(path);
	}
}
