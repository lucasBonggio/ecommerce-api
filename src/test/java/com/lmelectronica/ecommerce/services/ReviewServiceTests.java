package com.lmelectronica.ecommerce.services;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.lmelectronica.ecommerce.product.Product;
import com.lmelectronica.ecommerce.product.ProductRepository;
import com.lmelectronica.ecommerce.review.Review;
import com.lmelectronica.ecommerce.review.ReviewRepository;
import com.lmelectronica.ecommerce.review.ReviewService;
import com.lmelectronica.ecommerce.shared.dtos.ReviewCreateDTO;
import com.lmelectronica.ecommerce.shared.dtos.ReviewDTO;
import com.lmelectronica.ecommerce.shared.dtos.UpdateReviewRequest;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void createReview_validData_returnReviewDTO() {
        Long productId = 1L;
        String username = "username";

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);

        ReviewCreateDTO reviewDTO = new ReviewCreateDTO("Nice", 10.0);

        Review reviewToSave = new Review();
        reviewToSave.setComment("Nice");
        reviewToSave.setRating(10.0);
        reviewToSave.setUser(user);
        reviewToSave.setProduct(product);

        Review savedReview = new Review();
        savedReview.setId(10L);
        savedReview.setComment("Nice");
        savedReview.setRating(10.0);
        savedReview.setProduct(product);
        savedReview.setUser(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewDTO result = reviewService.createReview(productId, username, reviewDTO);

        assertThat(result).isNotNull();
        assertThat(result.getComment()).isEqualTo("Nice");
        assertThat(result.getRating()).isEqualTo(10.0);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getUserId()).isEqualTo(user.getId());

        verify(reviewRepository).save(argThat(r ->
            "Nice".equals(r.getComment()) &&
            10.0 == r.getRating() &&
            user.equals(r.getUser()) &&
            product.equals(r.getProduct())
        ));
    }
    
    @Test
    void createReview_productNotExists_throwException() {
        Long userId = 1L, productId = 1L;
        ReviewCreateDTO dto = new ReviewCreateDTO( "¡Very cool!", 9.0);

        User user = new User(); user.setId(userId);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(productId, "username", dto));

        String messageExpected = String.format("Product with id '%s' not found. ", productId);

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void createReview_userNotExists_returnUserIdException(){
        Long productId = 1L;

        ReviewCreateDTO dto = new ReviewCreateDTO("Bad", 2.5);

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(productId, "username", dto));
        
        String messageExpected = String.format("User with id '%s' not found. ", "username");

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void getReviewById_validData_returnReviewDTO(){
        Long reviewId = 1L;
        
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);

        Review review = new Review();
        review.setId(reviewId);
        review.setComment("Safe");
        review.setRating(6);
        review.setUser(user);
        review.setProduct(product);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ReviewDTO result = reviewService.getReviewById(reviewId);

        assertEquals("Safe", result.getComment());
    }

    @Test
    void getReviewById_idNotExists_throwException(){
        Long reviewId = 1L;

        Review review = new Review();
        review.setId(reviewId);
        
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reviewService.getReviewById(reviewId));

        String messageExcepted = String.format("Review with id '%s' not found. ", reviewId);
    
        assertEquals(ex.getMessage(), messageExcepted);
    }

    @Test
    void getReviewByUser_validData_returnPageReviewDTO(){
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setName("Mouse gamer");

        Review review1 = new Review();
        review1.setComment("Bad");
        review1.setRating(1);
        review1.setId(2L);
        review1.setUser(user);
        review1.setProduct(product);

        Review review2 = new Review();
        review2.setComment("Nice");
        review2.setRating(7);
        review2.setId(5L);
        review2.setUser(user);
        review2.setProduct(product);

        List<Review> reviews = List.of(review1, review2);
        
        Sort sort = Sort.by("id").ascending();
        PageRequest pageable = PageRequest.of(0, 10, sort);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(reviewRepository.findByUser(user, pageable)).thenReturn(reviewPage);

        Page<ReviewDTO> reviewsDTO = reviewService.getReviewsByUser("username", 0, 10, sort);

        assertNotNull(reviewsDTO);
        assertThat(reviewsDTO.getContent())
                    .hasSize(2)
                    .extracting("comment")
                    .contains("Bad", "Nice");
    }

    @Test
    void getReviewByUser_reviewNotExists_returnPageEmpty(){
        User user = new User();
        user.setId(1L);

        Sort sort = Sort.by("id").ascending();
        PageRequest pageable = PageRequest.of(0, 10, sort);
        Page<Review> reviewPage = Page.empty();

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(reviewRepository.findByUser(user, pageable)).thenReturn(reviewPage);

        Page<ReviewDTO> reviews = reviewService.getReviewsByUser("username", 0, 10, sort);

        assertNotNull(reviews);
        verify(reviewRepository).findByUser(user, pageable);
    }

    @Test
    void getReviewsByProductId(){
        Long productId = 1L;
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(productId);
        product.setName("Mouse gamer");

        Review review1 = new Review();
        review1.setComment("Bad");
        review1.setRating(1);
        review1.setId(2L);
        review1.setProduct(product);
        review1.setUser(user);
        

        Review review2 = new Review();
        review2.setComment("Nice");
        review2.setRating(7);
        review2.setId(5L);
        review2.setProduct(product);
        review2.setUser(user);

        List<Review> reviews = List.of(review1, review2);
        
        Sort sort = Sort.by("id").ascending();
        PageRequest pageable = PageRequest.of(0, 10, sort);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());

        when(reviewRepository.findByProductId(productId, pageable)).thenReturn(reviewPage);

        Page<ReviewDTO> reviewsDTO = reviewService.getReviewsByProductId(productId, 0, 10, sort);

        assertNotNull(reviewsDTO);
        assertThat(reviewsDTO.getContent())
                    .hasSize(2)
                    .extracting("comment")
                    .contains("Bad", "Nice");
    }

    @Test
    void getReviewByProductId_reviewNotExists_returnListEmpty(){
        Long productId = 1L;
        User user = new User();
        user.setId(1L);

        Sort sort = Sort.by("id").ascending();
        PageRequest pageable = PageRequest.of(0, 10, sort);
        Page<Review> reviewPage = Page.empty();

        when(reviewRepository.findByProductId(productId, pageable)).thenReturn(reviewPage);

        Page<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId, 0, 10, sort);

        assertNotNull(reviews);
        assertThat(reviews.getContent()).hasSize(0);
        
        verify(reviewRepository).findByProductId(productId, pageable);
    }

    @Test
    void updateReview_validData(){
        Long id = 1L;
        User user = new User();
        user.setId(1L);

        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setComment("Nice toy");
        request.setRating(7.8);

        Review reviewExisting = new Review();
        reviewExisting.setComment("Usseles");
        reviewExisting.setRating(2.5);
        reviewExisting.setUser(user);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewExisting));

        reviewService.updateReview("username", id, request);

        assertEquals("Nice toy", reviewExisting.getComment());
        assertEquals(7.8, reviewExisting.getRating());    
    }

    @Test
    void updateReview_idNotFound_throwException(){
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        
        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setComment("Nice");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reviewService.updateReview("username", id, request));

        String messageExpected = String.format("Review with id '%s' not found. ", id);

        assertEquals(ex.getMessage(), messageExpected);
    }   

    @Test
    void deleteReview_verifyDeleting(){
        Long id = 1L;
        User user = new User();
        user.setId(1L);     
        
        Review review = new Review();
        review.setComment("Nice");
        review.setRating(10);
        review.setUser(user);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(review));

        reviewService.deleteReview("username", id);

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_idNotFound_throwException(){
        Long id = 1L;
        User user = new User();
        user.setId(1L);  

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview("username", id));

        String messageExpected = String.format("Review with id '%s' not found. ", id);

        assertEquals(ex.getMessage(), messageExpected);

    }
}
