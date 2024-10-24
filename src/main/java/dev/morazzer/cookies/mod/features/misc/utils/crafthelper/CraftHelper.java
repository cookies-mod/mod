package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.data.profile.ProfileStorage;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.Recipe;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeResult;
import dev.morazzer.cookies.mod.utils.ColorUtils;
import dev.morazzer.cookies.mod.utils.TextUtils;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.SkyblockUtils;
import dev.morazzer.cookies.mod.utils.accessors.InventoryScreenAccessor;
import dev.morazzer.cookies.mod.utils.cookies.CookiesUtils;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.maths.MathUtils;

import dev.morazzer.cookies.mod.utils.minecraft.NonCacheMutableText;
import dev.morazzer.cookies.mod.utils.minecraft.SupplierTextContent;

import dev.morazzer.cookies.mod.utils.skyblock.ForgeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.function.Supplier;

import lombok.Getter;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2ic;

/**
 * Helper to display a recipe (and its respective sub recipes) in game and also show the ingredients plus their
 * respective amount of items that are required.
 */
public class CraftHelper {
	/**
	 * Logging key for the {@link DevUtils#log(String, Object, Object...)} method.
	 */
	public static final String LOGGING_KEY = "crafthelper";
	private static final Identifier DEBUG_INFO = DevUtils.createIdentifier("craft_helper/debug");
	private static final Identifier DEBUG_HITBOX = DevUtils.createIdentifier("craft_helper/hitbox");
	private CraftHelperLocation location = CraftHelperLocation.RIGHT;
	@Getter
	private static CraftHelper instance;
	private final int buttonWidthHeight = 8;
	@Getter
	private RepositoryItem selectedItem;
	@Getter
	private RecipeCalculationResult calculation;
	private int yOffset = 0;
	private List<MutableText> tooltip = new ArrayList<>();
	private int buttonX;
	private int buttonY;
	private int width = 0;
	private int scrolled = 1;

