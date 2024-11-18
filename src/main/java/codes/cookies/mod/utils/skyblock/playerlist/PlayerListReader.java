package codes.cookies.mod.utils.skyblock.playerlist;

import java.util.List;

/**
 * Reader for the player list to allow for easy widget parsing.
 */
public class PlayerListReader {

	private final List<String> playerListContent;
	private int index = 0;

	public PlayerListReader(List<String> playerListContent) {
		this.playerListContent = playerListContent;
	}

	public boolean canRead() {
		return index < playerListContent.size();
	}

	public String peek() {
		return playerListContent.get(index);
	}

	public String read() {
		return playerListContent.get(index++);
	}

	public void skip() {
		index++;
	}

	public void reset() {
		index = 0;
	}

	public boolean isTitle() {
		return this.canRead() && !this.peek().startsWith(" ");
	}
}
