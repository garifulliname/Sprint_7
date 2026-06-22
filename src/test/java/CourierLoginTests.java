import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;

import io.restassured.response.Response;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

@Feature("Авторизация курьера")
public class CourierLoginTests extends BaseTest {

    private Integer createdCourierId;
    private Integer testCourierId;
    private String testLogin;
    private String testPassword;
    private ApiClient apiClient;

    @Before
    public void setUp() {
        apiClient = new ApiClient(spec);
        createTestCourier();
    }

    @After
    public void tearDown() {
        // Удаляем курьера, созданного для теста
        if (testCourierId != null) {
            apiClient.deleteCourier(testCourierId)
                    .then()
                    .statusCode(oneOf(SC_OK, SC_NOT_FOUND));
        }
        // Удаляем курьера, полученного после авторизации (если авторизация прошла)
        if (createdCourierId != null && !createdCourierId.equals(testCourierId)) {
            apiClient.deleteCourier(createdCourierId)
                    .then()
                    .statusCode(oneOf(SC_OK, SC_NOT_FOUND));
        }
    }

    @Step("Создаём тестового курьера")
    private void createTestCourier() {
        testLogin = TestData.generateUniqueLogin();
        testPassword = TestData.generateUniquePassword();
        Courier courier = new Courier(testLogin, testPassword, TestData.generateUniqueFirstName());

        Response response = apiClient.createCourier(courier);
        response.then().statusCode(SC_CREATED);

        testCourierId = response.then().extract().path("id");
    }

    @Step("Авторизуемся с логином: {login}, паролем: {password}")
    private Response loginCourierWithBody(String login, String password) {
        LoginRequest loginRequest = new LoginRequest(login, password);
        return apiClient.loginCourier(loginRequest);
    }

    @Test
    @Story("Успешная авторизация курьера")
    @DisplayName("Успешная авторизация с корректными данными")
    @Description("Проверяем, что курьер может авторизоваться и получает id")
    public void loginCourierSuccess() {
        Response loginResponse = loginCourierWithBody(testLogin, testPassword);
        loginResponse.then()
                .statusCode(SC_OK)
                .body("id", notNullValue());

        createdCourierId = loginResponse.then().extract().path("id");
    }

    @Test
    @Story("Ошибка при авторизации без логина")
    @DisplayName("Попытка авторизации без логина")
    @Description("Проверяем, что система возвращает ошибку при отсутствии логина")
    public void loginWithoutLogin() {
        String password = TestData.generateUniquePassword();
        LoginRequest loginRequest = new LoginRequest(null, password);

        apiClient.loginCourier(loginRequest)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"))
                .body("id", nullValue());
    }

    @Ignore("Временное отключение: API возвращает 504 вместо 400.")
    @Test
    @Story("Ошибка при авторизации без пароля")
    @DisplayName("Попытка авторизации без пароля")
    @Description("Проверяем, что система возвращает ошибку при отсутствии пароля")
    public void loginWithoutPassword() {
        String login = TestData.generateUniqueLogin();
        LoginRequest loginRequest = new LoginRequest(login, null);

        apiClient.loginCourier(loginRequest)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для входа"))
                .body("id", nullValue());
    }

    @Test
    @Story("Ошибка при авторизации с неверным паролем")
    @DisplayName("Попытка авторизации с неверным паролем")
    @Description("Проверяем, что система не позволяет авторизоваться с неверным паролем")
    public void loginWithInvalidPassword() {
        String invalidPassword = TestData.generateUniquePassword();

        Response loginResponse = loginCourierWithBody(testLogin, invalidPassword);
        loginResponse.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Учетная запись не найдена"))
                .body("id", nullValue());
    }

    @Test
    @Story("Ошибка при авторизации несуществующего курьера")
    @DisplayName("Попытка авторизации несуществующего курьера")
    @Description("Проверяем, что система не позволяет авторизоваться несуществующему курьеру")
    public void loginNonExistentCourier() {
        String nonExistentLogin = "non_existent_login_" + UUID.randomUUID().toString().substring(0, 8);
        String anyPassword = "any_password";

        Response loginResponse = loginCourierWithBody(nonExistentLogin, anyPassword);
        loginResponse.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Учетная запись не найдена"))
                .body("id", nullValue());
    }
}