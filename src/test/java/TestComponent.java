import dev.JustRed23.jdautils.component.Component;

public class TestComponent extends Component {

    public TestComponent() {
        super("TestComponent");
    }

    protected void create() {
        System.out.println("COMPONENT CREATE ON " + getName());
    }

    protected void remove() {
        System.out.println("COMPONENT REMOVE ON " + getName());
    }
}
