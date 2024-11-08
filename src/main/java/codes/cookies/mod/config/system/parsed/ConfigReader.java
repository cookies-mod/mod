package codes.cookies.mod.config.system.parsed;

import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.Config;
import codes.cookies.mod.config.system.Foldable;
import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.config.system.options.FoldableOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Config reader to correctly parse a config from its source.
 */
public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    @Getter
    private final List<ProcessedCategory> categories = new LinkedList<>();
    private final AtomicInteger foldableId = new AtomicInteger(0);
    private final Stack<Integer> foldableStack = new Stack<>();
    @Getter
    private Config<?> config;
    private ProcessedCategory currentCategory;
    private ProcessedOption<?, ?> parent;

    /**
     * Begins a category.
     *
     * @param category The category that begins.
     */
    public void beginCategory(Category category) {
        this.currentCategory = new ProcessedCategory(category);
        this.categories.add(this.currentCategory);
    }

    /**
     * Ends a category.
     */
    public void endCategory() {
        this.foldableId.set(0);
        if (!this.foldableStack.isEmpty()) {
            logger.warn("End Category with {} open foldables", this.foldableId);
        }
        this.foldableStack.clear();
        this.endParent();
    }

    /**
     * Ends a parent option.
     */
    public void endParent() {
        this.parent = null;
    }

    /**
     * Creates a foldable.
     *
     * @param foldable The foldable to open.
     */
    public void beginFoldable(Foldable foldable) {
        //noinspection rawtypes

        ProcessedOption processedOption = new ProcessedOption<>(new FoldableOption(
            foldable,
            foldableId.incrementAndGet()
        ));
        if (!this.foldableStack.isEmpty()) {
            processedOption.setFoldable(this.foldableStack.peek());
        }
        this.currentCategory.addOption(processedOption);
        this.foldableStack.push(this.foldableId.get());
    }

    /**
     * Ends a foldable.
     */
    public void endFoldable() {
        this.foldableStack.pop();
    }

    /**
     * Begins the config.
     *
     * @param config The config that begins.
     */
    public void beginConfig(Config<?> config) {
        this.config = config;
    }

    /**
     * Ends the config.
     */
    public void endConfig() {
        this.categories.forEach(category -> category.complete(this));
        this.endParent();
    }

    /**
     * Begins a parent option..
     *
     * @param option    The parent option.
     * @param fieldName The field name of the option.
     */
    public void beginParent(Option<?, ?> option, String fieldName) {
        this.parent = processOption(option, fieldName);
    }

    /**
     * Marks an option as processed and add it to the finished list.
     *
     * @param option    The option to be processed.
     * @param fieldName The field name of the option.
     * @param <T>       The type of the value.
     * @param <O>       The type of the option.
     * @return The processed option.
     */
    public <T, O extends Option<T, O>> ProcessedOption<T, O> processOption(Option<T, O> option, String fieldName) {
        ProcessedOption<T, O> processedOption = new ProcessedOption<>(option);
        if (!this.foldableStack.isEmpty()) {
            processedOption.setFoldable(this.foldableStack.peek());
        }
        if (this.parent != null) {
            processedOption.setParent(this.parent);
        }
        this.currentCategory.addOption(processedOption);
        processedOption.setFieldName(fieldName);
        return processedOption;
    }

}
