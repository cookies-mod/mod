package codes.cookies.mod.features.farming.inventory;

import codes.cookies.mod.config.categories.FarmingCategory;
import com.google.common.collect.Lists;
import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.config.ConfigManager;

import codes.cookies.mod.config.data.RancherSpeedConfig;
import codes.cookies.mod.data.farming.RancherSpeeds;
import codes.cookies.mod.data.profile.ProfileData;
import codes.cookies.mod.data.profile.ProfileStorage;
import codes.cookies.mod.events.api.ScreenKeyEvents;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.IntReference;
import codes.cookies.mod.utils.SkyblockUtils;
import codes.cookies.mod.utils.dev.DevUtils;
import codes.cookies.mod.utils.minecraft.SoundUtils;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
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

/**
 * Shows the optimal speeds on the rancher boots
 */
public class RancherBootsOverlay {
    private static final Identifier SKIP_RANCHER_BOOTS_CHECK =
        DevUtils.createIdentifier("garden/rancher_boots/disable_boots_check");
    boolean useProfile = false;
    private RancherSpeeds rancherSpeeds;
    private int editing = -1;
    private int maxWidth = 0;
    private boolean show = false;
    private float track = 0;
    private String editingValue = "";
    private Runnable save = this::saveConfig;

    @SuppressWarnings("MissingJavadoc")
    public RancherBootsOverlay() {
        ScreenEvents.BEFORE_INIT.register(this::openScreen);
    }

    private void saveCurrent() {
        if (this.editingValue.isBlank()) {
            this.editingValue = "0";
        }
        final IntReference intReference = this.indexToReference(this.editing);
        if (intReference != null) {
            intReference.set(Integer.parseInt(this.editingValue));
        }
    }

    private void setEditing(int index) {
        this.saveCurrent();
        this.editing = index;

        final IntReference newReference = this.indexToReference(this.editing);
        if (newReference != null) {
            this.editingValue = String.valueOf(newReference.get());
        } else {
            this.editingValue = "";
        }

        if (index == -1) {
            CookiesMod.getExecutorService().execute(this.save);
        }
    }

    private void openScreen(MinecraftClient minecraftClient, Screen screen, int width, int height) {
        if (!(screen instanceof SignEditScreen)) {
            return;
        }
        if (!SkyblockUtils.isCurrentlyInSkyblock()) {
            return;
        }
        if (!FarmingCategory.showRancherOptimalSpeed) {
            return;
        }
        if (!DevUtils.isEnabled(SKIP_RANCHER_BOOTS_CHECK) && !this.isRancherBootsScreen((SignEditScreen) screen)) {
            return;
        }

        this.evaluateSpeeds();
        this.setEditing(-1);
        this.maxWidth = -1;
        ScreenEvents.afterRender(screen).register(this::render);
        ScreenMouseEvents.afterMouseClick(screen).register(this::mouseClick);
        ScreenKeyEvents.getExtension(screen).cookies$allowCharTyped().register(this::charTyped);
        ScreenKeyboardEvents.allowKeyPress(screen).register(this::keyPressed);
    }

    private boolean isRancherBootsScreen(SignEditScreen screen) {
        return screen.messages[1].trim().equals("^^^^^^") && screen.messages[2].trim().equals("Set your") &&
               screen.messages[3].trim().equals("speed cap!");
    }

    private void evaluateSpeeds() {
        final RancherSpeedConfig rancherSpeed = FarmingCategory.rancherSpeed.getValue();

        final ProfileData profile = ProfileStorage.getCurrentProfile().orElse(null);

        boolean useProfile =
            profile != null && rancherSpeed.useProfileSettings.contains(profile.getProfileUuid().toString());

        RancherSpeeds rancherSpeeds;
        this.useProfile = useProfile;

        if (useProfile) {
            rancherSpeeds = profile.getRancherSpeeds().asData();
            this.save = ProfileStorage::saveCurrentProfile;
        } else {
            rancherSpeeds = FarmingCategory.rancherSpeed.getValue().asData();
            this.save = this::saveConfig;
        }

        this.rancherSpeeds = rancherSpeeds;
    }

