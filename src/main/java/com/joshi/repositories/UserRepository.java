package com.joshi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.joshi.entities.User;
import com.joshi.enums.UserRole;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findFirstByEmail(String username);

	Optional<User> findByUserRole(UserRole userRole);

}
