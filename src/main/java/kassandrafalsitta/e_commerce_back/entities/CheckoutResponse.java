package kassandrafalsitta.e_commerce_back.entities;

import lombok.Getter;

@Getter
public class CheckoutResponse {
    private final String sessionId;
    private final Order order;

    public CheckoutResponse(String sessionId, Order order) {
        this.sessionId = sessionId;
        this.order = order;
    }


}

