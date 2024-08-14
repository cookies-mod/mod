package dev.morazzer.cookies.mod.config.categories;

import com.google.gson.annotations.Expose;
import dev.morazzer.cookies.mod.config.system.Category;
import dev.morazzer.cookies.mod.config.system.Hidden;
import dev.morazzer.cookies.mod.config.system.Parent;
import dev.morazzer.cookies.mod.config.system.Row;
import dev.morazzer.cookies.mod.config.system.options.BooleanOption;
import dev.morazzer.cookies.mod.config.system.options.SliderOption;
import dev.morazzer.cookies.mod.config.system.options.TextDisplayOption;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Category related to all miscellaneous settings.
 */
@SuppressWarnings({"MissingJavadoc", "unused"})
public class MiscConfig extends Category {

    @Expose
    public BooleanOption enableScrollableTooltips = new BooleanOption(CONFIG_MISC_ENABLE_SCROLL_TOOLTIPS, true);

    @Expose
    public BooleanOption enableStoragePreview = new BooleanOption(CONFIG_MISC_STORAGE_PREVIEW, false);

    @Expose
    public BooleanOption showPing = new BooleanOption(CONFIG_MISC_SHOW_PING, false);

    @Parent
    public TextDisplayOption itemSubCategory = new TextDisplayOption(CONFIG_MISC_CATEGORIES_ITEMS);

    @Expose
    public BooleanOption showItemCreationDate = new BooleanOption(CONFIG_MISC_SHOW_ITEM_CREATION_DATE, true);

    @Expose
    public BooleanOption showItemDonatedToMuseum = new BooleanOption(CONFIG_MISC_SHOW_ITEM_DONATED_TO_MUSEUM, true);

    @Expose
    public BooleanOption showItemNpcValue = new BooleanOption(CONFIG_MISC_SHOW_ITEM_NPC_VALUE, true);

    @Parent
    public TextDisplayOption renderCategory = new TextDisplayOption(CONFIG_MISC_CATEGORIES_RENDER);

    @Expose
    public BooleanOption hideOwnArmor = new BooleanOption(CONFIG_MISC_HIDE_OWN_ARMOR, false);

    @Expose
    public BooleanOption hideOtherArmor = new BooleanOption(CONFIG_MISC_HIDE_OTHER_ARMOR, false);

    @Expose
    public BooleanOption showDyeArmor = new BooleanOption(CONFIG_MISC_SHOW_DYE_ARMOR, false);

    @Expose
    public BooleanOption hideFireOnEntities = new BooleanOption(CONFIG_MISC_HIDE_FIRE_ON_ENTITIES, false);

    @Expose
    public BooleanOption hideLightningBolt = new BooleanOption(CONFIG_MISC_HIDE_LIGHTNING_BOLT, false);

    @Parent
    public TextDisplayOption renderUiCategory = new TextDisplayOption(CONFIG_MISC_CATEGORIES_RENDER_UI);

    @Expose
    public BooleanOption hidePotionEffects = new BooleanOption(CONFIG_MISC_HIDE_POTION_EFFECTS, false);

    @Expose
    public BooleanOption hideHealth = new BooleanOption(CONFIG_MISC_HIDE_HEALTH, false);

    @Expose
    public BooleanOption hideArmor = new BooleanOption(CONFIG_MISC_HIDE_ARMOR, false);

    @Expose
    public BooleanOption hideFood = new BooleanOption(CONFIG_MISC_HIDE_FOOD, false);

    @Parent
    public TextDisplayOption renderInventoryCategory = new TextDisplayOption(CONFIG_MISC_CATEGORIES_RENDER_INVENTORY);

    @Expose
    public BooleanOption showPetLevelAsStackSize = new BooleanOption(CONFIG_MISC_SHOW_PET_LEVEL, false);

    @Expose
    public BooleanOption showPetRarityInLevelText =
        new BooleanOption(CONFIG_MISC_SHOW_PET_RARITY_IN_LEVEL_TEXT, false).onlyIf(this.showPetLevelAsStackSize);

    @Expose
    public BooleanOption showForgeRecipeStack = new BooleanOption(CONFIG_MISC_SHOW_FORGE_RECIPE_STACK, true);

    @Hidden
    @Expose
    public SliderOption<Integer> forgeRecipeSlot = SliderOption.integerOption(Text.empty(), Text.empty(), 47);

    public MiscConfig() {
        super(new ItemStack(Items.COMPASS), CONFIG_MISC);
        this.hideOtherArmor.withCallback((oldValue, newValue) -> this.showDyeArmor.setActive(
            this.hideOwnArmor.getValue() || newValue));
        this.hideOwnArmor.withCallback((oldValue, newValue) -> this.showDyeArmor.setActive(
            this.hideOtherArmor.getValue() || newValue));
        this.showDyeArmor.setActive(this.hideOwnArmor.getValue() || this.hideOtherArmor.getValue());
    }

    @Override
    public Row getRow() {
        return Row.TOP;
    }

    @Override
    public int getColumn() {
        return 0;
    }
}
