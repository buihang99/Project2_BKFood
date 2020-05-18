package hangbt.hust.bkfoodserver.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Order {
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();

    private String userId, name, address, phone, orderId, total;
    private String time = dateFormat.format(date);
    private List<Food_Cart> listFood;
    private String status;

    public Order() {
    }

    public Order(String userId, String name, String address, String phone, String total, List<Food_Cart> listFood, String status) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.total = total;
        this.listFood = listFood;
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Food_Cart> getListFood() {
        return listFood;
    }

    public void setListFood(List<Food_Cart> listFood) {
        this.listFood = listFood;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
