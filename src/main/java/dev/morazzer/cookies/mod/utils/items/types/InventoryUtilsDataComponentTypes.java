package dev.morazzer.cookies.mod.utils.items.types;

import dev.morazzer.cookies.mod.utils.items.CookiesDataComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;

/**
 * Data components to store states of items.
 */
public class InventoryUtilsDataComponentTypes {

    public static final ComponentType<Boolean> MODIFIED =
        new CookiesDataComponent<>(Identifier.of("cookies:inventory/modified"));

}
