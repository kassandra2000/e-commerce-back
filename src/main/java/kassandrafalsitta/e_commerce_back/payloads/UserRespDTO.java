package kassandrafalsitta.e_commerce_back.payloads;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRespDTO(
        @NotNull(message = "L'UUID è obbligatorio")
        UUID employeeId
) {
}
