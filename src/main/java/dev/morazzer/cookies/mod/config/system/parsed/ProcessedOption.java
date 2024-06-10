package dev.morazzer.cookies.mod.config.system.parsed;

import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * A processed option representing every option type.
 *
 * @param <T> The type of the value.
 * @param <O> The option type.
 */
@Getter
public class ProcessedOption<T, O extends Option<T, O>> {

    private final Option<T, O> option;
    private final ConfigOptionEditor<T, O> editor;
    @Setter(AccessLevel.PACKAGE)
    @Getter
    private String fieldName;
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private ProcessedOption<?, ?> parent;
    @Setter
    private int foldable = -1;

    /**
     * Creates a processed option.
     *
     * @param option The option to be processed.
     */
    public ProcessedOption(Option<T, O> option) {
        this.option = option;
        this.editor = this.option.getEditor();
    }

}
