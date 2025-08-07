package com.myarea.myarea.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SubSubLocationDto {
    private Long subsubId;
    private String address;
    private Double latitude;
    private Double longitude;
}
