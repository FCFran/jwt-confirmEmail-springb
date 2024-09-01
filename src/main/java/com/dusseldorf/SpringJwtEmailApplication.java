package com.dusseldorf;

import com.dusseldorf.model.Role;
import com.dusseldorf.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync //HABILITAR EL ENVIO DE CORREO DE MANERA ASINCRONA
public class SpringJwtEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJwtEmailApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(RoleRepository roleRepository){
		return args -> {
			if (roleRepository.findByName("User").isEmpty()){
				roleRepository.save(Role.builder().name("User").build());
			}
		};
	}

}
