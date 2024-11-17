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

	private static void buildPerks() {
		for (Perk perk : mayor.perks) {
			addPerk(perk.name);
		}

		if(mayor.minister != null) {
			addPerk(mayor.minister.perk.name);
		}
	}

	private static void addPerk(String name) {
		activePerks.add(name.replaceAll(" ", "_").replaceAll("[^\\w_]", ""));
	}

	public static boolean isPerkActive(String perkId) {
		return activePerks.contains(perkId);
	}

	public record Response(boolean success, long lastUpdated, Mayor mayor, Current current) {
	}

	public record Mayor(String key, String name, Perk[] perks, Minister minister) {
	}

	public record Current(int year, Candidate[] candidates) {
	}

	public record Minister(String key, String name, Perk perk) {
	}

	public record Candidate(String key, String name, Perk[] perks) {
	}

	public record Perk(String name, String description, boolean minister) {
	}
}
