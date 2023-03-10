import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.component.SendableComponent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestComponent extends SendableComponent {

    public TestComponent() {
        super("TestComponent");
    }

    protected void onCreate() {
        System.out.println("COMPONENT CREATE ON " + getName());
    }

    protected void onRemove() {
        System.out.println("COMPONENT REMOVE ON " + getName());
    }

    public MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        return null;
    }

    public ReplyCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        return null;
    }

    protected List<Component> getChildren() {
        return null;
    }
}
