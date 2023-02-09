import dev.JustRed23.jdautils.Builder;
import dev.JustRed23.jdautils.JDAUtilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BaseTest {

    private static Properties secrets;

    @BeforeAll
    static void getProperties() {
        try (InputStream secretsFile = BaseTest.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            secrets = new Properties();
            secrets.load(secretsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInternalEventListener() {
        Builder builder = JDAUtilities.newInstance();
        ListenerAdapter build = builder.build();

        assertNotNull(build);

        JDA instance = JDABuilder.createDefault(secrets.getProperty("token"))
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(build)
                .build();

        assertNotNull(instance);
    }
}
