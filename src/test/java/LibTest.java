import com.github.natanbc.lavadsp.natives.TimescaleNativeLibLoader;
import org.junit.jupiter.api.Test;

class LibTest {

    @Test
    void testlib() {
        TimescaleNativeLibLoader.loadTimescaleLibrary();
    }
}
