package codes.cookies.mod.features.farming.inventory.squeakymousemat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.ConfigManager;
import codes.cookies.mod.config.data.SqueakyMousematOption;
import codes.cookies.mod.data.farming.squeakymousemat.SqueakyMousematData;
import codes.cookies.mod.data.farming.squeakymousemat.SqueakyMousematEntry;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.events.api.ScreenKeyEvents;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.minecraft.SoundUtils;
import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

/**
 * Shows the optimal speeds on the rancher boots
 */
public class SqueakyMousematOverlay {
	private static final Identifier SKIP_RANCHER_BOOTS_CHECK =
			DevUtils.createIdentifier("garden/squeaky_mousemat/disable_check");
	boolean useProfile = false;
	private SqueakyMousematData data;
	private int editing = -1;
	private boolean isEditingYaw = true;
	private int maxWidth = 0;
	private boolean show = false;
	private float track = 0;
	private String editingValue = "";

	@SuppressWarnings("MissingJavadoc")
	public SqueakyMousematOverlay() {
		ScreenEvents.BEFORE_INIT.register(this::openScreen);
	}

	private void saveCurrent() {
		if (this.editingValue.isBlank()) {
			this.editingValue = "0";
		}
		final SqueakyMousematEntry intReference = this.indexToReference(this.editing);
		if (intReference != null) {
			final double newNumber = Double.parseDouble(this.editingValue);
			if (this.isEditingYaw) {
				intReference.setYaw(newNumber);
			} else {
				intReference.setPitch(newNumber);
			}
		}
	}

	private void setEditing(int index, boolean isEditingYaw) {
		this.saveCurrent();
		this.isEditingYaw = isEditingYaw;
		this.editing = index;

		final SqueakyMousematEntry entry = this.indexToReference(this.editing);
		if (entry != null) {
			this.editingValue = String.valueOf(this.isEditingYaw ? entry.getYaw() : entry.getPitch());
		} else {
			this.editingValue = "";
		}

		if (index == -1) {
			CookiesMod.getExecutorService().execute(this::saveConfig);
		}
	}

	private void openScreen(MinecraftClient minecraftClient, Screen screen, int width, int height) {
		if (!(screen instanceof SignEditScreen)) {
			return;
		}
		if (!SkyblockUtils.isCurrentlyInSkyblock()) {
			return;
		}
		if (!ConfigManager.getConfig().farmingConfig.showSqueakyMousematOverlay.getValue()) {
			return;
		}
		if (!DevUtils.isEnabled(SKIP_RANCHER_BOOTS_CHECK) && !this.isRancherBootsScreen((SignEditScreen) screen)) {
			return;
		}

		this.evaluateSpeeds();
		this.setEditing(-1, true);
		this.maxWidth = -1;
		ScreenEvents.afterRender(screen).register(this::render);
		ScreenMouseEvents.afterMouseClick(screen).register(this::mouseClick);
		ScreenKeyEvents.getExtension(screen).cookies$allowCharTyped().register(this::charTyped);
		ScreenKeyboardEvents.allowKeyPress(screen).register(this::keyPressed);
	}

	private boolean isRancherBootsScreen(SignEditScreen screen) {
		return screen.messages[1].trim().equals("Set Yaw Above!") && screen.messages[2].trim()
				.equals("Set Pitch Below!");
	}

	private void evaluateSpeeds() {
		final SqueakyMousematOption mousematOption = ConfigManager.getConfig().farmingConfig.squeakyMousematOption.getValue();

		final ProfileData profile = ProfileStorage.getCurrentProfile().orElse(null);

		boolean useProfile =
				profile != null && mousematOption.useProfileData().contains(profile.getProfileUuid());

		if (useProfile) {
			this.data = profile.getSqueakyMousematData();
		} else {
			this.data = mousematOption.data();
		}
	}

	private void render(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
		drawContext.getMatrices().push();

		int x = this.getX(screen);
		int y = this.getY(screen);
		drawContext.getMatrices().translate(x, y, 50.0f);
		int translatedMouseX = mouseX - x;
		int translatedMouseY = mouseY - y;

		drawContext.drawTooltip(
				MinecraftClient.getInstance().textRenderer,
				this.getText(translatedMouseX, translatedMouseY, tickDelta),
				0,
				0);

		drawContext.getMatrices().pop();
	}

