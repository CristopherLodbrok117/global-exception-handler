package com.videogames.exception;

public class VideogameNotFoundException extends RuntimeException{

    public VideogameNotFoundException(Long id){
        super("No existe ningún videojuego con id: " + id);
    }
}
