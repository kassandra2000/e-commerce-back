package kassandrafalsitta.e_commerce_back.payloads;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserProductListDTO(
        @NotNull(message = "La lista di UUID dei prodotti è obbligatoria")
        List<String> productId
) {
}
