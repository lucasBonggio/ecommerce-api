package com.lmelectronica.ecommerce.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lmelectronica.ecommerce.shared.dtos.ChangePasswordRequest;
import com.lmelectronica.ecommerce.shared.dtos.DeleteRequest;
import com.lmelectronica.ecommerce.shared.dtos.UpdateUserDTO;
import com.lmelectronica.ecommerce.shared.dtos.UserDTO;
import com.lmelectronica.ecommerce.shared.exceptions.AuthorizationException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;
import com.lmelectronica.ecommerce.user.User;
import com.lmelectronica.ecommerce.user.UserRepository;
import com.lmelectronica.ecommerce.user.UserService;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelmapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void updatePassword_validationPassword(){
        String email = "user@gmail.com";
        String oldPasswordEncoded = "encodedOldPassword";
        String oldPasswordRaw = "oldPass";
        String newPasswordRaw = "newPass";
        String newPasswordEncoded = "encodedNewPass";

        
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setUsername("Richard");
        user.setPassword(oldPasswordEncoded);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail(email);
        request.setOldPassword(oldPasswordRaw);
        request.setNewPassword(newPasswordRaw);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPasswordRaw, oldPasswordEncoded)).thenReturn(true);
        when(passwordEncoder.encode(newPasswordRaw)).thenReturn(newPasswordEncoded);
        when(userRepository.save(any(User.class))).thenReturn(user);
    
        userService.updatePassword("username", request);

        assertEquals(newPasswordEncoded, user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_validIdAndData_returnUserDTO(){
        Long id = 1L;

        UpdateUserDTO request = new UpdateUserDTO();
        request.setFirstName("Richard");
        request.setLastName("Lison");

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setEmail("user@gmail.com");
        existingUser.setUsername("Richard123");

        User updatedUser = new User();
        updatedUser.setFirstName("Richard");
        updatedUser.setLastName("Lison");

        UserDTO expectedDTO= new UserDTO();
        expectedDTO.setFirstName("Richard");
        expectedDTO.setLastName("Lison");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(modelmapper.map(updatedUser, UserDTO.class)).thenReturn(expectedDTO);

        UserDTO result = userService.updateProfile("username", request);

        assertEquals("Richard", result.getFirstName());
        assertEquals("Lison", result.getLastName());     
    }
    
    @Test
    void updateProfile_userNotFound_throwsException() {
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setFirstName("Lucas");

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.updateProfile("username", updateDTO));

        String messageExpected = String.format("User with id '%s' not found. ", "username");

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void deleteAccount_verifyDeleting(){
        String email = "user@mail.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPass";

        DeleteRequest request = new DeleteRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);

        userService.deleteAccount("username", request);
        
        verify(userRepository).delete(user);
    }
    
    @Test
    void deleteAccount_nonExists_throwException() {
        DeleteRequest request = new DeleteRequest();
        request.setEmail("notExists@mail.com");

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.deleteAccount("username", request));

        String messageExpected = String.format("User with id '%s' not found. ", "username");

        assertEquals(ex.getMessage(), messageExpected);
    }

    @Test
    void deleteAccount_wrongPassword_throwException() {
        String rawPassword = "user123";
        String encodedPassword = "encodedPass";

        DeleteRequest request = new DeleteRequest();
        request.setPassword(rawPassword);

        User user = new User();
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        AuthorizationException ex = assertThrows(AuthorizationException.class, () -> userService.deleteAccount("username", request));

        assertEquals(ex.getMessage(), "Invalid username or password. ");
    }
}

