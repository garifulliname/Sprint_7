import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.After;
import org.junit.Test;

import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Feature("Создание курьера")
public class CourierCreationTests extends BaseTest {

    private Integer createdCourierId;

    @After
    public void tearDown() {
        if (createdCourierId != null) {
            given().spec(spec)
                    .delete("/api/v1/courier/" + createdCourierId)
                    .then()
                    .statusCode(200);
        }
    }

    @Step("Создаём курьера с логином: {login}")
    private Response createCourierWithBody(String body) {
        return given().spec(spec).body(body).post("/api/v1/courier");
    }

    @Test
    @Story("Успешное создание курьера")
    public void createCourierSuccess() {
        String login = TestData.generateUniqueLogin();
        Courier courier = new Courier(login, TestData.generateUniquePassword(), TestData.generateUniqueFirstName());
        String jsonBody = gson.toJson(courier);

        Response response = createCourierWithBody(jsonBody);

        response.then()
                .statusCode(201)
                .body("ok", is(true));

        createdCourierId = response.then().extract().path("id");
    }

    @Test
    @Story("Ошибка при создании курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier(null, TestData.generateUniquePassword(), TestData.generateUniqueFirstName());
        String jsonBody = gson.toJson(courier);

        given()
                .spec(spec)
                .body(jsonBody)
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Ошибка при создании курьера без пароля")
    public void cannotCreateCourierWithoutPassword() {
        Courier courier = new Courier(TestData.generateUniqueLogin(), null, TestData.generateUniqueFirstName());
        String jsonBody = gson.toJson(courier);

        given()
                .spec(spec)
                .body(jsonBody)
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Ошибка при попытке создать курьера с существующим логином")
    public void cannotCreateCourierWithDuplicateLogin() {
        // Создаём первого курьера
        String duplicateLogin = TestData.generateUniqueLogin();
        String password = TestData.generateUniquePassword();
        String firstName = TestData.generateUniqueFirstName();

        Courier firstCourier = new Courier(duplicateLogin, password, firstName);
        String firstJsonBody = gson.toJson(firstCourier);
        Response firstResponse = createCourierWithBody(firstJsonBody);
        firstResponse.then().statusCode(201);
        Integer firstCourierId = firstResponse.then().extract().path("id");

        // Пытаемся создать второго курьера с тем же логином
        Courier secondCourier = new Courier(duplicateLogin, TestData.generateUniquePassword(), TestData.generateUniqueFirstName());
        String secondJsonBody = gson.toJson(secondCourier);

        given()
                .spec(spec)
                .body(secondJsonBody)
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));

        // Удаляем первого курьера после теста
        if (firstCourierId != null) {
            given().spec(spec)
                    .delete("/api/v1/courier/" + firstCourierId)
                    .then()
                    .statusCode(200);
        }
    }
}