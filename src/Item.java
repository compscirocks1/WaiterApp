import javax.swing.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Item implements Serializable {
    private String name;
    private String price;
    private JLabel imageIcon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceDouble() {
        return Double.parseDouble(price);
    }

    public String getPriceString() {
        return price;
    }



    public void setPrice(String price) {
        this.price = price;
    }

    public JLabel getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(JLabel imageIcon) {
        this.imageIcon = imageIcon;
    }

    public Item(String name, String price) {
        this.name = name;
        this.price = price;
        this.imageIcon = null;
    }
}
