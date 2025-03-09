package codes.cookies.mod.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import codes.cookies.mod.config.categories.ItemSearchCategory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConfigMigrator {

	public static JsonObject migrateToNewConfig(JsonObject oldConfig) {
		ConfigMappingBuilder builder = new ConfigMappingBuilder()
				.object(
						"miscConfig", "misc_config",
						miscBuilder ->
								miscBuilder.rename("enableScrollableTooltips", "scrollable_tooltips")
										.rename("signEditEnterSubmits", "sign_enter_submits")
										.rename("enableStoragePreview", "enable_storage_preview")
										.rename("showPing", "show_ping")
										.rename("showMuseumArmorSets", "show_museum_armor_sets")
										.object(
												"notificationFoldable", "primal_fear", primalFearBuilder ->
														primalFearBuilder.keep("enabled")
																.keep("type")
																.rename("enableSound", "enable_sound")
										).rename("showItemCreationDate", "item_creation_date")
										.rename("showItemNpcValue", "item_npc_value")
										.rename("hideOwnArmor", "hide_own_armour")
										.rename("hideOtherArmor", "hide_other_armour")
										.rename("showDyeArmor", "show_dyed_armour")
										.rename("hideFireOnEntities", "hide_fire_on_entities")
										.rename("hideLightningBolt", "hide_lightning_bolt")
										.rename("hidePotionEffects", "hide_potion_effects")
										.rename("hideHealth", "hide_health")
										.rename("hideArmor", "hide_armour")
										.rename("hideFood", "hide_food")
										.rename("showPetLevelAsStackSize", "show_pet_level")
										.rename("showPetRarityInLevelText", "show_pet_rarity_in_level_text")
										.rename("showItemUpgrades", "show_item_upgrades")
										.rename("showForgeRecipeStack", "show_forge_recipes")
										.rename("forgeRecipeSlot", "forge_slot")
				).object(
						"farmingConfig", "farming_category", farmingBuilder ->
								farmingBuilder.rename("showPlotPriceBreakdown", "show_plot_price_breakdown")
										.rename("yawPitchDisplay", "yaw_pitch_display")
										.object(
												"pestFoldable", "pest_timer", pestTimerBuilder ->
														pestTimerBuilder.keep("enabled")
																.keep("type")
																.rename("timerType", "timer_type")
																.rename("enableSound", "enable_sound")
										).rename("gardenKeybindPredicate", "keybind_predicate")
										.rename("showRancherSpeed", "show_rancher_speed")
										.rename("showRancherOptimalSpeeds", "show_rancher_optimal_speed")
										.rename("visitorRareDropProtection", "visitor_drop_protection")
										.rename("visitorNotAsRareDropProtection", "visitor_common_protection")
										.rename("showSqueakyMousematOverlay", "mousemat_overlay")
										.rename("squeakyMousematOption", "squeaky_mousemat_data")
										.rename("showCompostPriceBreakdown", "compost_price_breakdown")
										.rename("compostSortOrder", "compost_sort_order")
										.rename("highlightUnclaimedJacobContests", "highlight_unclaimed_jacob_contests")
										.rename("highlightGlowingMushrooms", "highlight_glowing_mushrooms")
										.debug()
										.object(
												"rancherSpeed", "rancher_speeds", rancherBuilder ->
														rancherBuilder.rename("useProfileSettings", "uuids")
																.rename("wheat", "wheat")
																.rename("carrot", "carrot")
																.rename("potato", "potato")
																.rename("netherWart", "nether_wart")
																.rename("pumpkin", "pumpkin")
																.rename("melon", "melon")
																.rename("cocoaBeans", "cocoa_beans")
																.rename("sugarCane", "sugar_cane")
																.rename("cactus", "cactus")
																.rename("mushroom", "mushroom")
										)
				).object(
						"miningConfig", "mining", miningBuilder ->
								miningBuilder.rename("modifyCommissions", "modify_commissions")
										.rename("puzzlerSolver", "puzzler_solver")
										.rename("modHelper", "mod_helper")
										.object(
												"shaftConfig", "shaft", shaftBuilder ->
														shaftBuilder.rename("enable", "enabled")
																.keep("text")
																.keep("beam")
																.keep("box")
																.keep("color")
																.rename("announcementType", "type")
										)
										.rename("glossyGemstoneMessages", "glossy_gemstone_message")
										.rename("showHotmPerkLevelAsStackSize", "show_hotm_perk_level_as_stack_size")
										.rename("highlightDisabledHotmPerks", "highlight_disabled_hotm_perks")
										.rename("showNext10Cost", "show_next_10_cost")
										.rename("showTotalCost", "show_total_cost")
				).object(
						"helpersConfig",
						"helpers_category",
						helpersBuilder -> helpersBuilder.rename("anvilHelper", "anvil_helper")
								.rename("itemChestTracker", "chest_tracker")
								.object(
										"craftHelper",
										"craft_helper",
										craftHelperBuilder -> craftHelperBuilder.rename("craftHelper", "enabled")
												.rename("craftHelperLocation", "location")
												.object(
														"sources", "sources", sourceBuilder ->
																sourceBuilder.keep("chests")
																		.keep("chests")
																		.keep("storage")
																		.keep("sacks")
																		.keep("inventory")
																		.keep("forge")
																		.keep("vault")
																		.rename("sacksOfSacks", "sack_of_sacks")
																		.rename("potionBag", "potion_bag")
																		.rename("accessoryBag", "accessory_bag")
												)
								)
				).object(
						"cleanupConfig", "cleanup", cleanupBuilder ->
								cleanupBuilder.rename("coopCleanupOption", "coop_cleanup")
										.rename("hideWatcherMessages", "hide_watcher_messages")
										.rename("hidePotionEffectMessage", "hide_potion_effect_message")
										.rename("hideClassMessages", "hide_class_messages")
										.rename("hideUltimateReady", "hide_ultimate_ready")
										.rename("hideBlessingMessage", "hide_blessing_messages")
										.rename("hideSilverfishMessage", "hide_silverfish_message")
										.rename("hideDungeonKeyMessage", "hide_dungeon_key_message")
										.rename("removeDungeonStats", "remove_dungeon_stats")
										.rename("removeReforgeStats", "remove_reforge_stats")
										.rename("removeHpbStats", "remove_hpb_stats")
										.rename("removeGemstoneStats", "remove_gemstone_stats")
										.rename("removeGearScore", "remove_gear_score")
										.rename("removeBlank", "remove_blank_line")
										.rename("removeFullSetBonus", "remove_full_set_bonus")
										.rename("removeGemstoneLine", "remove_gemstone_line")
										.rename("removeAbility", "remove_ability")
										.rename("removePieceBonus", "remove_piece_bonus")
										.rename("removeEnchants", "remove_enchants")
										.rename("removeReforge", "remove_reforge")
										.rename("removeSoulbound", "remove_soulbound")
										.rename("removeRunes", "remove_runes")
										.rename("removeMaxLevel", "remove_max_level")
										.rename("removeActions", "remove_actions")
										.rename("removeHeldItem", "remove_held_item")
				).object(
						"dungeonConfig", "dungeon", dungeonBuilder -> {
							dungeonBuilder.rename("useDungeonFeatures", "use_dungeon_features")
									.rename("relayToBackend", "relay_to_backend")
									.object(
											"terminalFoldable", "terminals", terminalBuilder ->
													terminalBuilder.rename("preventMissclicks", "prevent_missclicks")
															.rename(
																	"changeAllToSameColorTerminal",
																	"change_all_to_same_color")
															.rename("clickInOrderTerminal", "click_in_order_terminal")
															.rename(
																	"correctAllThePanesTerminal",
																	"correct_all_the_panes")
															.rename("selectAllColorsTerminal", "select_all_colors")
															.rename("startsWithTerminal", "starts_with")
									).object(
											"spiritLeapFoldable", "spirit_leap", spiritLeapBuilder ->
													spiritLeapBuilder
															.rename("colorInClassColor", "color_in_class_color")
															.rename("modifyNormalIfAvailable", "modify_normal")
															.rename("usePlayerHeadsInsteadOfClassItems", "use_player_heads")
															.rename("spiritLeapUi", "spirit_leap_ui")
															.rename("showMap", "show_map")
															.rename("sortByClassName", "sort_by_class_name")
															.rename("colorOption", "fallback_color")
									).object(
											"puzzleFoldable", "puzzles", puzzleBuilder ->
													puzzleBuilder.rename("creeperBeams", "creeper_beams")
															.rename("higherLower", "higher_lower")
															.keep("quiz")
															.rename("threeWeirdos", "three_weirdos")
															.rename("waterBoard", "water_board")
									).rename("classColorFoldable", "class_color")
									.rename("glowClassColor", "glow_in_class_color")
									.rename("renderMap", "render_map")
									.rename("showPlayerSkulls", "show_player_skulls")
									.rename("rotatePlayerHeads", "rotate_player_heads")
									.rename("showPlayerNames", "show_player_names")
									.rename("renderOverRoomText", "render_over_room_text")
									.rename("keepWitherDoor", "keep_wither_doors")
									.rename("showSecrets", "show_secrets")
									.rename("showPuzzleName", "show_puzzle_name")
									.rename("showRoomStatusAsTextColor", "show_room_status")
									.rename("showTrapAsCleared", "show_trap_as_cleared")
									.rename("mapBackgroundColor", "map_background_color"); //TODO croesus
						}
				).object(
						"itemSearchConfig", "item_search", itemBuilder -> itemBuilder
								.rename("enableCraftableItems", "enable_craftable_items")
								.rename("enableNotCraftableItems", "enable_non_craftable_items")
								.rename("showOnlyMissingItems", "show_only_missing_items")
								.rename("showInMuseum", "show_in_museum")
								.rename("persistSearch", "persist_search")
								.modify("highlightTime", "highlight_time", ConfigMigrator::mapItemSearchDuration)
								.rename("highlightColor", "highlightColor")
				).object(
						"devConfig", "dev", devBuilder ->
								devBuilder.rename("hideConsoleSpam", "hide_console_spam")
										.rename("dataRepo", "data_repo")
										.rename("dataRepoBranch", "data_repo_branch")
										.rename("connectToBackend", "connect_to_backend")
										.rename("backendUrl", "backend_url")
										.rename("useVersionSuffix", "use_version_suffix")
				);

		final JsonObject newConfig = builder.build().apply(oldConfig);

		newConfig.addProperty("rconfig:version", 0);

		return newConfig;
	}

	private static JsonElement mapItemSearchDuration(JsonElement jsonElement) {

		final JsonPrimitive defaultValue = new JsonPrimitive(ItemSearchCategory.highlightTime);
		if (jsonElement == null || jsonElement.isJsonNull()) {
			return defaultValue;
		}

		if (!jsonElement.isJsonPrimitive()) {
			return defaultValue;
		}

		if (!jsonElement.getAsJsonPrimitive().isString()) {
			return defaultValue;
		}

		String value = jsonElement.getAsJsonPrimitive().getAsString();
		return new JsonPrimitive(switch (value) {
			case "TEN" -> 10;
			case "TWENTY" -> 20;
			case "THIRTY" -> 30;
			case "SIXTY" -> 60;
			case "ONETWENTY" -> 120;
			default -> defaultValue.getAsNumber();
		});
	}

	interface ConfigMapping {
		void apply(JsonObject oldObject, JsonObject newObject);
	}

	record CompiledConfigMapping(List<ConfigMapping> mappings) {
		public JsonObject apply(JsonObject oldObject) {
			JsonObject newObject = new JsonObject();
			for (ConfigMapping mapping : mappings) {
				mapping.apply(oldObject, newObject);
			}
			return newObject;
		}
	}

	record ModifyMapping(String oldName, String newName, Function<JsonElement, JsonElement> mapper)
			implements ConfigMapping {
		@Override
		public void apply(JsonObject oldObject, JsonObject newObject) {
			final JsonElement jsonElement = oldObject.get(oldName);
			newObject.add(newName, mapper.apply(jsonElement));
		}
	}

	static class ConfigMappingBuilder {

		List<ConfigMapping> mappings = new ArrayList<>();

		public ConfigMappingBuilder debug() {
			mappings.add((oldObject, newObject) -> System.out.println("add debug breakpoint :3"));
			return this;
		}

		public ConfigMappingBuilder modify(String oldName, String newName, Function<JsonElement, JsonElement> mapper) {
			mappings.add(new ModifyMapping(oldName, newName, mapper));
			return this;
		}

		public ConfigMappingBuilder rename(String oldName, String newName) {
			mappings.add(new RenameMapping(oldName, newName));
			return this;
		}

		public ConfigMappingBuilder keep(String name) {
			mappings.add(new RenameMapping(name, name));
			return this;
		}

		public ConfigMappingBuilder object(String oldName, String newName, Consumer<ConfigMappingBuilder> builder) {
			final ConfigMappingBuilder configMappingBuilder = new ConfigMappingBuilder();
			builder.accept(configMappingBuilder);
			final ObjectMapping objectMapping = new ObjectMapping(oldName, newName, configMappingBuilder.mappings);
			mappings.add(objectMapping);
			return this;
		}

		public CompiledConfigMapping build() {
			return new CompiledConfigMapping(mappings);
		}

	}

	record ObjectMapping(String oldObjectName, String newObjectName, List<ConfigMapping> objectMappings)
			implements ConfigMapping {
		@Override
		public void apply(JsonObject oldObject, JsonObject newObject) {
			JsonObject newConfigObject = new JsonObject();

			newConfigObject.addProperty("cookies_mod:autoport", oldObjectName);
			final JsonObject oldConfigObject = oldObject.getAsJsonObject(oldObjectName);

			objectMappings.forEach(configMapping -> configMapping.apply(oldConfigObject, newConfigObject));

			newObject.add(newObjectName, newConfigObject);
		}
	}

	record RenameMapping(String oldConfig, String newConfig) implements ConfigMapping {
		public void apply(JsonObject oldObject, JsonObject newObject) {
			newObject.add(newConfig, oldObject.get(oldConfig));
		}
	}

}
