package codes.cookies.mod.services.item.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Predicates;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import codes.cookies.mod.config.categories.ItemSearchConfig;
import codes.cookies.mod.services.IsSameResult;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.items.CookiesDataComponentTypes;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Item search filter for string queries.
 */
@RequiredArgsConstructor
public class SearchQueryMatcher implements ItemSearchFilter {

	private final boolean isRegex;
	private final String searchString;
	private final boolean negate;
	private final List<LevelProperty> attributes;
	private final List<LevelProperty> enchants;
	private final List<String> name;
	private final List<String> id;
	private final List<String> lore;
	private final List<SearchQueryMatcher> children;
	private boolean isRoot = true;
	private final Predicate<ItemStack> arbituaryPredicate;

	public static Optional<Builder> parse(String rawQuery) {
		if (rawQuery == null || rawQuery.isEmpty()) {
			return Optional.of(new Builder());
		}
		try {
			StringReader stringBuffer = new StringReader(rawQuery);
			Builder builder = new Builder();

			if (stringBuffer.peek() == '!') {
				stringBuffer.skip();
				builder.negate();
			}

			if (stringBuffer.canRead(2) && stringBuffer.peek() == '$' && stringBuffer.peek(1) == ':') {
				stringBuffer.skip();
				stringBuffer.skip();
				builder.regex();
			}

			final String remaining = stringBuffer.getRemaining();
			builder.search(remaining);
			final String[] split = remaining.split("&");
			for (String s : split) {
				parseIndividual(s, builder);
			}

			return Optional.of(builder);
		} catch (CommandSyntaxException e) {
			return Optional.empty();
		}
	}

	private static void parseIndividual(String queryPart, Builder parent) throws CommandSyntaxException {
		final Builder builder = new Builder();
		StringReader part = new StringReader(queryPart.trim());
		if (queryPart.contains(":")) {
			switch (part.readStringUntil(':')) {
				case "i", "id" -> builder.parseString(part.getRemaining(), builder::addId);
				case "n", "name" -> builder.parseString(part.getRemaining(), builder::addName);
				case "l", "lore" -> builder.parseString(part.getRemaining(), builder::addLore);
				case "a", "attribute" -> builder.parseLevelProperty(part.getRemaining(), builder::attribute);
				case "e", "enchants" -> builder.parseLevelProperty(part.getRemaining(), builder::enchant);
				default -> {
					builder.addName(part.getRemaining());
					builder.addLore(part.getRemaining());
				}
			}
			parent.append(builder);
			return;
		}

		builder.addName(part.getRemaining());
		builder.addLore(part.getRemaining());
		parent.append(builder);
	}

	@Override
	public int getColor() {
		return 0xFF << 24 | (ItemSearchConfig.getInstance().highlightColor.getColorValue() & 0xFFFFFF);
	}

	@Override
	public IsSameResult doesMatch(ItemStack stack) {
		final boolean result = performSearch(stack);
		return IsSameResult.wrapBoolean(negate != result);
	}

	private boolean performSearch(ItemStack stack) {
		if (!arbituaryPredicate.test(stack)) {
			return false;
		}
		if ((this.searchString == null || this.searchString.isEmpty()) && this.isRoot) {
			return true;
		}
		if (isRegex) {
			if (this.searchString == null || this.searchString.isEmpty()) {
				return true;
			}
			Set<String> set = new HashSet<>();
			set.add(stack.getName().getString().toLowerCase(Locale.ROOT));

			final LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
			if (loreComponent != null) {
				for (Text line : loreComponent.lines()) {
					set.add(line.getString());
				}
			}
			for (String s : set) {
				if (s.matches(searchString)) {
					return true;
				}
			}
			return false;
		}

		if (!children.isEmpty()) {
			boolean doesMatch = true;
			for (SearchQueryMatcher child : children) {
				doesMatch = doesMatch && child.performSearch(stack);
			}
			if (doesMatch) {
				return true;
			}
		}

		if (Optional.of(stack)
				.map(ItemStack::getName)
				.map(Text::getString)
				.flatMap(this::sanitize)
				.map(this::checkName)
				.orElse(false)) {
			return true;
		}

		if (this.retrieve(stack, DataComponentTypes.LORE)
				.map(LoreComponent::lines)
				.orElseGet(Collections::emptyList)
				.stream().map(Text::getString)
				.map(this::sanitize)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.anyMatch(this::checkLore)) {
			return true;
		}

		if (this.retrieve(stack, CookiesDataComponentTypes.SKYBLOCK_ID)
				.flatMap(this::sanitize)
				.map(this::checkId)
				.orElse(false)) {
			return true;
		}

		if (this.retrieve(stack, CookiesDataComponentTypes.ENCHANTMENTS)
				.map(this::checkEnchants)
				.orElse(false)) {
			return true;
		}

		return this.retrieve(stack, CookiesDataComponentTypes.ATTRIBUTES)
				.map(this::checkAttributes)
				.orElse(false);
	}

