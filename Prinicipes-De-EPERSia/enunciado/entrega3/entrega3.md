## Entrega 3 - ORM Spring

## Una vez mas con los científicos del laboratorio

Después de cumplir con los últimos requerimientos solicitados en su anterior visita al laboratorio, parten de la empresa donde desarrollan para presentar los progresos al grupo de científicos y recibir indicaciones sobre qué hacer para continuar avanzando con el proyecto.

Dentro del laboratorio, son recibidos en una pequeña y lúgubre sala de proyecciones. Allí, unos pocos científicos observan los avances con caras taciturnas, pero dejando entrever aquí y allá algún gesto de aceptación.

Luego de la presentación, entra en la habitación el amable científico con ojos cansados y un guante negro en su brazo derecho que habían visto antes, indicándoles ahora que lo sigan para mostrarles de qué se tratará la próxima iteración sobre la simulación.

Sin palabras de por medio, los lleva a un largo pasillo poco iluminado donde pueden verse varias jaulas de cristal dispuestas muy ordenadamente una detrás de la otra, pero separadas por muros de hormigón. Dentro de las terribles jaulas, pueden divisar difícilmente lo que parecerían ser siluetas humanas bañadas por una luz mortecina que las hace parecer como si estuvieran suspendidas en el tiempo y en el espacio.

El científico, sin dar muchos rodeos, les indica que estos son especímenes enfermos con uno de los viruses bio-mecánicos que están afectando el mundo.

Desde su lugar y ya acostumbrados a la falta de luz, se toman unos segundos para observar más de cerca a los especímenes. Ven como uno teníe enormes garras que parecerían estar hechas de metal. Otro teníe ojos rojos brillantes que parecían emitir un resplandor antinatural. Uno más atrás se lo ve con una piel escamosa y brillante, casi como si estuviera cubierta de metal líquido.

Sin esperar a que terminen de examinar los especímenes, el científico les informa que de esto se tratará la próxima iteración en nuestra simulación.

## Funcionalidad

## Mutaciones

<p align="center">
  <img src="mutacion.png" />
</p>

Hasta el momento, las especies no eran más que una iteración concreta de un patógeno. No había mucha diferencia entre una y otra. A partir de ahora, nos interesará diferenciar a las especies entre sí por las mutaciones que pueden causar en los vectores que enferman.

Una mutación es una nueva característica que los vectores podrán obtener a medida que su enfermedad empeora y hará que ciertas reglas de nuestra simulación cambien dependiendo de la mutación generada.

Cada vez que un vector contagie exitosamente a otro vector, se deberá analizar si la especie contagiada genera una mutación en el vector que contagio la enfermedad. Para esto, se deberá resolver la **Capacidad de biomecanización** de esa especie como porcentaje de éxito.

Ejemplo:

Un vector humano, llamémosle **John**, contagia con **Meca-Viruela** a un vector insecto. **Meca-Viruela** es un virus cuyo atributo de **Capacidad de biomecanización** es igual a 30. Eso significará que en el momento en el que **John** consiga contagiar la **Meca-Viruela**, tendrá un 30% de contraer una mutación al azar de la lista de mutaciones que tenga **Meca-Viruela**.

### Tipos de mutaciones 

Por el momento nos interesara solo simular dos tipos de mutaciones:

## Supresion biomecanica

Esta mutacion permite a la enfermedad defenderse y eliminar a otras enfermedades competidoras.

Viene acompañada de un numero del 1 al 100 que representa la potencia de esta mutacion.
Cuando un vector muta con supresion biomecanica, se eliminaran todas las otras especies que el vector tenga que posean una **defensa contra otros micro-organismos** menor al numero de esta mutacion. 
Asi mismo, la mutacion no permitira que otra enfermedad con menor capacidad de defensa afecte a su vector.

Ejemplo:

**John** tiene tres enfermedades: **CromaGripe**, **Meca-Viruela** y **RoboRabia**.
**John** contagia **Meca-Viruela** y muta con **Supresion biomecanica**, una de las mutaciones disponibles en **Meca-Viruela**. Esta mutacion tiene una potencia de 35.
En ese momento entonces se fija: **CromaGripe** y **RoboRabia** tienen una defensa contra micro-organismos igual o superior a 35?
Digamos que **CromaGripe** tiene 30 de defensa, y **Roborabia** 35.
Como **CromaGripe** tiene menos defensa que el poder de la mutacion de **Supresion biomecanica**, es eliminada y **John** ya no se encuentra enfermo por **CromaGripe**.
Como **RoboRabia** tiene 35 de defensa, no pasa nada y **John** sigue enfermo.

