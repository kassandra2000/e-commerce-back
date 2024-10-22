package kassandrafalsitta.e_commerce_back.payloads;

import jakarta.validation.constraints.Min;

public record ProductQuantityDTO(
        @Min(1)
       int quantity
) {
}
