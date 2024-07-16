package com.maveric.repo;

import com.maveric.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterRepo extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u WHERE u.emailId = :email AND u.passwordStatus = 'ACTIVE'")
  Optional<User> findByEmailIdAndStatusActive(@Param("email") String email);

  @Query(value = "SELECT u FROM User u WHERE u.emailId = :email", nativeQuery = false)
  List<User> getUsersByEmailId(@Param("email") String email);
}
