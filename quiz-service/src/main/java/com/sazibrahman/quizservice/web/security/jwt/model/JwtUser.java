package com.sazibrahman.quizservice.web.security.jwt.model;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class JwtUser implements UserDetails {

	private static final long serialVersionUID = -4045208853445395941L;

	private final UUID uuid;
	
	private final String username;
	private final String password;
	
	private final Collection<? extends GrantedAuthority> authorities;
	
	private final Date lastPasswordResetDate;
	private transient LocalDateTime disabledDate;
	private final Date lockDate;
	private final Date passwordExpiryDate;
	private final Date forcePasswordChangeDate;
	
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	
	public JwtUser(UUID uuid, String username, String password,
			Collection<? extends GrantedAuthority> authorities, 
			Date lastPasswordResetDate, LocalDateTime disabledDate, Date lockDate, 
			Date passwordExpiryDate, Date forcePasswordChangeDate,
			boolean accountNonLocked, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired) {
	    
		this.uuid = uuid;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.lastPasswordResetDate = lastPasswordResetDate;
		this.disabledDate = disabledDate;
		this.lockDate = lockDate;
		this.passwordExpiryDate = passwordExpiryDate;
		this.forcePasswordChangeDate = forcePasswordChangeDate;
		this.accountNonLocked = accountNonLocked;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

}
