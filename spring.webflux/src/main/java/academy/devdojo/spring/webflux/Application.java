package academy.devdojo.spring.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.blockhound.BlockHound;

@SpringBootApplication
@EnableScheduling
public class Application {

	static{
		BlockHound.install(builder ->
				builder.allowBlockingCallsInside("java.util.UUID", "randomUUID"));
	}

	public static void main(String[] args) {
		//System.out.println(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("devdojo"));
		SpringApplication.run(Application.class, args);
	}

}
