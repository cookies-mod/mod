package codes.cookies.mod.config.system.parsed;

import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.Row;
import codes.cookies.mod.config.system.SearchCategory;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * A processed category that is generic for all categories.
 */
public class ProcessedCategory {

    @Getter
    private final List<ProcessedOption<?, ?>> processedOptions = new ArrayList<>();
    private final Category category;

    /**
     * Creates a new processed category.
     *
     * @param category The category to be processed.
     */
    public ProcessedCategory(final Category category) {
        this.category = category;
    }

    /**
     * Gets the name of the category.
     *
     * @return The name.
     */
    public Text getName() {
        return this.category.getName();
    }

    /**
     * Gets the description of the category.
     *
     * @return The description.
     */
    public Text getDescription() {
        return this.category.getDescription();
    }

    /**
     * Gets the item stack of the category.
     *
     * @return The item stack.
     */
    public ItemStack getItemStack() {
        return this.category.getItemStack();
    }

    /**
     * Gets the row of the category.
     *
     * @return The row.
     */
    public Row getRow() {
        return this.category.getRow();
    }

    /**
     * Gets the column of the category.
     *
     * @return The column.
     */
    public int getColumn() {
        return this.category.getColumn();
    }

    /**
     * Whether the category uses a different offset or not.
     *
     * @return If it uses a different offset.
     */
    public boolean isSpecial() {
        return this.category.isSpecial();
    }

    /**
     * Tells the category that everything has finished loading.
     *
     * @param configReader The reader that contains the config category.
     */
    public void complete(final ConfigReader configReader) {
        if (this.isSearch()) {
            configReader.getCategories().forEach(miscCategory -> {
                if (miscCategory.isSearch()) {
                    return;
                }
                miscCategory.getProcessedOptions().forEach(this::addOption);
            });
        }
    }

    /**
     * Whether the category is the search category.
     *
     * @return Whether it is the search category.
     */
    public boolean isSearch() {
        return this.category instanceof SearchCategory;
    }

    /**
     * Adds a processed option to the category.
     *
     * @param processedOption The option to add.
     */
    public void addOption(final ProcessedOption<?, ?> processedOption) {
        this.processedOptions.add(processedOption);
    }
}
