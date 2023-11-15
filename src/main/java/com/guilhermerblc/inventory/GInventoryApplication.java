package com.guilhermerblc.inventory;

import com.guilhermerblc.inventory.models.Configuration;
import com.guilhermerblc.inventory.models.Permission;
import com.guilhermerblc.inventory.models.Status;
import com.guilhermerblc.inventory.models.User;
import com.guilhermerblc.inventory.repository.ConfigurationRepository;
import com.guilhermerblc.inventory.repository.PermissionRepository;
import com.guilhermerblc.inventory.repository.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@EnableAsync
@SpringBootApplication
public class GInventoryApplication {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(GInventoryApplication.class, args);
	}

	@Bean(name="processExecutor")
	public TaskExecutor workExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix("GInventory-");
		threadPoolTaskExecutor.setCorePoolSize(3);
		threadPoolTaskExecutor.setMaxPoolSize(3);
		threadPoolTaskExecutor.setQueueCapacity(600);
		threadPoolTaskExecutor.afterPropertiesSet();
		return threadPoolTaskExecutor;
	}

	@Bean
	InitializingBean createDefaultData() {
		return () -> {

			if(userRepository.findAll().toArray().length > 0) return;

			permissionRepository.saveAll(List.of(
					new Permission(1L, "VIEW_USERS", LocalDateTime.now(), null),
					new Permission(2L, "EDIT_USERS", LocalDateTime.now(), null),
					new Permission(3L, "DELETE_USERS", LocalDateTime.now(), null),
					new Permission(4L, "VIEW_PRODUCTS", LocalDateTime.now(), null),
					new Permission(5L, "EDIT_PRODUCTS", LocalDateTime.now(), null),
					new Permission(6L, "DELETE_PRODUCTS", LocalDateTime.now(), null),
					new Permission(7L, "VIEW_INPUTS", LocalDateTime.now(), null),
					new Permission(8L, "EDIT_INPUTS", LocalDateTime.now(), null),
					new Permission(9L, "DELETE_INPUTS", LocalDateTime.now(), null),
					new Permission(10L, "VIEW_OUTPUTS", LocalDateTime.now(), null),
					new Permission(11L, "EDIT_OUTPUTS", LocalDateTime.now(), null),
					new Permission(12L, "DELETE_OUTPUTS", LocalDateTime.now(), null),
					new Permission(13L, "GENERATE_REPORTS", LocalDateTime.now(), null),
					new Permission(14L, "EDIT_CONFIGURATIONS", LocalDateTime.now(), null)
			));

			configurationRepository.saveAll(List.of(
				new Configuration(null, "COMPANY_NAME", "G Inventory", LocalDateTime.now(), null),
				new Configuration(null, "COMPANY_LOGO", "", LocalDateTime.now(), null),
				new Configuration(null, "ALERT_EMAIL", "admin@mail.com", LocalDateTime.now(), null)
			));

			User defaultUser = new User(
					null,
					"Default 1234",
					"Gerente",
					"gerente",
					passwordEncoder.encode("1234"),
					Status.ACTIVE,
					permissionRepository.findAll(),
					LocalDateTime.now(),
					null
			);
			userRepository.save(defaultUser);

			Set<Long> limitedPermissions = Set.of(1L, 4L, 7L, 10L);
			User limitedUser = new User(
					null,
					"Limitado",
					"Estagiario",
					"limitado",
					passwordEncoder.encode("4321"),
					Status.ACTIVE,
					permissionRepository.findAll().stream().filter( a -> limitedPermissions.contains(a.getId()) ).toList(),
					LocalDateTime.now(),
					null
			);
			userRepository.save(limitedUser);

		};
	}

}
