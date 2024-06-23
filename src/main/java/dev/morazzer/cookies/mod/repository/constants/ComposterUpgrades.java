package dev.morazzer.cookies.mod.repository.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.morazzer.cookies.mod.repository.Ingredient;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

/**
 * Constant values related to composter upgrades
 */
@SuppressWarnings("MissingJavadoc")
@Getter
public class ComposterUpgrades {

    private final List<CompostUpgrade> speed;
    private final List<CompostUpgrade> multiDrop;
    private final List<CompostUpgrade> fuelCap;
    private final List<CompostUpgrade> organicMatterCap;
    private final List<CompostUpgrade> costReduction;

    public ComposterUpgrades(JsonObject jsonObject) {
        if (jsonObject == null) {
            throw new UnsupportedOperationException();
        }
        this.speed = getUpgrades(jsonObject.getAsJsonArray("speed"));
        this.multiDrop = getUpgrades(jsonObject.getAsJsonArray("multi_drop"));
        this.fuelCap = getUpgrades(jsonObject.getAsJsonArray("fuel_cap"));
        this.organicMatterCap = getUpgrades(jsonObject.getAsJsonArray("organic_matter_cap"));
        this.costReduction = getUpgrades(jsonObject.getAsJsonArray("cost_reduction"));
    }

    private List<CompostUpgrade> getUpgrades(JsonArray jsonElements) {
        LinkedList<CompostUpgrade> list = new LinkedList<>();
        for (JsonElement jsonElement : jsonElements) {
            if (!jsonElement.isJsonObject()) {
                continue;
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            int copper = jsonObject.get("copper").getAsInt();
            LinkedList<Ingredient> upgradeList = new LinkedList<>();
            JsonObject costObject = jsonObject.getAsJsonObject("cost");
            for (String key : costObject.keySet()) {
                upgradeList.add(new Ingredient(
                    key,
                    costObject.get(key).getAsInt()
                ));
            }
            list.add(new CompostUpgrade(copper, upgradeList));
        }
        return list;
    }

    public record CompostUpgrade(int copper, List<Ingredient> cost) {
    }
}
