package com.maveric.repo;

import com.maveric.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

  //  Optional<User> findByEmailId(String email);

    Optional<List<String>> getPasswordsByEmail(String email);
}
