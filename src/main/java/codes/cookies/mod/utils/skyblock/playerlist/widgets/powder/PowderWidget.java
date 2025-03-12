package codes.cookies.mod.utils.skyblock.playerlist.widgets.powder;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.skyblock.playerlist.PlayerListReader;
import codes.cookies.mod.utils.skyblock.playerlist.widgets.PlayerListWidget;
import lombok.Getter;

import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;

@Getter
public class PowderWidget extends PlayerListWidget {

	private static final int MAX_SIZE = 3;
	private OptionalInt mithrilPowder = OptionalInt.empty();
	private OptionalInt gemstonePowder = OptionalInt.empty();
	private OptionalInt glacitePowder = OptionalInt.empty();

	public static boolean doesMatch(String title) {
		return "powders:".equalsIgnoreCase(title);
	}

	@Override
	protected void read(PlayerListReader reader) {
		int currentIteration = 0;
		final CompletableFuture<Integer> mithrilReference = new CompletableFuture<>();
		mithrilReference.whenComplete((integer, throwable) -> mithrilPowder = OptionalInt.of(integer));
		final CompletableFuture<Integer> gemstoneReference = new CompletableFuture<>();
		gemstoneReference.whenComplete((integer, throwable) -> gemstonePowder = OptionalInt.of(integer));
		final CompletableFuture<Integer> glaciteReference = new CompletableFuture<>();
		glaciteReference.whenComplete((integer, throwable) -> glacitePowder = OptionalInt.of(integer));

		while (reader.canRead() && !reader.isTitle() && currentIteration < MAX_SIZE) {
			tryParse(mithrilReference, "mithril", reader);
			tryParse(gemstoneReference, "gemstone", reader);
			tryParse(glaciteReference, "glacite", reader);
			currentIteration++;
		}
	}

	private void tryParse(CompletableFuture<Integer> powderReference, String prefix, PlayerListReader reader) {
		final String peek = reader.peek().trim().toLowerCase();
		if (!peek.startsWith(prefix)) {
			return;
		}

		reader.skip();
		final String literalPowderAmount = peek.replaceAll("\\D", "");
		final int powderAmount = CookiesUtils.parseIntSafe(literalPowderAmount);
		powderReference.complete(powderAmount);
	}
}
