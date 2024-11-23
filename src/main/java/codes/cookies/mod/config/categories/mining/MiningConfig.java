package codes.cookies.mod.config.categories.mining;

import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.categories.mining.powder.PowderTrackerHudFoldable;
import codes.cookies.mod.config.categories.mining.shaft.ShaftConfig;
import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.HudSetting;
import codes.cookies.mod.config.system.Parent;
import codes.cookies.mod.config.system.Row;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.TextDisplayOption;
import codes.cookies.mod.features.mining.hollows.CrystalRunHud;
import codes.cookies.mod.features.mining.hollows.MinesOfDivanHelper;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * All settings related to mining.
 */
@SuppressWarnings("MissingJavadoc")
public class MiningConfig extends Category {

    public Predicate<Object> updateHotm;

    public BooleanOption modifyCommissions = new BooleanOption(CONFIG_MINING_MODIFY_COMMISSIONS, false);
    public BooleanOption puzzlerSolver = new BooleanOption(CONFIG_MINING_PUZZLER_SOLVER, false);
    public BooleanOption modHelper = new BooleanOption(CONFIG_MINING_MOD_HELPER, false)
			.withCallback(MinesOfDivanHelper::reset);

	public ShaftConfig shaftConfig = new ShaftConfig();
	public PowderTrackerHudFoldable powderTrackerHud = new PowderTrackerHudFoldable();
	@HudSetting(CrystalRunHud.class)
	public BooleanOption crystalHud = new BooleanOption(CONFIG_MINING_MOD_CRYSTAL_HUD, true);

    @Parent
    public TextDisplayOption hotmParentDisplay = new TextDisplayOption(CONFIG_MINING_CATEGORIES_HOTM);
    public BooleanOption showHotmPerkLevelAsStackSize = new BooleanOption(CONFIG_MINING_SHOW_HOTM_PERK_LEVEL_AS_STACK_SIZE, false);
    public BooleanOption highlightDisabledHotmPerks = new BooleanOption(CONFIG_MINING_HIGHLIGHT_DISABLED_HOTM_PERKS, false);
    public BooleanOption showNext10Cost = new BooleanOption(CONFIG_MINING_SHOW_NEXT_10_COST, false);
    public BooleanOption showTotalCost = new BooleanOption(CONFIG_MINING_SHOW_TOTAL_COST, false);

    public MiningConfig() {
        super(new ItemStack(Items.DIAMOND_PICKAXE), CONFIG_MINING);
        updateHotm =
            ((Predicate<Object>) (o -> showHotmPerkLevelAsStackSize.getValue())).or(o -> highlightDisabledHotmPerks.getValue())
                .or(o -> showNext10Cost.getValue())
                .or(o -> showTotalCost.getValue());
    }

	public static MiningConfig getInstance() {
		return ConfigManager.getConfig().miningConfig;
	}

	@Override
    public Row getRow() {
        return Row.TOP;
    }

    @Override
    public int getColumn() {
        return 2;
    }
}
