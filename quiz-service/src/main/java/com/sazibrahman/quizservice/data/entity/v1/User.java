package com.sazibrahman.quizservice.data.entity.v1;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;  

@Entity
@Setter
@Getter
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)") 
	private UUID uuid;

    private String email;
    private String password;

    private String firstName;
    private String middleName;
    private String lastName;
    
    private ZonedDateTime createdDate;

    private Date lastPasswordResetDate;
    private LocalDateTime deletedDate;
    
    private LocalDateTime disabledDate;

    private Date lockDate;

    private Date passwordExpiryDate;

    private Date loginDate;
    
    private Date forcePasswordChangeDate;
    
    private Integer loginFailedCount = 0;
    
    /**
     * Indicated if the account not locked
     * 
     * @return boolean
     */
    public boolean isAccountNonLocked() {
        return lockDate == null || new Date().before(lockDate);
    }

    /**
     * Indicated if the account is active/enabled
     * 
     * @return boolean
     */
    public boolean isEnabled() {
        return disabledDate == null || LocalDateTime.now().isBefore(disabledDate);
    }

    /**
     * Indicates if the account was not deleted/expired
     * 
     * @return boolean
     */
    public boolean isAccountNonExpired() {
        return deletedDate == null || LocalDateTime.now().isBefore(deletedDate);
    }

    /**
     * Indicates if the password is not expired
     * 
     * @return boolean
     */
    public boolean isCredentialsNonExpired() {
        return passwordExpiryDate == null || new Date().before(passwordExpiryDate);
    }	
}
