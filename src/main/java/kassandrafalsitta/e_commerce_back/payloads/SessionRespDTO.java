package kassandrafalsitta.e_commerce_back.payloads;

import jakarta.validation.constraints.NotEmpty;

public record SessionRespDTO(
        @NotEmpty(message = "La session id è obbligatoria")
        String message
) {
}
