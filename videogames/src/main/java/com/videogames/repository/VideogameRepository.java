package com.videogames.repository;

import com.videogames.model.Videogame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideogameRepository extends JpaRepository<Videogame, Long> {

    List<Videogame> findByNameContains(String name);
}
