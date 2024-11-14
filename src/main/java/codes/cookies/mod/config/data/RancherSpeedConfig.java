package codes.cookies.mod.config.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import codes.cookies.mod.config.system.SaveLoadHelper;
import codes.cookies.mod.data.farming.RancherSpeeds;
import codes.cookies.mod.utils.IntReference;
import codes.cookies.mod.utils.json.JsonSerializable;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * Profile specific rancher boot speeds.
 */
@SuppressWarnings("MissingJavadoc")
public class RancherSpeedConfig implements JsonSerializable, SaveLoadHelper {
    public static RancherSpeeds DEFAULT = new RancherSpeedConfig().asData();

    public ListData<String> useProfileSettings = new ListData<>(
        new ArrayList<>(),
        JsonElement::getAsString,
        JsonPrimitive::new);

    public IntegerData wheat = new IntegerData(93);
    public IntegerData carrot = new IntegerData(93);
    public IntegerData potato = new IntegerData(93);
    public IntegerData netherWart = new IntegerData(93);
    public IntegerData pumpkin = new IntegerData(258);
    public IntegerData melon = new IntegerData(258);
    public IntegerData cocoaBeans = new IntegerData(155);
    public IntegerData sugarCane = new IntegerData(327);
    public IntegerData cactus = new IntegerData(400);
    public IntegerData mushroom = new IntegerData(200);

    public RancherSpeeds asData() {
        return new RancherSpeeds(
            new IntReference(wheat::getValue, wheat::setValue),
            new IntReference(carrot::getValue, carrot::setValue),
            new IntReference(potato::getValue, potato::setValue),
            new IntReference(netherWart::getValue, netherWart::setValue),
            new IntReference(pumpkin::getValue, pumpkin::setValue),
            new IntReference(melon::getValue, melon::setValue),
            new IntReference(cocoaBeans::getValue, cocoaBeans::setValue),
            new IntReference(sugarCane::getValue, sugarCane::setValue),
            new IntReference(cactus::getValue, cactus::setValue),
            new IntReference(mushroom::getValue, mushroom::setValue)
        );
    }

    @Override
    public void read(@NotNull JsonElement jsonElement) {
        load_(jsonElement);
    }

    @Override
    public @NotNull JsonElement write() {
        return save_();
    }
}
