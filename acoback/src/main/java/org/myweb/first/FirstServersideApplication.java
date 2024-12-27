package org.myweb.first;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//excluede 속성 추가함 : 프로젝트 구동시 Security 의 Configuration 클래스도 같이 구동
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class FirstServersideApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirstServersideApplication.class, args);
	}

}
