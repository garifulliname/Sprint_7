import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiClient {
    private final RequestSpecification spec;

    public ApiClient(RequestSpecification spec) {
        if (spec == null) {
            throw new IllegalArgumentException("RequestSpecification cannot be null");
        }
        this.spec = spec;
    }

    public Response createCourier(Courier courier) {
        return given().spec(spec).body(courier).post("/api/v1/courier");
    }

    public Response deleteCourier(Integer courierId) {
        return given().spec(spec).delete("/api/v1/courier/" + courierId);
    }

    public Response loginCourier(LoginRequest loginRequest) {
        return given().spec(spec).body(loginRequest).post("/api/v1/courier/login");
    }

    public Response createOrder(Order order) {
        return given().spec(spec).body(order).post("/api/v1/orders");
    }

    public Response getOrdersList() {
        return given().spec(spec).get("/api/v1/orders");
    }

    public Response deleteOrderByTrack(Integer track) {
        return given().spec(spec).delete("/api/v1/orders/cancel?track=" + track);
    }
}
