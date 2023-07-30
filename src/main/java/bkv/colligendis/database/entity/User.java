package bkv.colligendis.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Node("USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractEntity implements UserDetails {

    private String username;

    private String email;
    //DigestUtils.sha256Hex
    private String password;

    private List<String> roles = new ArrayList<>();

    private String profilePictureUrl;

    private boolean is_auth;
    private boolean isExpired;
    private boolean isLocked;
    private boolean isCredentialsExpired;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles != null ? this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()) : null;
    }

    @Override
    public String getUsername() {
        return username;
    }

//    public void setUsername(String username) {
//        this.username = username;
//    }

    @Override
    public boolean isAccountNonExpired() {
        return !isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
