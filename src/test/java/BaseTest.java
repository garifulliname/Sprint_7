import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;

public abstract class BaseTest {
    protected static RequestSpecification spec;

    @BeforeClass
    public static void setUpBase() {
        spec = new RequestSpecBuilder()
                .setBaseUri(TestData.BASE_URI)
                .setContentType("application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }
}