package codes.cookies.mod.repository.constants.mining;

import codes.cookies.mod.data.mining.PowderType;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import codes.cookies.mod.utils.json.JsonUtils;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constant values related to the Heart of the Mountain
 */
public class Hotm {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hotm.class);
    private final Map<String, Perk> perks = new HashMap<>();

    /**
     * Creates a new hotm constants instance.
     *
     * @param jsonObject The json object to read.
     */
    public Hotm(JsonObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        jsonObject.entrySet().forEach(entry -> {
            if (entry.getValue().isJsonObject()) {
                addPerk(entry.getKey(), entry.getValue().getAsJsonObject());
            }
        });
    }

    private void addPerk(String name, JsonObject object) {
        final Perk perk = JsonUtils.CLEAN_GSON.fromJson(object, Perk.class);
        perks.put(name, perk);
    }

    /**
     * Gets a perk by its internal name.
     * You get the internal name by replacing ' ' with '_' and '\W' with ''.
     *
     * @param name The internal name.
     * @return The perk (or null).
     */
    public Perk getPerk(String name) {
        return perks.get(name);
    }


	/**
     * Describes a perk in the hotm tree.
     *
     * @param powderType The different types of powder.
     * @param levels      The cost for every level starting at level 2.
     */
    public record Perk(@SerializedName("type") PowderType powderType, int[] levels) {
        /**
         * Calculates the cost for the next n levels, where n is amount - 1.
         *
         * @param n     The amount of levels.
         * @param start The start level.
         * @return The cost.
         */
        public int calculateNextN(int n, int start) {
            int amount = 0;
            for (int i = Math.max(0, start - 1); i < Math.min(Math.max(0, start - 1) + n, levels.length); i++) {
                amount += levels[i];
            }
            return amount;
        }

        /**
         * Calculates the total cost from the start level to the max level.
         *
         * @param start The start level.
         * @return The total cost.
         */
        public int calculateTotal(int start) {
            int amount = 0;
            for (int i = Math.max(0, start - 1); i < levels.length; i++) {
                amount += levels[i];
            }
            return amount;
        }

		public boolean isOverMax(int i) {
			return levels.length < i;
		}
	}

}
