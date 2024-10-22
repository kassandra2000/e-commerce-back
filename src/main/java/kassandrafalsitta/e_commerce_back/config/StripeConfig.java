package kassandrafalsitta.e_commerce_back.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StripeConfig {

    @Value("${secret.key.stripe}")  // Estrae il valore da env.properties o application.properties
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;  // Imposta la chiave API di Stripe
    }
}
