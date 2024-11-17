package codes.cookies.mod.features.mining.shafts;

import codes.cookies.mod.config.categories.mining.shaft.ShaftConfig;
import codes.cookies.mod.events.PlayerListWidgetEvent;
import codes.cookies.mod.events.mining.MineshaftEvents;
import codes.cookies.mod.repository.constants.mining.ShaftCorpseLocations;
import codes.cookies.mod.utils.cookies.CookiesUtils;
import codes.cookies.mod.utils.skyblock.PartyUtils;
import codes.cookies.mod.utils.skyblock.tab.widgets.PlayerListWidgets;
import codes.cookies.mod.utils.skyblock.tab.widgets.corpse.CorpseEntry;
import codes.cookies.mod.utils.skyblock.tab.widgets.corpse.CorpseType;
import codes.cookies.mod.utils.skyblock.tab.widgets.corpse.FrozenCorpseWidget;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sends a message upon joining a mineshaft, that informs about type and corpses in the shaft.
 */
public class MineshaftAnnouncements {

    private static boolean hasSendMessage = false;
    private static ShaftCorpseLocations.ShaftLocations shaftType;
    private static long lastShaftFoundAt = -1;

    public static void register() {
        MineshaftEvents.JOIN_SHAFT.register(MineshaftAnnouncements::join);
        MineshaftEvents.LEAVE.register(MineshaftAnnouncements::leave);
        MineshaftEvents.FIND.register(MineshaftAnnouncements::find);
        PlayerListWidgetEvent.register(PlayerListWidgets.CORPSE, MineshaftAnnouncements::corpseWidgetUpdate);
    }

    private static void find() {
        lastShaftFoundAt = System.currentTimeMillis();
        PartyUtils.request();
    }

    private static void join(ShaftCorpseLocations.ShaftLocations shaftLocations) {
        if (lastShaftFoundAt + 60 * 1000 < System.currentTimeMillis()) {
            return;
        }
        shaftType = shaftLocations;
        hasSendMessage = false;
    }

    private static void corpseWidgetUpdate(FrozenCorpseWidget widget) {
        if (hasSendMessage || shaftType == null) {
            return;
        }

        final Map<CorpseType, List<CorpseEntry>> collect = widget.getCorpses()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.corpseType().ordinal()))
                .collect(Collectors.groupingBy(CorpseEntry::corpseType));

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Mineshaft entered | ");
        stringBuilder.append(shaftType.id()).append(" | ");

		final List<CorpseEntry> unknown = collect.remove(CorpseType.UNKNOWN);

		if (collect.containsKey(CorpseType.VANGUARD)) {
            stringBuilder.append("vang ");
        } else {
            collect.forEach((type, list) -> stringBuilder.append(list.size())
                    .append(type.name().charAt(0))
                    .append(" "));
        }

		if (unknown != null) {
			stringBuilder.append("| ").append(unknown.size()).append(" Unknown");
		}

        switch (ShaftConfig.getConfig().announcementType.getValue()) {
            case CHAT -> CookiesUtils.sendWhiteMessage(stringBuilder.toString());
            case PARTY -> {
                if (PartyUtils.isInParty()) {
                    CookiesUtils.sendInformation("Sending message into party chat!");
                    CookiesUtils.sendCommand("pc " + stringBuilder);
                } else {
                    CookiesUtils.sendFailedMessage("Not in a party!");
                    CookiesUtils.sendWhiteMessage(stringBuilder.toString());
                }
            }
        }
        hasSendMessage = true;
    }

    private static void leave() {
        hasSendMessage = true;
        shaftType = null;
    }

}
