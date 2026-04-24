package com.bootcamp.user_service.repository;

import com.bootcamp.user_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByEmail(String email);

    @Query(value = "SELECT * from mst_user u WHERE u.first_name = :name", nativeQuery = true)
    Optional<List<UserEntity>> findUserByFirstName(@Param("name") String first_name);

    Optional<UserEntity> findByEmail(String email);
}
