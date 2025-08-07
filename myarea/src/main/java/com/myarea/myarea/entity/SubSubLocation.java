package com.myarea.myarea.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "subsublocation")
public class SubSubLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subsubId;

    private Double latitude;
    private Double longitude;
    private String address; // 예: 강남구, 강동구 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subloc_id")
    private SubLocation sublocation;
}
