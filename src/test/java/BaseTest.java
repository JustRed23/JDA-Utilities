import dev.JustRed23.jdautils.Builder;
import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.jdautils.component.Component;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        // Register a component
        builder.registerComponent(TestComponent.class);
        // You cannot register Component.class as it is an abstract class
        assertThrows(IllegalArgumentException.class, () -> builder.registerComponent(Component.class));
        // You cannot register the same component twice
        assertThrows(IllegalArgumentException.class, () -> builder.registerComponent(TestComponent.class));
        // Create a new component - this should fail as the listener was not initialized
        assertThrows(IllegalStateException.class, () -> JDAUtilities.createComponent("TestComponent"));

        JDA instance = createInstance()
                .addEventListeners(builder.listener())
                .build().awaitReady();

        // Register a component - this should fail as the registries are frozen
        assertThrows(IllegalStateException.class, () -> builder.registerComponent(Test2Component.class));

        // Create a new component
        Component component = JDAUtilities.createComponent("TestComponent");
        assertNotNull(component);
        LoggerFactory.getLogger(BaseTest.class).info("Component: " + component.getName() + " created!");
        LoggerFactory.getLogger(BaseTest.class).info("UUID: " + component.getUuid().toString());

        // Create a new component - this should fail as the component does not exist
        assertThrows(IllegalArgumentException.class, () -> JDAUtilities.createComponent("Test2Component"));

        Thread.sleep(5000);

        instance.shutdown();

        // Create a new component - this should fail as the builder was destroyed
        assertThrows(IllegalStateException.class, () -> JDAUtilities.createComponent("TestComponent"));
    }
}