	private void mouseClick(Screen screen, double mouseX, double mouseY, int button) {
		int x = this.getX(screen);
		int y = this.getY(screen);
		int translatedMouseX = (int) (mouseX - x);
		int translatedMouseY = (int) (mouseY - y);
		if (translatedMouseX <= 0 || translatedMouseX >= this.maxWidth) {
			return;
		}

		final int index = this.getIndex(translatedMouseY) - 2;
		if (index == 12) {
			this.setEditing(-1, true);
			SoundUtils.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 3, 1);
			this.toggleUseProfile();
		} else if (index == 13) {
			SoundUtils.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 3, 1);
			this.data.loadFrom(SqueakyMousematData.getDefault());
			this.setEditing(-1, true);
		} else if (this.editing == -1 && index == 11) {
			this.setEditing(0, true);
		} else if (this.editing != -1) {
			this.setEditing(index, true);
		} else {
			final SqueakyMousematEntry intReference = this.indexToReference(index);
			if (intReference == null) {
				return;
			}

			SignEditScreen signEditScreen = (SignEditScreen) screen;
			signEditScreen.messages[0] = String.valueOf(intReference.getYaw());
			signEditScreen.messages[3] = String.valueOf(intReference.getPitch());
		}
	}

	private void saveConfig() {
		ConfigManager.saveConfig(true, "squeaky-mousemat-overlay");
	}

	private void toggleUseProfile() {
		ProfileStorage.getCurrentProfile().ifPresent(profileData -> {
			final UUID uuid = profileData.getProfileUuid();
			final List<UUID> uuids = ConfigManager.getConfig().farmingConfig.squeakyMousematOption.getValue()
					.useProfileData();
			if (this.useProfile) {
				uuids.remove(uuid);
			} else {
				uuids.add(uuid);
			}
			this.useProfile = !this.useProfile;
			CookiesMod.getExecutorService().execute(this::saveConfig);
			this.evaluateSpeeds();
		});
	}

	private boolean charTyped(Screen screen, char c, int i) {
		if (this.editing == -1) {
			return true;
		}
		final SqueakyMousematEntry intReference = this.indexToReference(this.editing);
		if (intReference == null) {
			return true;
		}
		if (this.editingValue.isBlank() && c == '-') {
			this.editingValue = "-";
			return false;
		}
		if (this.editingValue.indexOf('.') == -1 && c == '.') {
			this.editingValue += ".";
			return false;
		}
		if (c < '0' || c > '9') {
			return false;
		}
		if (!this.editingValue.contains(".")) {
			if ((!this.editingValue.startsWith("-") || this.editingValue.length() >= 4) && this.editingValue.length() >= 3) {
				return false;
			}
		}

		this.editingValue += c;
		return false;
	}

	private boolean keyPressed(Screen screen, int key, int scancode, int modifiers) {
		if (this.editing == -1) {
			return true;
		}
		final SqueakyMousematEntry intReference = this.indexToReference(this.editing);
		if (intReference == null) {
			return true;
		}

		switch (key) {
			case InputUtil.GLFW_KEY_ENTER, InputUtil.GLFW_KEY_KP_ENTER -> this.setEditing(-1, true);
			case InputUtil.GLFW_KEY_BACKSPACE -> {
				if (this.editingValue.length() <= 1) {
					this.editingValue = "";
				} else {
					this.editingValue = this.editingValue.substring(0, this.editingValue.length() - 1);
				}
				return false;
			}
			case InputUtil.GLFW_KEY_TAB -> {
				int temp = this.editing;
				if (Screen.hasShiftDown()) {
					temp--;
				} else {
					temp++;
				}
				if (temp < 0) {
					temp = 18;
				} else if (temp > 18) {
					temp = 0;
				}
				this.setEditing(temp / 2, temp % 2 == 0);
			}
			case InputUtil.GLFW_KEY_ESCAPE -> {
				this.setEditing(-1, true);
				return true;
			}
			case InputUtil.GLFW_KEY_UP -> this.setEditing(Math.max(this.editing - 1, 0), this.isEditingYaw);
			case InputUtil.GLFW_KEY_DOWN -> this.setEditing(Math.min(this.editing + 1, 9), this.isEditingYaw);
			case InputUtil.GLFW_KEY_LEFT, InputUtil.GLFW_KEY_RIGHT -> this.setEditing(this.editing, !this.isEditingYaw);
		}

		return false;
	}

	private int getX(Screen screen) {
		return (screen.width / 2 + 55);
	}

	private int getY(Screen screen) {
		int difference = screen.height / 4 + 55;
		int negativeOffsetY = 0;
		if (difference < 160) {
			negativeOffsetY = 160 - difference;
		}
		return 67 - negativeOffsetY;
	}

	private List<Text> getText(int mouseX, int mouseY, float tickDelta) {
		List<MutableText> texts = new ArrayList<>();

		texts.add(Text.literal("Farming Angles")
				.formatted(Formatting.BOLD)
				.withColor(Constants.MAIN_COLOR));
		texts.add(Text.empty());
		texts.add(Text.translatable("item.minecraft.wheat")
				.append(": ")
				.withColor(0xF5DEB3)
				.append(Text.literal(String.valueOf(this.data.wheat().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("item.minecraft.carrot")
				.append(": ")
				.withColor(0xED9121)
				.append(Text.literal(String.valueOf(this.data.carrot().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("item.minecraft.potato")
				.append(": ")
				.withColor(0xB79268)
				.append(Text.literal(String.valueOf(this.data.potato().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("item.minecraft.nether_wart")
				.append(": ")
				.withColor(0x9F1B0F)
				.append(Text.literal(String.valueOf(this.data.netherWart().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("block.minecraft.pumpkin")
				.append(": ")
				.withColor(0xFF7518)
				.append(Text.literal(String.valueOf(this.data.pumpkin().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("block.minecraft.melon")
				.append(": ")
				.withColor(0x74AC8D)
				.append(Text.literal(String.valueOf(this.data.melon().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("item.minecraft.cocoa_beans")
				.append(": ")
				.withColor(0x481C1C)
				.append(Text.literal(String.valueOf(this.data.cocoaBeans().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("block.minecraft.sugar_cane")
				.append(": ")
				.withColor(0xC3DB79)
				.append(Text.literal(String.valueOf(this.data.sugarCane().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("block.minecraft.cactus")
				.append(": ")
				.withColor(0x5C755E)
				.append(Text.literal(String.valueOf(this.data.cactus().get())).formatted(Formatting.GRAY)));
		texts.add(Text.translatable("block.minecraft.red_mushroom")
				.append(": ")
				.withColor(0x90806D)
				.append(Text.literal(String.valueOf(this.data.mushroom().get())).formatted(Formatting.GRAY)));

		texts.add(Text.empty());
		texts.add(Text.literal(Constants.Emojis.PEN + " ")
				.append(Text.translatable(TranslationKeys.CLICK_TO_EDIT))
				.formatted(Formatting.DARK_AQUA));
		if (this.useProfile) {
			texts.add(Text.literal(Constants.Emojis.EMPTY_BOX + " ")
					.append(Text.translatable(TranslationKeys.RANCHER_BOOTS_SAVE_GLOBAL))
					.formatted(Formatting.DARK_GREEN));
		} else {
			texts.add(Text.literal(Constants.Emojis.CHECKED_BOX + " ")
					.append(Text.translatable(TranslationKeys.RANCHER_BOOTS_SAVE_GLOBAL))
					.formatted(Formatting.DARK_GREEN));
		}
		texts.add(Text.literal(Constants.Emojis.REPEAT_ARROW + " ")
				.append(Text.translatable(TranslationKeys.RANCHER_BOOTS_RESET_TO_DEFAULT))
				.formatted(Formatting.RED));
		for (MutableText text : texts) {
			int width = MinecraftClient.getInstance().textRenderer.getWidth(text);
			if (width > this.maxWidth) {
				this.maxWidth = width;
			}
		}

		if (this.editing >= 0 && this.editing <= 9) {
			final MutableText mutableText = texts.get(this.editing + 2);
			mutableText.getSiblings().removeLast();
			MutableText newSibling = Text.empty().formatted(Formatting.GRAY);
			final SqueakyMousematEntry squeakyMousematEntry = Optional.ofNullable(this.indexToReference(this.editing))
					.orElse(SqueakyMousematEntry.EMPTY);
			MutableText editing = Text.literal(this.editingValue);
			if (this.isEditingYaw) {
				newSibling.append(editing);
				newSibling.append("/ ").append(String.valueOf(squeakyMousematEntry.getPitch()));
			} else {
				newSibling.append(String.valueOf(squeakyMousematEntry.getYaw())).append(" / ");
				newSibling.append(editing);
			}
			if (this.show) {
				editing.append(Text.literal("_").formatted(Formatting.GRAY));
			} else {
				editing.append(Text.literal(" ").formatted(Formatting.DARK_GRAY));
			}
			mutableText.append(newSibling);
			this.track += tickDelta;
			if (this.track >= 8) {
				this.track = 0;
				this.show = !this.show;
			}
		}

		if (mouseX > 0 && mouseX <= this.maxWidth) {
			if (this.editing != this.getIndex(mouseY) - 2) {

				final MutableText textAt = this.getTextAt(texts, mouseY);

				if (textAt != null) {
					textAt.formatted(Formatting.UNDERLINE);
					for (Text sibling : textAt.getSiblings()) {
						if (sibling instanceof MutableText mutableText) {
							mutableText.formatted(Formatting.UNDERLINE);
						}
					}
				}
			}
		}


		return Lists.transform(texts, Text.class::cast);
	}

	private int getIndex(int y) {
		return (y) / 10 + 1;
	}

	private SqueakyMousematEntry indexToReference(int index) {
		return switch (index) {
			case 0 -> this.data.wheat();
			case 1 -> this.data.carrot();
			case 2 -> this.data.potato();
			case 3 -> this.data.netherWart();
			case 4 -> this.data.pumpkin();
			case 5 -> this.data.melon();
			case 6 -> this.data.cocoaBeans();
			case 7 -> this.data.sugarCane();
			case 8 -> this.data.cactus();
			case 9 -> this.data.mushroom();
			default -> null;
		};
	}

	private MutableText getTextAt(List<MutableText> texts, int y) {
		final int i = this.getIndex(y);
		if (i >= 1 && i < texts.size()) {
			return texts.get(i);
		}
		return null;
	}
}
