package com.sazibrahman.quizservice.repositiry;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sazibrahman.quizservice.data.entity.v1.User; 

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
	 
	User findByUuid(UUID uuid);
	
	User findByEmail(String email);

}
