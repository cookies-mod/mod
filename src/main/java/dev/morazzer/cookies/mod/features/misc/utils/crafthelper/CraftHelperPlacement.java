package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.data.profile.items.ItemSources;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculationResult;
import dev.morazzer.cookies.mod.repository.recipes.calculations.RecipeCalculator;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.skyblock.components.PressableField;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ClientSideInventory;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class CraftHelperPlacement extends ClientSideInventory {

	private final PressableField[] fields = new PressableField[CraftHelperLocation.values().length];
	private CraftHelperLocation selected;
	private static final int UNSELECTED_COLOR = 0x44FFFFFF & Constants.SUCCESS_COLOR;
	private List<OrderedText> craftHelperText = Collections.emptyList();
	private int yOffset = 0;

	public CraftHelperPlacement() {
		super(Text.translatable(TranslationKeys.CRAFT_HELPER_PLACEMENT), 6);
		this.getContents().fill(new ItemBuilder(Items.BLACK_STAINED_GLASS_PANE).hideTooltips().build());
		this.selected = ConfigManager.getConfig().helpersConfig.craftHelperLocation.getValue();
	}

	@Override
	public void tick() {
		if (CraftHelper.getInstance().getCalculation() == null && this.craftHelperText.isEmpty()) {
			LinkedList<MutableText> texts = new LinkedList<>();
			final RecipeCalculationResult calculate = RecipeCalculator.calculate(RepositoryItem.of("TERMINATOR"));
			CraftHelper.append("",
					texts,
					calculate,
					0,
					new EvaluationContext(calculate, null),
					new StackCountContext(ItemSources.none()),
					CraftHelper::formatted);

			this.craftHelperText = texts.stream().map(Text::asOrderedText).toList();
			this.yOffset = (this.craftHelperText.size() * 9) / 2;
		}
	}

	@Override
	protected void init() {
		super.init();
		this.add(
				new PressableField(0, this.getY() + 10, 10, this.cookies$getBackgroundHeight() - 20, UNSELECTED_COLOR),
				CraftHelperLocation.LEFT);
		this.add(new PressableField(this.getX() - 9,
				this.getY() + 10,
				10,
				this.cookies$getBackgroundHeight() - 20,
				UNSELECTED_COLOR), CraftHelperLocation.LEFT_INVENTORY);
		this.add(new PressableField(this.getX() + this.cookies$getBackgroundWidth() - 1,
				this.getY() + 10,
				10,
				this.cookies$getBackgroundHeight() - 20,
				UNSELECTED_COLOR), CraftHelperLocation.RIGHT_INVENTORY);
		this.add(new PressableField(this.width - 10,
				this.getY() + 10,
				10,
				this.cookies$getBackgroundHeight() - 20,
				UNSELECTED_COLOR), CraftHelperLocation.RIGHT);
		final PressableField field = this.getField(this.selected);
		field.setShouldRender(false);
	}

	private void add(PressableField field, CraftHelperLocation location) {
		field.setRunnable(this.click(location));
		this.fields[location.ordinal()] = field;
		this.addDrawableChild(field);
	}

	private Runnable click(CraftHelperLocation craftHelperLocation) {
		return () -> {
			final PressableField field = this.getField(craftHelperLocation);
			if (this.selected == craftHelperLocation) {
				return;
			}
			ConfigManager.getConfig().helpersConfig.craftHelperLocation.setValue(craftHelperLocation);
			field.setShouldRender(false);
			final PressableField previous = this.getField(this.selected);
			previous.setColor(UNSELECTED_COLOR);
			previous.setShouldRender(true);
			this.selected = craftHelperLocation;
		};
	}

	private PressableField getField(CraftHelperLocation craftHelperLocation) {
		return this.fields[craftHelperLocation.ordinal()];
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		final PressableField field = this.getField(this.selected);
		if (this.craftHelperText.isEmpty()) {
			return;
		}
		context.drawTooltip(this.textRenderer,
				this.craftHelperText,
				new CraftHelperTooltipPositioner(),
				field.getX(),
				this.cookies$getY() + (this.cookies$getBackgroundHeight() / 2) - this.yOffset);
	}

	@Override
	public void close() {
		super.close();
		ConfigManager.saveConfig(true, "edit-craft-helper-location");
	}
}
