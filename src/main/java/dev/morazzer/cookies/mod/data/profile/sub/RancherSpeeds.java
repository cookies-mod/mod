package dev.morazzer.cookies.mod.data.profile.sub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.data.profile.ProfileData;
import dev.morazzer.cookies.mod.utils.IntReference;
import dev.morazzer.cookies.mod.utils.json.JsonSerializable;
import org.jetbrains.annotations.NotNull;

/**
 * Global rancher speeds.
 */
@SuppressWarnings("MissingJavadoc")
public class RancherSpeeds implements JsonSerializable {

    private final ProfileData profileData;
    public int wheat = 93;
    public int carrot = 93;
    public int potato = 93;
    public int netherWart = 93;
    public int pumpkin = 258;
    public int melon = 258;
    public int cocoaBeans = 155;
    public int sugarCane = 327;
    public int cactus = 400;
    public int mushroom = 200;

    public RancherSpeeds(ProfileData profileData) {
        this.profileData = profileData;
    }

    public dev.morazzer.cookies.mod.data.RancherSpeeds asData() {
        return new dev.morazzer.cookies.mod.data.RancherSpeeds(
            new IntReference(() -> wheat, newSpeed -> wheat = newSpeed),
            new IntReference(() -> carrot, newSpeed -> carrot = newSpeed),
            new IntReference(() -> potato, newSpeed -> potato = newSpeed),
            new IntReference(() -> netherWart, newSpeed -> netherWart = newSpeed),
            new IntReference(() -> pumpkin, newSpeed -> pumpkin = newSpeed),
            new IntReference(() -> melon, newSpeed -> melon = newSpeed),
            new IntReference(() -> cocoaBeans, newSpeed -> cocoaBeans = newSpeed),
            new IntReference(() -> sugarCane, newSpeed -> sugarCane = newSpeed),
            new IntReference(() -> cactus, newSpeed -> cactus = newSpeed),
            new IntReference(() -> mushroom, newSpeed -> mushroom = newSpeed)
        );
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        if (!jsonElement.isJsonObject() || jsonElement.getAsJsonObject().isEmpty()) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("wheat")) {
            wheat = jsonObject.get("wheat").getAsInt();
        }
        if (jsonObject.has("carrot")) {
            carrot = jsonObject.get("carrot").getAsInt();
        }
        if (jsonObject.has("potato")) {
            potato = jsonObject.get("potato").getAsInt();
        }
        if (jsonObject.has("nether_wart")) {
            netherWart = jsonObject.get("nether_wart").getAsInt();
        }
        if (jsonObject.has("pumpkin")) {
            pumpkin = jsonObject.get("pumpkin").getAsInt();
        }
        if (jsonObject.has("melon")) {
            melon = jsonObject.get("melon").getAsInt();
        }
        if (jsonObject.has("cocoa_beans")) {
            cocoaBeans = jsonObject.get("cocoa_beans").getAsInt();
        }
        if (jsonObject.has("sugar_cane")) {
            sugarCane = jsonObject.get("sugar_cane").getAsInt();
        }
        if (jsonObject.has("cactus")) {
            cactus = jsonObject.get("cactus").getAsInt();
        }
        if (jsonObject.has("mushroom")) {
            mushroom = jsonObject.get("mushroom").getAsInt();
        }
    }

    @Override
    public @NotNull JsonElement write() {
        JsonObject jsonObject = new JsonObject();
        if (!ConfigManager.getConfig().farmingConfig.rancherSpeed.useProfileSettings.contains(this.profileData.getProfileUuid()
                                                                                                              .toString())) {
            return jsonObject;
        }
        jsonObject.addProperty("wheat", wheat);
        jsonObject.addProperty("carrot", carrot);
        jsonObject.addProperty("potato", potato);
        jsonObject.addProperty("nether_wart", netherWart);
        jsonObject.addProperty("pumpkin", pumpkin);
        jsonObject.addProperty("melon", melon);
        jsonObject.addProperty("cocoa_beans", cocoaBeans);
        jsonObject.addProperty("sugar_cane", sugarCane);
        jsonObject.addProperty("cactus", cactus);
        jsonObject.addProperty("mushroom", mushroom);
        return jsonObject;
    }
}
