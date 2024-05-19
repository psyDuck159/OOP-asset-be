package ptit.oop.assetmanagement.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ptit.oop.assetmanagement.entities.LocationEntity;
import ptit.oop.assetmanagement.entities.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@Getter
public class UserDetailsImpl implements UserDetails {
    private String username;
    @JsonIgnore
    private String password;
    private LocationEntity locationEntity;
    private LocalDateTime lastLogin;
    private LocalDate dob;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String password, LocalDateTime lastLogin, LocalDate dob,
                           Collection<? extends GrantedAuthority> authorities,
                           LocationEntity locationEntity) {
        this.username = username;
        this.password = password;
        this.lastLogin = lastLogin;
        this.dob = dob;
        this.authorities = authorities;
        this.locationEntity = locationEntity;
    }

    public static UserDetailsImpl build(UserEntity user) {
        return new UserDetailsImpl(
                user.getUsername(),
                user.getPassword(),
                user.getLastLogin(),
                user.getDob(),
                Arrays.asList(new SimpleGrantedAuthority(user.getType())),
                user.getLocation());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(username, user.getUsername());
    }
}
