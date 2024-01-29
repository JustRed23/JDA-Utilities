package musicbot;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.command.CommandOption;
import dev.JustRed23.jdautils.music.TrackInfo;
import dev.JustRed23.jdautils.music.TrackLoadCallback;
import dev.JustRed23.jdautils.music.effect.AbstractEffect;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import testapp.Main;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class MusicMain {

    public static void main(String[] args) throws InterruptedException {
        Properties secrets = null;
        try (InputStream secretsFile = Main.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            secrets = new Properties();
            secrets.load(secretsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListenerAdapter listener = JDAUtilities.getInstance().listener();

        JDA instance = JDABuilder.createDefault(secrets.getProperty("token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(listener)
                .build().awaitReady();

        instance.updateCommands()
                .addCommands(
                        JDAUtilities.createSlashCommand("join", "Joins the voice channel")
                                .executes(event -> {
                                    JDAUtilities.getGuildAudioManager(event.getGuild()).join(event.getMember().getVoiceState().getChannel().asVoiceChannel());
                                    event.reply("Joined voice channel").queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("play", "Plays a song")
                                .addOption(new CommandOption(OptionType.STRING, "song", "The song to play, must be a url", true))
                                .executes(event -> {
                                    JDAUtilities.getGuildAudioManager(event.getGuild())
                                            .loadAndPlay(event.getOption("song").getAsString(), event.getMember(), new TrackLoadCallback() {
                                                public void onTrackLoaded(TrackInfo trackInfo, boolean addedToQueue, long durationMs) {
                                                    event.reply("Loaded track " + trackInfo.track().getInfo().title + " by " + trackInfo.track().getInfo().author).queue();
                                                }

                                                public void onPlaylistLoaded(AudioPlaylist playlist, List<TrackInfo> tracks, long totalDurationMs) {
                                                    event.reply("Loaded playlist " + playlist.getName() + " with " + tracks.size() + " tracks").queue();
                                                }

                                                public void onNoMatches() {
                                                    event.reply("No matches found").queue();
                                                }

                                                public void onTrackLoadError(Exception exception) {
                                                    event.reply("An error occurred while loading the track: " + exception.getMessage()).queue();
                                                }
                                            });
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("skip", "Skips the current song")
                                .executes(event -> {
                                    TrackInfo skip = JDAUtilities.getGuildAudioManager(event.getGuild()).getControls().skip();
                                    if (skip == null)
                                        event.reply("Skipped track, no more tracks in queue").queue();
                                    else
                                        event.reply("Skipped track, now playing " + skip.track().getInfo().title + " by " + skip.track().getInfo().author).queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("prev", "Goes back to the previous song")
                                .executes(event -> {
                                    TrackInfo prev = JDAUtilities.getGuildAudioManager(event.getGuild()).getControls().prev();
                                    if (prev == null)
                                        event.reply("Went back to previous track, no more tracks in queue").queue();
                                    else
                                        event.reply("Went back to previous track, now playing " + prev.track().getInfo().title + " by " + prev.track().getInfo().author).queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("pause", "Pauses the current song")
                                .executes(event -> {
                                    JDAUtilities.getGuildAudioManager(event.getGuild()).getControls().pause();
                                    event.reply("Paused track").queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("resume", "Resumes the current song")
                                .executes(event -> {
                                    JDAUtilities.getGuildAudioManager(event.getGuild()).getControls().resume();
                                    event.reply("Resumed track").queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("stop", "Stops the current song")
                                .executes(event -> {
                                    JDAUtilities.getGuildAudioManager(event.getGuild()).getControls().stop();
                                    event.reply("Stopped track").queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("effect", "Add an effect to the current song")
                                .addOption(new CommandOption(OptionType.STRING, "effect", "The effect to add", true)
                                        .addChoice("bassboost", "bassboost")
                                        .addChoice("nightcore", "nightcore")
                                        .addChoice("speed", "speed")
                                )
                                .addOption(new CommandOption(OptionType.NUMBER, "value", "The value of the effect", true))
                                .executes(event -> {
                                    String effect = event.getOption("effect").getAsString();
                                    float value = event.getOption("value") != null ? (float) event.getOption("value").getAsDouble() : 0F;

                                    if (value <= 0) {
                                        JDAUtilities.getGuildAudioManager(event.getGuild()).getAudioModifier().disableEffect();
                                        event.reply("Removed all effects").queue();
                                        return;
                                    }

                                    AbstractEffect theEffect = AbstractEffect.getEffect(effect);

                                    JDAUtilities.getGuildAudioManager(event.getGuild()).getAudioModifier().enableEffect(theEffect.setValue(value));
                                    event.reply("Added effect " + effect + " with value " + value).queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("removeeffect", "Remove an effect from the current song")
                                .executes(event -> {
                                    JDAUtilities.getGuildAudioManager(event.getGuild()).getAudioModifier().disableEffect();
                                    event.reply("Removed all effects").queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("disconnect", "Disconnects the bot from the voice channel")
                                .executes(event -> {
                                    JDAUtilities.getGuildAudioManager(event.getGuild()).disconnect();
                                    event.reply("Disconnected from voice channel").queue();
                                })
                                .build(),
                        JDAUtilities.createSlashCommand("queue", "Shows the current song queue")
                                .executes(event -> {
                                    EmbedBuilder builder = new EmbedBuilder();
                                    builder.setTitle("Queue");
                                    JDAUtilities.getGuildAudioManager(event.getGuild()).getScheduler().getQueue().forEach(track -> {
                                        builder.addField("", track.getInfo().title + " by " + track.getInfo().author, false);
                                    });
                                    event.replyEmbeds(builder.build()).queue();
                                })
                                .build()
                )
                .queue();
    }
}
