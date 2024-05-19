package ptit.oop.assetmanagement.services;

import ptit.oop.assetmanagement.entities.UserEntity;
import ptit.oop.assetmanagement.exceptions.NotFoundException;
import ptit.oop.assetmanagement.repositories.UserRepository;
import ptit.oop.assetmanagement.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository
                .findById(username)
                .orElseThrow(() -> new NotFoundException("Cannot find user with username = " + username));

        return UserDetailsImpl.build(userEntity);
    }
}
