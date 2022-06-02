package com.epam.esm.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Class represent Tag entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "tags")
public class TagDto extends RepresentationModel<TagDto> {

    private Long id;
    private String name;

    public TagDto(String name) {
        this.name = name;
    }
}
