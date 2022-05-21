/*
Trans-AlfaMorse-Late v1.2
Grupo M1 - SOA
*/

//----------------------------------------------------------
// Habilitacion de debug para la impresion por el puerto serial

#define SERIAL_DEBUG_ENABLED 1

#if SERIAL_DEBUG_ENABLED
#define DebugPrint(str)  \
  {                      \
    Serial.println(str); \
  }
#else
#define DebugPrint(str)
#endif

#define DebugPrintEstado(est, evt)                           \
  {                                                               \
    String str;                                                    \
    str = "-----------------------------------------------------"; \
    DebugPrint(str);                                               \
    str = "EST-> [" + est + "]: " + "EVT-> [" + evt + "].";        \
    DebugPrint(str);                                               \
    str = "-----------------------------------------------------"; \
    DebugPrint(str);                                               \
  }

//----------------------------------------------------------
// Bibliotecas

#include <LiquidCrystal.h>
#include <string.h>

//----------------------------------------------------------
// Definición de constantes.

// Display
#define PIN_ENABLE_LCD                         11
#define PIN_RS_LCD                             12
#define PIN_DB7_LCD                             4
#define PIN_DB6_LCD                             5
#define PIN_DB5_LCD                             6
#define PIN_DB4_LCD                             7
#define DATOS_BUS_LCD                          16
#define LINEA_0_LCD                             0
#define LINEA_1_LCD                             1

#define BRILLO_BAJO                             0
#define BRILLO_MEDIO                           64
#define BRILLO_ALTO                           128
#define UMBRAL_DISTANCIA_MIN                   50
#define UMBRAL_DISTANCIA_MAX                  140
#define COLORES_RGB                             3
#define DIV_PWM                                 8

// Pines
#define PIN_ACT_NEOPIXEL4                       8
#define PIN_ACT_LED                             9
#define PIN_ACT_BRILLOLCD                      10
#define PIN_ACT_BUZZER                         13
#define PIN_SENSOR_POTENCIOMETRO               A0
#define PIN_SENSOR_DISTANCIA                   A1
#define PIN_SENSOR_PULSADOR                     2
#define PIN_SENSOR_MODO                         3

// Sensores
#define SENSOR_PULSADOR                         0
#define SENSOR_MODO                             1
#define SENSOR_DISTANCIA                        2
#define SENSOR_POTENCIOMETRO                    3
#define MAX_CANT_SENSORES                       4
#define ESTADO_SENSOR_OK                      108

// Serial consola
#define SERIAL_SPEED                         9600

// Modos
#define MODO_MORSE                             98
#define MODO_ALFA                              99

// Tiempos
#define TIEMPO_MAX_PUNTO                      400
#define TIEMPO_MAX_RAYA                       800
#define TIME_FOR_1CM                           29

#define TAM_BUFFER_MORSE                        6
#define TAM_BUFFER_LCD                         17

// Defino las notas que sonaran en el buzzer
#define NOTE_C4                               262
#define NOTE_G3                               196
#define NOTE_A3                               220
#define NOTE_B3                               247
#define NOTE_ONE_SECOND                      1000
#define DURACION_NOTA                           8

#define BUZZER_VALOR_MIN                       50
#define TAM_MAX_POTENCIOMETRO                1023
#define AJUSTE_FRECUENCIA                     450

//----------------------------------------------------------
// Variables Globales.
int brillo_actual;
int valor_pwm;
float valor_potenciometro;
int modo;
long delta;
long tiempo_previo;
bool interrupcion;
char morse_buffer[TAM_BUFFER_MORSE];
char lcd_buffer_superior[TAM_BUFFER_LCD];
char lcd_buffer_inferior[TAM_BUFFER_LCD];
char mensaje_error[TAM_BUFFER_LCD];
int elementos_lcd;
char *cadena_morse;
String message;
int tamanio_entrada;
int caracter_numero;
int led_numero;
char buffer_lectura;
float brillo_led;
char *trad[] = {".-","-...","-.-.","-..",".","..-.","--.",
"....","..",".---","-.-",".-..","--","-.","---",".--.","--.-",
".-.","...","-","..-","...-",".--","-..-","-.--","--.."};

