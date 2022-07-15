import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FailedControllerTest {

    @Test
    public void controllerTestShouldBeFailed(){
        Assertions.assertTrue(false);
    }
}
