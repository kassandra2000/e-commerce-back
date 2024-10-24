package kassandrafalsitta.e_commerce_back.payloads;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderIdRespDTO(
        @NotNull(message = "L'UUID è obbligatorio")
        UUID orderId

) {
}