//----------------------------------------------------------
// Funciones auxiliares

// Funcion para encontrar el caracter morse a partir de simbolos morse
char *encode(const char alpha)
{
  char* retorno;
  if (isalpha(alpha))
  {
    retorno = trad[(int)alpha - (int)'a'];
  }
  else
  {
    retorno = "";
  }
  return retorno;
}

// Funcion para decodificar una entrada de caracter morse a caracter alfabetico
char decode(const char *key)
{
  char retorno;

  if (strcmp(key, ".-") == 0)
    retorno = 'a';
  else if (strcmp(key, "-...") == 0)
    retorno = 'b';
  else if (strcmp(key, "-.-.") == 0)
    retorno = 'c';
  else if (strcmp(key, "-..") == 0)
    retorno = 'd';
  else if (strcmp(key, ".") == 0)
    retorno = 'e';
  else if (strcmp(key, "..-.") == 0)
    retorno = 'f';
  else if (strcmp(key, "--.") == 0)
    retorno = 'g';
  else if (strcmp(key, "....") == 0)
    retorno = 'h';
  else if (strcmp(key, "..") == 0)
    retorno = 'i';
  else if (strcmp(key, ".---") == 0)
    retorno = 'j';
  else if (strcmp(key, "-.-") == 0)
    retorno = 'k';
  else if (strcmp(key, ".-..") == 0)
    retorno = 'l';
  else if (strcmp(key, "--") == 0)
    retorno = 'm';
  else if (strcmp(key, "-.") == 0)
    retorno = 'n';
  else if (strcmp(key, "---") == 0)
    retorno = 'o';
  else if (strcmp(key, ".--.") == 0)
    retorno = 'p';
  else if (strcmp(key, "--.-") == 0)
    retorno = 'q';
  else if (strcmp(key, ".-.") == 0)
    retorno = 'r';
  else if (strcmp(key, "...") == 0)
    retorno = 's';
  else if (strcmp(key, "-") == 0)
    retorno = 't';
  else if (strcmp(key, "..-") == 0)
    retorno = 'u';
  else if (strcmp(key, "...-") == 0)
    retorno = 'v';
  else if (strcmp(key, ".--") == 0)
    retorno = 'w';
  else if (strcmp(key, "-..-") == 0)
    retorno = 'x';
  else if (strcmp(key, "-.--") == 0)
    retorno = 'y';
  else if (strcmp(key, "--..") == 0)
    retorno = 'z';
  else
    retorno = '\0';

  return retorno;
}

// Limpieza de serial para evitar falsas lecturas
void serial_flush()
{
  if(Serial.read()>0)
  {
    Serial.flush();
    Serial.end();
    Serial.begin(SERIAL_SPEED);
    Serial.flush();
  }
}

//----------------------------------------------------------

// Display
LiquidCrystal lcd(PIN_RS_LCD, PIN_ENABLE_LCD, PIN_DB4_LCD, PIN_DB5_LCD, PIN_DB6_LCD, PIN_DB7_LCD);

// LED Neopixel 4
#include <Adafruit_NeoPixel.h>
#define RED_COLOR 255, 0, 0
#define BLUE_COLOR 0, 0, 255
#define OFF_COLOR 0, 0, 0

#define CANTIDAD_LEDS_NPX 18
Adafruit_NeoPixel barraNeoPX = Adafruit_NeoPixel(CANTIDAD_LEDS_NPX, PIN_ACT_NEOPIXEL4, NEO_GRB + NEO_KHZ800);

//----------------------------------------------------------
struct stSensor
{
  int pin;
  int estado;
  long valor_actual;
  long valor_previo;
};
stSensor sensores[MAX_CANT_SENSORES];
//----------------------------------------------------------

//----------------------------------------------------------
// Maquina de estados

#define MAX_ESTADOS 5
#define MAX_EVENTOS 10

