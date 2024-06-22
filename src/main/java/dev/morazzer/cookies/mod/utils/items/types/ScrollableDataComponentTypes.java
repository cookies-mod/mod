package dev.morazzer.cookies.mod.utils.items.types;

import dev.morazzer.cookies.mod.utils.items.CookiesDataComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;

/**
 * All fields that are used to store the values of the scrollable tooltips.
 */
@SuppressWarnings("MissingJavadoc")
public class ScrollableDataComponentTypes {


    public static final ComponentType<Integer> TOOLTIP_OFFSET_VERTICAL =
        new CookiesDataComponent<>(Identifier.of("cookies:scrollable_tooltip/vertical"));
    public static final ComponentType<Integer> TOOLTIP_OFFSET_HORIZONTAL =
        new CookiesDataComponent<>(Identifier.of("cookies:scrollable_tooltip/horizontal"));
    public static final ComponentType<Integer> TOOLTIP_OFFSET_FIRST =
        new CookiesDataComponent<>(Identifier.of("cookies:scrollable_tooltip/first"));
    public static final ComponentType<Integer> TOOLTIP_OFFSET_LAST =
        new CookiesDataComponent<>(Identifier.of("cookies:scrollable_tooltip/last"));

}
