package com.thesharehub.TheShareHub.repository;

import com.thesharehub.TheShareHub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
