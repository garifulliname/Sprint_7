import java.util.List;

public class Order {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private Integer rentTime;
    private String deliveryDate;
    private String comment;
    private List<String> color;

    // Сеттеры
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAddress(String address) { this.address = address; }
    public void setMetroStation(String metroStation) { this.metroStation = metroStation; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRentTime(Integer rentTime) { this.rentTime = rentTime; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
    public void setComment(String comment) { this.comment = comment; }
    public void setColor(List<String> color) { this.color = color; }

    // Геттеры
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getMetroStation() { return metroStation; }
    public String getPhone() { return phone; }
    public Integer getRentTime() { return rentTime; }
    public String getDeliveryDate() { return deliveryDate; }
    public String getComment() { return comment; }
    public List<String> getColor() { return color; }
}