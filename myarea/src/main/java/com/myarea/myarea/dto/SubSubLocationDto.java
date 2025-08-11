package com.myarea.myarea.dto;

import com.myarea.myarea.entity.SubSubLocation;
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

    public static SubSubLocationDto fromEntity(SubSubLocation entity) {
        return new SubSubLocationDto(
                entity.getSubsubId(),
                entity.getAddress(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}
