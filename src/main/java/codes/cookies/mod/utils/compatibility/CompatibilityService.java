package codes.cookies.mod.utils.compatibility;

import codes.cookies.mod.utils.compatibility.system.CompatabilityMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import codes.cookies.mod.utils.compatibility.system.Requires;

import lombok.extern.slf4j.Slf4j;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Service to allow for workarounds and compatibility with certain mods.
 */
@Slf4j
public class CompatibilityService {

	private static final List<Entry<?>> loaded = new ArrayList<>();

	public static <T> T get(Class<T> clazz) {
		for (Entry<?> entry : loaded) {
			if (entry.entry.equals(clazz)) {
				//noinspection unchecked
				return (T) entry.value;
			}
		}

		final T load = load(clazz);
		loaded.add(new Entry<>(clazz, load));
		return load;
	}

	private static <T> T load(Class<T> clazz) {
		boolean loadActual;
		if (clazz.isAnnotationPresent(Requires.class)) {
			final Requires annotation = clazz.getAnnotation(Requires.class);
			final String value = annotation.value();
			loadActual = FabricLoader.getInstance().isModLoaded(value);
		} else {
			loadActual = false;
		}

		if (loadActual) {
			try {
				final Class<?> instanceClass = Class.forName(clazz.getName() + "Impl");
				//noinspection unchecked
				return (T) instanceClass.getDeclaredConstructor().newInstance();
			} catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
					 NoSuchMethodException ignored) {
				log.error("Can't load compatibility tool, returning empty!");
				// will return proxy instead
			}
		}

		//noinspection unchecked
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, (proxy, method, args) -> {
			if (method.isAnnotationPresent(CompatabilityMethod.class)) {
				return null;
			}
			return method.invoke(proxy, args);
		});
	}

	public record Entry<T>(Class<T> entry, T value) {}
}
