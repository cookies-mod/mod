package codes.cookies.mod.utils.skyblock.playerlist.widgets;

import codes.cookies.mod.events.PlayerListWidgetEvent;
import codes.cookies.mod.utils.skyblock.playerlist.PlayerListReader;

/**
 * A generic player list widget.
 */
public abstract class PlayerListWidget {

	/**
	 * Sends the creation of this widget as event.
	 */
	public void sendEvent() {
		PlayerListWidgetEvent.EVENT.invoker().accept(this);
	}

	/**
	 * Parses the widget instance based on the provided reader.
	 * @param reader The reader.
	 */
	protected abstract void read(PlayerListReader reader);

}
