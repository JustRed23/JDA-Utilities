import dev.JustRed23.jdautils.Builder;
import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.component.Component;
import dev.JustRed23.jdautils.data.DataStore;
import dev.JustRed23.jdautils.data.InteractionResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

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

    JDABuilder createInstance() {
        return JDABuilder.createDefault(secrets.getProperty("token"))
                .setActivity(Activity.playing("with cats"))
                .setStatus(OnlineStatus.IDLE);
    }

    static class TestListener extends ListenerAdapter {
        TestListener() {}

        public void onReady(@NotNull ReadyEvent ignored) {
            LoggerFactory.getLogger(TestListener.class).info("Test listener is working!");
        }
    }

    @Test
    void testInternalEventListener() {
        Builder builder = JDAUtilities.getInstance();
        ListenerAdapter build = builder.listener();

        assertNotNull(build);

        JDA instance = createInstance()
                .addEventListeners(build, new TestListener())
                .build();

        assertNotNull(instance);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        instance.shutdown();
    }

    @Test
    void testComponents() throws InterruptedException {
        // Create a new builder
        Builder builder = JDAUtilities.getInstance();

        // Create a new component - this should fail as the listener was not initialized
        assertThrows(IllegalStateException.class, () -> JDAUtilities.createComponent(TestComponent.class));

        JDA instance = createInstance()
                .addEventListeners(builder.listener())
                .build().awaitReady();

        // Create a new component
        Component component = JDAUtilities.createComponent(TestComponent.class);
        assertNotNull(component);
        LoggerFactory.getLogger(BaseTest.class).info("Component: " + component.getName() + " created!");
        LoggerFactory.getLogger(BaseTest.class).info("UUID: " + component.getUuid().toString());

        Thread.sleep(5000);

        instance.shutdown();

        // Create a new component - this should fail as the builder was destroyed
        assertThrows(IllegalStateException.class, () -> JDAUtilities.createComponent(TestComponent.class));
    }

    @Test
    void testDB() throws InterruptedException {
        final Builder builder = JDAUtilities.getInstance().withDatabase().fileBased("testdb.db");

        JDA instance = createInstance()
                .addEventListeners(builder.listener())
                .build().awaitReady();

        assertNotNull(instance);

        DataStore.GUILD.createTable(123456789L);

        System.out.println("Has testing value? " + DataStore.GUILD.has(123456789L, "testing"));
        System.out.println("Has testing2 value? " + DataStore.GUILD.has(123456789L, "testing2"));

        final String s = DataStore.GUILD.get(123456789L, "testing").orElse("NO VALUE");
        System.out.println(s);

        final InteractionResult insert = DataStore.GUILD.insert(123456789L, "testing", "test value");
        System.out.println(insert.name());

        if (insert == InteractionResult.ERROR)
            fail("Failed to insert value into database", insert.getError());

        Thread.sleep(5000);

        instance.shutdown();
    }

    @AfterEach
    void deleteDB() {
        new File("JDAU-guild_settings.db").delete();
    }
}