Luego de resolver la mutacion, entonces **John** solo tendra las enfermedades: **Meca-Viruela**, y **RoboRabia**.

Digamos ahora que otro vector intenta contagiar a **John** con una nueva enfermedad, **Circuitomialgia**, la cual tiene una defensa de 15. 
**John** no podra ser contagiado por esta enfermedad, ya que su mutacion de supresion biomecanica lo impedira.


## Bioalteracion genetica

Esta mutacion viene acompañada de un **tipo de vector**: Humano, insecto o animal.

Cuando se contrae, a partir de ese momento le permite al vector contagiar la enfermedad que genero la mutacion al tipo de vector asociado sin importar las restircciones previas.

Ejemplo:

**John** es un vector de tipo humano, no puede contagiar animales, pero contrae **RoboRabia**, la contagia, y muta contrayendo la mutacion de **Bioalteracion genetica** de tipo animal.
A partir de ese momento, John podra contagiar animales con **RoboRabia**.

**Recuerden**: Esta nueva regla solo se aplica para la enfermedad que genero la mutacion.


### Aclaraciones:

- La especie puede crearse y mantenerse sin ninguna mutacion disponible, en ese caso, por mas que el porcentaje de generar mutacion es exitoso, no generara ninguna nueva mutacion en el vector.
- Un vector puede tener varias mutaciones repetidas siempre y cuando estas se correspondan a distintas enfermedades que contrajo.

Ejemplo:

**John** contrajo la mutacion de **Supresion biomecanica** de **Meca-Viruela**. Esa mutacion tenia una potencia de 5.

Luego es enfermdado por otra especie, **RoboRabia**, con una **Defensa contra bio-organismos** de 10. Como esa proteccion es mayor que la potencia de la mutacion que tenia **John**, la **RoboRabia** no es eliminada.

Mas adelante, **John** contagia la **RoboRabia** y esa misma enfermedad le vuelve a generar la misma mutacion de **Supresion biomecanica** a partir de la RoboRabia, e intentara eliminar a las otras enfermedades que puedan llegar o esten afectando a **John**.

- Las mutaciones no se eliminan si la especie es eliminada del vector. 

Digamos que la **RoboRabia** que afecta a John, al generar la mutacion de **Supresion biomecanica** con una fuerza de 20, elimina a la **Meca-Viruela** que tenia de su organismo.
La mutacion que habia generado la **Meca-Viruela** sigue estando y sigue teniendose en cuenta y aplicandose. 
Esto nos importa por que en un futuro puede que nos sea importante analizar la cantidad de mutaciones que lleva un organismo.

## DTO

Una vez terminadas las charlas con los científicos, presto, vuelven a su trabajo de investigación dejándolos parados en una sala de espera con una cafetera y unas revistas "Gente" para leer. Mientras se preguntaban si se podían retirar ya o no, ven como el líder técnico se acerca y les comenta sobre grandes avances que estuvo realizando el equipo de Frontend los cuales ya poseen una interfaz semi-funcional lista como prototipo, y nos comenta que nos van a presentar unos nuevos DTOs (Data Transfer Object) para que implementemos.
Sera nuestro trabajo implementar todos los DTOs que se nos den.

## Servicios

Se pide que se agregue el siguiente servicio:

### MutacionService

- agregarMutacion(especieId:Long, Mutacion:mutacion) - Agrega la mutacion a la lista de posibles mutaciones de la especie.

### Integracion a Spring
Además, nos interesara:

Pasar la transaccionalidad de todos los servicios a Spring
Que los DAOs implementen la interfaz de Spring 'CRUDRepository' en lugar del DAO genérico previsto en el TP anterior.
Crear controllers REST para todos los servicios implementados hasta ahora y los implementados en este TP también.
Se implementen los DTO que les daremos para generar un contrato de comunicación con el front.

### Se pide:

- Que provean implementaciones para las interfaces descriptas anteriormente.

- Creen test que prueben todas las funcionalidades pedidas, con casos favorables y desfavorables.

- Provean la implementacion a los DTO mencionados.