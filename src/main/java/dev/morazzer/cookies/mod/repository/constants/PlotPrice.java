package dev.morazzer.cookies.mod.repository.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.morazzer.cookies.mod.utils.json.JsonUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constant values related to garden plot prices.
 */
public class PlotPrice {

    private static final Logger LOGGER = LoggerFactory.getLogger("plot_price");

    List<Cost> center;
    List<Cost> middle;
    List<Cost> edges;
    List<Cost> corners;

    /**
     * Creates a new plot price constants instance.
     *
     * @param jsonObject The json object to read.
     */
    public PlotPrice(JsonObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        center = read(jsonObject.getAsJsonArray("center"));
        middle = read(jsonObject.getAsJsonArray("middle"));
        edges = read(jsonObject.getAsJsonArray("edges"));
        corners = read(jsonObject.getAsJsonArray("corners"));
    }

    /**
     * Gets the list by the index.
     * @param index The index.
     * @return The list.
     */
    public List<Cost> getByIndex(int index) {
        return switch (index) {
            case 0 -> center;
            case 1 -> middle;
            case 2 -> edges;
            case 3 -> corners;
            default -> Collections.emptyList();
        };
    }

    private List<Cost> read(JsonArray json) {
        List<Cost> costs = new ArrayList<>(json.size());
        for (JsonElement jsonElement : json) {
            if (jsonElement instanceof JsonObject jsonObject) {
                costs.add(
                    new Cost(
                        jsonObject.get("amount").getAsInt(),
                        jsonObject.get("bundle").getAsBoolean()
                    )
                );
            } else if (jsonElement instanceof JsonPrimitive jsonPrimitive) {
                if (!jsonPrimitive.isNumber()) {
                    LOGGER.error("Plot price cannot contain non numbers");
                    continue;
                }
                costs.add(
                    new Cost(
                        jsonPrimitive.getAsInt(),
                        false
                    )
                );
            }
        }
        return costs;
    }

    @SuppressWarnings("MissingJavadoc")
    public record Cost(int amount, boolean bundle) {
    }

}
