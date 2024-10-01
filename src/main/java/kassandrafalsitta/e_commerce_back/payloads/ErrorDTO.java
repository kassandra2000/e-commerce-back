package kassandrafalsitta.e_commerce_back.payloads;

import java.time.LocalDateTime;

public record ErrorDTO(String message,LocalDateTime timestamp) {
}
