package dev.morazzer.cookies.mod.config.system.options;

import com.google.gson.JsonElement;
import dev.morazzer.cookies.mod.config.system.Foldable;
import dev.morazzer.cookies.mod.config.system.Option;
import dev.morazzer.cookies.mod.config.system.editor.ConfigOptionEditor;
import dev.morazzer.cookies.mod.config.system.editor.FoldableEditor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Option to represent a foldable in the compiled version of the config.
 */
public class FoldableOption extends Option<Object, FoldableOption> {

    private final int id;

    public FoldableOption(Foldable foldable, int id) {
        super(foldable.getName(), foldable);
        this.id = id;
    }

    @Override
    @Contract("_->fail")
    public void read(@NotNull JsonElement jsonElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Contract("->fail")
    public @NotNull JsonElement write() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ConfigOptionEditor<Object, FoldableOption> getEditor() {
        return new FoldableEditor(this, this.id);
    }

    @Override
    public boolean canBeSerialized() {
        return false;
    }

}
