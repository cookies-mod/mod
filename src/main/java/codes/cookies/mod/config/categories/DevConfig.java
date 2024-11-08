package codes.cookies.mod.config.categories;

import codes.cookies.mod.api.ApiManager;
import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.Parent;
import codes.cookies.mod.config.system.Row;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.ButtonOption;
import codes.cookies.mod.config.system.options.StringInputOption;
import codes.cookies.mod.config.system.options.TextDisplayOption;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Config category that contains dev related settings.
 */
public class DevConfig extends Category {
	public static DevConfig getInstance() {
		return ConfigManager.getConfig().devConfig;
	}

    public BooleanOption hideConsoleSpam = new BooleanOption(CONFIG_DEV_HIDE_CONSOLE_SPAM, true);


	@Parent
	public TextDisplayOption repoOption = new TextDisplayOption(CONFIG_DEV_REPO);
	public StringInputOption dataRepo = new StringInputOption(CONFIG_DEV_DATA_REPO, "cookies-mod/data");
	public StringInputOption dataRepoBranch = new StringInputOption(CONFIG_DEV_DATA_REPO_BRANCH, "main");

	@Parent
	public TextDisplayOption backendOption = new TextDisplayOption(CONFIG_DEV_BACKEND);
	public BooleanOption connectToBackend = new BooleanOption(CONFIG_DEV_BACKEND_CONNECT, true);

	public final StringInputOption backendUrl = new StringInputOption(CONFIG_DEV_BACKEND_SERVER, "https://api.cookies-mod.cloud/");

	public final ButtonOption reconnectApiButton = new ButtonOption(CONFIG_DEV_BACKEND_RECONNECT, ApiManager::reconnect, CONFIG_DEV_BACKEND_RECONNECT_VALUE);

	public final BooleanOption useVersionSuffix = new BooleanOption(CONFIG_DEV_BACKEND_VERSION_SUFFIX, true);

    @SuppressWarnings("MissingJavadoc")
    public DevConfig() {
        super(new ItemStack(Items.COMPARATOR), CONFIG_DEV);
    }

    @Override
    public Row getRow() {
        return Row.BOTTOM;
    }

    @Override
    public int getColumn() {
        return 5;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
