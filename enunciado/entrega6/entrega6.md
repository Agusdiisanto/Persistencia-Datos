## Entrega 6 - Firebase

Una vez más, ha sido una semana agotadora, llena de trabajo incesante y sin descanso, en medio de las constantes amenazas que nos plantea este mundo en grave crisis sanitaria. En estos momentos, parece que las esperanzas se desvanecen, y resulta difícil encontrar consuelo.

Con miradas cansadas fijas en las pantallas, continuando arduamente con nuestro labor, el teléfono suena. Al otro lado de la línea, un científico nos informa sobre una nueva tarea y nos solicita que nos acerquemos al laboratorio.

Llegamos exhaustos, desanimados pero decididos a continuar con los requerimientos solicitados de este sombrío juego de simulación. Fuimos recibidos por un científico entusiasta que, con una leve sonrisa triste en su rostro, compartió su idea con nosotros. A pesar de que la situación aún no se puede controlar y no existe una cura o solución definitiva contra los males que nos azotan, este científico, lejos de rendirse, propuso una medida preventiva. Ésta implica que las personas tengan acceso a la información necesaria y actualizada sobre su ubicación y el nivel de peligro al que se exponen, con el objetivo de proteger, de alguna manera, lo que queda de la humanidad.

Poco tiempo permaneció la idea en las cuatro paredes del laboratorio, ya que pronto comenzo a esparcirse como las hojas al viento. Tal fue la fama que tomó que no tardó en llegar a oídos del Comité de Emergencia Sanitaria Internacional, que pronto se puso en contacto con el laboratorio a fin de conocer el estado de nuestros progresos.Se nos propuso entonces el desarrollo de una versión de prueba para presentar ante las autoridades, para que las mismas pudieran interactuar y corroborar su correcto funcionamiento previo a aprobar su financiamiento a escala global. Tan llenos de orgullo como de cansancio continuamos entonces con nuestro proyecto, ávidos de finalmente poder coronar todo nuestro trabajo con el gran reconocimiento internacional, y por que no, un nada despreciable impulso económico. 


<p align="center">
  <img src="notebook-alerta.png" />
</p>


## Funcionalidad


## Suscripción
Con el objetivo de brindar a los ciudadanos información actualizada sobre el estado de las ubicaciones en términos de vectores, hemos implementado un sistema de suscripción. A partir de ahora, los ciudadanos tienen la posibilidad de suscribirse a una ubicación específica y recibir notificaciones en tiempo real sobre cualquier cambio en el estado de alerta de dicha ubicación.

Por ejemplo, si Doña Rosa decide suscribirse a la ubicación "Quilmes" entonces el sistema de suscripción permitirá a Doña Rosa tomar decisiones informadas sobre si es seguro o no salir de su casa, basándose en la información en tiempo real sobre la presencia de vectores y el nivel de riesgo en la ubicación suscrita.
Esta herramienta de suscripción se ha desarrollado con el objetivo de mantener a los ciudadanos informados, brindándoles una mayor capacidad de tomar decisiones conscientes para proteger su salud y seguridad en relación con la propagación de los vectores en las diferentes ubicaciones.

<p align="center">
  <img src="subscribite2.jpg" width="900" height="600" />
</p>

##  Alerta:
Es poco real que las ubicaciones no cuenten con un sistema de alerta basado en la cantidad de casos positivos que alojan. Para hacernos cargo de esta irregularidad, se nos ha encomendado desarrollar un modelo de ubicaciones más realista, que refleje su estado actual.

En principio, cada ubicación debería contar con un sistema de alerta que, según su estado, se muestre de la siguiente manera:

- Alerta verde: Indica una situación segura, con una baja presencia de vectores. No existe un riesgo significativo.

- Alerta amarilla: Señala la presencia de un número limitado de vectores. Aunque el riesgo no es alto, se debe tener precaución adicional.

- Alerta roja: Indica una situación crítica, con más de 15 vectores detectados. Existe un alto peligro de contagio debido a que hay muchos vectores en dicha ubicación y se deben tomar medidas urgentes para minimizar la propagación del virus.

Nuestro objetivo es implementar este modelo de alerta en todas las ubicaciones, para brindar a los usuarios información precisa y oportuna sobre el nivel de riesgo en cada lugar. De esta manera, podrán tomar decisiones informadas para proteger su salud y la de los demás.

##  Cambios en el DTO

- Agregar las implementaciones adecuadas en el DTO ubicación para el funcionamiento correcto ante la solución de esta problemática .

<p align="center">
  <img src="ALERTA-2.png" width="250" height="250" />
</p>

##  Frontend
Desarrollar la capa visual del lado del usuario. Para eso se deberán tener en cuenta las siguientes consideraciones que no afectarán al backend:
- Al llamar al mensaje conectar, si el usuario está suscrito a la ubicación actual, entonces habrá que notificarlo en caso de que el estado de la ubicación recientemente conectada sea alerta amarilla o roja.
- Al llamar el mensaje conectados, si el usuario está suscrito a la ubicación actual, entonces notificará en el caso de que alguna ubicación colindante está en alerta amarilla o roja.
- Al llamar todas las ubicaciones, debemos saber de alguna forma  en que alerta se encuentran.
- Poder suscribirnos a una ubicación, donde podamos recibir notificaciones en cualquier momento.

Opcional:
- Tener un mapa donde se visualicen todas las ubicaciones y un mapa de calor donde marquen en que alerta están las dichas ubicaciones

##  Se pide:
-  Que además de modificar el UbicacionService, también modifiquen el UbicacionController para que se puedan realizar las llamadas al service desde el frontend .
-  Asimismo, desarrollar un frontend con las implementaciones necesarias mencionadas anteriormente.
-  Que agreguen un nuevo mecanismo de persistencia para Ubicación de forma tal que se utilice Firebase.
-  El objetivo de esta entrega es implementar los requerimientos utilizando una base de datos orientada a documentos.
-  Creen test unitarios para cada unidad de código entregada que prueben todas las funcionalidades pedidas, con casos favorables y desfavorables.
-  Provean la implementación a los DTO mencionados.
