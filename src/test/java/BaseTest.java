import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import com.google.gson.Gson;

import org.junit.BeforeClass;

public class BaseTest {
    protected static RequestSpecification spec;
    protected Gson gson = new Gson();

    @BeforeClass
    public static void setUpBase() {
        RestAssured.baseURI = TestData.BASE_URI;
        spec = new RequestSpecBuilder()
                .setContentType("application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }
}