	@SuppressWarnings("MissingJavadoc")
	public CraftHelper() {
		instance = this;
		ConfigManager.getConfig().helpersConfig.craftHelperLocation.withCallback((oldValue, newValue) -> this.location =
				newValue);
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof InventoryScreenAccessor)) {
				return;
			}
			if (!SkyblockUtils.isCurrentlyInSkyblock()) {
				return;
			}
			if (!ConfigManager.getConfig().helpersConfig.craftHelper.getValue()) {
				return;
			}

			this.recalculate();
			this.location = ConfigManager.getConfig().helpersConfig.craftHelperLocation.getValue();
			ScreenEvents.afterRender(screen).register(this::afterRender);
			ScreenMouseEvents.beforeMouseClick(screen).register(this::clicked);
			ScreenMouseEvents.beforeMouseScroll(screen).register(this::beforeScroll);
		});
	}

	public static void setSelectedItem(RepositoryItem item) {
		setSelectedItem(item, 1);
	}

	/**
	 * Sets the currently selected item that is displayed and invokes a recalculation.
	 *
	 * @param item The item to use.
	 */
	public static void setSelectedItem(RepositoryItem item, int amount) {
		if (item == null) {
			instance.calculation = null;
			instance.selectedItem = null;
			ProfileStorage.getCurrentProfile().ifPresent(data -> data.setSelectedCraftHelperItem(null));
			instance.recalculate();
			return;
		}

		final Optional<Recipe> first = item.getRecipes().stream().findFirst();
		if (first.isEmpty()) {
			instance.calculation = null;
			instance.selectedItem = null;
			instance.recalculate();
			return;
		}
		instance.selectedItem = item;
		instance.calculation = RecipeCalculator.calculate(item).multiply(amount);
		ProfileStorage.getCurrentProfile().ifPresent(data -> data.setSelectedCraftHelperItem(item.getInternalId()));
		instance.recalculate();
		instance.scrolled = 1;
	}

	@SuppressWarnings("MissingJavadoc")
	public static boolean append(
			String prefix,
			List<MutableText> text,
			RecipeResult<?> calculationResult,
			int depth,
			EvaluationContext parent,
			StackCountContext stackCountContext,
			Formatter formatter) {
		EvaluationContext context = new EvaluationContext(calculationResult, parent);
		final int amount = getAmount(context, calculationResult.getAmount(), stackCountContext);
		final int amountThroughParents =
				getAmountThroughParents(context, calculationResult.getAmount(), stackCountContext);
		DevUtils.runIf(DEBUG_INFO,
				() -> text.add(Text.literal("Amount: %s, through parents: %s, required: %s".formatted(amount,
						amountThroughParents,
						calculationResult.getAmount()))));
		if (amountThroughParents == calculationResult.getAmount()) {
			DevUtils.runIf(DEBUG_INFO, () -> text.add(Text.literal("Parent full, skipping")));
			return true;
		}

		if (calculationResult instanceof RecipeCalculationResult subResult) {
			int index = text.size();

			boolean childrenFinished = true;
			stackCountContext.integers.push(amount);
			if (amount == calculationResult.getAmount()) {
				DevUtils.runIf(DEBUG_INFO, () -> text.add(Text.literal("Skipping childs, parent full")));
			}
			for (int i = 0; i < subResult.getRequired().size(); i++) {
				if (amount == calculationResult.getAmount()) {
					continue;
				}
				RecipeResult<?> recipeResult = subResult.getRequired().get(i);
				String newPrefix;
				boolean isLast = i + 1 == subResult.getRequired().size();
				if (prefix.isEmpty()) {
					newPrefix = isLast ? "└ " : "├ ";
				} else {
					newPrefix = prefix.replace("├", "│").replace("└", "  ") + (isLast ? "└ " : "├ ");
				}

				childrenFinished &=
						append(newPrefix, text, recipeResult, depth + 1, context, stackCountContext, formatter);
			}
			stackCountContext.integers.pop();

			childrenFinished = childrenFinished && !subResult.getRequired().isEmpty();

			text.add(index, formatter.format(prefix,
					calculationResult.getId(),
					calculationResult.getRepositoryItem(),
					calculationResult.getAmount() - amountThroughParents,
					amount - amountThroughParents,
					stackCountContext.getLastTimeStarted(calculationResult.getRepositoryItem()),
					stackCountContext.usedForge(calculationResult.getRepositoryItem()),
					childrenFinished,
					depth));

			return childrenFinished;
		} else {
			text.add(formatter.format(prefix,
					calculationResult.getId(),
					calculationResult.getRepositoryItem(),
					calculationResult.getAmount() - amountThroughParents,
					amount - amountThroughParents,
					stackCountContext.getLastTimeStarted(calculationResult.getRepositoryItem()),
					stackCountContext.usedForge(calculationResult.getRepositoryItem()),
					false,
					depth));
		}

		return amount == calculationResult.getAmount();
	}

	public static void swap() {
		instance.profileSwap();
	}

	private static int getAmount(EvaluationContext context, int max, StackCountContext stackCountContext) {
		int amount = 0;

		if (context.parent() != null) {
			amount = getAmountThroughParents(context, max, stackCountContext);
		}

		amount += stackCountContext.take(context.recipeResult().getRepositoryItem(), max - amount);

		return Math.min(amount, max);
	}

	private static int getAmountThroughParents(
			EvaluationContext context, int max, StackCountContext stackCountContext) {
		int amount = 0;
		amount += (stackCountContext.integers.peek() *
				   (context.recipeResult().getAmount() / context.parent().recipeResult().getAmount()));
		return Math.min(amount, max);
	}

	public static MutableText formatted(
			String prefix,
			String id,
			RepositoryItem repositoryItem,
			int amount,
			int amountOfItem,
			long lastForgeStarted,
			boolean usedForge,
			boolean childrenFinished,
			int depth) {
		final MutableText literal = Text.empty().append(Text.literal(prefix).formatted(Formatting.DARK_GRAY));

		final double percentage = (double) amountOfItem / amount;
		//noinspection DataFlowIssue
		final int color = ColorUtils.calculateBetween(Formatting.RED.getColorValue(),
				Formatting.GREEN.getColorValue(),
				percentage);


		if (percentage == 1) {
			if (depth == 0) {
				literal.append(Text.literal(Constants.Emojis.FLAG_FILLED).formatted(Formatting.GREEN));
			} else {
				literal.append(Text.literal(Constants.Emojis.YES).formatted(Formatting.GREEN, Formatting.BOLD));
			}
		} else {
			if (childrenFinished) {
				if (depth == 0) {
					literal.append(Text.literal(Constants.Emojis.FLAG_FILLED).formatted(Formatting.YELLOW));
				} else {
					literal.append(Text.literal(Constants.Emojis.WARNING).formatted(Formatting.YELLOW));
				}
			} else {
				if (depth == 0) {
					literal.append(Text.literal(Constants.Emojis.FLAG_EMPTY).formatted(Formatting.RED));
				} else {
					literal.append(Text.literal(Constants.Emojis.NO).formatted(Formatting.RED, Formatting.BOLD));
				}
			}
		}

		literal.append(" ");
		final String formatted =
				"%s/%s".formatted(MathUtils.NUMBER_FORMAT.format(amountOfItem),
						MathUtils.NUMBER_FORMAT.format(amount));
		literal.append(Text.literal(formatted).withColor(color));
		literal.append(" ");
		if (repositoryItem != null) {
			literal.append(repositoryItem.getFormattedName());
		} else {
			literal.append(id);
		}
		if (lastForgeStarted != -1 && usedForge) {
			Text forgeTime = TextUtils.literal(" (", Formatting.DARK_GRAY).append(MutableText.of(new SupplierTextContent(CraftHelper.getSupplier(() -> ForgeUtils.getForgeTime(
					repositoryItem), lastForgeStarted)))).append(")");
			return new NonCacheMutableText(literal).append(forgeTime);
		}

		return literal;
	}

	/**
	 *
	 * @param forgeTime The supplier for the time
	 * @param lastForgeStartedSeconds The last started forge time in seconds
	 * @return The formatted time remaining.
	 */
	private static Supplier<String> getSupplier(Supplier<Long> forgeTime, long lastForgeStartedSeconds) {
		return () -> {
			final long time = forgeTime.get();
			if (time == -1) {
				return "unknown";
			}
			final long delta = (System.currentTimeMillis() / 1000) - lastForgeStartedSeconds;
			int remaining = (int) (time - delta);
			if (remaining <= 0) {
				return "Done";
			}


			return CookiesUtils.formattedMs(remaining * 1000L);
		};
	}

	private void beforeScroll(
			Screen screen, double mouseX, double mouseY, double horizontalScroll, double verticalScroll) {

		if (!this.shouldRender(screen)) {
			return;
		}

		if (this.tooltip.size() < 30) {
			return;
		}

		final Vector2ic position = CraftHelperTooltipPositioner.INSTANCE.getPosition(screen.width,
				screen.height,
				this.calculateX(screen),
				this.calculateY(screen),
				this.width,
				0);
		final int x = position.x();
		final int y = position.y();

		if (mouseX > x && mouseX < x + this.width && mouseY > y && mouseY < y + 270) {
			this.scrolled = (int) Math.max(1, Math.min(this.scrolled - verticalScroll, this.tooltip.size() - 29));
		}
	}

	private void clicked(Screen screen, double mouseX, double mouseY, int button) {

		if (!this.shouldRender(screen)) {
			return;
		}

		final Vector2ic position = CraftHelperTooltipPositioner.INSTANCE.getPosition(screen.width,
				screen.height,
				this.calculateX(screen),
				this.calculateY(screen),
				this.width,
				0);

		final int x = position.x();
		final int y = position.y();
		final int buttonX = x + this.buttonX;
		final int buttonY = y + this.buttonY;

		if (mouseX >= buttonX && mouseX <= buttonX + this.buttonWidthHeight && mouseY >= buttonY &&
			mouseY <= buttonY + this.buttonWidthHeight) {
			setSelectedItem(null);
		}
	}

	private void afterRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
		if (!this.shouldRender(screen)) {
			return;
		}


		final int x = this.calculateX(screen);
		final int y = this.calculateY(screen);

		drawContext.getMatrices().push();
		drawContext.getMatrices().translate(0, 0, -100);
		final int size = this.tooltip.size();
		final List<MutableText> tooltip;
		if (this.tooltip.size() > 30) {
			tooltip = new ArrayList<>(this.tooltip.subList(Math.min(size - 29, this.scrolled),
					Math.min(29 + this.scrolled, size)));
			tooltip.addFirst(this.tooltip.getFirst());
		} else {
			tooltip = this.tooltip;
		}

		drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer,
				tooltip.stream().map(Text::asOrderedText).toList(),
				CraftHelperTooltipPositioner.INSTANCE,
				x,
				y);


		if (DevUtils.isEnabled(DEBUG_HITBOX)) {
			drawContext.getMatrices().translate(0, 0, 1000);
			drawContext.drawBorder(x + this.buttonX,
					y + this.buttonY,
					this.buttonWidthHeight,
					this.buttonWidthHeight,
					-1);
		}
		drawContext.getMatrices().pop();
	}

	private int calculateX(Screen screen) {
		return switch (this.location) {
			case LEFT -> 0;
			case LEFT_INVENTORY -> InventoryScreenAccessor.getX(screen) - 1;
			case RIGHT_INVENTORY -> InventoryScreenAccessor.getX(screen) + 1;
			case RIGHT -> screen.width;
		};
	}

	private int calculateY(Screen screen) {
		return InventoryScreenAccessor.getY(screen) + InventoryScreenAccessor.getBackgroundHeight(screen) / 2 -
			   this.yOffset;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean shouldRender(Screen screen) {
		if (InventoryScreenAccessor.isDisabled(screen, InventoryScreenAccessor.Disabled.CRAFT_HELPER)) {
			return false;
		}
		return this.calculation != null && !this.tooltip.isEmpty();
	}

	private void profileSwap() {
		final Optional<ProfileData> currentProfile = ProfileStorage.getCurrentProfile();
		if (currentProfile.isEmpty()) {
			return;
		}
		final ProfileData profileData = currentProfile.get();
		if (profileData.getSelectedCraftHelperItem() != null && !profileData.getSelectedCraftHelperItem().isEmpty()) {
			String item = profileData.getSelectedCraftHelperItem();
			final RepositoryItem repositoryItem = RepositoryItem.of(item);
			if (repositoryItem != null) {
				DevUtils.log(LOGGING_KEY, "Swapping craft item to %s", item);
				setSelectedItem(repositoryItem);
			}
		} else {
			DevUtils.log(LOGGING_KEY, "Craft item of profile empty, setting to null.");
			setSelectedItem(null);
		}
	}

	private void recalculate() {
		if (this.calculation == null) {
			return;
		}

		long start = System.nanoTime();
		List<MutableText> tooltip = new ArrayList<>();
		append("",
				tooltip,
				this.calculation,
				0,
				new EvaluationContext(this.calculation, null),
				new StackCountContext(),
				CraftHelper::formatted);

		if (!tooltip.isEmpty()) {
			this.addClose(tooltip);
		}

		DevUtils.runIf(DEBUG_INFO,
				() -> tooltip.add(Text.literal(("Time to calculate: %sμs").formatted(
						(System.nanoTime() - start) / 1000))));
		this.tooltip = tooltip;
		this.yOffset = (Math.min(this.tooltip.size(), 30) * 9) / 2;
	}

	private void addClose(List<MutableText> tooltip) {
		int maxSize = 0;
		final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		for (MutableText orderedText : tooltip) {
			int size = textRenderer.getWidth(orderedText);
			if (size > maxSize) {
				maxSize = size;
			}
		}

		final MutableText orderedText = tooltip.getFirst();
		int size = textRenderer.getWidth(orderedText);
		if (size != maxSize) {
			int spaceSize = textRenderer.getWidth(" ");
			int delta = maxSize - size;
			orderedText.append(StringUtils.leftPad("", delta / spaceSize - 1));
		}

		this.width = maxSize + (size == maxSize ? 5 : 0);

		orderedText.append(Text.literal(Constants.Emojis.NO).formatted(Formatting.RED, Formatting.BOLD));

		int buttonOffset = switch (this.location) {
			case RIGHT_INVENTORY, LEFT -> 8;
			case RIGHT, LEFT_INVENTORY -> 10;
		};
		this.buttonX = textRenderer.getWidth(orderedText) - buttonOffset;
		this.buttonY = 0;
	}

	public interface Formatter {
		MutableText format(
				String prefix,
				String id,
				RepositoryItem repositoryItem,
				int amount,
				int amountOfItem,
				long lastForgeStarted,
				boolean usedForge,
				boolean childrenFinished,
				int depth);
	}

}
