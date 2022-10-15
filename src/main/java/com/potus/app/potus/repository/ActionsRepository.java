package com.potus.app.potus.repository;

import com.potus.app.potus.model.PotusAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionsRepository extends JpaRepository<PotusAction, Long> {
}
