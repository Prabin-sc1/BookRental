package com.bookrental.bookrental;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@OpenAPIDefinition(
        info = @Info(title = "BookRental API",
                version = "1.0.0",
                description = "Rest API for the Book Rental application. It is role based application where there are two " +
                        "types of users, ADMIN, LIBRARIAN. Admin able to perform all functionality related to librarian" +
                        "while librarian able to perform all the functionality related to book, author, category, member, transaction."
        ),
        servers = {
                @Server(
                        url = "http://localhost:9889/book-rental/",
                        description = "Book Rental API : Local ENV"
                ),
                @Server(
                        url = "https://bookrental-production.up.railway.app/",
                        description = "Book Rental API : Prod ENV"
                )
        }
        , security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(name = "bearerAuth", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@SpringBootApplication
@EnableScheduling
public class BookRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookRentalApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer configure() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}
