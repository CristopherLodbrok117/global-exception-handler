package com.videogames.controller;

import com.videogames.exception.VideogameNotFoundException;
import com.videogames.model.Videogame;
import com.videogames.service.VideogameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/videogames")
public class VideogameController {

    private final VideogameService videogameService;

    public VideogameController(VideogameService videogameService){
        this.videogameService = videogameService;
    }

    @GetMapping
    public ResponseEntity<List<Videogame>> getAll(){
        return ResponseEntity.ok(videogameService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Videogame> getById(@PathVariable Long id){
        return ResponseEntity.ok(videogameService.findById(id));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<Videogame>> findByName(@RequestParam("name") String name){

        return ResponseEntity.ok(videogameService.findByNamePattern(name));
    }

    @PostMapping
    public ResponseEntity<Videogame> create(@RequestBody Videogame videogame,
                                            UriComponentsBuilder ucb){
        Videogame newVideogame = videogameService.save(videogame);

        URI savedVideogame = ucb
                .path("/api/v1/videogames/{id}")
                .buildAndExpand(newVideogame.getId())
                .toUri();

        return ResponseEntity.created(savedVideogame).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Videogame> update(@RequestBody Videogame newVideogame,
                                            @PathVariable Long id){

        return ResponseEntity.ok(videogameService.update(newVideogame, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        videogameService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
