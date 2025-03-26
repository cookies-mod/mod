package codes.cookies.mod.utils;

import codes.cookies.mod.generated.BuildInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import codes.cookies.mod.translations.TranslationKeys;
import codes.cookies.mod.utils.cookies.Constants;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import lombok.extern.slf4j.Slf4j;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.URI;

/**
 * Used to check if the mod is up-to-date, if not send a message that notifies the user of the new release.
 */
@Slf4j
public class UpdateChecker {
    private static boolean isUpToDate = false;
    private static String latestVersion = "";
    private static boolean firstJoin = true;

    /**
     * Initializes the update checker.
     */
    public static void init() {
        ClientPlayConnectionEvents.JOIN.register(UpdateChecker::checkForUpdates);
    }

    private static void checkForUpdates(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (!firstJoin || isUpToDate) {
            return;
        }
        firstJoin = false;

        final MutableText firstLine =
            CookiesUtils.createPrefix(Constants.FAIL_COLOR).append(Text.translatable(TranslationKeys.UPDATE_AVAILABLE));

        final Text secondLine = Text.translatable(TranslationKeys.UPDATE_MODRINTH)
            .styled(style -> style.withClickEvent(new ClickEvent.OpenUrl(
                    URI.create("https://modrinth.com/mod/Te5vDuHn/version/" + latestVersion)))
                .withColor(Formatting.DARK_GRAY));

        CookiesUtils.sendMessage(firstLine.append("\n").append(secondLine));
    }

    /**
     * Checks whether the mod version is equal to the latest mod info.
     *
     * @param modInfo Mod info.
     */
    public static void setLatestModInfo(JsonElement modInfo) {
        if (modInfo == null) {
            return;
        }
        if (modInfo.isJsonObject()) {
            JsonObject latestModInfo = modInfo.getAsJsonObject();
            JsonElement version;
            if (BuildInfo.isStable) {
                version = latestModInfo.get("version");
            } else {
                version = latestModInfo.get("version_beta");
            }
            try {
				final SemanticVersion parse = SemanticVersion.parse(version.getAsString());
				final int i = parse.compareTo((Version) BuildInfo.version);
				isUpToDate = i <= 0;
                latestVersion = version.getAsString();
            } catch (VersionParsingException e) {
                isUpToDate = true;
                latestVersion = BuildInfo.version.toString();
            }
        }
    }

}
