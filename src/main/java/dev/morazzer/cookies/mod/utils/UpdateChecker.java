package dev.morazzer.cookies.mod.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.mods.cookies.generated.BuildInfo;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Used to check if the mod is up-to-date, if not send a message that notifies the user of the new release.
 */
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
            CookiesUtils.createPrefix(Constants.FAIL_COLOR).append("Your version of the mod isn't up-to-date!");

        final Text secondLine = Text.literal("(Click here to open modrinth)")
            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                    "https://modrinth.com/mod/Te5vDuHn/version/" + latestVersion))
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
                version = latestModInfo.get("beta_version");
            }
            try {
                final SemanticVersion parse = SemanticVersion.parse(version.getAsString());
                isUpToDate = BuildInfo.version.equals(parse);
                latestVersion = version.getAsString();
            } catch (VersionParsingException e) {
                isUpToDate = true;
                latestVersion = BuildInfo.version.toString();
            }
        }
    }

}
