package com.myarea.myarea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "sublocation")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
public class Sublocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subloc_id")
    private Long subloc_id;

    @JoinColumn(name = "name", nullable = false)
    private String name;
}
