package dev.morazzer.cookies.mod.config.system;

import com.google.gson.JsonElement;
import dev.morazzer.cookies.mod.translations.TranslationKey;
import dev.morazzer.cookies.mod.translations.TranslationKeys;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * A category in the config.
 */
@Getter
public abstract class Category implements SaveLoadHelper, TranslationKeys {

    private final ItemStack itemStack;
    private final Text name;
    private final Text description;

    @SuppressWarnings("MissingJavadoc")
    public Category(ItemStack itemStack, @TranslationKey @NotNull String translationKey) {
        this.itemStack = itemStack;
        this.name = Text.translatable(TranslationKeys.name(translationKey));
        this.description = Text.translatable(TranslationKeys.tooltip(translationKey));
    }

    /**
     * Gets the row of the category.
     *
     * @return The row.
     */
    public abstract Row getRow();

    /**
     * Gets the column of the inventory.
     *
     * @return The column.
     */
    public abstract int getColumn();

    /**
     * Whether the category uses a different offset.
     *
     * @return True or false.
     */
    public boolean isSpecial() {
        return false;
    }

    /**
     * @see SaveLoadHelper#load_(JsonElement)
     */
    @SuppressWarnings("MissingJavadoc")
    public final void load(JsonElement jsonObject) {
        load_(jsonObject);
    }

    /**
     * @see SaveLoadHelper#save_()
     */
    @SuppressWarnings("MissingJavadoc")
    public final JsonElement save() {
        return save_();
    }

}
