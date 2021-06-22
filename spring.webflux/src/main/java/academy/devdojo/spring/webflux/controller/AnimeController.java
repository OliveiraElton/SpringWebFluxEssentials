package academy.devdojo.spring.webflux.controller;

import academy.devdojo.spring.webflux.domain.Anime;
import academy.devdojo.spring.webflux.services.AnimeServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("animes")
@Slf4j
public class AnimeController {

    private final AnimeServices animeServices;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasRole('ADMIN')")
    public Flux<Anime> listAll(){
        return animeServices.findAll();
    }

    @GetMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Anime> findById(@PathVariable int id){
        return animeServices.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Anime> save(@Valid @RequestBody Anime anime){
        return animeServices.save(anime);
    }

    @PostMapping("batch")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<Anime> saveBatch(@RequestBody List<Anime> animes){
        return animeServices.saveAll(animes);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable int id, @Valid @RequestBody Anime anime){
        return animeServices.update(anime.withId(id));
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable int id){
        return animeServices.delete(id);
    }
}
