package kassandrafalsitta.e_commerce_back.controllers;

import kassandrafalsitta.e_commerce_back.entities.Order;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.payloads.OrderDTO;
import kassandrafalsitta.e_commerce_back.payloads.OrderRespDTO;
import kassandrafalsitta.e_commerce_back.services.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrdersController {
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
        } else {
            return new OrderRespDTO(this.ordersService.saveOrder(body).getId());
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
