package codes.cookies.mod.utils.minecraft;

import codes.cookies.mod.utils.cookies.CookiesUtils;
import net.minecraft.sound.SoundEvent;

/**
 * Various utility methods related to sound.
 */
public class SoundUtils {

    /**
     * Plays a sound at the players' location.
     *
     * @param soundEvent The sound to play.
     */
    public static void playSound(final SoundEvent soundEvent) {
        playSound(soundEvent, 1);
    }

    /**
     * Plays a sound at the players location with the specified pitch.
     *
     * @param soundEvent The sound to play.
     * @param pitch      The pitch.
     */
    public static void playSound(final SoundEvent soundEvent,
                                 final float pitch) {
        playSound(soundEvent, pitch, 1);
    }

    /**
     * Plays a sound at the players location with the specified pitch and volume.
     *
     * @param soundEvent The sound to play.
     * @param pitch      The pitch.
     * @param volume     The volume.
     */
    public static void playSound(final SoundEvent soundEvent,
                                 final float pitch,
                                 final float volume) {
        CookiesUtils.getPlayer().ifPresent(player -> player.playSound(soundEvent, volume, pitch));
    }

}
