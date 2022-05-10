package com.epam.esm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

/**
 * Class represent Tag entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDto extends RepresentationModel<TagDto> {

    private Long id;
    private String name;

    public TagDto (String name){
        this.name = name;
    }
}
