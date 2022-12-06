package org.maggus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicGame {
    private Integer rank;
    private Long bggId;
    private String thumbUrl;
    private String name;
    private String shortDesc;
    private Integer year;
    private Double geekRating;
    private Double avgRating;
    private Integer numVotes;
}
