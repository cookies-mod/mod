package dev.morazzer.cookies.mod.config;

import com.google.gson.annotations.Expose;
import dev.morazzer.cookies.mod.config.categories.CleanupConfig;
import dev.morazzer.cookies.mod.config.categories.DevConfig;
import dev.morazzer.cookies.mod.config.categories.FarmingConfig;
import dev.morazzer.cookies.mod.config.categories.HelpersConfig;
import dev.morazzer.cookies.mod.config.categories.MiningConfig;
import dev.morazzer.cookies.mod.config.categories.MiscConfig;
import dev.morazzer.cookies.mod.config.system.Config;
import dev.morazzer.cookies.mod.config.system.SearchCategory;
import dev.morazzer.cookies.mod.config.system.ToggledCategory;
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
    public DevConfig devConfig = new DevConfig();
    public SearchCategory searchCategory = new SearchCategory();
    public ToggledCategory toggledCategory = new ToggledCategory();


    @Override
    public Text getTitle() {
        return Text.literal("Test");
    }
}
