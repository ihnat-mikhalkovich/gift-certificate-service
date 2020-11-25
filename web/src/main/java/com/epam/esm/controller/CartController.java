package com.epam.esm.controller;


import com.epam.esm.dto.CartDto;
import com.epam.esm.dto.CartItemDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.GiftCertificateNotFoundException;
import com.epam.esm.exception.UserNotFoundException;
import com.epam.esm.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * Viewing the cart.
     *
     * @throws UserNotFoundException if the user with the specified email doesn't exist
     */
    @GetMapping
    public ResponseEntity<CartDto> getCart(
            @RequestBody UserDto userDto
    ) throws UserNotFoundException {
        String userEmail = userDto.getEmail();
        CartDto cart = cartService.getCartOrCreate(userEmail);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cart);
    }

    /**
     * Adding a gift certificate.
     *
     * @return updated cart
     * @throws GiftCertificateNotFoundException if the specified gift certificate does not exist
     * @throws UserNotFoundException            if the user with the specified email doesn't exist
     */
    @PutMapping
    public ResponseEntity<CartDto> addItem(
            @RequestBody CartItemDto item
    ) throws GiftCertificateNotFoundException, UserNotFoundException {
        CartDto cart = cartService.addToCart(
                item.getUserEmail(),
                item.getCertificateId(),
                item.getQuantity()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cart);
    }

    /**
     * Clearing the cart.
     *
     * @return cleared cart
     * @throws UserNotFoundException if the user with the specified email doesn't exist
     */
    @DeleteMapping
    public ResponseEntity<CartDto> clearCart(
            @RequestBody UserDto userDto
    ) throws UserNotFoundException {
        String userEmail = userDto.getEmail();
        CartDto clearedCart = cartService.clearCart(userEmail);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clearedCart);
    }
}