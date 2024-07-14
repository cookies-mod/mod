package dev.morazzer.cookies.mod.utils.items.types;

import dev.morazzer.cookies.mod.utils.items.CookiesDataComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;

/**
 * Data components for various features.
 */
public class MiscDataComponentTypes {

    public static final ComponentType<Boolean> ANVIL_HELPER_MODIFIED;

    static {
        ANVIL_HELPER_MODIFIED = new CookiesDataComponent<>(Identifier.of("cookies", "anvil_helper_modified"));
    }

}