enum states
{
  EST_INICIO,
  EST_INACTIVO,
  EST_TRADUCIENDO_MORSE,
  EST_TRADUCIENDO_ALFA,
  EST_ERROR
} estado_actual;
String estados[MAX_ESTADOS] = {"EST_INICIO", "EST_INACTIVO", "EST_TRADUCIENDO_MORSE", "EST_TRADUCIENDO_ALFA", "EST_ERROR"};

enum events
{
  EV_CONT,
  EV_PULSADO,
  EV_SOLTADO,
  EV_EMP_ALFA,
  EV_MOSTRAR,
  EV_ACTUALIZAR_LCD,
  EV_TECLADO,
  EV_MODO,
  EV_LEDPWM,
  EV_ERROR,
} nuevo_evento;
String eventos[MAX_EVENTOS] = {"EV_CONT", "EV_PULSADO", "EV_SOLTADO", "EV_EMP_ALFA", "EV_MOSTRAR", "EV_ACTUALIZAR_LCD", "EV_TECLADO", "EV_MODO", "EV_LEDPWM", "EV_ERROR"};

typedef void (*transition)();

transition tabla_de_estados[MAX_ESTADOS][MAX_EVENTOS] = {
    {init_              , error             , error             , error             , error         , actualizar_brillo_lcd , error         , cambiar_modo    , actualizar_led_pwm      , error     },  // EST_INICIO
    {reset_sensors      , traduccion_morse  , obtener_caracter  , traduccion_alfa   , mostrar_alfa  , actualizar_brillo_lcd , leer_teclado  , cambiar_modo    , actualizar_led_pwm      , error     },  // EST_INACTIVO
    {reset_sensors      , traduccion_morse  , error             , error             , mostrar_alfa  , actualizar_brillo_lcd , leer_teclado  , cambiar_modo    , actualizar_led_pwm      , error     },  // EST_TRADUCIENDO_MORSE
    {reset_sensors      , error             , error             , error             , mostrar_morse , actualizar_brillo_lcd , leer_teclado  , cambiar_modo    , actualizar_led_pwm      , error     },  // EST_TRADUCIENDO_ALFA
    {reset_sensors      , error             , error             , error             , error         , error                 , error         , cambiar_modo    , error                   , error     }   // EST_ERROR

    // EV_CONT        , EV_PULSADO        , EV_SOLTADO        , EV_EMP_ALFA       , EV_MOSTRAR    , EV_ACTUALIZAR_LCD     , EV_TECLADO      , EV_MODO         , EV_LEDPWM         , EV_ERROR
};

//----------------------------------------------------------
// Funciones Maquina de Estados

void init_()
{
  DebugPrintEstado(estados[estado_actual], eventos[nuevo_evento]);
  // Sonido de bienvenida.
  sonar_buzzer(NOTE_G3);
  estado_actual = EST_INACTIVO;
}

void error()
{
  estado_actual = EST_ERROR;

  // Mensaje de error en display.
  lcd.clear();
  lcd.setCursor(0, LINEA_0_LCD);
  lcd.print(mensaje_error);
  lcd.setCursor(0, LINEA_1_LCD);


  // Sueno alarma de error y reseteo buffers
  sonar_buzzer(NOTE_A3);
  morse_buffer[0] = '\0';
  serial_flush();

  // Evento para cambiar de estado
  nuevo_evento = EV_CONT;
  DebugPrintEstado(estados[estado_actual], eventos[nuevo_evento]);
  interrupcion = true;
}

void none()
{
}

void traduccion_morse()
{
  tiempo_previo = millis();
  // Actualizo LCD
  actualizar_lcd();
}

