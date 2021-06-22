package academy.devdojo.spring.webflux.utils;

import academy.devdojo.spring.webflux.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeToBeSaved(){
        return Anime.builder()
                .name("Gohan")
                .build();
    }

    public static Anime createValidAnime(){
        return Anime.builder()
                .id(1)
                .name("Goku")
                .build();
    }

    public static Anime createValidUpdatedAnime(){
        return Anime.builder()
                .id(1)
                .name("Tensei")
                .build();
    }
}
