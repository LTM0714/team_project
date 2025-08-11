package com.myarea.myarea.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sublocation")
public class SubLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sublocId;

    private String name;  // 예: 서울특별시, 부산광역시
}