    private void render(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        drawContext.getMatrices().push();

        int x = this.getX(screen);
        int y = this.getY(screen);
        drawContext.getMatrices().translate(x, y, 50.0f);
        int translatedMouseX = mouseX - x;
        int translatedMouseY = mouseY - y;

        drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer,
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
            this.setEditing(-1);
            SoundUtils.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 3, 1);
            this.toggleUseProfile();
        } else if (index == 13) {
            SoundUtils.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 3, 1);
            this.rancherSpeeds.loadFrom(RancherSpeedConfig.DEFAULT);
            this.setEditing(-1);
        } else if (this.editing == -1 && index == 11) {
            this.setEditing(0);
        } else if (this.editing != -1) {
            this.setEditing(index);
        } else {
            final IntReference intReference = this.indexToReference(index);
            if (intReference == null) {
                return;
            }

            SignEditScreen signEditScreen = (SignEditScreen) screen;
            signEditScreen.messages[0] = String.valueOf(intReference.get());
        }
    }

    private void saveConfig() {
        ConfigManager.saveConfig("ranchers-boots-speed");
    }

    private void toggleUseProfile() {
        ProfileStorage.getCurrentProfile().ifPresent(profileData -> {
            final String uuid = profileData.getProfileUuid().toString();
            final List<String> uuids = FarmingCategory.rancherSpeed.getValue().useProfileSettings;
            if (this.useProfile) {
                uuids.remove(uuid);
            } else {
                uuids.add(uuid);
            }
            CookiesMod.getExecutorService().execute(this::saveConfig);
            this.evaluateSpeeds();
        });
    }

    private boolean charTyped(Screen screen, char c, int i) {
        if (this.editing == -1) {
            return true;
        }
        final IntReference intReference = this.indexToReference(this.editing);
        if (intReference == null) {
            return true;
        }
        if (c < '0' || c > '9') {
            return false;
        }
        if (this.editingValue.length() >= 3) {
            return false;
        }

        this.editingValue += c;
        return false;
    }

    private boolean keyPressed(Screen screen, int key, int scancode, int modifiers) {
        if (this.editing == -1) {
            return true;
        }
        final IntReference intReference = this.indexToReference(this.editing);
        if (intReference == null) {
            return true;
        }

        switch (key) {
            case InputUtil.GLFW_KEY_ENTER, InputUtil.GLFW_KEY_KP_ENTER -> this.setEditing(-1);
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
                    temp = 9;
                } else if (temp > 9) {
                    temp = 0;
                }
                this.setEditing(temp);
            }
            case InputUtil.GLFW_KEY_ESCAPE -> {
                this.setEditing(-1);
                return true;
            }
            case InputUtil.GLFW_KEY_UP -> this.setEditing(Math.max(this.editing - 1, 0));
            case InputUtil.GLFW_KEY_DOWN -> this.setEditing(Math.min(this.editing + 1, 9));
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

        texts.add(Text.translatable(TranslationKeys.RANCHER_BOOTS_FARMING_SPEEDS)
            .formatted(Formatting.BOLD)
            .withColor(Constants.MAIN_COLOR));
        texts.add(Text.empty());
        texts.add(Text.translatable("item.minecraft.wheat")
            .append(": ")
            .withColor(0xF5DEB3)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.wheat().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("item.minecraft.carrot")
            .append(": ")
            .withColor(0xED9121)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.carrot().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("item.minecraft.potato")
            .append(": ")
            .withColor(0xB79268)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.potato().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("item.minecraft.nether_wart")
            .append(": ")
            .withColor(0x9F1B0F)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.netherWart().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("block.minecraft.pumpkin")
            .append(": ")
            .withColor(0xFF7518)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.pumpkin().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("block.minecraft.melon")
            .append(": ")
            .withColor(0x74AC8D)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.melon().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("item.minecraft.cocoa_beans")
            .append(": ")
            .withColor(0x481C1C)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.cocoaBeans().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("block.minecraft.sugar_cane")
            .append(": ")
            .withColor(0xC3DB79)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.sugarCane().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("block.minecraft.cactus")
            .append(": ")
            .withColor(0x5C755E)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.cactus().get())).formatted(Formatting.GRAY)));
        texts.add(Text.translatable("block.minecraft.red_mushroom")
            .append(": ")
            .withColor(0x90806D)
            .append(Text.literal(String.valueOf(this.rancherSpeeds.mushroom().get())).formatted(Formatting.GRAY)));

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
            mutableText.append(Text.literal(this.editingValue).formatted(Formatting.GRAY));
            if (this.show) {
                mutableText.append(Text.literal("_").formatted(Formatting.GRAY));
            }
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

    private IntReference indexToReference(int index) {
        return switch (index) {
            case 0 -> this.rancherSpeeds.wheat();
            case 1 -> this.rancherSpeeds.carrot();
            case 2 -> this.rancherSpeeds.potato();
            case 3 -> this.rancherSpeeds.netherWart();
            case 4 -> this.rancherSpeeds.pumpkin();
            case 5 -> this.rancherSpeeds.melon();
            case 6 -> this.rancherSpeeds.cocoaBeans();
            case 7 -> this.rancherSpeeds.sugarCane();
            case 8 -> this.rancherSpeeds.cactus();
            case 9 -> this.rancherSpeeds.mushroom();
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
