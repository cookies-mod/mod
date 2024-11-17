package codes.cookies.mod.utils.skyblock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import codes.cookies.mod.CookiesMod;
import codes.cookies.mod.api.ApiManager;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.json.JsonUtils;
import lombok.Getter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils to get current mayor data.
 */
public class MayorUtils {
	private static final Set<String> activePerks = new HashSet<>();
	static Logger LOGGER = LoggerFactory.getLogger(MayorUtils.class);
	static SkyblockDateTime nextMayor;
	@Getter
	private static Mayor mayor;

	public static void load() {
		nextMayor = SkyblockDateTime.now().getNext(SkyblockDateTime.SkyblockEvents.ELECTION_CLOSE);
		final long epochMilli = nextMayor.getInstant().toEpochMilli();
		long difference = epochMilli - System.currentTimeMillis();
		final long delay = difference / 1000 + new Random().nextInt(60);
		LOGGER.info(
				"Expecting next mayor at {}, scheduling refresh in {}s",
				CookiesUtils.formattedMs(difference),
				delay);
		CookiesMod.getExecutorService().submit(MayorUtils::update);
		CookiesMod.getExecutorService().schedule(MayorUtils::update, delay, TimeUnit.SECONDS);
	}

	/**
	 * Updates the cached data.
	 */
	private static void update() {
		activePerks.clear();
		mayor = null;
		try (CloseableHttpClient httpClient = HttpClients.createMinimal()) {
			final HttpGet httpGet = new HttpGet("https://api.hypixel.net/v2/resources/skyblock/election");
			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("User-Agent", ApiManager.USER_AGENT);

			final CloseableHttpResponse execute = httpClient.execute(httpGet);
			if (execute.getStatusLine().getStatusCode() != 200) {
				LOGGER.error("Failed to load mayor data, certain features may not work as intended!");
				return;
			}

			final byte[] bytes = execute.getEntity().getContent().readAllBytes();
			Response response = JsonUtils.CLEAN_GSON.fromJson(
					new String(bytes, StandardCharsets.UTF_8),
					Response.class);
			mayor = response.mayor;
			try {
				LOGGER.info("Current mayor is {}", mayor.name);
				buildPerks();
			} catch (NullPointerException e) {
				mayor = null;
				activePerks.clear();
				LOGGER.error("An error occurred while loading mayor data", e);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Builds the perk list.
	 */
	private static void buildPerks() {
		for (Perk perk : mayor.perks) {
			addPerk(perk.name);
		}
		addPerk(mayor.minister.name);
	}

	/**
	 * Adds a perk to the active list.
	 * @param name The perk to add.
	 */
	private static void addPerk(String name) {
		activePerks.add(name.replaceAll(" ", "_").replaceAll("[^\\w_]", ""));
	}

	/**
	 * Checks if a mayor perk (id) is active.
	 * @param perkId The perk to check.
	 * @return Whether the perk is active.
	 */
	public static boolean isPerkActive(String perkId) {
		return activePerks.contains(perkId);
	}

	/**
	 * Model that represents the api response.
	 * @param success Whether the request had success.
	 * @param lastUpdated Last time the data was updates.
	 * @param mayor The current mayor.
	 * @param current The current election.
	 */
	public record Response(boolean success, long lastUpdated, Mayor mayor, Current current) {
	}

	/**
	 * Mayor model that is returned by the api.
	 * @param key The mayor id.
	 * @param name The name of the mayor.
	 * @param perks The active perks.
	 * @param minister The minister of the last election.
 	 */
	public record Mayor(String key, String name, Perk[] perks, Minister minister) {
	}

	/**
	 * Current election model that is returned by the api.
	 * @param year The year the election is for.
	 * @param candidates The candidates present in the election.
	 */
	public record Current(int year, Candidate[] candidates) {
	}

	/**
	 * Minister model that is returned by the api.
	 * @param key The id of the minister.
	 * @param name The name of the minister.
	 * @param perk Their minister perk.
	 */
	public record Minister(String key, String name, Perk perk) {
	}

	/**
	 * Candidate model that is returned by the api.
	 * @param key The id of the candidate.
	 * @param name The name of the candidate.
	 * @param perks Their perks.
	 */
	public record Candidate(String key, String name, Perk[] perks) {
	}

	/**
	 * Perk model that is returned by the api.
	 * @param name The name of the perk.
	 * @param description The description of the perk.
	 * @param minister Whether it would be the minister perk.
	 */
	public record Perk(String name, String description, boolean minister) {
	}
}
