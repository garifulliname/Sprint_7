import com.google.gson.Gson;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

@Feature("Создание заказов с разными параметрами")
@RunWith(Parameterized.class)
public class OrderCreationTests extends BaseTest {

    private final List<String> colors;
    private List<Integer> createdOrderTracks = new ArrayList<>();

    public OrderCreationTests(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Arrays.asList("BLACK") },
                { Arrays.asList("GREY") },
                { Arrays.asList("BLACK", "GREY") },
                { new ArrayList<>() } // пустой список цветов
        });
    }

    @After
    public void tearDown() {
        for (Integer track : createdOrderTracks) {
            given().spec(spec)
                    .delete("/api/v1/orders/cancel?track=" + track)
                    .then()
                    .statusCode(oneOf(200, 404)); // Ожидаем только успешный статус 200
        }
        createdOrderTracks.clear();
    }

    @Step("Создаём заказ с цветами: {colors}")
    private Response createOrderWithBody(String body) {
        return given().spec(spec).body(body).post("/api/v1/orders");
    }

    @Step("Генерируем данные для заказа с цветами: {colors}")
    private Order generateOrderData(List<String> orderColors) {
        return TestData.generateUniqueOrder(orderColors);
    }

    @Step("Проверяем, что заказ успешно создан. Трек-номер: {trackId}")
    private void verifyOrderCreatedSuccessfully(Response response, Integer trackId) {
        response.then()
                .statusCode(201)
                .body("track", equalTo(trackId))
                .body("message", nullValue());
    }

    @Test
    @Story("Создание заказа с разными комбинациями цветов")
    public void createOrderWithDifferentColors() {
        // Генерируем данные заказа с текущими цветами
        Order order = generateOrderData(colors);
        String jsonBody = gson.toJson(order);

        // Создаём заказ
        Response response = createOrderWithBody(jsonBody);

        // Извлекаем трек‑номер из ответа
        Integer trackId = response.then().extract().path("track");

        // Сохраняем трек‑номер для последующей очистки
        createdOrderTracks.add(trackId);

        // Проверяем успешность создания заказа
        verifyOrderCreatedSuccessfully(response, trackId);
    }
}