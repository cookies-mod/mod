package codes.cookies.mod.data;

import codes.cookies.mod.utils.IntReference;

/**
 * Speed for specific crops.
 */
public record RancherSpeeds(
    IntReference wheat,
    IntReference carrot,
    IntReference potato,
    IntReference netherWart,
    IntReference pumpkin,
    IntReference melon,
    IntReference cocoaBeans,
    IntReference sugarCane,
    IntReference cactus,
    IntReference mushroom
) {

    public void loadFrom(RancherSpeeds speeds) {
        wheat.set(speeds.wheat.get());
        carrot.set(speeds.carrot.get());
        potato.set(speeds.potato.get());
        netherWart.set(speeds.netherWart.get());
        pumpkin.set(speeds.pumpkin.get());
        melon.set(speeds.melon.get());
        cocoaBeans.set(speeds.cocoaBeans.get());
        sugarCane.set(speeds.sugarCane.get());
        cactus.set(speeds.cactus.get());
        mushroom.set(speeds.mushroom.get());
    }

}
