package dev.morazzer.cookies.mod.utils.accessors;

import net.minecraft.entity.Entity;

public interface GlowingEntityAccessor {

	static GlowingEntityAccessor toAccessor(Entity entity) {
		return (GlowingEntityAccessor) entity;
	}

	static void setGlowing(Entity entity, boolean glowing) {
		toAccessor(entity).cookies$setGlowing(glowing);
	}

	static void setGlowColor(Entity entity, Integer color) {
		toAccessor(entity).cookies$setGlowColor(color);
	}

	void cookies$setGlowing(boolean glowing);

	void cookies$setGlowColor(Integer color);


}
