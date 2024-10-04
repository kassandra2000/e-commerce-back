package kassandrafalsitta.e_commerce_back.entities;

import jakarta.persistence.*;
import kassandrafalsitta.e_commerce_back.enums.Status;
import lombok.*;

import java.time.LocalDate;
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

    //aggiungere pagamento


    public Order(LocalDate dateAdded, Status status, double total, User user) {
        this.dateAdded = dateAdded;
        this.status = status;
        this.dateModified = LocalDate.now();
        this.total = total;
        this.user = user;
    }
}
