package codes.cookies.mod.features.misc.utils.crafthelper.tooltips;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.data.profile.items.sources.ForgeItemSource;
import codes.cookies.mod.features.misc.utils.crafthelper.ItemTracker;
import codes.cookies.mod.features.misc.utils.crafthelper.ToolTipContext;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.repository.recipes.CraftRecipe;
import codes.cookies.mod.repository.recipes.ForgeRecipe;
import codes.cookies.mod.screen.inventory.ForgeRecipeScreen;
import codes.cookies.mod.utils.ColorUtils;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.maths.MathUtils;
import codes.cookies.mod.utils.minecraft.NonCacheMutableText;
import codes.cookies.mod.utils.minecraft.SupplierTextContent;
import codes.cookies.mod.utils.minecraft.TextBuilder;
import codes.cookies.mod.utils.skyblock.ForgeUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

public class NormalComponent extends CraftHelperComponent {

	private final ToolTipContext toolTipContext;
	private CraftHelperText text;
	private CraftHelperText secondText;

	public NormalComponent(ToolTipContext toolTipContext) {
		this.toolTipContext = toolTipContext;
	}

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

	@Override
	public void init() {
		secondText = CraftHelperText.of(Text.empty()
				.append(Text.literal(toolTipContext.getPrefix().replace("├", "│").replace("└", ""))
						.formatted(Formatting.GRAY)));
		secondText.setY(2);
		this.updateText();
	}

	private int getColor() {
		final double percentage = (double) toolTipContext.getAmount() / toolTipContext.getRequired();
		return ColorUtils.calculateBetween(
				Formatting.RED.getColorValue(),
				Formatting.GREEN.getColorValue(),
				percentage);
	}

	private Text getIcon() {
		if (toolTipContext.isDone()) {
			if (toolTipContext.getPrefix().isBlank()) {
				return Text.literal(Constants.Emojis.FLAG_FILLED).formatted(Formatting.GREEN);
			} else {
				return Text.literal(Constants.Emojis.YES).formatted(Formatting.GREEN, Formatting.BOLD);
			}
		} else {
			if (toolTipContext.isChildrenDone()) {
				if (toolTipContext.getPrefix().isBlank()) {
					return Text.literal(Constants.Emojis.FLAG_FILLED).formatted(Formatting.YELLOW);
				} else {
					return Text.literal(Constants.Emojis.WARNING).formatted(Formatting.YELLOW);
				}
			} else {
				if (toolTipContext.getPrefix().isBlank()) {
					return Text.literal(Constants.Emojis.FLAG_EMPTY).formatted(Formatting.RED);
				} else {
					return Text.literal(Constants.Emojis.NO).formatted(Formatting.RED, Formatting.BOLD);
				}
			}
		}
	}

	private void updateText() {
		final MutableText text = createText();
		if (toolTipContext.nonCachedText) {
			this.text = CraftHelperText.of(new NonCacheMutableText(text));
		} else {
			this.text = CraftHelperText.of(text);
		}
 	}

	private void collapse() {
		this.collapsed = !this.collapsed;
		this.updateText();
		if (this.collapsed) {
			toolTipContext.instance.setCollapsed(this.toolTipContext.getPath());
		} else {
			toolTipContext.instance.getCollapsed().remove(this.toolTipContext.getPath());
		}
		this.toolTipContext.instance.recalculate();
	}

