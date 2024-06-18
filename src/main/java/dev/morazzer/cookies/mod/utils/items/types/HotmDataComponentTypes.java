package dev.morazzer.cookies.mod.utils.items.types;

import dev.morazzer.cookies.mod.utils.items.CookiesDataComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;

/**
 * Data components for the Heart of the Mountain perks.
 */
@SuppressWarnings("MissingJavadoc")
public class HotmDataComponentTypes {

    public static final ComponentType<Integer> HOTM_PERK_LEVEL;
    public static final ComponentType<Integer> HOTM_COST_NEXT_10;
    public static final ComponentType<Integer> HOTM_COST_ALL;
    public static final ComponentType<Boolean> HOTM_DISABLED;
    public static final ComponentType<String> HOTM_PERK_TYPE;

    static {
        HOTM_PERK_LEVEL = new CookiesDataComponent<>(Identifier.of("cookies:hotm/perk_level"));
        HOTM_COST_NEXT_10 = new CookiesDataComponent<>(Identifier.of("cookies:hotm/cost_next_10"));
        HOTM_COST_ALL = new CookiesDataComponent<>(Identifier.of("cookies:hotm/cost_all"));
        HOTM_DISABLED = new CookiesDataComponent<>(Identifier.of("cookies:hotm/disabled"));
        HOTM_PERK_TYPE = new CookiesDataComponent<>(Identifier.of("cookies:hotm/perk_type"));
    }
}
