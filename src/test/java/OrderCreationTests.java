import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;

import io.restassured.response.Response;
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
    private ApiClient apiClient;

    public OrderCreationTests(List<String> colors) {
        this.colors = colors;
    }

    @Before
    public void setUp() {
        apiClient = new ApiClient(spec);
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {new ArrayList<>()} // пустой список цветов
        });
    }

    @After
    public void tearDown() {
        for (Integer track : createdOrderTracks) {
            apiClient.deleteOrderByTrack(track)
                    .then()
                    .statusCode(oneOf(SC_OK, SC_NOT_FOUND));
        }
        createdOrderTracks.clear();
    }

    private Order generateOrderData(List<String> orderColors) {
        return TestData.generateUniqueOrder(orderColors);
    }

    private Response createOrderWithBody(Order order) {
        return apiClient.createOrder(order);
    }

    private void verifyOrderCreatedSuccessfully(Response response, Integer trackId) {
        response.then()
                .statusCode(SC_CREATED)
                .body("track", equalTo(trackId))
                .body("message", nullValue());
    }

    @Test
    @Story("Создание заказа с разными комбинациями цветов")
    @DisplayName("Создание заказа с различными вариантами указания цвета")
    @Description("Проверяем создание заказа с указанием BLACK, GREY, обоих цветов или без указания цвета")
    public void createOrderWithDifferentColors() {
        // Генерируем данные заказа с текущими цветами
        Order order = generateOrderData(colors);

        // Создаём заказ
        Response response = createOrderWithBody(order);

        // Извлекаем трек‑номер из ответа
        Integer trackId = response.then().extract().path("track");
        if (trackId != null) {
            createdOrderTracks.add(trackId);
            verifyOrderCreatedSuccessfully(response, trackId);
        } else {
            throw new RuntimeException("Track ID not returned in response");
        }
    }
}