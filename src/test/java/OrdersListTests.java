import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;

import io.restassured.response.Response;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Feature("Список заказов")
public class OrdersListTests extends BaseTest {
    private ApiClient apiClient;

    @Before
    public void setUp() {
        apiClient = new ApiClient(spec);
    }

    @Step("Получаем список заказов")
    private Response getOrdersList() {
        return apiClient.getOrdersList();
    }

    @Test
    @Story("Проверка, что возвращается список заказов")
    @DisplayName("Получение списка заказов")
    @Description("Проверяем, что в тело ответа возвращается список заказов")
    public void checkOrdersListIsReturned() {
        Response listResponse = getOrdersList();
        listResponse.then()
                .statusCode(SC_OK)
                .body("orders", notNullValue());


        List<?> orders = listResponse.jsonPath().getList("orders");
        assertNotNull("Ответ должен содержать поле 'orders'", orders);
        assertTrue("Поле 'orders' должно быть списком (массивом)", orders instanceof List);
        for (Object order : orders) {
            assertNotNull("Элементы списка заказов не должны быть null", order);
        }
    }
}
