import dev.JustRed23.jdautils.utils.ValueStore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;

class StoreTest {

    @Test
    void testEmpty() {
        System.out.println("testEmpty");
        ValueStore valueStore = ValueStore.loadOrCreate("valuestore-test");
        valueStore.print();
    }

    @Test
    void testAdd() {
        System.out.println("testAdd");
        ValueStore valueStore = ValueStore.loadOrCreate("valuestore-test-2");
        valueStore.put("test", "This is a test value!");
        valueStore.print();
    }

    @Test
    void testRetrieve() {
        System.out.println("testRetrieve");
        ValueStore valueStore = ValueStore.loadOrCreate("valuestore-test-2");
        System.out.println(valueStore.get("test"));
        valueStore.print();
    }

    @AfterAll
    static void cleanup() {
        new File("valuestore-test.yml").delete();
        new File("valuestore-test-2.yml").delete();
    }
}
