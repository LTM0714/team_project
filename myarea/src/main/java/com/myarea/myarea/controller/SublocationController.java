package com.myarea.myarea.controller;

import com.myarea.myarea.entity.Sublocation;
import com.myarea.myarea.service.SublocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sublocation")
public class SublocationController {
    @Autowired
    private SublocationService sublocationService;

    @GetMapping
    public List<Sublocation> getAll() {
        return sublocationService.getAll();
    }
}
