package codes.cookies.mod.features.misc.utils.crafthelper.tooltips;

import java.util.List;
import java.util.Optional;

import codes.cookies.mod.utils.dev.DevUtils;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

@Setter
public abstract class CraftHelperComponent implements TooltipComponent {
	protected static final Identifier DEBUG = DevUtils.createIdentifier("craft_helper/debug_messages");

	CraftHelperComponent parent;
	@Getter
	boolean collapsed = false;

	@Setter
	int width;
	@Setter
	int height;


	public Optional<CraftHelperComponent> getParent() {
		return Optional.ofNullable(parent);
	}

	public boolean isHiddenOrCollapsed() {
		return isHidden() || isCollapsed();
	}

	public boolean isHidden() {
		return getParent().map(CraftHelperComponent::isHiddenOrCollapsed).orElse(false);
	}

	public void init() {}

	public abstract List<CraftHelperComponentPart> getTextParts();
}
