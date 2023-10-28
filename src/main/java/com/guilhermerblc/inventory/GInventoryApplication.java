package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Status;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.PermissionRepository;
import com.guilhermerblc.inventory.repository.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class GInventoryApplication {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(GInventoryApplication.class, args);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}


	@Bean
	InitializingBean createDefaultUser() {
		return () -> {

			if(userRepository.findAll().toArray().length > 0) return;

			User defaultUser = new User();
			defaultUser.setName("Default 1234");
			defaultUser.setRole("Gerente");
			defaultUser.setStatus(Status.ACTIVE);
			defaultUser.setUsername("gerente");
			defaultUser.setPassword(passwordEncoder.encode("1234"));
			defaultUser.setCreated(LocalDateTime.now());
			defaultUser.setPermissions(permissionRepository.findAll());
			userRepository.save(defaultUser);
		};
	}

}
