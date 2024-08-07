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
    public BooleanOption enableScrollableTooltips = new BooleanOption(Text.literal("Scrollable Tooltips"),
        true,
        Text.literal("Allows you to scroll through tooltips"),
        Text.empty(),
        Text.literal("CTRL + Scroll -> move horizontal"),
        Text.literal("SHIFT + Scroll -> chop tooltips"));

    @Expose
    public BooleanOption enableStoragePreview = new BooleanOption(Text.literal("Storage Preview"),
        Text.literal("Shows a preview of the content in the storage."),
        false);

    @Expose
    public BooleanOption showPing = new BooleanOption(Text.literal("Show Ping"),
        Text.literal("Shows the ping in the action bar"),
        false);

    @Parent
    public TextDisplayOption itemSubCategory = new TextDisplayOption(Text.literal("Items"), Text.literal(""));

    @Expose
    public BooleanOption showItemCreationDate =
        new BooleanOption(Text.literal("Creation date"), Text.literal("Shows the creation date of an item."), true);

    @Expose
    public BooleanOption showItemDonatedToMuseum = new BooleanOption(Text.literal("Donated to museum"),
        Text.literal("Shows whether the item is donated to the museum or not."),
        true);

    @Expose
    public BooleanOption showItemNpcValue =
        new BooleanOption(Text.literal("NPC Value"), Text.literal("Show the npc value of the item."), true);

    @Parent
    public TextDisplayOption renderCategory = new TextDisplayOption(Text.literal("Render"), Text.literal(""));

    @Expose
    public BooleanOption hideOwnArmor =
        new BooleanOption(Text.literal("Hide own armor"), Text.literal("Hides your own armor."), false);

    @Expose
    public BooleanOption hideOtherArmor =
        new BooleanOption(Text.literal("Hide others armor"), Text.literal("Hides others armor."), false);

    @Expose
    public BooleanOption showDyeArmor = new BooleanOption(Text.literal("Show armor if dyed"),
        Text.literal("Shows the armor if a dye is applied to it."),
        false);

    @Expose
    public BooleanOption hideFireOnEntities =
        new BooleanOption(Text.literal("Hide fire"), Text.literal("Hide fire from entities."), false);

    @Expose
    public BooleanOption hideLightningBolt =
        new BooleanOption(Text.literal("Hide lightning"), Text.literal("Hide the lightning bolt entity."), false);

    @Parent
    public TextDisplayOption renderUiCategory = new TextDisplayOption(Text.literal("Render - UI"), Text.literal(""));

    @Expose
    public BooleanOption hidePotionEffects = new BooleanOption(Text.literal("Hide inventory potions"),
        Text.literal("Hides potion effects from the inventory."),
        false);

    @Expose
    public BooleanOption hideHealth =
        new BooleanOption(Text.literal("Hide health bar"), Text.literal("Hide health bar from ui."), false);

    @Expose
    public BooleanOption hideArmor =
        new BooleanOption(Text.literal("Hide armor bar"), Text.literal("Hide armor bar from ui."), false);

    @Expose
    public BooleanOption hideFood =
        new BooleanOption(Text.literal("Hide food bar"), Text.literal("Hide food bar from ui."), false);

    @Parent
    public TextDisplayOption renderInventoryCategory =
        new TextDisplayOption(Text.literal("Render - Inventory"), Text.literal(""));

    @Expose
    public BooleanOption showPetLevelAsStackSize =
        new BooleanOption(Text.literal("Show pet level"), Text.literal("Shows the pet level as stack size."), false);

    @Expose
    public BooleanOption showPetRarityInLevelText = new BooleanOption(Text.literal("Show rarity in level"),
        Text.literal("Shows the pet level in the color of the rarity"),
        false).onlyIf(this.showPetLevelAsStackSize);

    @Expose
    public BooleanOption showForgeRecipeStack = new BooleanOption(Text.literal("Show forge recipes"),
        Text.literal("Shows forge recipes in the recipe book"),
        true);

    @Hidden
    @Expose
    public SliderOption<Integer> forgeRecipeSlot = SliderOption.integerOption(Text.empty(), Text.empty(), 47);

    public MiscConfig() {
        super(new ItemStack(Items.COMPASS));
        this.hideOtherArmor.withCallback((oldValue, newValue) -> this.showDyeArmor.setActive(
            this.hideOwnArmor.getValue() || newValue));
        this.hideOwnArmor.withCallback((oldValue, newValue) -> this.showDyeArmor.setActive(
            this.hideOtherArmor.getValue() || newValue));
        this.showDyeArmor.setActive(this.hideOwnArmor.getValue() || this.hideOtherArmor.getValue());
    }

    @Override
    public Text getName() {
        return Text.literal("Misc Config");
    }

    @Override
    public Text getDescription() {
        return Text.literal("Miscellaneous settings");
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
