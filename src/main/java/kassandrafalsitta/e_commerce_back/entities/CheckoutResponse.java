package kassandrafalsitta.e_commerce_back.entities;

public class CheckoutResponse {
    private String sessionId;
    private Order order; // Tipo di savedOrder

    public CheckoutResponse(String sessionId, Order order) {
        this.sessionId = sessionId;
        this.order = order;
    }


    public String getSessionId() {
        return sessionId;
    }

    public Order getOrder() {
        return order;
    }
}

