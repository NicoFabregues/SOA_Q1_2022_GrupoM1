/*
Trans-AlfaMorse-Late v1.1
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

#define DebugPrintEstado(estado, evento)                           \
  {                                                                \
    String est = estado;                                           \
    String evt = evento;                                           \
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
#define PIN_RS_LCD 12
#define PIN_ENABLE_LCD 11
#define PIN_DB7_LCD 4
#define PIN_DB6_LCD 5
#define PIN_DB5_LCD 6
#define PIN_DB4_LCD 7
#define DATOS_BUS_LCD 16
#define LINEA_0_LCD 0
#define LINEA_1_LCD 1

#define BRILLO_BAJO 0
#define BRILLO_MEDIO 64
#define BRILLO_ALTO 128
#define UMBRAL_DISTANCIA_MIN 50
#define UMBRAL_DISTANCIA_MAX 140

// Pines
#define PIN_ACT_BRILLOLCD 10
#define PIN_ACT_NEOPIXEL4 8
#define PIN_ACT_BUZZER 13
#define PIN_ACT_LED 9
#define PIN_SENSOR_POTENCIOMETRO A0
#define PIN_SENSOR_DISTANCIA A1
#define PIN_SENSOR_MODO 3
#define PIN_SENSOR_PULSADOR 2

// Sensores
#define SENSOR_PULSADOR 0
#define SENSOR_MODO 1
#define SENSOR_DISTANCIA 2
#define SENSOR_POTENCIOMETRO 3
#define MAX_CANT_SENSORES 4
#define ESTADO_SENSOR_OK 108

// Serial consola
#define SERIAL_SPEED 9600

// Modos
#define MODO_MORSE 98
#define MODO_ALFA 99

// Tiempos
#define TIEMPO_MAX_PUNTO 400
#define TIEMPO_MAX_RAYA 800
#define TIEMPO_ESPERA 1200
#define TIEMPO_LED 100
#define TIME_FOR_1CM 29

#define TAM_BUFFER_MORSE 6
#define TAM_BUFFER_LCD 17

// Defino las notas que sonaran en el buzzer
#define NOTE_C4 262
#define NOTE_G3 196
#define NOTE_A3 220
#define NOTE_B3 247
#define NOTE_C4 262
#define NOTE_ONE_SECOND 1000
#define BUZZER_VALOR_MIN 50
#define TAM_MAX_POTENCIOMETRO 1023
#define AJUSTE_FRECUENCIA 450

//----------------------------------------------------------
// Variables Globales.
int led_estado;
int brillo_actual;
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
String message = "";
int tamanio_entrada;
int caracter_numero;
int led_numero;
String buffer_lectura;

// Lista de notas para la melodia del buzzer.
int melodia[] = {
    NOTE_C4, NOTE_G3, NOTE_G3, NOTE_A3, NOTE_G3, 0, NOTE_B3, NOTE_C4};
int duracion_de_notas[] = {
    4, 8, 8, 4, 4, 4, 4, 4};

const struct
{
  char key, *value;
} diccionario[] = {
    {'a', ".-"},
    {'b', "-..."},
    {'c', "-.."},
    {'d', "-.."},
    {'e', "."},
    {'f', "..-."},
    {'g', "--."},
    {'h', "...."},
    {'i', ".."},
    {'j', ".---"},
    {'k', "-.-"},
    {'l', ".-.."},
    {'m', "--"},
    {'n', "-."},
    {'o', "---"},
    {'p', ".--."},
    {'q', "--.-"},
    {'r', ".-."},
    {'s', "..."},
    {'t', "-"},
    {'u', "..-"},
    {'v', "...-"},
    {'w', ".--"},
    {'x', "-..-"},
    {'y', "-.--"},
    {'z', "--.."},
    {'1', ".----"},
    {'2', "..---"},
    {'3', "...--"},
    {'4', "....-"},
    {'5', "....."},
    {'6', "-...."},
    {'7', "--..."},
    {'8', "---.."},
    {'9', "----."},
    {' ', " "}};
size_t diccionario_size = sizeof(diccionario) / sizeof(*diccionario);

//----------------------------------------------------------
// Funciones auxiliares

char *encode(const char key)
{
  for (size_t i = 0; i < diccionario_size; i++)
  {
    if (diccionario[i].key == key)
    {
      return diccionario[i].value;
    }
  }
  return "";
}

char decode(const char *key)
{
  for (size_t i = 0; i < diccionario_size; i++)
  {
    if (!strcmp(key, diccionario[i].value))
    {
      return diccionario[i].key;
    }
  }
  return '\0';
}

void serial_flush()
{
  char t;
  while (Serial.available() > 0)
  {
    t = Serial.read();
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

enum states
{
  EST_INICIO,
  EST_INACTIVO,
  EST_TRADUCIENDO_MORSE,
  EST_TRADUCIENDO_ALFA,
  EST_ERROR
} estado_actual;
String estados[] = {"EST_INICIO", "EST_INACTIVO", "EST_TRADUCIENDO_MORSE", "EST_TRADUCIENDO_ALFA", "EST_ERROR"};

enum events
{
  EV_CONT,
  EV_PULSADO,
  EV_SOLTADO,
  EV_EMP_ALFA,
  EV_MOSTRAR,
  EV_ACTUALIZAR_LCD,
  EV_ERROR,
  EV_UNKNOWN
} nuevo_evento;
String eventos[] = {"EV_CONT", "EV_PULSADO", "EV_SOLTADO", "EV_EMP_ALFA", "EV_MOSTRAR", "EV_ACTUALIZAR_LCD", "EV_ERROR", "EV_UNKNOWN"};

#define MAX_ESTADOS 5
#define MAX_EVENTOS 7

typedef void (*transition)();

transition tabla_de_estados[MAX_ESTADOS][MAX_EVENTOS] = {
    {init_          , error             , error             , error             , error         , actualizar_brillo_lcd , error     },  // EST_INICIO
    {none           , traduccion_morse  , obtener_caracter  , traduccion_alfa   , error         , actualizar_brillo_lcd , error     },  // EST_INACTIVO
    {none           , error             , error             , error             , mostrar_alfa  , actualizar_brillo_lcd , error     },  // EST_TRADUCIENDO_MORSE
    {traduccion_alfa, error             , error             , error             , mostrar_morse , actualizar_brillo_lcd , error     },  // EST_TRADUCIENDO_ALFA
    {reset_sensors  , error             , error             , error             , error         , error                 , error     }   // EST_ERROR

    // EV_CONT      , EV_PULSADO        , EV_SOLTADO        , EV_EMP_ALFA       , EV_MOSTRAR    , EV_ACTUALIZAR_LCD     , EV_ERROR
};

//----------------------------------------------------------
// Funciones Maquina de Estados

void init_()
{
  DebugPrintEstado(estados[estado_actual], eventos[nuevo_evento]);
  // Sonido de bienvenida.
  sonar_buzzer();
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
  lcd.print("\0");
  actualizar_lcd();

  DebugPrintEstado(estados[estado_actual], eventos[nuevo_evento]);

  // Sueno alarma de error
  sonar_buzzer();

  // Evento para cambiar de estado
  nuevo_evento = EV_CONT;
  interrupcion = true;
}

void none()
{
}

void traduccion_morse()
{
  tiempo_previo = millis();
}

void obtener_caracter()
{
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
	  mostrar_morse_por_led('.');
    strcat(morse_buffer, ".");
    Serial.print("Usted ingreso: '.'\n");
    Serial.print("Buffer parcial: ");
    Serial.println(morse_buffer);
  }
  else if (delta < TIEMPO_MAX_RAYA)
  {
	  mostrar_morse_por_led('-');
    strcat(morse_buffer, "-");
    Serial.print("Usted ingreso: '-'\n");
    Serial.print("Buffer parcial: ");
    Serial.println(morse_buffer);
  }
  if (delta > TIEMPO_MAX_RAYA)
  {
  	barraNeoPX.clear();
	  led_numero=0;
    Serial.println("#Obteniendo Caracter#");
    // Si llego al fin de caracter, cambio el evento para traducir.
    nuevo_evento = EV_MOSTRAR;
    interrupcion = true;
    estado_actual = EST_TRADUCIENDO_MORSE;
  }
}

void mostrar_alfa()
{
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
    strcpy(lcd_buffer_superior, "NOT FOUND MORSE!");
    actualizar_lcd();
    morse_buffer[0] = '\0';
  }

  // Reseteo buffer
  morse_buffer[0] = '\0';
  estado_actual = EST_INACTIVO;
}

void traduccion_alfa()
{
	estado_actual = EST_TRADUCIENDO_ALFA;
	if(caracter_numero == tamanio_entrada)
	{
		estado_actual = EST_INACTIVO;
		return;
	}
	if (modo == MODO_MORSE)
	{
	  // Si estoy en Modo morse, dejo de traducir alfa.
	  strcpy(lcd_buffer_superior, "Trad. Morse:");
	  strcpy(lcd_buffer_inferior, "\0");
	  actualizar_lcd();
	  estado_actual = EST_INACTIVO;
	  return;
	}
	// Traduccion de alfanumerico a morse.
	// Busco traduccion.
  	char aux = message[caracter_numero];
	strcpy(morse_buffer, encode(tolower(message[caracter_numero])));
	if (morse_buffer != "")
	{
		nuevo_evento = EV_MOSTRAR;
		interrupcion = true;
	}
	else
	{
	  // Si no encontre traduccion, muestro error.
	  strcpy(lcd_buffer_superior, "NOT FOUND ALFA!!");
	  actualizar_lcd();
	  estado_actual = EST_INACTIVO;
	}
	caracter_numero++;
}

void mostrar_morse()
{

	// Si encontré traduccion, muestro por led y lcd.
	Serial.println(morse_buffer);
	//mostrar_morse_por_led(morse_buffer);
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
	strcpy(lcd_buffer_superior, "Trad. Alfanum:");
	actualizar_lcd();
}

void reset_sensors()
{
  morse_buffer[0] = '\0';
  estado_actual = EST_INACTIVO;
}

//----------------------------------------------------------
// Funciones actuadores

void mostrar_morse_por_led(const char morse)
{
	size_t j = led_numero > CANTIDAD_LEDS_NPX / 3 ? 0 : led_numero;
	led_estado = !led_estado;
	digitalWrite(PIN_ACT_LED, led_estado);
	led_estado = !led_estado;
	digitalWrite(PIN_ACT_LED, led_estado);
	// Muestro Simbolo Morse por NeoPixel
	mostrarSimboloMorseNeoPX(j, morse);
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
  // Funcion para actualizar el display.
  lcd.clear();
  lcd.setCursor(0, LINEA_0_LCD);
  lcd.print(lcd_buffer_superior);
  lcd.setCursor(0, LINEA_1_LCD);

  strcpy(lcd_buffer_inferior, lcd_buffer_inferior);

  lcd.print(lcd_buffer_inferior);
}

void sonar_buzzer()
{
  // Itero las notas
  //for (int nota = 0; nota < 8; nota++)
  //{
    // Calculo la duracion de la nota
    int duracion = NOTE_ONE_SECOND / duracion_de_notas[1];
    tone(PIN_ACT_BUZZER, 100, duracion);

    // Apago el buzzer.
    // noTone(PIN_ACT_BUZZER);
  //}
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

  // Prendo el led si pasa el umbral
  if (distcm < UMBRAL_DISTANCIA_MIN && brillo_actual != BRILLO_ALTO)
  {
    brillo_actual = BRILLO_ALTO;
    nuevo_evento = EV_ACTUALIZAR_LCD;
  }
  else if (distcm < UMBRAL_DISTANCIA_MAX && brillo_actual != BRILLO_MEDIO)
  {
    brillo_actual = BRILLO_MEDIO;
    nuevo_evento = EV_ACTUALIZAR_LCD;
  }
  else if (distcm > UMBRAL_DISTANCIA_MAX && brillo_actual != BRILLO_BAJO)
  {
    brillo_actual = BRILLO_BAJO;
    nuevo_evento = EV_ACTUALIZAR_LCD;
  }
  else
  {
    return false; // No hubo cambios
  }
  return true; // Hay evento
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
  tamanio_entrada=0;
  led_numero=0;
  caracter_numero=0;

  // Mensaje bienvenida
  lcd.begin(DATOS_BUS_LCD, PIN_DB7_LCD);

  strcpy(lcd_buffer_superior, "Trad. Morse:");
  strcpy(lcd_buffer_inferior, "\0");
  actualizar_lcd();

  // Inicia modo en morse
  modo = MODO_MORSE;

  interrupcion = false;
  morse_buffer[0] = '\0';

  // Inicializa estado de LED.
  led_estado = LOW;

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

  if (leer_sensor_distancia() || leer_sensor_potenciometro())
  {
    return;
  }

  // Leo serial para traducir.
	if (Serial.available())
	{
	  if ((buffer_lectura = Serial.readString()) != "")
	  {
      message = buffer_lectura;
      serial_flush();
      Serial.print("Se ingreso: ");
      Serial.println(message);
      if (modo == MODO_ALFA)
      {
        tamanio_entrada = strlen(&message[0]);
        caracter_numero = 0;
        nuevo_evento = EV_EMP_ALFA;
        interrupcion = true;
        return;
      }
	  }
	}

	if(modo == MODO_MORSE && message != "")
	{
		barraNeoPX.clear();
		(message.substring(0, TAM_BUFFER_MORSE - 1)).toCharArray(morse_buffer, 6);
		message=message.substring(TAM_BUFFER_MORSE - 1, message.length() + 1);
		nuevo_evento = EV_MOSTRAR;
		interrupcion = true;
		estado_actual = EST_TRADUCIENDO_MORSE;
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
    DebugPrintEstado(estados[EST_ERROR], eventos[EV_UNKNOWN]);
  }
}

//----------------------------------------------------------
// Funciones de interrupciones

void isr()
{
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
  message = "";
  morse_buffer[0] = '\0';
  serial_flush();
  if (modo == MODO_ALFA)
  {
    strcpy(lcd_buffer_superior, "Trad. Morse:");
    strcpy(lcd_buffer_inferior, "\0");
    actualizar_lcd();
    modo = MODO_MORSE;
  }
  else
  {
    strcpy(lcd_buffer_superior, "Trad. Alfanum:");
    strcpy(lcd_buffer_inferior, "\0");
    actualizar_lcd();
    modo = MODO_ALFA;
	}
  // Agregar evento para limpiar.
  estado_actual = EST_INACTIVO;
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