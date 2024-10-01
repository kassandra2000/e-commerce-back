package kassandrafalsitta.e_commerce_back.tools;

import kassandrafalsitta.e_commerce_back.entities.User;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailgunSender {
    private final String apiKey;
    private final String domainName;
    private final String email;

    public MailgunSender(@Value("${mailgun.key}") String apiKey, @Value("${mailgun.domain}") String domainName, @Value("${email}") String email) {
        this.apiKey = apiKey;
        this.domainName = domainName;
        this.email = email;
    }

    public void sendRegistrationEmail(User recipient) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", this.email) 
                .queryString("to", recipient.getEmail())
                .queryString("subject", "Registrazione completata")
                .queryString("text", "Ciao " + recipient.getName() + ", grazie per esserti registrato!")
                .asJson();
        System.out.println(response.getBody());
    }
}
