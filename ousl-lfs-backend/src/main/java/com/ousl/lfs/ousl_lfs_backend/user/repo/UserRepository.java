package com.ousl.lfs.ousl_lfs_backend.user.repo;


import com.ousl.lfs.ousl_lfs_backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUniversityId(String universityId);
}
