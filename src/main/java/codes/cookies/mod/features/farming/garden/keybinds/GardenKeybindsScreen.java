package codes.cookies.mod.features.farming.garden.keybinds;

import codes.cookies.mod.data.cookiesdata.CookieDataInstances;
import codes.cookies.mod.data.farming.GardenKeybindsData;
import com.google.common.collect.ImmutableList;

import com.mojang.logging.LogUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import codes.cookies.mod.translations.TranslationKeys;

import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class GardenKeybindsScreen extends Screen implements TranslationKeys {
	private final Screen parent;
	public GardenKeybindsScreen(Screen parent) {
		super(Text.empty());
		this.parent = parent;
		this.title = Text.translatable(CONFIG_FARMING_KEYBIND_MENU_TITLE);
	}

	@Nullable
	public KeyBinding selectedKeyBinding;
	private ControlsListWidget controlsList;
	private ButtonWidget resetAllButton;

	protected void initBody() {
		this.controlsList = this.layout.addBody(new GardenKeybindsScreen.ControlsListWidget(this, this.client));
	}

	protected void initFooter() {
		this.resetAllButton = ButtonWidget.builder(Text.translatable("controls.resetAll"), button -> {
			CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.clear();
			this.controlsList.update();
		}).build();
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget.add(this.resetAllButton);
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
	}

	protected void refreshWidgetPositions() {
		this.layout.refreshPositions();
		this.controlsList.position(this.width, this.layout);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.selectedKeyBinding != null) {
			var key = InputUtil.Type.MOUSE.createFromCode(button);
			if(key == selectedKeyBinding.getDefaultKey()) {
				CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.put(selectedKeyBinding.getTranslationKey(), null);
			} else {
				CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.put(selectedKeyBinding.getTranslationKey(), new GardenKeybindsData.GardenKeyBindOverride(key));
			}
			this.selectedKeyBinding = null;
			this.controlsList.update();
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.selectedKeyBinding != null) {
			var key = keyCode == GLFW.GLFW_KEY_ESCAPE ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode);
			if(key == selectedKeyBinding.getDefaultKey()) {
				CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.put(selectedKeyBinding.getTranslationKey(), null);
			} else {
				CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.put(selectedKeyBinding.getTranslationKey(), new GardenKeybindsData.GardenKeyBindOverride(key));
			}

			this.selectedKeyBinding = null;
			this.controlsList.update();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		boolean bl = false;

		for (KeyBinding keyBinding : this.client.options.allKeys) {
			if (CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.get(keyBinding.getTranslationKey()) != null) {
				bl = true;
				break;
			}
		}

		this.resetAllButton.active = bl;
	}

	protected OptionListWidget body;
	public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

	@Override
	protected void init() {
		this.initHeader();
		this.initBody();
		this.initFooter();
		this.layout.forEachChild(this::addDrawableChild);
		this.refreshWidgetPositions();
	}

	protected void initHeader() {
		this.layout.addHeader(this.title, this.textRenderer);
	}

	@Override
	public void removed() {
		CookieDataInstances.gardenKeybindsData.save();
	}

	@Override
	public void close() {
		if (this.body != null) {
			this.body.applyAllPendingValues();
		}

		this.client.setScreen(this.parent);
	}

	@Environment(EnvType.CLIENT)
	private class ControlsListWidget extends ElementListWidget<ControlsListWidget.Entry> {
		final GardenKeybindsScreen parent;
		private int maxKeyNameLength;

		public ControlsListWidget(GardenKeybindsScreen parent, MinecraftClient client) {
			super(client, parent.width, parent.layout.getContentHeight(), parent.layout.getHeaderHeight(), 20);
			this.parent = parent;
			KeyBinding[] keyBindings = ArrayUtils.clone(client.options.allKeys);
			Arrays.sort(keyBindings);
			String string = null;

			for (KeyBinding keyBinding : keyBindings) {
				String string2 = keyBinding.getCategory();
				if (!string2.equals(string)) {
					string = string2;
					this.addEntry(new CategoryEntry(Text.translatable(string2)));
				}

				Text text = Text.translatable(keyBinding.getTranslationKey());
				int i = client.textRenderer.getWidth(text);
				if (i > this.maxKeyNameLength) {
					this.maxKeyNameLength = i;
				}

				if(!CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.containsKey(keyBinding.getTranslationKey())) {
					CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.put(keyBinding.getTranslationKey(), null);
				}

				this.addEntry(new KeyBindingEntry(keyBinding, text));
			}
		}

		public void update() {
			this.updateChildren();
		}

		public void updateChildren() {
			this.children().forEach(ControlsListWidget.Entry::update);
		}

		@Override
		public int getRowWidth() {
			return 340;
		}

		@Environment(EnvType.CLIENT)
		public class CategoryEntry extends Entry {
			final Text text;
			private final int textWidth;

			public CategoryEntry(final Text text) {
				this.text = text;
				this.textWidth = client.textRenderer.getWidth(this.text);
			}

			@Override
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				context.drawText(
						client.textRenderer,
						this.text,
						width / 2 - this.textWidth / 2,
						y + entryHeight - 9 - 1,
						Colors.WHITE,
						false
				);
			}

			@Nullable
			@Override
			public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
				return null;
			}

			@Override
			public List<? extends Element> children() {
				return Collections.emptyList();
			}

			@Override
			public List<? extends Selectable> selectableChildren() {
				return ImmutableList.of(new Selectable() {
					@Override
					public Selectable.SelectionType getType() {
						return Selectable.SelectionType.HOVERED;
					}

					@Override
					public void appendNarrations(NarrationMessageBuilder builder) {
						builder.put(NarrationPart.TITLE,CategoryEntry.this.text);
					}
				});
			}

			@Override
			protected void update() {
			}
		}

		@Environment(EnvType.CLIENT)
		public abstract static class Entry extends ElementListWidget.Entry<ControlsListWidget.Entry> {
			abstract void update();
		}

		@Environment(EnvType.CLIENT)
		public class KeyBindingEntry extends  ControlsListWidget.Entry {
			private static final Text RESET_TEXT = Text.translatable("controls.reset");
			private final KeyBinding binding;
			private final Text bindingName;
			private final ButtonWidget editButton;
			private final ButtonWidget resetButton;
			private boolean duplicate = false;

			public KeyBindingEntry(final KeyBinding binding, final Text bindingName) {
				this.binding = binding;
				this.bindingName = bindingName;
				this.editButton = ButtonWidget.builder(bindingName, button -> {
							parent.selectedKeyBinding = binding;
							update();
						})
						.dimensions(0, 0, 75, 20)
						.narrationSupplier(
								textSupplier -> binding.isUnbound()
										? Text.translatable("narrator.controls.unbound", bindingName)
										: Text.translatable("narrator.controls.bound", bindingName, textSupplier.get())
						)
						.build();
				this.resetButton = ButtonWidget.builder(RESET_TEXT, button -> {
					CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.put(binding.getTranslationKey(), null);
					ControlsListWidget.this.update();
				}).dimensions(0, 0, 50, 20).narrationSupplier(textSupplier -> Text.translatable("narrator.controls.reset", bindingName)).build();
				this.update();
			}

			@Override
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				int i = getScrollbarX() - this.resetButton.getWidth() - 10;
				int j = y - 2;
				this.resetButton.setPosition(i, j);
				this.resetButton.render(context, mouseX, mouseY, tickDelta);
				int k = i - 5 - this.editButton.getWidth();
				this.editButton.setPosition(k, j);
				this.editButton.render(context, mouseX, mouseY, tickDelta);
				context.drawTextWithShadow(client.textRenderer, this.bindingName, x, y + entryHeight / 2 - 9 / 2, Colors.WHITE);
				if (this.duplicate) {
					int m = this.editButton.getX() - 6;
					context.fill(m, y - 1, m + 3, y + entryHeight, -65536);
				}
			}

			@Override
			public List<? extends Element> children() {
				return ImmutableList.of(this.editButton, this.resetButton);
			}

			@Override
			public List<? extends Selectable> selectableChildren() {
				return ImmutableList.of(this.editButton, this.resetButton);
			}

			@Override
			protected void update() {
				KeyBinding.updateKeysByCode();
				Text message;
				if (CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.get(this.binding.getTranslationKey()) != null) {
					message = CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.get(this.binding.getTranslationKey()).key().getLocalizedText();
				} else {
					message = this.binding.getBoundKeyLocalizedText();
				}
				this.editButton.setMessage(message);

				this.resetButton.active = CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.get(this.binding.getTranslationKey()) != null;

				this.duplicate = false;
				MutableText mutableText = Text.empty();

				var thisOverride = CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.get(this.binding.getTranslationKey());
				if(thisOverride == null)
				{
					thisOverride = new GardenKeybindsData.GardenKeyBindOverride(this.binding.boundKey);
				}

				for (var otherKeyOverride : CookieDataInstances.gardenKeybindsData.gardenKeyBindOverrides.entrySet()) {
					if(otherKeyOverride.getKey().equals(this.binding.getTranslationKey())) {
						continue;
					}

					var otherKey = otherKeyOverride.getValue();
					if(otherKey == null) {
						otherKey = new GardenKeybindsData.GardenKeyBindOverride(KeyBinding.KEYS_BY_ID.get(otherKeyOverride.getKey()).boundKey);
					}

					if(otherKey.key().equals(thisOverride.key()) && !thisOverride.key().equals(InputUtil.UNKNOWN_KEY)) {
						if (this.duplicate) {
							mutableText.append(", ");
						}

						this.duplicate = true;
						mutableText.append(Text.translatable(KeyBinding.KEYS_BY_ID.get(otherKeyOverride.getKey()).getTranslationKey()));
					}
				}


				if (this.duplicate) {
					this.editButton.setMessage(Text.literal("[ ").append(this.editButton.getMessage().copy().formatted(Formatting.WHITE)).append(" ]").formatted(Formatting.RED));
					this.editButton.setTooltip(Tooltip.of(Text.translatable("controls.keybinds.duplicateKeybinds", mutableText)));
				} else {
					this.editButton.setTooltip(null);
				}

				if (parent.selectedKeyBinding == this.binding) {
					this.editButton
							.setMessage(
									Text.literal("> ")
											.append(this.editButton.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
											.append(" <")
											.formatted(Formatting.YELLOW)
							);
				}
			}
		}
	}
}
