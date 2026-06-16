package com.clienthub.gateway;

import com.clienthub.gateway.user.User;
import com.clienthub.gateway.user.UserRepository;
import com.clienthub.gateway.user.Role;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@AllArgsConstructor
public class ClientHubApplication {

	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext =
		SpringApplication.run(ClientHubApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepository) {
		return args -> {
			if (userRepository.existsByUsername("kevinRice")) return;

			User kevin = new User("kevinRice",
			"kevin@email.com",
			passwordEncoder.encode("C0mpl3x#Pa$$"),
			Role.ADMIN,
			false,
			true);

			User dave = new User("daveBean",
			"dave@email.com",
			passwordEncoder.encode("C0mpl3x#Pa$$"),
			Role.USER,
			false,
			true);

			userRepository.saveAll(List.of(kevin, dave));
		};
	}

}
