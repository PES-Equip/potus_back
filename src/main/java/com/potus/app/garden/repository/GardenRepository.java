package com.potus.app.garden.repository;

import com.potus.app.garden.model.Garden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GardenRepository extends JpaRepository<Garden, Long> {
    boolean existsByName(String name);

    Optional<Garden> findByName(String name);
}
