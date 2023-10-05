package dev.JustRed23.jdautils.component.interact;

import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.NoRegistry;
import dev.JustRed23.jdautils.event.EventWatcher;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SmartButton extends Component implements NoRegistry {

    private Button parent;
    private final EventWatcher<ButtonInteractionEvent> eventWatcher;

    private final String label;
    private final Emoji emoji;
    private final ButtonStyle style;
    private final String url;

    private SmartButton(String label, Emoji emoji, ButtonStyle style, String url) {
        super("SmartButton");
        this.label = label;
        this.emoji = emoji;
        this.style = style;
        this.url = url;

        eventWatcher = new EventWatcher<>(this, ButtonInteractionEvent.class);
    }

    public static SmartButton primary(@NotNull String label) {
        return new ButtonBuilder(label, ButtonStyle.PRIMARY).build();
    }

    public static SmartButton primary(@NotNull Emoji emoji) {
        return new ButtonBuilder(emoji, ButtonStyle.PRIMARY).build();
    }

    public static SmartButton secondary(@NotNull String label) {
        return new ButtonBuilder(label, ButtonStyle.SECONDARY).build();
    }

    public static SmartButton secondary(@NotNull Emoji emoji) {
        return new ButtonBuilder(emoji, ButtonStyle.SECONDARY).build();
    }

    public static SmartButton success(@NotNull String label) {
        return new ButtonBuilder(label, ButtonStyle.SUCCESS).build();
    }

    public static SmartButton success(@NotNull Emoji emoji) {
        return new ButtonBuilder(emoji, ButtonStyle.SUCCESS).build();
    }

    public static SmartButton danger(@NotNull String label) {
        return new ButtonBuilder(label, ButtonStyle.DANGER).build();
    }

    public static SmartButton danger(@NotNull Emoji emoji) {
        return new ButtonBuilder(emoji, ButtonStyle.DANGER).build();
    }

    public static SmartButton link(@NotNull String url, @NotNull String label) {
        return new ButtonBuilder(label, ButtonStyle.LINK).setUrl(url).build();
    }

    public static SmartButton link(@NotNull String url, @NotNull Emoji emoji) {
        return new ButtonBuilder(emoji, ButtonStyle.LINK).setUrl(url).build();
    }

    public SmartButton withListener(EventWatcher.Listener<ButtonInteractionEvent> listener) {
        eventWatcher.setListener(listener);
        return this;
    }

    public SmartButton withListener(EventWatcher.Listener<ButtonInteractionEvent> listener, int expireAfter, TimeUnit unit) {
        eventWatcher.setListener(listener, expireAfter, unit);
        return this;
    }

    public Button build() {
        return parent;
    }

    protected void onCreate() {
        parent = Button.of(style, style == ButtonStyle.LINK ? url : uuid.toString(), label, emoji);
    }

    protected void onRemove() {
        eventWatcher.destroy();
        parent = null;
    }

    static class ButtonBuilder {

        private final String label;
        private final Emoji emoji;
        private final ButtonStyle style;
        private String url;

        private ButtonBuilder(String label, ButtonStyle style) {
            this.label = label;
            this.emoji = null;
            this.style = style;
        }

        private ButtonBuilder(Emoji emoji, ButtonStyle style) {
            this.label = null;
            this.emoji = emoji;
            this.style = style;
        }

        public ButtonBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public SmartButton build() {
            SmartButton button = new SmartButton(label, emoji, style, url);
            button.create();
            return button;
        }
    }
}
