package com.potus.app.potus.repository;

import com.potus.app.potus.model.Modifier;
import com.potus.app.potus.model.PotusAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifierRepository extends JpaRepository<Modifier, Long> {
}
