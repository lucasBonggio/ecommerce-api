package com.lmelectronica.ecommerce.user;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lmelectronica.ecommerce.shared.dtos.ChangePasswordRequest;
import com.lmelectronica.ecommerce.shared.dtos.DeleteRequest;
import com.lmelectronica.ecommerce.shared.dtos.UpdateUserDTO;
import com.lmelectronica.ecommerce.shared.dtos.UserDTO;
import com.lmelectronica.ecommerce.shared.exceptions.AuthorizationException;
import com.lmelectronica.ecommerce.shared.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private UserDTO toDTO(User user){
        return modelMapper.map(user, UserDTO.class);
    }

    public void updatePassword(String username, ChangePasswordRequest request){
        User userFound = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));

        if(!passwordEncoder.matches(request.getOldPassword(), userFound.getPassword())){
            throw AuthorizationException.invalidCredentials();
        }

        String encodedNewPasssword = passwordEncoder.encode(request.getNewPassword());

        userFound.setPassword(encodedNewPasssword);
        
        userRepository.save(userFound);
    }

    public UserDTO updateProfile(String username, UpdateUserDTO request){
        User userFound = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));

        if(request.getFirstName() != null){
            userFound.setFirstName(request.getFirstName());
        }
        if(request.getLastName() != null){
            userFound.setLastName(request.getLastName());
        }

        User userSaved = userRepository.save(userFound);

        return toDTO(userSaved);
    }

    public void deleteAccount(String username, DeleteRequest request){
        User userFound = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));

        if(!passwordEncoder.matches(request.getPassword(), userFound.getPassword())){
            throw AuthorizationException.invalidCredentials();
        }
        userRepository.delete(userFound);
    }
}
