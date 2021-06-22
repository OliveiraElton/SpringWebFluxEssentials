package academy.devdojo.spring.webflux.controller;

import academy.devdojo.spring.webflux.domain.Anime;
import academy.devdojo.spring.webflux.repository.AnimeRepository;
import academy.devdojo.spring.webflux.services.AnimeServices;
import academy.devdojo.spring.webflux.utils.AnimeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeServices animeServicesMock;

    private final Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    public static void blockHoundSetup(){
        BlockHound.install();
    }

    @BeforeEach
    public void setUp(){
        BDDMockito.when(animeServicesMock.findAll())
                .thenReturn(Flux.just(anime));

        BDDMockito.when(animeServicesMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeServicesMock.save(AnimeCreator.createAnimeToBeSaved()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeServicesMock.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
                .thenReturn(Flux.just(anime, anime));

        BDDMockito.when(animeServicesMock.delete(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        BDDMockito.when(animeServicesMock.update(AnimeCreator.createValidAnime()))
                .thenReturn(Mono.empty());
    }

    @Test
    public void blockHoundWorks(){
        try{
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @Test
    @DisplayName("findAll returns a flu of anime")
    public void findAll_ReturnFluxOfAnime_WhenSucessful(){
        StepVerifier.create(animeController.listAll())
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns Mono with anime when it exists")
    public void findById_ReturnMonoAnime_WhenSucessful(){
        StepVerifier.create(animeController.findById(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("save creates an anime when sucessful")
    public void save_CreatesAnime_WhenSuccessful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        StepVerifier.create(animeController.save(animeToBeSaved))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("save creates an anime when sucessful")
    public void saveBatch_CreatesAnime_WhenSuccessful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        StepVerifier.create(animeController.saveBatch(List.of(animeToBeSaved, animeToBeSaved)))
                .expectSubscription()
                .expectNext(anime, anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes the anime when sucessful")
    public void delete_RemovesAnime_WhenSuccessful(){

        StepVerifier.create(animeController.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update save updated anime and returns empty mono whensucessful")
    public void update_SaveUpdateAnime_WhenSuccessful(){
        StepVerifier.create(animeController.update(1, AnimeCreator.createValidAnime()))
                .expectSubscription()
                .verifyComplete();
    }

}