void obtener_caracter()
{
  estado_actual = EST_TRADUCIENDO_MORSE;
  // Calculo el tiempo que estuvo pulsado el boton.
  delta = millis() - tiempo_previo;
  tiempo_previo = 0;

  // Chequeo que el tamaño de buffer no se exceda.
  if (strlen(morse_buffer) >= TAM_BUFFER_MORSE - 1 && delta < TIEMPO_MAX_RAYA)
  {
    strcpy(mensaje_error, "ERR:BUFFER LLENO");
    nuevo_evento = EV_ERROR;
    interrupcion = true;
    return;
  }

  // En base a las franjas de tiempo definidas y el tiempo pulsado,
  // escribo un punto, raya o fin de caracter.
  if (delta < TIEMPO_MAX_PUNTO)
  {
    mostrar_morse_por_ledNeoPX('.');
    strcat(morse_buffer, ".");
    Serial.print("Usted ingreso: '.'\n");
    Serial.print("Buffer parcial: ");
    Serial.println(morse_buffer);
  }
  else if (delta < TIEMPO_MAX_RAYA)
  {
    mostrar_morse_por_ledNeoPX('-');
    strcat(morse_buffer, "-");
    Serial.print("Usted ingreso: '-'\n");
    Serial.print("Buffer parcial: ");
    Serial.println(morse_buffer);
  }
  if (delta > TIEMPO_MAX_RAYA)
  {
    barraNeoPX.clear();
    led_numero = 0;
    Serial.println("#Obteniendo Caracter#");
    // Si llego al fin de caracter, cambio el evento para traducir.
    nuevo_evento = EV_MOSTRAR;
    interrupcion = true;
  }
}

void mostrar_alfa()
{

  estado_actual = EST_INACTIVO;
  // Busco traduccion
  char cad = decode(morse_buffer);

  // Si encontre traduccion muestro por display
  if (cad != '\0')
  {
    if (strlen(lcd_buffer_inferior) < DATOS_BUS_LCD)
      strncat(lcd_buffer_inferior, &cad, 1);
    else
    {
      strcpy(lcd_buffer_inferior, &lcd_buffer_inferior[1]);
      strncat(lcd_buffer_inferior, &cad, 1);
    }
    strcpy(lcd_buffer_superior, "Trad. Morse:");
    actualizar_lcd();
  }
  else
  {
    // Si NO encontre traduccion muestro error por display
    strcpy(mensaje_error, "NOT FOUND MORSE!");
    nuevo_evento = EV_ERROR;
    interrupcion = true;
    return;
  }
  // Indicar fin de traduccion
  encender_led_testigo();
  // Reseteo buffer
  morse_buffer[0] = '\0';
}

void traduccion_alfa()
{
  estado_actual = EST_TRADUCIENDO_ALFA;
  // Traduccion de alfabetico a morse.
  // Busco traduccion.
  strcpy(morse_buffer, encode(tolower(message[caracter_numero])));
  if (strcmp(morse_buffer, ""))
  {
    nuevo_evento = EV_MOSTRAR;
    interrupcion = true;
  }
  else
  {
    // Si no encontre traduccion, muestro error.
    strcpy(mensaje_error, "NOT FOUND ALFA!!");
    nuevo_evento = EV_ERROR;
    interrupcion = true;
    return;
  }
}

void mostrar_morse()
{
  estado_actual = EST_INACTIVO;
  // Si encontré traduccion, muestro por led y lcd.
  Serial.println("La traduccion es: "); Serial.println(morse_buffer);
  //mostrar_morse_por_ledNeoPX(morse_buffer);
  if (strlen(lcd_buffer_inferior) + strlen(morse_buffer) + 1 <= DATOS_BUS_LCD)
  {
    strncat(lcd_buffer_inferior, morse_buffer, strlen(morse_buffer));
    strcat(lcd_buffer_inferior, "|");
  }
  else
  {
    strcpy(lcd_buffer_inferior, &lcd_buffer_inferior[strlen(morse_buffer) + 1]);
    strncat(lcd_buffer_inferior, morse_buffer, strlen(morse_buffer));
    strcat(lcd_buffer_inferior, "|");
  }
  strcpy(lcd_buffer_superior, "Trad. Alfab:");

  // Actualizar display e indicar fin de traduccion
  actualizar_lcd();
  encender_led_testigo();
}

