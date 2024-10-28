package dev.morazzer.cookies.mod.utils.accessors;

import net.minecraft.text.ClickEvent;

import java.util.Optional;

public interface ClickEventAccessor {

	static ClickEventAccessor get(ClickEvent clickEvent) {
		return (ClickEventAccessor) clickEvent;
	}

	static void setRunnable(ClickEvent clickEvent, Runnable runnable) {
		get(clickEvent).cookies$setRunnable(runnable);
	}

	static Optional<Runnable> getRunnable(ClickEvent clickEvent) {
		return Optional.ofNullable(get(clickEvent).cookies$getRunnable());
	}

	void cookies$setRunnable(Runnable runnable);
	Runnable cookies$getRunnable();

}
