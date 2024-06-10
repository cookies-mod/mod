package dev.morazzer.cookies.mod.render.types;

import dev.morazzer.cookies.mod.render.Renderable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import lombok.Setter;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * A mutable version of the {@linkplain Composite} object.
 */
public class MutableComposite implements Renderable {

    private final List<Renderable> list = new LinkedList<>();
    @Setter
    private Predicate<WorldRenderContext> predicate = context -> true;

    @Override
    public void render(WorldRenderContext context) {
        synchronized (list) {
            for (Renderable renderable : list) {
                renderable.render(context);
            }
        }
    }

    @Override
    public boolean shouldRender(WorldRenderContext context) {
        return predicate.test(context);
    }

    /**
     * Adds an object to the composite.
     *
     * @param renderable The object to add.
     */
    public void add(Renderable renderable) {
        synchronized (list) {
            list.add(renderable);
        }
    }

    /**
     * Removes an object from the composite.
     *
     * @param renderable The object to remove.
     */
    public void remove(Renderable renderable) {
        synchronized (list) {
            list.remove(renderable);
        }
    }
}
