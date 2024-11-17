package codes.cookies.mod.utils.skyblock.tab.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import codes.cookies.mod.utils.skyblock.tab.PlayerListReader;
import codes.cookies.mod.utils.skyblock.tab.widgets.corpse.FrozenCorpseWidget;

public class PlayerListWidgets {

	private static final List<Entry<?>> WIDGETS = new ArrayList<>();

	public static Entry<FrozenCorpseWidget> CORPSE = register(
			FrozenCorpseWidget::doesMatch,
			FrozenCorpseWidget::new,
			FrozenCorpseWidget.class);

	private static <T extends PlayerListWidget> Entry<T> register(
			Predicate<String> predicate,
			Supplier<T> creator,
			Class<T> clazz
	) {
		final Entry<T> entry = new Entry<>(predicate, creator, clazz);
		WIDGETS.add(entry);
		return entry;
	}

	public static List<PlayerListWidget> extractAll(PlayerListReader playerListReader) {
		List<PlayerListWidget> widgets = new ArrayList<>();

		while (playerListReader.canRead()) {
			if (!playerListReader.isTitle()) {
				playerListReader.skip();
			} else {
				final Optional<PlayerListWidget> byTitle = getByTitle(playerListReader.read());
				byTitle.ifPresent(widget -> {
					widget.read(playerListReader);
					widgets.add(widget);
				});
			}
		}

		return widgets;
	}

	private static Optional<PlayerListWidget> getByTitle(String title) {
		return WIDGETS.stream()
				.filter(entry -> entry.predicate().test(title))
				.findFirst()
				.map(entry -> entry.creator.get());
	}

	public record Entry<T extends PlayerListWidget>(Predicate<String> predicate, Supplier<T> creator, Class<T> clazz) {
	}
}
