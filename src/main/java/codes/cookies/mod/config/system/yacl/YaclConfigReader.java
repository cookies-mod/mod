package codes.cookies.mod.config.system.yacl;

import java.awt.*;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

import codes.cookies.mod.config.system.Category;
import codes.cookies.mod.config.system.Config;
import codes.cookies.mod.config.system.Foldable;
import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.config.system.options.BooleanOption;
import codes.cookies.mod.config.system.options.ButtonOption;
import codes.cookies.mod.config.system.options.ColorOption;
import codes.cookies.mod.config.system.options.EnumCycleOption;
import codes.cookies.mod.config.system.options.FoldableOption;
import codes.cookies.mod.config.system.options.SliderOption;
import codes.cookies.mod.config.system.options.StringInputOption;
import codes.cookies.mod.config.system.options.TextDisplayOption;
import codes.cookies.mod.config.system.parsed.ConfigReader;
import codes.cookies.mod.config.system.parsed.ProcessedOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option.Builder;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import net.minecraft.text.Text;

@Slf4j
public class YaclConfigReader extends ConfigReader {

	@Getter
	final CompletableFuture<YetAnotherConfigLib.Builder> finalConfig = new CompletableFuture<>();
	final Stack<OptionGroup.Builder> optionGroups = new Stack<>();
	YetAnotherConfigLib.Builder builder;
	ConfigCategory.Builder categoryBuilder;

	@Override
	public void beginConfig(Config<?> config) {
		super.beginConfig(config);

		builder = YetAnotherConfigLib.createBuilder();
		builder.title(Text.literal("Cookies Mod"));
	}

	@Override
	public void beginCategory(Category category) {
		super.beginCategory(category);
		categoryBuilder = ConfigCategory.createBuilder();
		categoryBuilder.name(category.getName());
		categoryBuilder.tooltip(category.getDescription());
	}

	@Override
	public void endCategory() {
		super.endCategory();
		builder.category(categoryBuilder.build());
		categoryBuilder = null;
	}

	private void addOption(dev.isxander.yacl3.api.Option<?> builder) {
		if (!optionGroups.isEmpty()) {
			final OptionGroup.Builder peek = optionGroups.peek();
			peek.option(builder);
		} else {
			categoryBuilder.option(builder);
		}
	}

	/**
	 * GOD THIS IS HORRIBLE QWQ
	 */
	@SuppressWarnings("unchecked")
	private <T extends Number> void configureSliderOption(SliderOption<T> option) {

		if (option.getValue() instanceof Double) {
			final SliderOption<Double> option1 = (SliderOption<Double>) option;
			final Builder<Double> basicBuilder = createBasicBuilder(option1);
			basicBuilder.controller(doubleOption -> DoubleSliderControllerBuilder.create(doubleOption)
					.range(option1.getMin(), option1.getMax()));
			addOption(basicBuilder.build());
		} else if (option.getValue() instanceof Integer) {
			final SliderOption<Integer> option1 = (SliderOption<Integer>) option;
			final Builder<Integer> basicBuilder = createBasicBuilder(option1);
			basicBuilder.controller(doubleOption -> IntegerSliderControllerBuilder.create(doubleOption)
					.range(option1.getMin(), option1.getMax()));
			addOption(basicBuilder.build());
		} else if (option.getValue() instanceof Float) {
			final SliderOption<Float> option1 = (SliderOption<Float>) option;
			final Builder<Float> basicBuilder = createBasicBuilder(option1);
			basicBuilder.controller(doubleOption -> FloatSliderControllerBuilder.create(doubleOption)
					.range(option1.getMin(), option1.getMax()));
			addOption(basicBuilder.build());
		} else if (option.getValue() instanceof Long) {
			final SliderOption<Long> option1 = (SliderOption<Long>) option;
			final Builder<Long> basicBuilder = createBasicBuilder(option1);
			basicBuilder.controller(doubleOption -> LongSliderControllerBuilder.create(doubleOption)
					.range(option1.getMin(), option1.getMax()));
			addOption(basicBuilder.build());
		}

	}

	private <T> Builder<T> createBasicBuilder(Option<T, ?> option) {
		final var yaclOption = dev.isxander.yacl3.api.Option.<T>createBuilder();
		yaclOption.name(option.getName());
		yaclOption.description(OptionDescription.createBuilder().text(option.getDescription()).build());
		yaclOption.binding(option.getValue(), option::getValue, option::setValue);
		return yaclOption;
	}

	private <T extends Enum<T>> void addEnumCycle(EnumCycleOption<T> option) {
		final Builder<T> basicBuilder = createBasicBuilder(option);
		basicBuilder.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(option.getValue().getDeclaringClass())
				.formatValue(option.getTextSupplier()::supplyText));
		addOption(basicBuilder.build());
	}

	@Override
	public <T, O extends Option<T, O>> ProcessedOption<T, O> processOption(Option<T, O> option, String fieldName) {
		if (option instanceof ButtonOption buttonOption) {
			addOption(dev.isxander.yacl3.api.ButtonOption.createBuilder()
					.name(buttonOption.getName())
					.description(OptionDescription.createBuilder().text(option.getDescription()).build())
					.action((yaclScreen, buttonOption1) -> buttonOption.getValue().run()).build());
			return super.processOption(option, fieldName);
		} else if (option instanceof TextDisplayOption textDisplayOption) {
			addOption(LabelOption.create(textDisplayOption.getName()));
			return super.processOption(option, fieldName);
		}

		final Builder<T> basicBuilder = createBasicBuilder(option);

		switch (option) {
			case BooleanOption booleanOption -> {
				((Builder<Boolean>) basicBuilder).controller(TickBoxControllerBuilder::create);
			}
			case SliderOption<?> sliderOption -> {
				configureSliderOption(sliderOption);
				return super.processOption(option, fieldName);
			}
			case ColorOption colorOption -> {
				((Builder<Color>) basicBuilder).controller(opt -> ColorControllerBuilder.create(opt)
						.allowAlpha(colorOption.isAllowAlpha()));
			}
			case StringInputOption stringInputOption -> {
				((Builder<String>) basicBuilder).controller(StringControllerBuilder::create);
			}
			case EnumCycleOption<? extends Enum<?>> enumCycleOption -> {
				addEnumCycle(enumCycleOption);
				return super.processOption(option, fieldName);
			}
			default -> {
				System.err.println("Unhandled option: " + fieldName + ": " + option);
			}
		}

		addOption(basicBuilder.build());

		return super.processOption(option, fieldName);
	}

	@Override
	public FoldableOption beginFoldable(Foldable foldable) {
		final FoldableOption foldableOption = super.beginFoldable(foldable);
		final OptionGroup.Builder categoryBuilder = OptionGroup.createBuilder();
		categoryBuilder.name(foldableOption.getName());
		categoryBuilder.description(OptionDescription.createBuilder().text(foldableOption.getDescription()).build());
		categoryBuilder.collapsed(true);
		optionGroups.push(categoryBuilder);

		return foldableOption;
	}

	@Override
	public void endFoldable() {
		super.endFoldable();
		final OptionGroup.Builder pop = optionGroups.pop();
		categoryBuilder.group(pop.build());
	}

	@Override
	public void endConfig() {
		super.endConfig();
		finalConfig.complete(builder);
	}
}
