package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Sublocation;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SublocationDto {
    private Long subloc_id;
    private String name;

    public static SublocationDto toDto(Sublocation sublocation) {
        return new SublocationDto(
                sublocation.getSubloc_id(),
                sublocation.getName()
        );
    }
}
