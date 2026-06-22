import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

@Feature("Создание курьера")
public class CourierCreationTests extends BaseTest {

    private List<Integer> couriersToDelete = new ArrayList<>();
    private Integer createdCourierId;
    private ApiClient apiClient;

    @Before
    public void setUp() {
        apiClient = new ApiClient(spec);
    }

    @After
    public void tearDown() {
        for (Integer courierId : couriersToDelete) {
            if (courierId != null) {
                apiClient.deleteCourier(courierId)
                        .then()
                        .statusCode(oneOf(SC_OK, SC_NOT_FOUND));
            }
        }
        couriersToDelete.clear();
    }

    @Step("Создаём курьера и извлекаем ID")
    private Integer createAndExtractCourierId(Courier courier) {
        Response response = apiClient.createCourier(courier);
        response.then()
                .statusCode(SC_CREATED)
                .body("ok", is(true));
        Integer courierId = response.then().extract().path("id");
        couriersToDelete.add(courierId);
        return courierId;
    }

    @Test
    @Story("Успешное создание курьера")
    @DisplayName("Создание курьера с корректными данными")
    @Description("Проверяем, что курьер успешно создаётся и возвращается ok: true")
    public void createCourierSuccess() {
        String login = TestData.generateUniqueLogin();
        Courier courier = new Courier(login, TestData.generateUniquePassword(), TestData.generateUniqueFirstName());
        createdCourierId = createAndExtractCourierId(courier);
    }

    @Test
    @Story("Ошибка при создании курьера без логина")
    @DisplayName("Попытка создания курьера без логина")
    @Description("Проверяем, что система возвращает ошибку при отсутствии логина")
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier(null, TestData.generateUniquePassword(), TestData.generateUniqueFirstName());

        apiClient.createCourier(courier)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Ошибка при создании курьера без пароля")
    @DisplayName("Попытка создания курьера без пароля")
    @Description("Проверяем, что система возвращает ошибку при отсутствии пароля")
    public void cannotCreateCourierWithoutPassword() {
        Courier courier = new Courier(TestData.generateUniqueLogin(), null, TestData.generateUniqueFirstName());


        apiClient.createCourier(courier)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Ошибка при попытке создать курьера с существующим логином")
    @DisplayName("Попытка создания курьера с дублирующимся логином")
    @Description("Проверяем, что система не позволяет создать курьера с уже существующим логином")
    public void cannotCreateCourierWithDuplicateLogin() {
        // Создаём первого курьера
        String existingLogin = TestData.generateUniqueLogin();
        String password = TestData.generateUniquePassword();
        String firstName = TestData.generateUniqueFirstName();

        Courier firstCourier = new Courier(existingLogin, password, firstName);
        createAndExtractCourierId(firstCourier);

        // Пытаемся создать второго курьера с тем же логином
        Courier secondCourier = new Courier(existingLogin, TestData.generateUniquePassword(), TestData.generateUniqueFirstName());

        apiClient.createCourier(secondCourier)
                .then()
                .statusCode(SC_CONFLICT)
                .body("message", containsString("Этот логин уже используется"));
    }
}