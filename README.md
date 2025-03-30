# Introducción

Spring cuenta con distintos enfoques y herramientas necesarias para el manejo de excepciones, además del conocido try-catch. Antes de introducir las anotaciones que veremos en este repositorio, el enfoque consistía en manejar las excepciones dentro de la capa del controlador. Es decir, cada clase anotada con *@Controller* podía manejar internamente sus excepciones. Para ello se incluyen métodos con la anotación *@ExceptionHandler(YourException.class)* y dentro del método se implementa el tratamiento a la excepción capturada. Este primer enfoque mejora bastante la legibilidad de nuestro código, pues un solo método evitaba la repetición de bloques try-catch para el mismo tipo de excepción.

Sin embargo, dicha técnica no era totalmente óptima, ya que los mismos tipos de excepciones se pueden arrojar desde más de un controlador, lo que implicaba escribir los mismos Exception Handlers una y otra vez en cada controlador. Un desarrollador pensó que sería fantástico si pudieran crear un único objeto centralizado para las excepciones, de modo que cada vez que cualquier controlador lance una excepción, sea capturada y manejada por dicho objeto. *@ControllerAdvice* entró en juego.
<br><br>

![Centralized exception handler](https://github.com/CristopherLodbrok117/controller-advice-example/blob/7dd6f78a14adfae5aab5d13c17aff356e40cc435/controller_advice.png)

*_Centralized Exception Handler_*
<br><br>
# @RestControllerAdvice vs @ControllerAdvice

Las anotaciones *@ControllerAdvice* y *@RestControllerAdvice* permiten utilizar las mismas técnicas de manejo de excepciones con un alcance global en nuestra aplicación.

*@ControllerAdvice* se introdujo en Spring 3.2 y es un *@Component* especializado que nos permite declarar métodos *@ExceptionHandler, @InitBinder o @ModelAttribute* para compartirlos entre las clases *@Controller*.

Dicho de otra manera, podemos tratarlo como un interceptor controlado por anotaciones, cuyos métodos se aplicarán a toda la aplicación (no sólo a un controlador individual). Aunque su alcance es global, también podemos definir a qué controladores nuestro Controller Advice estará manejando sus excepciones. Para ello escribiremos los packages de cada controlador en la misma anotación: *@ControllerAdvice("org.example.controllers")*

Por otro lado, *@RestControllerAdvice* combina las anotaciones *@ControllerAdvice* y *@ResponseBody*.
La ventaja de incluir *@ResponseBody*  es que el valor de retorno de un método será directamente inyectado al response body, sin necesidad de utilizar ResponseEntity<T> (o HttpEntity<T>). A pesar que podemos utilizar tanto una como otra anotación, *@RestControllerAdvice* es más adecuada para API REST.
<br><br>
Desde la documentación oficial podemos ver mas detalles de la anotación: [Annotation Interface RestControllerAdvice](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestControllerAdvice.html) 


## Creación de la API

<br>

Dependencias:
- Spring Web
- Spring Data JPA 
- MySQL Driver
- Lombok

Seguiremos los principios REST para establecer una arquitectura tradicional de nuestros componentes:
- Entidades/modelo
- Repositorios
- Servicios
- Controladores
- Excepciones
- Configuración (las clases anotadas con @Configuration son fuentes de beans para la aplicación)

## Manejo de excepciones

<br>

Con RestControllerAdvice garantizamos el manejo centralizado de excepciones desde un solo punto. Opcionalmente un controller advice puede manejar las excepciones de toda la aplicación
o crear uno por cada RestController.

### GlobalControllerAdvice
```java
package com.videogames.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(VideogameNotFoundException.class)
    ResponseEntity<Map<String, String>> videogameNotFoundHandler(VideogameNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}

```

<br>

Desde la capa de Servicio implementaremos la lógica de la aplicación para el cumplimiento de la lógica de negocio. Es aquí donde controlaremos las excepciones que se arrojan.
Un punto importante a considerar es que solo manejara excepciones no verificadas (unchecked). Por lo que excepciones verificadas por el compilador requeriran de un manejo adicional
de try-catch para hacer un rethrow, arrojando excepciones personalizadas o Runtime Exceptions para su manejo desde el Controller Advice.

### Service
```java
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

```

<br>

Creamos un archivo de configuración para poblar inicialmente la base de datos

### DatabaseConfig
```java
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

```

## Pruebas

Ahora pondremos a prueba los endpoints creados (capa de controladores) y verificar que la respuesta que obtenemos sea la esperada.

<br>

Obtener todos los registros

![get all videogames image](https://github.com/CristopherLodbrok117/global-exception-handler/blob/7a8a0738c69aefe82c21f557602ccd5641d36146/screenshots/00%20-%20getAll.png)

<br>

Obtener un registro por su ID

![get video game by id image](https://github.com/CristopherLodbrok117/global-exception-handler/blob/77a0b4d2d14a9b4ca1d8aa07ed456b6486423d6b/screenshots/01%20-%20getOne.png)

<br>

Crear un uevo registro

![create new videogame image](https://github.com/CristopherLodbrok117/global-exception-handler/blob/77a0b4d2d14a9b4ca1d8aa07ed456b6486423d6b/screenshots/03%20-%20create.png)

<br>

Actualizamos información

![update video game image](https://github.com/CristopherLodbrok117/global-exception-handler/blob/77a0b4d2d14a9b4ca1d8aa07ed456b6486423d6b/screenshots/04%20-%20update.png)

<br>

Eliminar registro por ID

![delete videogame by ID image](https://github.com/CristopherLodbrok117/global-exception-handler/blob/77a0b4d2d14a9b4ca1d8aa07ed456b6486423d6b/screenshots/05%20-%20delete.png)

<br>

Generar excepción solicitando un registro inexistente (aplica para GET y DELETE mientras que UPDATE en esta implementación crea un nuevo registro si no existe)

![trigger error image](https://github.com/CristopherLodbrok117/global-exception-handler/blob/77a0b4d2d14a9b4ca1d8aa07ed456b6486423d6b/screenshots/06%20-%20exceptionHandler.png)