	private List<Text> getHover() {
		List<Text> hover = new ArrayList<>();

		hover.add(toolTipContext.getRepositoryItem().getFormattedName());
		hover.add(Text.empty());

		int amount = toolTipContext.getAmount();
		if (amount != 0) {
			final ItemTracker.TrackedItem trackedItem = this.toolTipContext.getItemTracker()
					.get(toolTipContext.getRepositoryItem());
			final List<Pair<ItemSources, Integer>> usedSources = trackedItem.getUsedSources(amount);
			hover.add(Text.literal("Item Sources").formatted(Formatting.GREEN));
			for (Pair<ItemSources, Integer> usedSource : usedSources) {
				ItemSources sources = usedSource.getLeft();
				toolTipContext.getUsedSources().add(sources);
				int usedAmount = usedSource.getRight();
				hover.add(sources.getName()
						.copy()
						.formatted(Formatting.GRAY)
						.append(": ")
						.append(Text.literal(MathUtils.NUMBER_FORMAT.format(usedAmount))
								.formatted(Formatting.YELLOW)));
			}
			hover.add(Text.empty());

			if (toolTipContext.getUsedSources().contains(ItemSources.FORGE)) {
				final List<ForgeItemSource.Context> allForgeStart = toolTipContext.getItemTracker()
						.get(toolTipContext.getRepositoryItem())
						.getAllForgeStart();
				hover.add(Text.literal("Forge Slots").formatted(Formatting.GREEN));
				for (ForgeItemSource.Context context : allForgeStart) {
					final TextBuilder forgeTime = getForgeTime(OptionalLong.of(context.startTime()));
					hover.add(new NonCacheMutableText(Text.literal("Slot #%s: ".formatted(context.slot() + 1)).formatted(Formatting.GRAY).append(forgeTime.build().formatted(Formatting.DARK_GRAY))));
				}

				hover.add(Text.empty());
			}
		}

		if (DevUtils.isEnabled(DEBUG)) {
			hover.add(Text.literal("A: " + toolTipContext.getAmount()));
			hover.add(Text.literal("ATP: : " + toolTipContext.getAmountThroughParents()));
			hover.add(Text.literal("Req: " + toolTipContext.getRequired()));
			hover.add(Text.literal("Item: " + toolTipContext.getRepositoryItem().getInternalId()));
			hover.add(Text.empty());
		}


		if (toolTipContext.isDone()) {
			hover.add(Text.literal("You finished this item!").formatted(Formatting.GREEN));
		} else {
			Formatting formatting;

			if (toolTipContext.isChildrenDone()) {
				formatting = Formatting.YELLOW;
				hover.add(Text.literal("You have all materials to craft this item!").formatted(Formatting.YELLOW));
			} else {
				formatting = Formatting.RED;
				hover.add(Text.literal("You are missing some materials to craft this item!").formatted(Formatting.RED));
			}
			if (!toolTipContext.getRepositoryItem().getRecipes().isEmpty()) {
				hover.add(Text.literal("Click to open recipe!").formatted(formatting));
			}
		}

		return hover;
	}

	public Text createIconWithStyle(List<Text> hover) {
		return new TextBuilder(getIcon()).onHover(hover)
				.setRunnable(this::clickText).build();
	}

	public Text createCountWithStyle(List<Text> hover) {
		return new TextBuilder(Text.literal(" %s/%s ".formatted(
				MathUtils.NUMBER_FORMAT.format(toolTipContext.getAmount()),
				MathUtils.NUMBER_FORMAT.format(toolTipContext.getRequired()))).withColor(getColor()))
				.onHover(hover).setRunnable(this::clickText).build();
	}

	public Text createNameWithStyle(List<Text> hover) {
		return new TextBuilder(toolTipContext.getRepositoryItem().getFormattedName()).onHover(hover)
				.setRunnable(this::clickText)
				.build();
	}

	private void clickText() {
		if (toolTipContext.isDone()) {
			return;
		}
		final RepositoryItem repositoryItem = this.toolTipContext.getRepositoryItem();
		if (repositoryItem.getRecipes().stream().anyMatch(CraftRecipe.class::isInstance)) {
			CookiesUtils.sendCommand("viewrecipe " + toolTipContext.getRepositoryItem().getInternalId());
			Optional.ofNullable(MinecraftClient.getInstance().currentScreen).ifPresent(Screen::close);
		} else if (repositoryItem.getRecipes().stream().anyMatch(ForgeRecipe.class::isInstance)) {
			CookiesMod.openScreen(new ForgeRecipeScreen(repositoryItem.getRecipes()
					.stream()
					.filter(ForgeRecipe.class::isInstance)
					.map(ForgeRecipe.class::cast)
					.findFirst()
					.orElseThrow(), null));
		}
	}

