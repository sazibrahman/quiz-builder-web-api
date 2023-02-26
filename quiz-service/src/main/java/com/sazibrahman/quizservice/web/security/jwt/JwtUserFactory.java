package com.sazibrahman.quizservice.web.security.jwt;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.sazibrahman.quizservice.data.entity.v1.User;
import com.sazibrahman.quizservice.web.security.jwt.model.JwtUser;

public final class JwtUserFactory {

	private JwtUserFactory() {
	}

	public static JwtUser createFromUser(User user, List<String> permissions) {
        return new JwtUser(user.getUuid(), 
                user.getEmail(), user.getPassword(),
                mapRolePermissionsToGrantedAuthorities(permissions),
                user.getLastPasswordResetDate(), user.getDisabledDate(), user.getLockDate(), 
                user.getPasswordExpiryDate(), user.getForcePasswordChangeDate(),
                user.isAccountNonLocked(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired());
    }

	private static List<GrantedAuthority> mapRolePermissionsToGrantedAuthorities(List<String> permissions) {
		return permissions.stream().map(perm -> new SimpleGrantedAuthority(perm))
				.collect(Collectors.toList());
	}
	
}
