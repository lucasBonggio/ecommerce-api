package com.lmelectronica.ecommerce.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.shared.dtos.ReviewCreateDTO;
import com.lmelectronica.ecommerce.shared.dtos.ReviewDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateReviewRequest;
import com.lmelectronica.ecommerce.shared.exceptions.BusinessRuleException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReviewService {

    public final ReviewRepository reviewRepository;

    public final UserRepository userRepository;

    public final ProductRepository productRepository;


    @Transactional
    public ReviewDTO createReview(Long productId, String username, ReviewCreateDTO createDTO){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        
        Review review = new Review();
        review.setRating(createDTO.getRating());
        review.setComment(createDTO.getComment());
        review.setUser(user);
        review.setProduct(product);

        Review reviewSaved = reviewRepository.save(review);

        return mapReviewDTO(reviewSaved);
    }

    public ReviewDTO getReviewById(Long id){
        Review foundReview = reviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review", id));
        
        return mapReviewDTO(foundReview);
    }

    public Page<ReviewDTO> getReviewsByUser(String username, int page, int size, Sort sort){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Review> reviews = reviewRepository.findByUser(user, pageable);

        return reviews.map(review -> mapReviewDTO(review));
    }

    public Page<ReviewDTO> getReviewsByProductId(Long productId, int page, int size, Sort sort){        
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);

        return reviews.map(review -> mapReviewDTO(review));
    }

    public void updateReview(String username, Long reviewId, UpdateReviewRequest request){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Review foundReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));

        if(!foundReview.getUser().getId().equals(user.getId())) throw new BusinessRuleException("You can only update your own reviews. ");

        if(request.getComment() != null){
            foundReview.setComment(request.getComment());
        }
        if(request.getRating() >= 0){
            foundReview.setRating(request.getRating());
        }

        reviewRepository.save(foundReview);        
    }

    public void deleteReview(String username, Long reviewId){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Review foundReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        
        if(!foundReview.getUser().getId().equals(user.getId())) throw new BusinessRuleException("You can only delete your own reviews. ");
        
        reviewRepository.delete(foundReview); 
    } 

    public ReviewDTO mapReviewDTO(Review review){
        ReviewDTO dto = new ReviewDTO();

        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setProductId(review.getProduct().getId());
        dto.setUserId(review.getUser().getId());

        return dto;
    }
}