void leer_teclado()
{
  // Validar entrada segun modo
  message = "";
  if (modo == MODO_ALFA)
  {
  // Si es modo alfa leo un caracter por teclado
  if ((buffer_lectura = Serial.read()) > 0)
  {
    message.concat(buffer_lectura);
    Serial.println(message);
    interrupcion = true;
    nuevo_evento = EV_EMP_ALFA;
  }
  }
  // Si es modo morse leo un caracter morse completo como maximo(5)
  else if ((message = Serial.readString()) != "")
  {
    (message.substring(0, TAM_BUFFER_MORSE - 1)).toCharArray(morse_buffer, TAM_BUFFER_MORSE);
    Serial.print("Se ingreso: ");
    interrupcion = true;
    barraNeoPX.clear();
    led_numero = 0;
    serial_flush();
    Serial.println(morse_buffer);
    nuevo_evento = EV_MOSTRAR;
  }
}

void cambiar_modo()
{
  // Cambio de modo, y escribo en el display modo actual
  message = "";
  morse_buffer[0] = '\0';
  serial_flush();
  estado_actual = EST_INACTIVO;
  interrupcion =false;

  // Preparar mensaje para buffer segun modo de traduccion
  if (modo == MODO_ALFA)
  {
    strcpy(lcd_buffer_superior, "Trad. Morse:");
    strcpy(lcd_buffer_inferior, "\0");
    actualizar_lcd();
    modo = MODO_MORSE;
  }
  else
  {
    strcpy(lcd_buffer_superior, "Trad. Alfab:");
    strcpy(lcd_buffer_inferior, "\0");
    actualizar_lcd();
    modo = MODO_ALFA;
  }
}

void reset_sensors()
{
  // Vuelvo a estado idle y voy apagando el led.
  if (brillo_led > 0)
  {
    brillo_led = brillo_led-valor_pwm;
  }
  else
  {
    brillo_led = 0;
  }
  analogWrite(PIN_ACT_LED, brillo_led);
  estado_actual = EST_INACTIVO;
}

//----------------------------------------------------------
// Funciones actuadores

void actualizar_led_pwm()
{
  // Actualiza el valor de encendido de testigo led pwm.
  valor_pwm = valor_potenciometro / DIV_PWM;
  analogWrite(PIN_ACT_LED, valor_pwm);
}

void encender_led_testigo()
{
  // Encendido de testigo led pwm - fin de traduccion.
  brillo_led = HIGH;
  analogWrite(PIN_ACT_LED, brillo_led);
}

void mostrar_morse_por_ledNeoPX(const char morse)
{
  size_t led_act = led_numero > CANTIDAD_LEDS_NPX / COLORES_RGB ? 0 : led_numero;

  // Muestro Simbolo Morse por NeoPixel
  mostrarSimboloMorseNeoPX(led_act, morse);

  // Limpio memoria actual
  led_numero++;
}

void mostrarSimboloMorseNeoPX(size_t led, char simbolo)
{
  // Valido cantidad led
  if (led > CANTIDAD_LEDS_NPX)
  {
    nuevo_evento = EV_ERROR;
    return;
  }

  // Muestro simbolo en neopixel
  if (simbolo == '.')
  {
    barraNeoPX.setPixelColor(led, barraNeoPX.Color(BLUE_COLOR));
  }
  else if (simbolo == '-')
  {
    barraNeoPX.setPixelColor(led, barraNeoPX.Color(RED_COLOR));
  }
  else
  {
    barraNeoPX.setPixelColor(led, barraNeoPX.Color(OFF_COLOR));
  }
  barraNeoPX.show();
}

void actualizar_brillo_lcd()
{
  // Cambio el nivel de brillo de la pantalla
  analogWrite(PIN_ACT_BRILLOLCD, brillo_actual);

  if (estado_actual == EST_TRADUCIENDO_ALFA)
  {
    // Esto es para que si cambio el brillo mientras se traduce alfa,
    // no se pierda el evento de mostrar alfa.
    nuevo_evento = EV_MOSTRAR;
  }
}

