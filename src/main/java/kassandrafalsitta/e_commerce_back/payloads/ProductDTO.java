package kassandrafalsitta.e_commerce_back.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ProductDTO(

        @NotEmpty(message = "Il titolo è obbligatorio")
        @Size(min = 3, max = 40, message = "Il titolo deve essere compreso tra 3 e 40 caratteri")
        String title,
        @NotEmpty(message = "Il sottotitolo è obbligatorio")
        @Size(min = 3, max = 40, message = "Il sottotitolo deve essere compreso tra 3 e 40 caratteri")
        String subtitle,
        @NotEmpty(message = "Il prezzo è obbligatorio")
        @Size(min = 1, max = 40, message = "Il prezzo deve essere compreso tra 3 e 40 caratteri")
        String price,
        @NotEmpty(message = "La quantità in stock è obbligatorio")
        @Size(min = 1, max = 40, message = "La quantità in stock deve essere compreso tra 1 e 40 caratteri")
        String stock
) {
}
