package dev.morazzer.cookies.mod.config.system;

import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * A category in the config.
 */
public abstract class Category implements SaveLoadHelper {

    private final ItemStack itemStack;

    @SuppressWarnings("MissingJavadoc")
    public Category(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * The display name that will be displayed in the config.
     *
     * @return The name.
     */
    public abstract Text getName();

    /**
     * A small description of what is in this category.
     *
     * @return A description.
     */
    public abstract Text getDescription();

    /**
     * Gets the stack that represents the category.
     *
     * @return The stack.
     */
    public ItemStack getItemStack() {
        return this.itemStack;
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
