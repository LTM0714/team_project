package com.myarea.myarea.service;

import com.myarea.myarea.entity.Sublocation;
import com.myarea.myarea.repository.SublocationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SublocationService {
    @Autowired
    private SublocationRepository sublocationRepository;

    public List<Sublocation> getAll() { return sublocationRepository.findAll(); }
}
