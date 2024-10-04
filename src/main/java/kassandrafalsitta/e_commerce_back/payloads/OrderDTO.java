package kassandrafalsitta.e_commerce_back.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record OrderDTO(
        @NotEmpty(message = "La data di aggiunta è obbligatoria")
        @Size(min = 10, max = 10, message = "La data di aggiunta deve avere 10 caratteri")
        String dateAdded,
        @NotEmpty(message = "Lo stato è obbligatorio")
        @Size(min = 3, max = 40, message = "Lo stato deve essere compreso tra 3 e 40 caratteri")
        String status,
        @NotEmpty(message = "Il totale è obbligatorio")
        @Size(min = 3, max = 40, message = "Il totale deve essere compreso tra 3 e 40 caratteri")
        String total,
        @NotEmpty(message = "L'UUID dell' utente è obbligatorio")
        @Size(min = 36, max = 36, message = "L'UUID dell' utente  deve avere 36 caratteri")
        String userId,
        @NotNull(message = "La lista di UUID dei prodotti è obbligatoria")
        List<String> productId
) {
}
