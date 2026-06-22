import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestData {
    public static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";

    public static String generateUniqueLogin() {
        return "login_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateUniquePassword() {
        return "pass_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateUniqueFirstName() {
        return "name_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static Order generateUniqueOrder(List<String> colors) {
        Order order = new Order();
        order.setFirstName("TestFirstName_" + UUID.randomUUID().toString().substring(0, 6));
        order.setLastName("TestLastName");
        order.setAddress("TestAddress");
        order.setMetroStation("TestMetro");
        order.setPhone("+79991234567");
        order.setRentTime(1);
        order.setDeliveryDate("2025-12-31");
        order.setComment("Test comment");
        order.setColor(colors);
        return order;
    }
}