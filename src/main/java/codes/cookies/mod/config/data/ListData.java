package codes.cookies.mod.config.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import codes.cookies.mod.utils.json.JsonSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * Data class to save and load a list from the config.
 */
@SuppressWarnings("MissingJavadoc")
public class ListData<T> implements JsonSerializable {

    private List<T> list;
    private final Function<JsonElement, T> deserializer;
    private final Function<T, JsonElement> serializer;

    public ListData(List<T> list, Function<JsonElement, T> deserializer, Function<T, JsonElement> serializer) {
        this.list = list;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        this.list = new ArrayList<>();
        final JsonArray asJsonArray = jsonElement.getAsJsonArray();
        for (JsonElement element : asJsonArray) {
            this.list.add(deserializer.apply(element));
        }
    }

    @Override
    public @NotNull JsonElement write() {
        JsonArray jsonElements = new JsonArray();

        for (T t : this.list) {
            jsonElements.add(serializer.apply(t));
        }

        return jsonElements;
    }


    //<editor-fold desc="Delegated methods from java.util.List">
    public int size() {
        return list.size();
    }

    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return list.addAll(index, c);
    }

    public void addFirst(T t) {
        list.addFirst(t);
    }

    public T set(int index, T element) {
        return list.set(index, element);
    }

    public boolean removeIf(Predicate<? super T> filter) {
        return list.removeIf(filter);
    }

    public boolean removeAll(@NotNull Collection<?> c) {
        return list.removeAll(c);
    }

    public void add(int index, T element) {
        list.add(index, element);
    }

    public void addLast(T t) {
        list.addLast(t);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public boolean remove(Object o) {
        return list.remove(o);
    }

    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    public boolean addAll(@NotNull Collection<? extends T> c) {
        return list.addAll(c);
    }

    public T get(int index) {
        return list.get(index);
    }

    public boolean containsAll(@NotNull Collection<T> c) {
        return list.containsAll(c);
    }

    public void sort(Comparator<? super T> c) {
        list.sort(c);
    }

    public List<T> reversed() {
        return list.reversed();
    }

    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        return list.toArray(generator);
    }

    public <T1> T1[] toArray(@NotNull T1[] a) {
        return list.toArray(a);
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public T removeLast() {
        return list.removeLast();
    }

    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    public Stream<T> stream() {
        return list.stream();
    }

    public boolean add(T t) {
        return list.add(t);
    }

    public void clear() {
        list.clear();
    }

    public void forEach(Consumer<? super T> action) {
        list.forEach(action);
    }

    public T getLast() {
        return list.getLast();
    }

    public Stream<T> parallelStream() {
        return list.parallelStream();
    }

    public Iterator<T> iterator() {
        return list.iterator();
    }

    public boolean retainAll(@NotNull Collection<?> c) {
        return list.retainAll(c);
    }

    public T remove(int index) {
        return list.remove(index);
    }

    public boolean contains(T o) {
        return list.contains(o);
    }

    public T getFirst() {
        return list.getFirst();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public void replaceAll(UnaryOperator<T> operator) {
        list.replaceAll(operator);
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public T removeFirst() {
        return list.removeFirst();
    }
    //</editor-fold>
}
