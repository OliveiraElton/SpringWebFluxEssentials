package academy.devdojo.spring.webflux.repository;

import academy.devdojo.spring.webflux.domain.Anime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AnimeRepository extends ReactiveCrudRepository<Anime, Integer> {

    Mono<Anime> findById(int id);
}
