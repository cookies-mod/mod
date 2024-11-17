package codes.cookies.mod.utils.skyblock.tab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A list of all info elements in the player list.
 */
public class PlayerListEntrySet {

	List<String> tabElements = new CopyOnWriteArrayList<>(new String[80]);

	public void replace(int x, int y, String element) {
		tabElements.set(x * 20 + y, element);
	}

	public List<String> getInfoElements() {
		List<String> infoElements = new ArrayList<>();

		boolean isInfo = false;

		for (String tabElement : tabElements) {
			if (tabElement == null) {
				continue;
			}
			if (tabElement.trim().equalsIgnoreCase("info")) {
				isInfo = true;
				continue;
			}
			if (isInfo) {
				infoElements.add(tabElement);
			}
		}

		return infoElements;
	}

}
