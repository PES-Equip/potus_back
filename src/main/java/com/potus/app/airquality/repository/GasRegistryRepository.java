package com.potus.app.airquality.repository;

import com.potus.app.airquality.model.GasRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GasRegistryRepository extends JpaRepository<GasRegistry, Long> {
}