void actualizar_lcd()
{
  // Funcion para actualizar el contenido del display.
  lcd.clear();
  lcd.setCursor(0, LINEA_0_LCD);
  lcd.print(lcd_buffer_superior);
  lcd.setCursor(0, LINEA_1_LCD);

  strcpy(lcd_buffer_inferior, lcd_buffer_inferior);

  lcd.print(lcd_buffer_inferior);
}

void sonar_buzzer(int nota)
{
  // Sueno una nota por el buzzer
  tone(PIN_ACT_BUZZER, nota, DURACION_NOTA);
}

//----------------------------------------------------------
// Funciones sensores

bool leer_sensor_distancia()
{
  long tiempo_vta, distcm;

  // Emito onda ultrasonido
  pinMode(PIN_SENSOR_DISTANCIA, OUTPUT);
  digitalWrite(PIN_SENSOR_DISTANCIA, LOW);
  delayMicroseconds(2);
  digitalWrite(PIN_SENSOR_DISTANCIA, HIGH);
  delayMicroseconds(5);
  digitalWrite(PIN_SENSOR_DISTANCIA, LOW);

  // Detecto el regreso de la onda
  pinMode(PIN_SENSOR_DISTANCIA, INPUT);
  tiempo_vta = pulseIn(PIN_SENSOR_DISTANCIA, HIGH);

  // Se divide la duracion de onda leida por la distancia recorrida de la
  // velocidad del sonido por cm y se divide a la mitad ya que la onda realiza
  // un viaje de ida y vuelta al sensor.
  distcm = tiempo_vta / TIME_FOR_1CM / 2;

  // Si se encuentra en un rango distinto, lanzo evento para actualizar brillo
  if (brillo_actual == BRILLO_ALTO)
  {
    if (distcm < UMBRAL_DISTANCIA_MAX && distcm > UMBRAL_DISTANCIA_MIN)
      brillo_actual = BRILLO_MEDIO;
    else if (distcm > UMBRAL_DISTANCIA_MAX)
        brillo_actual = BRILLO_BAJO;
      else
          return false;
    nuevo_evento = EV_ACTUALIZAR_LCD;
    return true;
  }

  if (brillo_actual == BRILLO_MEDIO)
  {
    if (distcm < UMBRAL_DISTANCIA_MIN)
      brillo_actual = BRILLO_ALTO;
    else if (distcm > UMBRAL_DISTANCIA_MAX)
        brillo_actual = BRILLO_BAJO;
      else
          return false;
    nuevo_evento = EV_ACTUALIZAR_LCD;
    return true;
  }

  if (brillo_actual == BRILLO_BAJO)
  {
    if (distcm < UMBRAL_DISTANCIA_MAX && distcm > UMBRAL_DISTANCIA_MIN)
      brillo_actual = BRILLO_MEDIO;
    else if (distcm < UMBRAL_DISTANCIA_MIN)
        brillo_actual = BRILLO_ALTO;
      else
          return false;
    nuevo_evento = EV_ACTUALIZAR_LCD;
    return true;
  }
}

bool leer_sensor_potenciometro()
{
  if (valor_potenciometro != (float)analogRead(PIN_SENSOR_POTENCIOMETRO))
  {
    valor_potenciometro = (float)analogRead(PIN_SENSOR_POTENCIOMETRO);
    return true; // Cambio.
  }
  return false; // No hubo cambios
}

//----------------------------------------------------------
// INIT

