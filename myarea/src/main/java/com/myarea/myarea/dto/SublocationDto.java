package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Like_post;
import com.myarea.myarea.entity.SubLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SubLocationDto {
    private Long sublocId;
    private String name;

    public static SubLocationDto toDto(SubLocation subLocation) {
        return new SubLocationDto(
                subLocation.getSublocId(),
                subLocation.getName()
        );
    }
}
