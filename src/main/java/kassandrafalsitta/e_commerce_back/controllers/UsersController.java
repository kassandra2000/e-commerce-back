package kassandrafalsitta.e_commerce_back.controllers;


import kassandrafalsitta.e_commerce_back.entities.Order;
import kassandrafalsitta.e_commerce_back.entities.Product;
import kassandrafalsitta.e_commerce_back.entities.User;
import kassandrafalsitta.e_commerce_back.exceptions.BadRequestException;
import kassandrafalsitta.e_commerce_back.exceptions.NotFoundException;
import kassandrafalsitta.e_commerce_back.payloads.AddProductDTO;
import kassandrafalsitta.e_commerce_back.payloads.OrderDTO;
import kassandrafalsitta.e_commerce_back.payloads.UserDTO;
import kassandrafalsitta.e_commerce_back.payloads.UserProductListDTO;
import kassandrafalsitta.e_commerce_back.services.OrdersService;
import kassandrafalsitta.e_commerce_back.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;
 @Autowired
    private OrdersService ordersService;



    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<User> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "30") int size,
                                  @RequestParam(defaultValue = "id") String sortBy) {
        return this.usersService.findAll(page, size, sortBy);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserById(@PathVariable UUID userId) {
        return usersService.findById(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User findUserByIdAndUpdate(@PathVariable UUID userId, @RequestBody @Validated UserDTO body) {
        return usersService.findByIdAndUpdate(userId, body);
    }
    @PutMapping("/{userId}/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User findUserByIdAndUpdateRole(@PathVariable UUID userId) {
        return usersService.findByIdAndUpdateRole(userId);
    }
    @PutMapping("/{userId}/productList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User findUserByIdAndUpdateProductList(@PathVariable UUID userId, @RequestBody @Validated UserProductListDTO body) {
        return usersService.findByIdAndUpdateProductList(userId, body);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findUserByIdAndDelete(@PathVariable UUID userId) {
        usersService.findByIdAndDelete(userId);
    }


    // me
    @GetMapping("/me")
    public User getProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return currentAuthenticatedUser;
    }

    @PutMapping("/me")
    public User updateProfile(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody UserDTO body) {
        return this.usersService.findByIdAndUpdate(currentAuthenticatedUser.getId(), body);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        this.usersService.findByIdAndDelete(currentAuthenticatedUser.getId());
    }


    //me/order

    @PostMapping("/me/addCart")
    @ResponseStatus(HttpStatus.CREATED)
    public Product addProductToCart(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated AddProductDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String messages = validationResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("Ci sono stati errori nel payload. " + messages);
        } else {
            UUID userId = currentAuthenticatedUser.getId();
            return this.usersService.addProductToCart(userId, body);
        }
    }

    @DeleteMapping("/me/removeCart/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProductFromCart(@AuthenticationPrincipal User currentAuthenticatedUser,
                                      @PathVariable UUID productId) {
        UUID userId = currentAuthenticatedUser.getId();
        this.usersService.removeProductFromCart(userId, productId);
    }

    @DeleteMapping("/me/removeAllQuantity/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllQuantityProductFromCart(@AuthenticationPrincipal User currentAuthenticatedUser,
                                                 @PathVariable UUID productId) {
        UUID userId = currentAuthenticatedUser.getId();
        this.usersService.removeAllQuantityProductFromCart(userId, productId);
    }

    @GetMapping("/me/order")
    public List<Order> getProfileOrder(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return this.ordersService.findByUser(currentAuthenticatedUser);
    }

    @GetMapping("/me/order/{orderId}")
    public Order getProfileOrderById(@AuthenticationPrincipal User currentAuthenticatedUser,@PathVariable UUID orderId) {
        List<Order> orderList = this.ordersService.findByUser(currentAuthenticatedUser);
        Order userOrder = orderList.stream().filter(order -> order.getId().equals(orderId)).findFirst()
                .orElseThrow(() -> new NotFoundException(orderId));
        return ordersService.findById(userOrder.getId());
    }


    @PutMapping("/me/order/{orderId}")
    public Order updateProfileOrder(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable UUID orderId, @RequestBody OrderDTO body) {
        List<Order> orderList = this.ordersService.findByUser(currentAuthenticatedUser);
        Order userOrder = orderList.stream().filter(order -> order.getId().equals(orderId)).findFirst()
                .orElseThrow(() -> new NotFoundException(orderId));
        return this.ordersService.findByIdAndUpdate(userOrder.getId(), body);
    }

    @DeleteMapping("/me/order/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfileOrder(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable UUID orderId) {
        List<Order> orderList = this.ordersService.findByUser(currentAuthenticatedUser);
        Order userOrder = orderList.stream().filter(order -> order.getId().equals(orderId)).findFirst()
                .orElseThrow(() -> new NotFoundException(orderId));
        this.ordersService.findByIdAndDelete(userOrder.getId());
    }



}
