package kassandrafalsitta.e_commerce_back.entities;

import jakarta.persistence.*;
import kassandrafalsitta.e_commerce_back.enums.Status;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;
    private LocalDate dateAdded;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate dateModified;
    private double total;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "junction_order",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> productList;

    //aggiungere pagamento
    public Order(LocalDate dateAdded, Status status, double total, User user, List<Product> productList) {
        this.dateAdded = dateAdded;
        this.status = status;
        this.dateModified = LocalDate.now();
        this.total = total;
        this.user = user;
        this.productList = productList;
    }
}
