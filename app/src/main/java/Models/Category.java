package Models;

/**
 * Created by osama on 10/2/2017.
 */

public class Category {
    private String Name,Image;

    public Category() {
    }

    public Category(String Name,String Image) {
        this.Name = Name;
        this.Image = Image;
    }

    public String getImage() {
        return this.Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
}
