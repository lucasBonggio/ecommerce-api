package com.lmelectronica.ecommerce.favorite;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.shared.dtos.FavoriteDTO;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    private final UserRepository userRepository;
    
    private final ProductRepository productRepository;
        
    @Transactional
    public FavoriteDTO createFavorite(String username, FavoriteDTO favoriteDTO) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        Product product = productRepository.findById(favoriteDTO.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", favoriteDTO.getProductId()));
        
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndProductId(user.getId(), product.getId());

        if(existingFavorite.isPresent()){
            throw BusinessRuleException.duplicateResource("Favorite", "userId and productId", existingFavorite);
        }
        
        Favorite favoriteMapped = new Favorite();
        favoriteMapped.setUser(user);
        favoriteMapped.setProduct(product);
        favoriteMapped.setCreatedAt(favoriteDTO.getCreatedAt());
        
        Favorite savedFavorite = favoriteRepository.save(favoriteMapped);
        return mapFavoriteDTO(savedFavorite);
    }

    public Page<FavoriteDTO> getFavoritesByUser(String username, int page, int size, Sort sort){
        User userFound = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Favorite> favorites = favoriteRepository.findFavoritesByUser(userFound, pageable);

        return favorites.map(favorite -> mapFavoriteDTO(favorite));
    }

    @Transactional
    public void removeFavorite(String username, Long productId) {
        User userFound = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userFound.getId(), productId)
            .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for user: " + userFound.getId() + " and product: " + productId));

        favoriteRepository.deleteByUserIdAndProductId(userFound.getId(), productId);
    }

    public long countFavoritesByProductId(Long productId) {
        return favoriteRepository.countByProductId(productId);
    }

    public FavoriteDTO mapFavoriteDTO(Favorite favorite) {
        Product product = productRepository.findById(favorite.getProduct().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", favorite.getProduct().getId()));

        FavoriteDTO resultDTO = new FavoriteDTO();
        resultDTO.setProductId(product.getId());
        resultDTO.setCreatedAt(favorite.getCreatedAt());

        return resultDTO;
    }

}
