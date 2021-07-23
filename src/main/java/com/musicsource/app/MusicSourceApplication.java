package com.musicsource.app;

import com.musicsource.interfaces.AlbumRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ComponentScan(basePackages = "com.musicsource.*")
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = AlbumRepository.class)
public class MusicSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicSourceApplication.class, args);
    }

}
