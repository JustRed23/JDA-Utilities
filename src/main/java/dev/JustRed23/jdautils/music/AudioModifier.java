package dev.JustRed23.jdautils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AudioModifier {

    private final long guildId;
    private final AudioPlayer player;
    private AbstractEffect effect;

    AudioModifier(@NotNull AudioPlayer audioPlayer, @NotNull Guild guild) {
        this.player = audioPlayer;
        this.guildId = guild.getIdLong();
        setVolume(getGuildDefaultVolume());
    }

    public void setVolume(int volume) {
        if (JDAUtilities.getGuildSettingManager() != null)
            JDAUtilities.getGuildSettingManager().set(guildId, "audioplayer-volume", volume);

        player.setVolume(volume);
    }

    public int getVolume() {
        return player.getVolume();
    }

    /**
     * Gets the default volume for the guild or 100 if no custom volume is set
     * @return The default volume for the guild
     */
    public int getGuildDefaultVolume() {
        return JDAUtilities.getGuildSettingManager() != null ?
                JDAUtilities.getGuildSettingManager().getOrDefault(guildId, "audioplayer-volume", 100).intValue()
                : 100;
    }

    /**
     * Enables an effect and disables the previous one
     * @param effect The effect to enable
     * @return The previous effect or null if there was no previous effect
     */
    public @Nullable AbstractEffect enableEffect(AbstractEffect effect) {
        AbstractEffect previousEffect = getEnabledEffect();

        if (previousEffect != null)
            previousEffect.disable();

        this.effect = effect;
        effect.enable();

        return previousEffect;
    }

    /**
     * Disables the current effect
     */
    public void disableEffect() {
        if (effect != null) {
            effect.disable();
            effect = null;
        }
    }

    public @Nullable AbstractEffect getEnabledEffect() {
        return effect;
    }
}
