package com.videogames.service;

import com.videogames.exception.VideogameNotFoundException;
import com.videogames.model.Videogame;
import com.videogames.repository.VideogameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideogameService {
    private final VideogameRepository videogameRepository;

    public VideogameService(VideogameRepository videogameRepository){
        this.videogameRepository = videogameRepository;
    }

    public List<Videogame> findAll(){

        return videogameRepository.findAll();
    }

    public Videogame findById(Long id){

        return videogameRepository.findById(id)
                .orElseThrow(() -> new VideogameNotFoundException(id));
    }

    public List<Videogame> findByNamePattern(String name){

        return videogameRepository.findByNameContains(name);
    }

    public Videogame save(Videogame videogame){
        return videogameRepository.save(videogame);
    }

    public Videogame update(Videogame newVideogame, Long id){

        return videogameRepository.findById(id)
                .map(videogame -> {
                    videogame.setName(newVideogame.getName());
                    videogame.setPlatform(newVideogame.getPlatform());
                    videogame.setPrice(newVideogame.getPrice());

                    return videogameRepository.save(videogame);
                })
                .orElseGet(() -> {
                    return videogameRepository.save(newVideogame);
                });
    }

    public void delete(Long videogameId){
        if(videogameRepository.findById(videogameId).isEmpty()){
            throw new VideogameNotFoundException(videogameId);
        }

        videogameRepository.deleteById(videogameId);
    }
}
