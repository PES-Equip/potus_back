package com.potus.app.admin.repository;

import com.potus.app.admin.model.APIToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface APITokenRepository extends JpaRepository<APIToken, Long> {

    boolean existsByName(String name);

    boolean existsByToken(String token);

    Optional<APIToken> findByName(String name);
}