	private boolean checkAttributes(Map<String, Integer> stringIntegerMap) {
		if (stringIntegerMap.containsKey("mending")) {
			stringIntegerMap.put("vitality", stringIntegerMap.get("mending"));
		}
		return this.checkLevelProperty(stringIntegerMap, this.attributes);
	}

	private boolean checkEnchants(Map<String, Integer> stringIntegerMap) {
		return this.checkLevelProperty(stringIntegerMap, this.enchants);
	}

	private boolean checkLevelProperty(Map<String, Integer> stringIntegerMap, List<LevelProperty> list) {
		for (LevelProperty levelProperty : list) {
			for (String attributeName : stringIntegerMap.keySet()) {
				String sanitizedAttributeName = attributeName.toLowerCase(Locale.ROOT).replace("_", " ").trim();
				if (sanitizedAttributeName.contains(levelProperty.name)) {
					if (levelProperty.level.isPresent() && levelProperty.level.orElse(-1) != -1) {
						return stringIntegerMap.getOrDefault(attributeName, 0) == levelProperty.level.orElse(-1);
					}
					return true;
				}
			}
		}

		return false;
	}

	private <T> Optional<T> retrieve(ItemStack stack, ComponentType<T> type) {
		if (stack == null) {
			return Optional.empty();
		}
		final T value = stack.get(type);
		return Optional.ofNullable(value);
	}

	private boolean checkId(String s) {
		for (String string : this.id) {
			if (s.contains(string)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkLore(String search) {
		for (String s : this.lore) {
			if (search.contains(s)) {
				return true;
			}
		}
		return false;
	}

	private Optional<String> sanitize(String original) {
		return Optional.of(original)
				.map(CookiesUtils::stripColor)
				.map(String::toLowerCase)
				.map(String::trim);
	}

	private boolean checkName(String name) {
		for (String s : this.name) {
			if (name.contains(s)) {
				return true;
			}
		}
		return false;
	}

	private record LevelProperty(String name, OptionalInt level) {
	}

	public static class Builder {

		private final List<LevelProperty> attributes = new ArrayList<>();
		private final List<LevelProperty> enchants = new ArrayList<>();
		private final List<String> name = new ArrayList<>();
		private final List<String> id = new ArrayList<>();
		private final List<String> lore = new ArrayList<>();
		private final List<SearchQueryMatcher> children = new ArrayList<>();
		private boolean isRegex = false;
		private String searchString = null;
		private boolean negate = false;
		private Predicate<ItemStack> arbituaryPredicate = Predicates.alwaysTrue();

		public Builder regex() {
			this.isRegex = true;
			return this;
		}

		public Builder predicate(Predicate<ItemStack> predicate) {
			this.arbituaryPredicate = predicate;
			return this;
		}

		public Builder search(String searchString) {
			this.searchString = searchString;
			return this;
		}

		public Builder negate() {
			this.negate = true;
			return this;
		}

		public Builder append(Builder other) {
			final SearchQueryMatcher build = other.build();
			build.isRoot = false;
			this.children.add(build);
			return this;
		}

		public Builder attribute(String attribute, @Nullable Integer level) {
			return this.addProperty(attribute, level, attributes);
		}

		public Builder enchant(String enchant, @Nullable Integer level) {
			return this.addProperty(enchant, level, enchants);
		}

		private Builder addProperty(String property, @Nullable Integer level, List<LevelProperty> list) {
			if (isRegex) {
				return this;
			}
			list.add(new LevelProperty(
					property,
					Optional.ofNullable(level).map(OptionalInt::of).orElse(OptionalInt.empty())));
			return this;
		}

		private Builder addName(String name) {
			if (isRegex) {
				return this;
			}
			this.name.add(name);
			return this;
		}

		private Builder addId(String id) {
			if (isRegex) {
				return this;
			}
			this.id.add(id);
			return this;
		}

		private Builder addLore(String lore) {
			if (isRegex) {
				return this;
			}
			this.lore.add(lore);
			return this;
		}

		public SearchQueryMatcher build() {
			return new SearchQueryMatcher(
					isRegex,
					searchString,
					negate,
					attributes,
					enchants,
					name,
					id,
					lore,
					children,
					arbituaryPredicate);
		}

		private void parseString(String string, Consumer<String> consumer) {
			for (String s : string.trim().toLowerCase(Locale.ROOT).split(",")) {
				consumer.accept(s);
			}
		}

		public void parseLevelProperty(String remaining, BiConsumer<String, Integer> consumer) {
			for (String search : remaining.trim().toLowerCase(Locale.ROOT).split(",")) {
				int level = -1;
				String targetName;
				if (search.endsWith(")")) {
					try {
						level = Integer.parseInt(search.substring(search.indexOf("(") + 1, search.lastIndexOf(")")));
						targetName = search.substring(0, search.lastIndexOf("(")).trim();
					} catch (NumberFormatException e) {
						targetName = search;
						level = -1;
					}
				} else {
					targetName = search;
				}
				String sanitizedTargetName = targetName.toLowerCase(Locale.ROOT)
						.replace("(", "")
						.replace(")", "")
						.trim();
				consumer.accept(sanitizedTargetName, level);
			}
		}
	}
}
