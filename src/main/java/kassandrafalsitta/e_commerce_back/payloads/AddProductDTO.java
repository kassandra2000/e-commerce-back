package kassandrafalsitta.e_commerce_back.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AddProductDTO(
        @NotEmpty(message = "L'UUID dell' prodotto è obbligatorio")
        @Size(min = 36, max = 36, message = "L'UUID dell' prodotto  deve avere 36 caratteri")
        String id
) {
}
