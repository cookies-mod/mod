package codes.cookies.mod.config;

import com.google.gson.annotations.Expose;
import codes.cookies.mod.config.categories.CleanupConfig;
import codes.cookies.mod.config.categories.DevConfig;
import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.config.categories.FarmingConfig;
import codes.cookies.mod.config.categories.HelpersConfig;
import codes.cookies.mod.config.categories.ItemSearchConfig;
import codes.cookies.mod.config.categories.MiningConfig;
import codes.cookies.mod.config.categories.MiscConfig;
import codes.cookies.mod.config.system.Config;
import codes.cookies.mod.config.system.SearchCategory;
import codes.cookies.mod.config.system.ToggledCategory;
import net.minecraft.text.Text;

/**
 * Main config for the whole mod.
 */
@SuppressWarnings({"MissingJavadoc", "unused"})
public class CookiesConfig extends Config<CookiesConfig> {

    @Expose
    public MiscConfig miscConfig = new MiscConfig();
    @Expose
    public FarmingConfig farmingConfig = new FarmingConfig();
    @Expose
    public MiningConfig miningConfig = new MiningConfig();
    @Expose
    public HelpersConfig helpersConfig = new HelpersConfig();
    @Expose
    public CleanupConfig cleanupConfig = new CleanupConfig();
	@Expose
	public DungeonConfig dungeonConfig = new DungeonConfig();
	@Expose
	public ItemSearchConfig itemSearchConfig = new ItemSearchConfig();

    @Expose
    public DevConfig devConfig = new DevConfig();
    public SearchCategory searchCategory = new SearchCategory();
    public ToggledCategory toggledCategory = new ToggledCategory();


    @Override
    public Text getTitle() {
        return Text.empty();
    }
}
