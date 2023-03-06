package dev.JustRed23.jdautils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import dev.JustRed23.jdautils.settings.ConfigReturnValue;
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

    /**
     * Sets the volume of the player, can be between 0 and 100
     * @param volume The volume to set
     * @return The result of the operation, can be SUCCESS, ERROR or INVALID_VALUE
     */
    public ConfigReturnValue setVolume(int volume) {
        if (volume <= 0 || volume > 100)
            return ConfigReturnValue.INVALID_VALUE;

        player.setVolume(volume);

        if (JDAUtilities.getGuildSettingManager() != null)
            return JDAUtilities.getGuildSettingManager().set(guildId, "audioplayer-volume", volume);
        else
            return ConfigReturnValue.SUCCESS;
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
    public @Nullable AbstractEffect enableEffect(@NotNull AbstractEffect effect) {
        AbstractEffect previousEffect = getEnabledEffect();
        disableEffect();

        this.effect = effect;
        effect.enable(player);

        return previousEffect;
    }

    /**
     * Disables the current effect
     */
    public void disableEffect() {
        if (effect != null) {
            effect.disable(player);
            effect = null;
        }
    }

    public @Nullable AbstractEffect getEnabledEffect() {
        return effect;
    }
}
