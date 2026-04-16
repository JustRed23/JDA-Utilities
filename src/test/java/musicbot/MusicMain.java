package musicbot;

import dev.JustRed23.jdautils.Builder;
import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.command.CommandOption;
import dev.JustRed23.jdautils.music.GuildMusicManager;
import dev.JustRed23.jdautils.music.RepeatMode;
import dev.JustRed23.jdautils.music.impl.lavalink.LavalinkMusicManager;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.NodeOptions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import testapp.Main;

import java.io.InputStream;
import java.util.Properties;

public class MusicMain {

    public static void main(String[] args) throws InterruptedException {
        addCommands();

        Properties secrets = null;
        try (InputStream secretsFile = Main.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            secrets = new Properties();
            secrets.load(secretsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LavalinkClient client = new LavalinkClient(Helpers.getUserIdFromToken(secrets.getProperty("token")));
        client.addNode(new NodeOptions.Builder()
                .setName("Serenetia")
                .setServerUri("https://lavalinkv4.serenetia.com:443")
                .setPassword("https://seretia.link/discord")
                .build()
        );
        client.addNode(new NodeOptions.Builder()
                .setName("Jirayu")
                .setServerUri("https://lavalink.jirayu.net:443")
                .setPassword("youshallnotpass")
                .build()
        );
        client.addNode(new NodeOptions.Builder()
                .setName("AneFaiz")
                .setServerUri("https://lava-v4.millohost.my.id:443")
                .setPassword("https://discord.gg/mjS5J2K3ep")
                .build()
        );

        final Builder.Configuration config = JDAUtilities.getInstance()
                .withMusicManager()
                    .useImplementation(new LavalinkMusicManager(client))
                    .build()
                .buildConfiguration();

        JDABuilder builder = config.configure(JDABuilder.createDefault(secrets.getProperty("token")))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .setActivity(Activity.playing("with the radio"))
                .setStatus(OnlineStatus.IDLE);
        JDA instance = builder.build();
        instance.awaitReady();
    }

    private static void addCommands() {
        JDAUtilities.createSlashCommand("music", "music commands")
                .addSubCommand("play", "plays music")
                .addOption(new CommandOption(OptionType.STRING, "url", "the url of the video", true))
                .executes(event -> {
                    event.deferReply().queue();
                    assert event.getMember() != null;
                    var state = event.getMember().getVoiceState();

                    if (state == null || state.getChannel() == null) {
                        event.getHook().sendMessage("You are not in a voice channel!").queue();
                        return;
                    }

                    gmm(event.getGuild()).play(event.getOption("url").getAsString(), state.getChannel());
                    event.getHook().sendMessage("Playing music!").queue();
                })
                .build()

                .addSubCommand("pause", "pauses the current track")
                .executes(event -> {
                    event.deferReply().queue();
                    gmm(event.getGuild()).pause();
                    event.getHook().sendMessage("Paused!").queue();
                })
                .build()

                .addSubCommand("resume", "resumes the current track")
                .executes(event -> {
                    event.deferReply().queue();
                    gmm(event.getGuild()).resume();
                    event.getHook().sendMessage("Resumed!").queue();
                })
                .build()

                .addSubCommand("stop", "stops music")
                .executes(event -> {
                    event.deferReply().queue();
                    gmm(event.getGuild()).stop();
                    event.getHook().sendMessage("Stopped music!").queue();
                })
                .build()

                .addSubCommand("skip", "skips the current track")
                .executes(event -> {
                    event.deferReply().queue();
                    boolean skipped = gmm(event.getGuild()).queue().skip();
                    event.getHook().sendMessage(skipped ? "Skipped!" : "Nothing to skip.").queue();
                })
                .build()

                .addSubCommand("back", "goes back to the previous track")
                .executes(event -> {
                    event.deferReply().queue();
                    boolean backed = gmm(event.getGuild()).queue().back();
                    event.getHook().sendMessage(backed ? "Went back!" : "Nothing to go back to.").queue();
                })
                .build()

                .addSubCommand("shuffle", "shuffles the queue")
                .executes(event -> {
                    event.deferReply().queue();
                    gmm(event.getGuild()).queue().shuffle();
                    event.getHook().sendMessage("Shuffled!").queue();
                })
                .build()

                .addSubCommand("clear", "clears the queue")
                .executes(event -> {
                    event.deferReply().queue();
                    gmm(event.getGuild()).queue().clear();
                    event.getHook().sendMessage("Cleared queue!").queue();
                })
                .build()

                .addSubCommand("volume", "sets or gets the volume")
                .addOption(new CommandOption(OptionType.INTEGER, "level", "volume level (0-100)", false))
                .executes(event -> {
                    event.deferReply().queue();
                    var gmm = gmm(event.getGuild());
                    var level = event.getOption("level");
                    if (level != null) {
                        int vol = level.getAsInt();
                        if (vol < 0 || vol > 100) {
                            event.getHook().sendMessage("Volume must be between 0 and 100!").queue();
                            return;
                        }
                        gmm.options().setVolume(vol);
                        event.getHook().sendMessage("Volume set to " + vol + "!").queue();
                    } else {
                        event.getHook().sendMessage("Current volume: " + gmm.options().getVolume()).queue();
                    }
                })
                .build()

                .addSubCommand("repeat", "sets the repeat mode")
                .addOption(new CommandOption(OptionType.STRING, "mode", "repeat mode (off, one, all)", true)
                    .addChoice("off", "OFF")
                    .addChoice("one", "ONE")
                    .addChoice("all", "ALL"))
                .executes(event -> {
                    event.deferReply().queue();
                    var modeStr = event.getOption("mode").getAsString();
                    var mode = RepeatMode.valueOf(modeStr);
                    gmm(event.getGuild()).options().setRepeatMode(mode);
                    event.getHook().sendMessage("Repeat mode set to " + mode + "!").queue();
                })
                .build()

                .addSubCommand("nowplaying", "shows the current track")
                .executes(event -> {
                    event.deferReply().queue();
                    var gmm = gmm(event.getGuild());
                    var track = gmm.getCurrentTrack();
                    if (track.isEmpty()) {
                        event.getHook().sendMessage("No track is currently playing.").queue();
                        return;
                    }
                    var t = track.get();
                    long pos = gmm.getTrackPosition();
                    String posStr = pos >= 0 ? formatTime(pos) + " / " + formatTime(t.durationMillis()) : "Unknown";
                    event.getHook().sendMessage("Now playing: " + t.title() + " by " + t.author() + " [" + posStr + "]").queue();
                })
                .build()

                .addSubCommand("queue", "shows the current queue")
                .executes(event -> {
                    event.deferReply().queue();
                    var queue = gmm(event.getGuild()).queue().getQueue();
                    if (queue.isEmpty()) {
                        event.getHook().sendMessage("Queue is empty.").queue();
                        return;
                    }
                    StringBuilder sb = new StringBuilder("Queue:\n");
                    for (int i = 0; i < Math.min(queue.size(), 10); i++) {
                        var t = queue.get(i);
                        sb.append(i + 1).append(". ").append(t.title()).append(" by ").append(t.author()).append("\n");
                    }
                    if (queue.size() > 10) {
                        sb.append("... and ").append(queue.size() - 10).append(" more");
                    }
                    event.getHook().sendMessage(sb.toString()).queue();
                })
                .build()

                .addSubCommand("seek", "seeks to a position in the current track")
                .addOption(new CommandOption(OptionType.STRING, "time", "time in format MM:SS or seconds", true))
                .executes(event -> {
                    event.deferReply().queue();
                    var timeStr = event.getOption("time").getAsString();
                    long millis = parseTime(timeStr);
                    if (millis < 0) {
                        event.getHook().sendMessage("Invalid time format! Use MM:SS or seconds.").queue();
                        return;
                    }
                    try {
                        gmm(event.getGuild()).seek(millis);
                        event.getHook().sendMessage("Seeked to " + formatTime(millis) + "!").queue();
                    } catch (Exception e) {
                        event.getHook().sendMessage("Seek failed: " + e.getMessage()).queue();
                    }
                })
                .build()

                .addSubCommand("dc", "disconnects the bot")
                .executes(event -> {
                    event.deferReply().queue();
                    gmm(event.getGuild()).disconnect();
                    event.getHook().sendMessage("Disconnected!").queue();
                })
                .build()

                .setGuildOnly()
                .buildAndRegister();
    }

    private static GuildMusicManager gmm(Guild guild) {
        return JDAUtilities.getGuildMusicManager(guild);
    }

    private static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private static long parseTime(String timeStr) {
        try {
            if (timeStr.contains(":")) {
                String[] parts = timeStr.split(":");
                if (parts.length == 2) {
                    int minutes = Integer.parseInt(parts[0]);
                    int seconds = Integer.parseInt(parts[1]);
                    return (minutes * 60 + seconds) * 1000L;
                }
            } else {
                return Long.parseLong(timeStr) * 1000L;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }
}
