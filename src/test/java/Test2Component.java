import dev.JustRed23.jdautils.component.Component;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

public class Test2Component extends Component {

    public Test2Component() {
        super("Test2Component");
    }

    @Override
    protected void onCreate() {}

    @Override
    protected void onRemove() {}

    public MessageCreateAction onSend(@NotNull MessageReceivedEvent event) {
        return null;
    }

    public ReplyCallbackAction onReply(@NotNull SlashCommandInteractionEvent event) {
        return null;
    }
}