	public MutableText createText() {
		final List<Text> hover = getHover();

		return new TextBuilder(Text.literal(toolTipContext.getPrefix()).formatted(Formatting.GRAY))
				.append(createIconWithStyle(hover))
				.append(createCountWithStyle(hover))
				.append(createNameWithStyle(hover))
				.append(createSpecialWithStyle(hover))
				.build();
	}

	private Text createSpecialWithStyle(List<Text> hover) {
		TextBuilder textBuilder = new TextBuilder(Text.literal(" ")).formatted(Formatting.DARK_GRAY);
		if (toolTipContext.getUsedSources().contains(ItemSources.CHESTS)) {
			textBuilder.append("\uD83E\uDDF0 ");
		}
		if (toolTipContext.getUsedSources().contains(ItemSources.FORGE)) {
			final OptionalLong lastForgeStarted = toolTipContext.getItemTracker()
					.get(toolTipContext.getRepositoryItem())
					.getLastForgeStarted();
			textBuilder.append("(");
			final TextBuilder forgeTime = getForgeTime(lastForgeStarted);
			forgeTime.onHover(hover).setRunnable(this::clickText).formatted(Formatting.DARK_GRAY);
			textBuilder.append(forgeTime.build());
			textBuilder.append(") ");
			toolTipContext.nonCachedText = true;
		}
		if (toolTipContext.isHasChildren()) {
			String fold;
			if (collapsed) {
				fold = "◀ ";
			} else {
				fold = "▼ ";
			}
			textBuilder.append(new TextBuilder(fold).formatted(Formatting.GRAY)
					.setRunnable(this::collapse)
					.onHover(List.of(Text.literal("Click to collapse!").formatted(Formatting.GRAY)))
					.build());
		}
		textBuilder.onHover(hover).setRunnable(this::clickText);
		if (toolTipContext.nonCachedText) {
			return new NonCacheMutableText(textBuilder.build());
		}

		return textBuilder.build();
	}

	private @NotNull TextBuilder getForgeTime(OptionalLong lastForgeStarted) {
		TextBuilder forgeTime;
		if (lastForgeStarted.isEmpty()) {
			forgeTime = new TextBuilder(Text.literal("Unknown"));
		} else {
			final Supplier<String> supplier = getSupplier(
					() -> ForgeUtils.getForgeTime(toolTipContext.getRepositoryItem()),
					lastForgeStarted.getAsLong());
			final SupplierTextContent supplierTextContent = new SupplierTextContent(supplier);
			final NonCacheMutableText nonCacheMutableText = new NonCacheMutableText(MutableText.of(
					supplierTextContent));
			forgeTime = new TextBuilder(nonCacheMutableText);
		}
		return forgeTime;
	}

	@Override
	public List<CraftHelperComponentPart> getTextParts() {
		return List.of(text, secondText);
	}

	@Override
	public void drawText(
			TextRenderer textRenderer,
			int x,
			int y,
			Matrix4f matrix,
			VertexConsumerProvider.Immediate vertexConsumers
	) {
		textRenderer.draw(
				this.text.text(),
				(float) x + this.text.x(),
				(float) y + this.text.y(),
				Colors.WHITE,
				true,
				matrix,
				vertexConsumers,
				TextRenderer.TextLayerType.NORMAL,
				0,
				15728880);
		textRenderer.draw(
				this.secondText.text(),
				(float) x + this.secondText.x(),
				(float) y + this.secondText.y(),
				Colors.WHITE,
				true,
				matrix,
				vertexConsumers,
				TextRenderer.TextLayerType.NORMAL,
				0,
				15728880);
	}

	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return textRenderer.getWidth(this.text.text());
	}
}
