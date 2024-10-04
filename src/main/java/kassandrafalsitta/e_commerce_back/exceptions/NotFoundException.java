package kassandrafalsitta.e_commerce_back.exceptions;

import java.util.List;
import java.util.UUID;

public class NotFoundException extends  RuntimeException{
    public NotFoundException(UUID id){
        super("L'elemento con id " + id + " non è stato trovato!");
    }

    public NotFoundException(List<UUID> id) {
        super("Gli elementi con id " + id + " non sono stati trovati!");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
