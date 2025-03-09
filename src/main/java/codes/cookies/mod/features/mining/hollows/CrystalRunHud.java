package codes.cookies.mod.features.mining.hollows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import codes.cookies.mod.data.mining.crystal.CrystalStatus;
import codes.cookies.mod.data.mining.crystal.CrystalType;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.data.profile.items.Item;
import codes.cookies.mod.data.profile.items.ItemSources;
import codes.cookies.mod.generated.Regions;
import codes.cookies.mod.render.hud.HudManager;
import codes.cookies.mod.render.hud.elements.MultiLineTextHudElement;
import codes.cookies.mod.render.hud.internal.HudEditAction;
import codes.cookies.mod.repository.RepositoryItem;
import codes.cookies.mod.services.mining.CrystalStatusService;
import codes.cookies.mod.utils.items.ItemUtils;
import codes.cookies.mod.utils.skyblock.LocationUtils;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Hud to display current crystal status while in the crystal hollows.<br>
 * Also displays information about the available robot parts and available tools in their respective area.
 */
@Deprecated
public class CrystalRunHud extends MultiLineTextHudElement {

	private long lastUpdate;
	private final List<Text> defaultTexts = new ArrayList<>();
	private final Map<Regions, List<Text>> regionTexts = new HashMap<>();

	public CrystalRunHud() {
		super(Identifier.of("cookies", "mining/crystal_hud"));
		HudManager.register(this);
	}

	@Override
	protected List<Text> getText() {
		if (ProfileStorage.getCurrentProfile().isEmpty()) {
			return List.of();
		}
		this.updateItemIndex();
		return regionTexts.getOrDefault(LocationUtils.getRegion(), defaultTexts);
	}

	private void updateItemIndex() {
		if (this.lastUpdate + 1000 > System.currentTimeMillis()) {
			return;
		}
		lastUpdate = System.currentTimeMillis();

		final Collection<Item<?>> relevantItemsOnPlayer = this.getRelevantItemsOnPlayer();

		this.createLostPrecursorCityTexts(relevantItemsOnPlayer);
		this.createMinesOfDivanText(relevantItemsOnPlayer);
		this.createDefaultTexts();
	}

	private void createMinesOfDivanText(Collection<Item<?>> relevantItems) {
		final List<Text> minesOfDivanText = new ArrayList<>();

		this.addItem("DWARVEN_LAPIS_SWORD", relevantItems, minesOfDivanText);
		this.addItem("DWARVEN_GOLD_HAMMER", relevantItems, minesOfDivanText);
		this.addItem("DWARVEN_DIAMOND_AXE", relevantItems, minesOfDivanText);
		this.addItem("DWARVEN_EMERALD_HAMMER", relevantItems, minesOfDivanText);

		this.regionTexts.put(Regions.MINES_OF_DIVAN, minesOfDivanText);
	}

	private void createLostPrecursorCityTexts(Collection<Item<?>> relevantItems) {
		final List<Text> lostPrecursorCityText = new ArrayList<>();

		this.addItem("CONTROL_SWITCH", relevantItems, lostPrecursorCityText);
		this.addItem("ELECTRON_TRANSMITTER", relevantItems, lostPrecursorCityText);
		this.addItem("FTX_3070", relevantItems, lostPrecursorCityText);
		this.addItem("ROBOTRON_REFLECTOR", relevantItems, lostPrecursorCityText);
		this.addItem("SUPERLITE_MOTOR", relevantItems, lostPrecursorCityText);
		this.addItem("SYNTHETIC_HEART", relevantItems, lostPrecursorCityText);
		this.addItem("PRECURSOR_APPARATUS", relevantItems, lostPrecursorCityText);

		this.regionTexts.put(Regions.LOST_PRECURSOR_CITY, lostPrecursorCityText);
	}

	private void addItem(String id, Collection<Item<?>> relevantItems, List<Text> texts) {
		final int itemAmountAvailable = this.getAmountOfItemAvailable(relevantItems, id);

		final RepositoryItem repositoryItem = RepositoryItem.ofOrEmpty(id);
		final Text itemName = repositoryItem.getFormattedName();
		final MutableText append = Text.empty().append(itemName)
				.append(Text.literal(" - ").formatted(Formatting.GRAY))
				.append(Text.literal(String.valueOf(itemAmountAvailable)).formatted(Formatting.YELLOW));
		texts.add(append);
	}

	private void createDefaultTexts() {
		final List<Text> defaultText = new ArrayList<>();
		this.addCrystalStatusFor(CrystalType.JADE, defaultText);
		this.addCrystalStatusFor(CrystalType.AMBER, defaultText);
		this.addCrystalStatusFor(CrystalType.AMETHYST, defaultText);
		this.addCrystalStatusFor(CrystalType.SAPPHIRE, defaultText);
		this.addCrystalStatusFor(CrystalType.TOPAZ, defaultText);
		this.defaultTexts.clear();
		this.defaultTexts.addAll(defaultText);
	}

	private int getAmountOfItemAvailable(Collection<Item<?>> items, String itemId) {
		return items.stream()
				.filter(item -> itemId.equalsIgnoreCase(ItemUtils.getId(item)))
				.mapToInt(Item::amount)
				.sum();
	}

	private Collection<Item<?>> getRelevantItemsOnPlayer() {
		return ItemSources.getItems(ItemSources.INVENTORY, ItemSources.STORAGE, ItemSources.SACKS);
	}

	private void addCrystalStatusFor(CrystalType type, List<Text> texts) {
		final String name = StringUtils.capitalize(type.name().toLowerCase(Locale.ROOT));
		texts.add(Text.literal(name)
				.formatted(type.getFormatting())
				.append(Text.literal(" - ").formatted(Formatting.GRAY))
				.append(getStatusForOrNotFound(type).getText()));
	}


	private CrystalStatus getStatusForOrNotFound(CrystalType type) {
		return CrystalStatusService.getCrystalStatus(type).orElse(CrystalStatus.NOT_FOUND);
	}

	@Override
	public int getMaxRows() {
		return 7;
	}

	@Override
	public boolean shouldRender() {
		if (this.hudEditAction == HudEditAction.SHOW_ALL) {
			return true;
		}
		if (this.hudEditAction == HudEditAction.ALL_ENABLED) {
			return true;
		}

		return LocationUtils.Island.CRYSTAL_HOLLOWS.isActive();
	}

	@Override
	public int getWidth() {
		if (this.hudEditAction != HudEditAction.NONE) {
			return 170;
		}

		return super.lastWidth;
	}

	@Override
	public Text getName() {
		return Text.literal("Crystals").formatted(Formatting.RED);
	}
}
