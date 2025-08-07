package com.myarea.myarea.repository;

import com.myarea.myarea.entity.SubSubLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubSubLocationRepository extends JpaRepository<SubSubLocation, Long> {
    List<SubSubLocation> findBySublocation_SublocId(Long sublocId);
}