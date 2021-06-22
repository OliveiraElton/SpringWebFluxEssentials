package academy.devdojo.spring.webflux.services;

import academy.devdojo.spring.webflux.domain.Anime;
import academy.devdojo.spring.webflux.repository.AnimeRepository;
import academy.devdojo.spring.webflux.utils.AnimeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AnimeServicesTest {

    @InjectMocks
    private AnimeServices animeServices;

    @Mock
    private AnimeRepository animeRepositoryMock;

    private final Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    public static void blockHoundSetup(){
        BlockHound.install();
    }

    @BeforeEach
    public void setUp(){
        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(Flux.just(anime));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeRepositoryMock.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(), AnimeCreator.createAnimeToBeSaved())))
                .thenReturn(Flux.just(anime, anime));

        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());

        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createValidAnime()))
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
        StepVerifier.create(animeServices.findAll())
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns Mono with anime when it exists")
    public void findById_ReturnMonoAnime_WhenSucessful(){
        StepVerifier.create(animeServices.findById(1))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns Mono with anime when it exists")
    public void findById_ReturnMonoError_WhenEmptyMonoIsReturned(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());


        StepVerifier.create(animeServices.findById(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("save creates an anime when sucessful")
    public void save_CreatesAnime_WhenSuccessful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        StepVerifier.create(animeServices.save(animeToBeSaved))
                .expectSubscription()
                .expectNext(anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("saveAll creates an anime when sucessful")
    public void saveAll_CreatesListOfAnime_WhenSuccessful(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        StepVerifier.create(animeServices.saveAll(List.of(animeToBeSaved, animeToBeSaved)))
                .expectSubscription()
                .expectNext(anime, anime)
                .verifyComplete();
    }

    @Test
    @DisplayName("saveAll returns Mono error when one of the objects in the list contains null or empty name")
    public void saveAll_ReturnsMonoError_WhenContainsInvalidName(){
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        BDDMockito.when(animeRepositoryMock.saveAll(ArgumentMatchers.anyIterable()))
                .thenReturn(Flux.just(anime, anime.withName("")));

        StepVerifier.create(animeServices.saveAll(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
                .expectSubscription()
                .expectNext(anime)
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("delete removes the anime when sucessful")
    public void delete_RemovesAnime_WhenSuccessful(){

        StepVerifier.create(animeServices.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("delete returns Mono error when anime does not exists")
    public void delete_ReturnMonoError_WhenEmptyMonoIsReturned(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(animeServices.delete(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("update save updated anime and returns empty mono whensucessful")
    public void update_SaveUpdateAnime_WhenSuccessful(){
        StepVerifier.create(animeServices.update(AnimeCreator.createValidAnime()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update returns Mono error when anime does exists")
    public void update_ReturnMonoError_WhenEmptyMonoIsReturned(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(animeServices.update(AnimeCreator.createValidAnime()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

}