package codes.cookies.mod.features.misc;

import codes.cookies.mod.config.categories.DungeonConfig;
import codes.cookies.mod.config.system.Option;
import codes.cookies.mod.events.ChatMessageEvents;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.skyblock.ChatUtils;
import codes.cookies.mod.utils.skyblock.PartyUtils;

import lombok.Getter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Map.entry;

public class PartyCommandsFeature {
	public static void register() {
		ChatMessageEvents.BEFORE_MODIFY.register(PartyCommandsFeature::handleChat);
	}

	private static final Random random = new Random();

	private static final Map<Predicate<String>, PartyCommand> commandMap = Map.ofEntries(
			entry(command -> command.equals("ptme"), new PartyCommand(() -> PartyCommandsFeature.shouldRunCommand("ptme"), (command, message) -> {
				var sender = getSender(message);
				if (sender.equals(MinecraftClient.getInstance().getSession().getUsername())) {
					return;
				}
				CookiesUtils.sendCommand("p transfer " + sender);
			})),
			entry(command -> command.equals("warp"), new PartyCommand(() -> PartyCommandsFeature.shouldRunCommand("warp"), (command, message) -> {
				CookiesUtils.sendCommand("p warp");
			})),
			entry(command -> {
				//only match f[1-7], m[1-7], and t[1-5]
				return command.matches("f[1-7]|m[1-7]|t[1-5]");
			}, new PartyCommand(() -> PartyCommandsFeature.shouldRunCommand("joinInstance"), (command, message) -> {
				StringBuilder joinInstanceCommand = new StringBuilder("joininstance ");
				switch (command.charAt(0)) {
					case 'f' -> joinInstanceCommand.append("CATACOMBS_FLOOR_");
					case 'm' -> joinInstanceCommand.append("MASTER_CATACOMBS_FLOOR_");
					case 't' -> {
						joinInstanceCommand.append("KUUDRA_");
						switch (command.charAt(1)) {
							case '1' -> joinInstanceCommand.append("BASIC");
							case '2' -> joinInstanceCommand.append("HOT");
							case '3' -> joinInstanceCommand.append("BURNING");
							case '4' -> joinInstanceCommand.append("FIERY");
							case '5' -> joinInstanceCommand.append("INFERNAL");
						}
						CookiesUtils.sendCommand(joinInstanceCommand.toString());
						return;
					}
				}

				switch (command.charAt(1)) {
					case '1' -> joinInstanceCommand.append("ONE");
					case '2' -> joinInstanceCommand.append("TWO");
					case '3' -> joinInstanceCommand.append("THREE");
					case '4' -> joinInstanceCommand.append("FOUR");
					case '5' -> joinInstanceCommand.append("FIVE");
					case '6' -> joinInstanceCommand.append("SIX");
					case '7' -> joinInstanceCommand.append("SEVEN");
				}

				CookiesUtils.sendCommand(joinInstanceCommand.toString());
			})),
			entry(command -> command.startsWith("dt"), new DownTimeCommand(() -> PartyCommandsFeature.shouldRunCommand("dt"))),
			entry(command -> command.equals("cf"), new PartyCommand(() -> PartyCommandsFeature.shouldRunCommand("cf"), (command, message) -> {
				if (random.nextBoolean()) {
					ChatUtils.sendPartyMessage("HEADS!");
				} else {
					ChatUtils.sendPartyMessage("TAILS!");
				}
			}))
	);

	private static boolean shouldRunCommand(String partyCommand) {
		PartyUtils.request();
		var player = CookiesUtils.getPlayer();
		if (partyCommand.matches("f[1-7]|m[1-7]|t[1-5]")) {
			partyCommand = "joininstance";
		}
		return Optional.ofNullable(DungeonConfig.getInstance().partyChatCommandsFoldable.partyChatCommands.get(partyCommand)).map(Option::getValue).orElse(true) && player.filter(clientPlayerEntity -> PartyUtils.isInParty() && PartyUtils.getPartyLeader().equals(clientPlayerEntity.getUuid())).isPresent();
	}

	private static void handleChat(Text text, boolean b) {
		if (b) {
			return;
		}
		var message = text.getString();

		if (!message.startsWith("§9Party §8> ")) {
			return;
		}

		var commandString = CookiesUtils.stripColor(message.substring(message.indexOf(": !") + 3).trim());

		var command = commandMap.entrySet().stream().filter(commandEntry -> commandEntry.getKey().test(commandString)).findFirst();

		if (command.isPresent()) {
			if (command.get().getValue().getPredicate().get()) {
				var action = command.get().getValue().getAction();
				if (action != null) {
					action.accept(commandString, message);
				}
			}
		}
	}

	private static String getSender(String message) {
		var sender = message.substring(message.indexOf("§8> ") + 4, message.indexOf(":")).trim();
		int rank = sender.indexOf("]");
		if (rank != -1) {
			sender = sender.substring(rank + 1).trim();
		}

		sender = CookiesUtils.stripColor(sender);
		return sender;
	}


	@Getter
	public static class PartyCommand {
		private final Supplier<Boolean> predicate;
		protected BiConsumer<String, String> action;

		public PartyCommand(Supplier<Boolean> predicate, BiConsumer<String, String> action) {
			this.predicate = predicate;
			this.action = action;
		}
	}

	public static class DownTimeCommand extends PartyCommand {
		private final ArrayList<DownTimeAction> downTimesThisInstance = new ArrayList<>();

		public DownTimeCommand(Supplier<Boolean> predicate) {
			super(predicate, null);
			ChatMessageEvents.BEFORE_MODIFY.register((text, b) -> {
				if (b) {
					return;
				}
				var message = CookiesUtils.stripColor(text.getString());
				if (!message.matches(".*> EXTRA STATS <.*")) {
					return;
				}
				onInstanceOver();
			});
			this.action = this::runAction;
		}

		private void runAction(String command, String message) {
			var sender = getSender(message);

			if (downTimesThisInstance.stream().anyMatch(x -> x.requester.equals(sender))) {
				ChatUtils.sendPartyMessage(sender + " has already requested down time.");
				return;
			}

			var split = command.split(" ");

			switch (split.length) {
				case 1 -> downTimesThisInstance.add(new DownTimeAction("Not Specified", sender, null));
				case 2 -> downTimesThisInstance.add(new DownTimeAction(split[1], sender, null));
				case 3 -> downTimesThisInstance.add(new DownTimeAction(split[1], sender, split[2]));
			}

			ChatUtils.sendPartyMessage("Down time requested by " + sender);
		}

		private void onInstanceOver() {
			downTimesThisInstance.forEach(DownTimeAction::onInstanceOver);
			downTimesThisInstance.clear();
		}


		public static class DownTimeAction {
			private final String reason;
			private final String requester;
			private final String duration;

			public DownTimeAction(String reason, String requester, String duration) {
				this.reason = reason;
				this.requester = requester;
				this.duration = duration;
			}

			public void onInstanceOver() {
				StringBuilder message = new StringBuilder("Down time requested by " + requester);
				if (duration != null) {
					message.append(" for ").append(duration);
				}
				if (reason != null) {
					message.append(" because: ").append(reason);
				}

				ChatUtils.sendPartyMessage(message.toString());
			}
		}
	}
}
