package kassandrafalsitta.e_commerce_back.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;
    private String img;
    private String title;
    private String subtitle;
    private double price;
    private int quantity;
    private int stock;

    public Product(String title, String subtitle, double price, int stock) {
        this.title = title;
        this.subtitle = subtitle;
        this.price = price;
        this.quantity = 1;
        this.stock = stock;
    }
}
