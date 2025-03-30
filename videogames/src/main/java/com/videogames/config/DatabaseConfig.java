package com.videogames.config;

import com.videogames.model.Videogame;
import com.videogames.repository.VideogameRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private final VideogameRepository videogameRepository;

    @Bean
    CommandLineRunner initDatabase(){
        return args ->{
            Videogame halo = Videogame.builder()
                    .name("Halo Reach")
                    .platform("Xbox 360")
                    .price(899.0)
                    .releaseDate("14/09/2010")
                    .build();

            Videogame ninjaGaiden = Videogame.builder()
                    .name("Ninja Gaiden 2")
                    .platform("Xbox 360")
                    .price(799.0)
                    .releaseDate("03/05/2080")
                    .build();

            Videogame assassins = Videogame.builder()
                    .name("Assassin's Creed 2")
                    .platform("Xbox 360")
                    .price(859.0)
                    .releaseDate("17/11/2009")
                    .build();

            log.info("Preloading: " + videogameRepository.save(halo));
            log.info("Preloading: " + videogameRepository.save(ninjaGaiden));
            log.info("Preloading: " + videogameRepository.save(assassins));
        };
    }
}
