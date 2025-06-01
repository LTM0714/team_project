package com.myarea.myarea.repository;

import com.myarea.myarea.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
} 