package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing	//jpa data의 Auditing 기능을 쓰려면 꼭 넣어야 함
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") //스프링부트가 알아서 설정해줌
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean	//jpa date의 createdBy, lastModifiedBy 사용하기 위해 추가
	public AuditorAware<String> auditorProvider(){
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
