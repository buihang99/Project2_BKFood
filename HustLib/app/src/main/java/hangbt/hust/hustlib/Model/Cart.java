package hangbt.hust.hustlib.Model;

import java.util.List;

public class Cart {
    private String cardId;
    private List<Food_Cart> foodList;
    private long total;

    public Cart() {
    }

    public Cart(String cardId, List<Food_Cart> foodList, long total) {
        this.cardId = cardId;
        this.foodList = foodList;
        this.total = total;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public List<Food_Cart> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Food_Cart> foodList) {
        this.foodList = foodList;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
