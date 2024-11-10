package codes.cookies.mod.render.hud.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HudEditAction {

	NONE(0), DEFAULT(2), ALL_ENABLED(3), SHOW_ALL(1);

	private final int next;
}
