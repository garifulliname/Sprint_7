import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.Test;

import io.restassured.response.Response;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Feature("Список заказов")
public class OrdersListTests extends BaseTest {

    @Test
    @Story("Проверка, что возвращается список заказов")
    public void checkOrdersListIsReturned() {
        // Получаем список заказов
        Response listResponse = given().spec(spec)
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .extract().response();

        // Извлекаем поле "orders" из JSON‑ответа как список
        List<?> orders = listResponse.jsonPath().getList("orders");

        // Проверяем, что поле "orders" существует
        assertNotNull("Ответ должен содержать поле 'orders'", orders);

        // Проверяем, что orders — это список (может быть пустым)
        assertTrue("Поле 'orders' должно быть списком (массивом)", orders instanceof List);

        // Дополнительно: проверяем, что все элементы списка — объекты (не null)
        for (Object order : orders) {
            assertNotNull("Элементы списка заказов не должны быть null", order);
        }
    }
}