package kassandrafalsitta.e_commerce_back.controllers;

import com.stripe.net.Webhook;
import kassandrafalsitta.e_commerce_back.entities.CheckoutResponse;
import kassandrafalsitta.e_commerce_back.entities.Order;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.payloads.OrderDTO;
import kassandrafalsitta.e_commerce_back.payloads.OrderRespDTO;
import kassandrafalsitta.e_commerce_back.payloads.SessionIdDTO;
import kassandrafalsitta.e_commerce_back.payloads.SessionRespDTO;
import kassandrafalsitta.e_commerce_back.services.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;


import com.stripe.model.Event;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Value("${secret.key.stripe}")
    private String stripeApiKey;
    @Autowired
    private OrdersService ordersService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Order> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "30") int size,
                                        @RequestParam(defaultValue = "id") String sortBy) {
        return this.ordersService.findAll(page, size, sortBy);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderRespDTO createOrder(@RequestBody @Validated OrderDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String messages = validationResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload. " + messages);
        }

        CheckoutResponse checkoutResponse = ordersService.saveOrder(body);
        return new OrderRespDTO(checkoutResponse.getOrder().getId(), checkoutResponse.getSessionId());
    }

    @PostMapping("/check-status")
    public SessionRespDTO checkPaymentStatus( @RequestBody @Validated SessionIdDTO sessionId,BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String messages = validationResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload. " + messages);
        }

        boolean isSuccess = ordersService.checkIfPaymentSucceeded(sessionId);

        if (isSuccess) {
            ordersService.saveOrderOnDatabase(sessionId);
            return new SessionRespDTO("Pagamento completato con successo.");
        } else {
            return new SessionRespDTO("Il pagamento non è andato a buon fine.");
        }
    }



    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Order getOrderById(@PathVariable UUID orderId) {
        return ordersService.findById(orderId);
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Order findOrderByIdAndUpdate(@PathVariable UUID orderId, @RequestBody @Validated OrderDTO body) {
        return ordersService.findByIdAndUpdate(orderId, body);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findOrderByIdAndDelete(@PathVariable UUID orderId) {
        ordersService.findByIdAndDelete(orderId);
    }



}
