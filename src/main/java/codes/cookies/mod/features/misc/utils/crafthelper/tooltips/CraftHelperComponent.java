package codes.cookies.mod.features.misc.utils.crafthelper.tooltips;

import java.util.List;
import java.util.Optional;

import codes.cookies.mod.utils.dev.DevUtils;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

/**
 * Standard component used in the craft helper.
 */
@Setter
public abstract class CraftHelperComponent implements TooltipComponent {
	protected static final Identifier DEBUG = DevUtils.createIdentifier("craft_helper/debug_messages");

	CraftHelperComponent parent;
	@Getter
	boolean collapsed = false;

	/**
	 * @return The parent of the component.
	 */
	public Optional<CraftHelperComponent> getParent() {
		return Optional.ofNullable(parent);
	}

	/**
	 * @return Whether the component is hidden or collapsed.
	 */
	public boolean isHiddenOrCollapsed() {
		return isHidden() || isCollapsed();
	}

	/**
	 * @return Whether the component is hidden.
	 */
	public boolean isHidden() {
		return getParent().map(CraftHelperComponent::isHiddenOrCollapsed).orElse(false);
	}

	/**
	 * Initializes the component.
	 */
	public void init() {}

	/**
	 * @return The parts used by the component.
	 */
	public abstract List<CraftHelperComponentPart> getTextParts();
}