void do_init()
{
  // Inicia consola serial
  Serial.begin(SERIAL_SPEED);

  // Seteo modo a los pines.
  pinMode(PIN_SENSOR_POTENCIOMETRO, INPUT);
  pinMode(PIN_SENSOR_PULSADOR, INPUT);
  pinMode(PIN_SENSOR_MODO, INPUT);
  pinMode(PIN_ACT_LED, OUTPUT);
  pinMode(PIN_ACT_NEOPIXEL4, OUTPUT);
  pinMode(PIN_ACT_BRILLOLCD, OUTPUT);
  pinMode(PIN_ACT_BUZZER, OUTPUT);

  // Inicializo sensores
  sensores[SENSOR_PULSADOR].pin = PIN_SENSOR_PULSADOR;
  sensores[SENSOR_PULSADOR].estado = ESTADO_SENSOR_OK;

  sensores[SENSOR_MODO].pin = PIN_SENSOR_MODO;
  sensores[SENSOR_MODO].estado = ESTADO_SENSOR_OK;

  sensores[SENSOR_POTENCIOMETRO].pin = PIN_SENSOR_POTENCIOMETRO;
  sensores[SENSOR_POTENCIOMETRO].estado = ESTADO_SENSOR_OK;

  sensores[SENSOR_DISTANCIA].pin = PIN_SENSOR_DISTANCIA;
  sensores[SENSOR_DISTANCIA].estado = ESTADO_SENSOR_OK;

  // Inicializo el evento inicial
  estado_actual = EST_INICIO;

  // Inicia tiempo
  tiempo_previo = 0;
  delta = 0;

  // Inicia contadores
  tamanio_entrada = 0;
  led_numero = 0;
  caracter_numero = 0;

  // Mensaje bienvenida
  lcd.begin(DATOS_BUS_LCD, PIN_DB7_LCD);

  strcpy(lcd_buffer_superior, "Trad. Morse:");
  strcpy(lcd_buffer_inferior, "\0");
  actualizar_lcd();

  // Inicia modo en morse
  modo = MODO_MORSE;

  interrupcion = true;
  morse_buffer[0] = '\0';

  brillo_actual = BRILLO_ALTO;

  // Interrupciones
  attachInterrupt(digitalPinToInterrupt(PIN_SENSOR_PULSADOR), isr, CHANGE);
  attachInterrupt(digitalPinToInterrupt(PIN_SENSOR_MODO), isr_modo, RISING);
}

void obtener_nuevo_evento()
{

  if (digitalRead(PIN_SENSOR_PULSADOR) == LOW && tiempo_previo)
  {
    interrupcion = true;
    nuevo_evento = EV_SOLTADO;
  }

  if (interrupcion == true)
  {
    interrupcion = false;
    return;
  }

  if (leer_sensor_potenciometro())
  {
    nuevo_evento = EV_LEDPWM;
    return;
  }
  // Leo si hay un caracter disponible para leer.
  if (Serial.available())
  {
    nuevo_evento = EV_TECLADO;
    return;
  }

  if (leer_sensor_distancia())
  {
    return;
  }

  if (interrupcion == true)
  {
    interrupcion = false;
    return;
  }

  nuevo_evento = EV_CONT;
}

void maquina_de_estados()
{
  obtener_nuevo_evento();

  if ((nuevo_evento >= 0) && (nuevo_evento < MAX_EVENTOS) && (estado_actual >= 0) && (estado_actual < MAX_ESTADOS))
  {
    if (nuevo_evento != EV_CONT)
    {
      DebugPrintEstado(estados[estado_actual], eventos[nuevo_evento]);
    }
    tabla_de_estados[estado_actual][nuevo_evento]();
  }
  else
  {
      DebugPrintEstado(estados[EST_ERROR], eventos[EV_ERROR]);
  }
}

//----------------------------------------------------------
// Funciones de interrupciones

void isr()
{
  // Si estoy en modo Morse, detecto la pulsacion del botón.
  if (modo == MODO_MORSE)
  {
    interrupcion = true;
    if (digitalRead(PIN_SENSOR_PULSADOR) == HIGH)
    {
      nuevo_evento = EV_PULSADO;
    }
  }
}

void isr_modo()
{
  // Lanzo el evento cambio de modo.
  interrupcion = true;
  nuevo_evento = EV_MODO;
  serial_flush();
}

//----------------------------------------------------------
// Funciones de Arduino

void setup()
{
  do_init();
}

void loop()
{
  maquina_de_estados();
}
//----------------------------------------------------------