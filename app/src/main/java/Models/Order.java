package Models;

/**
 * Created by osama on 10/13/2017.
 */

public class Order {
    private String productId,productName,discount,price,quality;

    public Order() {
    }

    public Order(String productId, String productName, String discount, String price, String quality) {
        this.productId = productId;
        this.productName = productName;
        this.discount = discount;
        this.price = price;
        this.quality = quality;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
