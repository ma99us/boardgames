package org.maggus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDetails {
    private Long bggId;
    private String name;
    private String description;
    private String thumbnailUrl;
    private String imageUrl;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer yearPublished;
    private String publisher;
    private String designer;
    private String artist;
    private String honors;
    private List<Integer> bestPlayers;
    private List<Integer> goodPlayers;
    //private Integer usersrated;
    private Double avgRating;
    //private Integer numweights;
    private Double avgWeight;
}

