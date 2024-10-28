package dev.morazzer.cookies.mod.features.misc.utils.crafthelper;

import java.util.ArrayList;

import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.repository.RepositoryItem;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import dev.morazzer.cookies.mod.utils.cookies.Constants;
import dev.morazzer.cookies.mod.utils.skyblock.components.PressableField;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ClientSideInventory;
import dev.morazzer.cookies.mod.utils.skyblock.inventories.ItemBuilder;

import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class CraftHelperPlacement extends ClientSideInventory {

	private static final int UNSELECTED_COLOR = Constants.SUCCESS_COLOR;
	private final PressableField[] fields = new PressableField[CraftHelperLocation.values().length];
	boolean removeItemAfterwards = false;
	private CraftHelperLocation selected;
	CraftHelperInstance defaultInstance;
	boolean isClosed = false;

	public CraftHelperPlacement() {
		super(Text.translatable(TranslationKeys.CRAFT_HELPER_PLACEMENT), 6);
		this.getContents().fill(new ItemBuilder(Items.BLACK_STAINED_GLASS_PANE).hideTooltips().build());
		this.selected = ConfigManager.getConfig().helpersConfig.craftHelper.craftHelperLocation.getValue();
		this.defaultInstance = new CraftHelperInstance(RepositoryItem.of("TERMINATOR"), 1, new ArrayList<>());
	}

	@Override
	public void tick() {
		if (CraftHelperManager.getActive() == CraftHelperInstance.EMPTY && !isClosed) {
			CraftHelperManager.setActive(defaultInstance);
			removeItemAfterwards = true;
			if (!defaultInstance.hasCalculated) {
				defaultInstance.recalculate();
			}
		}
	}

	@Override
	protected void init() {
		super.init();
		this.add(
				new PressableField(0, this.getY() + 10, 10, this.cookies$getBackgroundHeight() - 20, UNSELECTED_COLOR),
				CraftHelperLocation.LEFT);
		this.add(new PressableField(
				this.getX() - 9,
				this.getY() + 10,
				10,
				this.cookies$getBackgroundHeight() - 20,
				UNSELECTED_COLOR), CraftHelperLocation.LEFT_INVENTORY);
		this.add(new PressableField(
				this.getX() + this.cookies$getBackgroundWidth() - 1,
				this.getY() + 10,
				10,
				this.cookies$getBackgroundHeight() - 20,
				UNSELECTED_COLOR), CraftHelperLocation.RIGHT_INVENTORY);
		this.add(new PressableField(
				this.width - 10,
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
			ConfigManager.getConfig().helpersConfig.craftHelper.craftHelperLocation.setValue(craftHelperLocation);
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
	public void close() {
		super.close();
		isClosed = true;
		if (this.removeItemAfterwards) {
			CraftHelperManager.remove();
		}
		ConfigManager.saveConfig(true, "edit-craft-helper-location");
	}
}
