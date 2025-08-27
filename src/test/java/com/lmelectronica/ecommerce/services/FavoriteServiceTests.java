package com.lmelectronica.ecommerce.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lmelectronica.ecommerce.favorite.Favorite;
import com.lmelectronica.ecommerce.favorite.FavoriteRepository;
import com.lmelectronica.ecommerce.favorite.FavoriteService;
import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.shared.dtos.FavoriteDTO;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTests {
    
    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;
    
    @Test
    void createFavorite_validData_returnFavoriteDTO() {
        Long userId = 1L, productId = 2L;
        Date createdAt = new Date(1234567890000L);

        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setProductId(productId);
        favoriteDTO.setCreatedAt(createdAt);

        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setId(productId);

        Favorite favorite = new Favorite();
        favorite.setId(10L); 
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCreatedAt(createdAt);

        Favorite favoriteSaved = new Favorite();
        favoriteSaved.setId(10L); 
        favoriteSaved.setUser(user);
        favoriteSaved.setProduct(product);
        favoriteSaved.setCreatedAt(createdAt);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(favoriteRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favoriteSaved);
        
        FavoriteDTO result = favoriteService.createFavorite("username", favoriteDTO);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(createdAt, result.getCreatedAt());

        verify(favoriteRepository).findByUserIdAndProductId(userId, productId);
        verify(userRepository, atLeast(1)).findByUsername("username");
        verify(productRepository, atLeast(1)).findById(productId);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void createFavorite_userNotFound_throwException(){
        FavoriteDTO favDTO = new FavoriteDTO();
        favDTO.setProductId(1L);

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> favoriteService.createFavorite("username", favDTO));

        String messageExpected = String.format("User with id '%s' not found. ", "username");

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void createFavorite_productNotFound_throwException(){
        FavoriteDTO favDTO = new FavoriteDTO();
        favDTO.setProductId(2L);
        Product product = new Product();
        product.setId(1L);

        User user = new User();
        user.setFirstName("Lucas");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> favoriteService.createFavorite("username", favDTO));

        String messageExpected = String.format("Product with id '%s' not found. ", favDTO.getProductId());

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void getFavoritesByUser_validData_returnPageFavoriteDTO() {
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setProductId(2L);

        FavoriteDTO favoriteDTO1 = new FavoriteDTO();
        favoriteDTO1.setProductId(3L);

        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(2L);
        Product product1 = new Product();
        product1.setId(3L);
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        Favorite favorite1 = new Favorite();
        favorite1.setUser(user);
        favorite1.setProduct(product1);

        List<Favorite> favs = List.of(favorite, favorite1);
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Favorite> favoritePage = new PageImpl<>(favs, pageable, favs.size());

        when(favoriteRepository.findFavoritesByUser(user, pageable)).thenReturn(favoritePage);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product1));

        Page<FavoriteDTO> result = favoriteService.getFavoritesByUser("username", 0, 10, sort);

        assertNotNull(result);
        assertThat(result.getContent())
                    .hasSize(2);

        verify(favoriteRepository).findFavoritesByUser(user, pageable);
    }

    @Test
    void getFavoritesByUser_noFavorites_returnEmptyPage() {
        User user = new User();
        user.setId(1L);
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Favorite> favoritPage = Page.empty();

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(favoriteRepository.findFavoritesByUser(user, pageable)).thenReturn(favoritPage);


        Page<FavoriteDTO> result = favoriteService.getFavoritesByUser("username", 0, 10, sort);

        assertNotNull(result);
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    void removeFavorite_verifyRemove(){
        Long userId = 1L, productId = 2L;

        User user = new User();
        user.setId(userId);
        Product product = new Product();
        product.setId(productId);

        Favorite fav = new Favorite();
        fav.setId(1L);
        fav.setUser(user);
        fav.setProduct(product);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.of(fav));

        favoriteService.removeFavorite("username", productId);

        verify(favoriteRepository).findByUserIdAndProductId(userId, productId);
        verify(favoriteRepository).deleteByUserIdAndProductId(userId, productId);
    }

    @Test
    void removeFavorite_favoriteNotFound_throwException(){
        Long productId = 2L;
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserIdAndProductId(1L, productId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> favoriteService.removeFavorite("username", productId));

        assertEquals(ex.getMessage(), "Favorite not found for user: 1 and product: 2");
    }

    @Test
    void countFavoritesByProductId_noFavorites_returnZero() {
        Long productId = 2L;

        when(favoriteRepository.countByProductId(productId)).thenReturn(0L);

        long result = favoriteService.countFavoritesByProductId(productId);

        assertEquals(0, result);
    }
}
