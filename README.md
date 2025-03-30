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

GlobalControllerAdvice
```java
```


Desde la capa de Servicio implementaremos la lógica de la aplicación para el cumplimiento de la lógica de negocio. Es aquí donde controlaremos las excepciones que se arrojan.
Un punto importante a considerar es que solo manejara excepciones no verificadas (unchecked). Por lo que excepciones verificadas por el compilador requeriran de un manejo adicional
de try-catch para hacer un rethrow, arrojando excepciones personalizadas o Runtime Exceptions para su manejo desde el Controller Advice.

Service
```java
```

Creamos un archivo de configuración para poblar inicialmente la base de datos

DatabaseConfig
```java
```

## Pruebas

Ahora pondremos a prueba los endpoints creados (capa de controladores) y verificar que la respuesta que obtenemos sea la esperada.

<br>

Obtener todos los registros

![get all videogames image]()

Obtener un registro por su ID

![get video game by id image]()

Crear un uevo registro

![create new videogame image]()

Actualizamos información

![update video game image]()

Eliminar registro por ID

![delete videogame by ID image]()

Generar excepción solicitando un registro inexistente (aplica para GET y DELETE mientras que UPDATE en esta implementación crea un nuevo registro si no existe)

![trigger error image]()


