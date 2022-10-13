package com.potus.app.airquality.repository;

import com.potus.app.airquality.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

}
