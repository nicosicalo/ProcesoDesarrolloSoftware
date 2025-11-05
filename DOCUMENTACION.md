# 📚 DOCUMENTACIÓN DEL SISTEMA DE EMPAREJAMIENTO

## 🎯 Descripción General

Este sistema simula un mecanismo de emparejamiento (matchmaking) para un videojuego que utiliza dos patrones de diseño:
- **Strategy Pattern**: Para diferentes estrategias de emparejamiento
- **Command Pattern**: Para gestionar cambios de roles en equipos

---

## 📁 Estructura del Proyecto y Necesidad de Cada Clase

### ✅ **¿Son necesarias todas las clases?**

**SÍ**, cada paquete cumple un rol específico y necesario:

#### 📦 **Paquete `matching/`** - Sistema de Emparejamiento
**¿Por qué es necesario?** Implementa el patrón Strategy para cambiar dinámicamente cómo se emparejan jugadores.

| Clase | Función | ¿Es necesaria? |
|-------|---------|----------------|
| `MatchingStrategy` (interfaz) | Define el contrato para todas las estrategias | ✅ **SÍ** - Base del patrón Strategy |
| `MatchingContext` | Contexto que permite cambiar estrategias dinámicamente | ✅ **SÍ** - Orquesta el emparejamiento |
| `PorRangoMMRStrategy` | Empareja por puntuación MMR similar | ✅ **SÍ** - Estrategia por defecto |
| `PorLatenciaStrategy` | Empareja por zona geográfica/latencia | ✅ **SÍ** - Para partidas con mejor conexión |
| `PorCompatibilidadStrategy` | Empareja por historial de partidas | ✅ **SÍ** - Para jugadores con experiencia similar |

**Sin este paquete:** No habría manera de emparejar jugadores de forma inteligente.

---

#### 📦 **Paquete `command/`** - Gestión de Roles
**¿Por qué es necesario?** Implementa el patrón Command para permitir deshacer/rehacer cambios de roles.

| Clase | Función | ¿Es necesaria? |
|-------|---------|----------------|
| `Command` (interfaz) | Define el contrato para comandos | ✅ **SÍ** - Base del patrón Command |
| `CommandInvoker` | Ejecuta comandos y mantiene historial | ✅ **SÍ** - Permite deshacer/rehacer |
| `AsignarRolCommand` | Comando para asignar un rol | ✅ **SÍ** - Para cambiar roles de jugadores |
| `SwapRolesCommand` | Comando para intercambiar roles | ✅ **SÍ** - Para intercambiar roles entre jugadores |

**Sin este paquete:** No habría manera de gestionar roles con capacidad de deshacer cambios.

---

#### 📦 **Paquete `system/`** - Gestión del Sistema
**¿Por qué es necesario?** Gestiona listas de espera y suplentes.

| Clase | Función | ¿Es necesaria? |
|-------|---------|----------------|
| `ListaEsperaManager` | Gestiona jugadores en espera y suplentes | ⚠️ **PARCIALMENTE** - Actualmente se usa poco en la simulación |

**Nota:** Esta clase está preparada para uso futuro pero en la simulación actual se usa mínimamente. El `MatchingContext` tiene su propia lista de espera.

---

#### 📦 **Paquete `model/`** - Modelo de Datos
**¿Por qué es necesario?** Representa las entidades del sistema.

| Clase | Función | ¿Es necesaria? |
|-------|---------|----------------|
| `Jugador` | Representa un jugador con sus estadísticas | ✅ **SÍ** - Entidad central |
| `Equipo` | Representa un equipo de 5 jugadores | ✅ **SÍ** - Se forman equipos para partidas |
| `Partida` | Representa una partida emparejada | ✅ **SÍ** - Resultado del emparejamiento |
| `Rol` | Enum con los 5 roles disponibles | ✅ **SÍ** - Define roles del juego |

**Sin este paquete:** No habría datos para trabajar.

---

## 🔄 Flujo de Ejecución de la Simulación

### 1. **Inicio** (`App.java` - línea 8)
```java
public static void main(String[] args)
```
- **Dónde está:** `src/App.java`
- **Qué hace:** Punto de entrada del programa
- **Datos que toma:** Ninguno (simulación)

---

### 2. **Creación de Jugadores** (`App.java` - línea 47)
```java
private static List<Jugador> crearJugadoresSimulados()
```
- **Dónde está:** `src/App.java` líneas 47-65
- **Qué hace:** Crea 10 jugadores con datos inventados
- **Datos que toma:** Ninguno (hardcodeados)
- **Datos que genera:** Lista de 10 objetos `Jugador`

**📝 ¿Dónde editar los jugadores?**
**RESPUESTA:** En `src/App.java`, método `crearJugadoresSimulados()` (líneas 50-61).

**Formato de cada jugador:**
```java
new Jugador(
    "ID",           // Identificador único
    "Nombre",       // Nombre del jugador
    MMR,            // Puntos MMR (2300-2600)
    partidas,       // Partidas jugadas
    "ZONA",         // Zona geográfica (NA-EAST, NA-WEST, EU-WEST, etc.)
    latencia,       // Latencia en milisegundos
    Rol.XXX,        // Rol preferido (TANQUE, DPS, SOPORTE, ASESINO, MAGE)
    victorias,      // Número de victorias
    derrotas        // Número de derrotas
)
```

**Ejemplo para editar:**
```java
new Jugador("J1", "MiJugador", 2700, 200, "EU-WEST", 60, Rol.TANQUE, 150, 50)
```

---

### 3. **Simulación de Emparejamiento** (`App.java` - línea 67)
```java
private static void simularEmparejamiento(List<Jugador> jugadores, MatchingContext context)
```
- **Dónde está:** `src/App.java` líneas 67-92
- **Qué hace:** Itera sobre los primeros 6 jugadores buscando partidas
- **Datos que toma:** 
  - Lista de jugadores
  - `MatchingContext` (contiene la estrategia actual)
- **Métodos que llama:**
  - `context.buscarEmparejamiento(jugador)` → `MatchingContext.buscarEmparejamiento()`

---

### 4. **Búsqueda de Emparejamiento** (`MatchingContext.java` - línea 39)
```java
public Partida buscarEmparejamiento(Jugador jugador)
```
- **Dónde está:** `src/matching/MatchingContext.java` líneas 39-80
- **Qué hace:** 
  1. Usa la estrategia actual para buscar un equipo
  2. Si encuentra equipo completo, busca oponente
  3. Si no encuentra, agrega jugador a lista de espera
- **Datos que toma:**
  - `Jugador jugador` - El jugador que busca partida
  - `listaEspera` - Lista interna de jugadores disponibles
  - `equiposParciales` - Lista de equipos incompletos
- **Métodos que llama:**
  - `estrategia.buscarEmparejamiento()` → Una de las 3 estrategias
  - `buscarEquipoOponente()` → Para encontrar rival

---

### 5. **Estrategias de Emparejamiento**

#### 5.1 **PorRangoMMRStrategy** (Estrategia por defecto)
- **Dónde está:** `src/matching/PorRangoMMRStrategy.java`
- **Método principal:** `buscarEmparejamiento()` (línea 17)
- **Qué hace:** Empareja jugadores con MMR similar (diferencia ≤ 200 puntos)
- **Datos que toma:**
  - `Jugador jugador` - Jugador buscando partida
  - `List<Jugador> jugadoresDisponibles` - Lista de espera
  - `List<Equipo> equiposParciales` - Equipos incompletos
- **Métodos auxiliares:**
  - `esCompatibleMMR()` - Verifica si MMR es compatible
  - `filtrarPorMMR()` - Filtra jugadores por MMR
  - `buscarRolDisponible()` - Busca rol vacío en equipo

#### 5.2 **PorLatenciaStrategy**
- **Dónde está:** `src/matching/PorLatenciaStrategy.java`
- **Método principal:** `buscarEmparejamiento()` (línea 17)
- **Qué hace:** Empareja jugadores de la misma zona o latencia similar (diferencia ≤ 50ms)
- **Datos que toma:** Mismos que PorRangoMMRStrategy
- **Métodos auxiliares:**
  - `esCompatibleLatencia()` - Verifica compatibilidad de latencia

#### 5.3 **PorCompatibilidadStrategy**
- **Dónde está:** `src/matching/PorCompatibilidadStrategy.java`
- **Método principal:** `buscarEmparejamiento()` (línea 18)
- **Qué hace:** Empareja jugadores con historial similar (win rate y partidas jugadas)
- **Datos que toma:** Mismos que las anteriores
- **Métodos auxiliares:**
  - `esCompatible()` - Verifica compatibilidad de historial
  - `filtrarPorCompatibilidad()` - Filtra por win rate y partidas

---

### 6. **Búsqueda de Equipo Oponente** (`MatchingContext.java` - línea 82)
```java
private Equipo buscarEquipoOponente(Equipo equipo)
```
- **Dónde está:** `src/matching/MatchingContext.java` líneas 82-96
- **Qué hace:** Busca un equipo completo con MMR similar (diferencia ≤ 150 puntos)
- **Datos que toma:**
  - `Equipo equipo` - Equipo que necesita oponente
- **Datos que usa:**
  - `equiposParciales` - Lista de equipos completos esperando rival

---

### 7. **Creación de Partida** (`MatchingContext.java` - línea 62)
```java
Partida partida = new Partida("PART-" + contadorPartidas++, equipo, oponente);
```
- **Dónde está:** `src/matching/MatchingContext.java` línea 62
- **Qué hace:** Crea una partida con dos equipos completos
- **Datos que toma:**
  - ID de partida (auto-generado)
  - Equipo 1 y Equipo 2

---

### 8. **Simulación de Gestión de Roles** (`App.java` - línea 94)
```java
private static void simularGestionRoles(List<Jugador> jugadores, CommandInvoker invoker)
```
- **Dónde está:** `src/App.java` líneas 94-139
- **Qué hace:** 
  1. Crea un equipo de ejemplo con 5 jugadores
  2. Asigna un rol diferente usando `AsignarRolCommand`
  3. Intercambia roles usando `SwapRolesCommand`
  4. Muestra el historial de comandos
- **Datos que toma:**
  - Lista de jugadores
  - `CommandInvoker` (para ejecutar comandos)
- **Métodos que llama:**
  - `invoker.ejecutarComando()` → `CommandInvoker.ejecutarComando()`

---

### 9. **Ejecución de Comandos** (`CommandInvoker.java` - línea 20)
```java
public boolean ejecutarComando(Command comando)
```
- **Dónde está:** `src/command/CommandInvoker.java` líneas 20-28
- **Qué hace:** 
  1. Ejecuta el comando
  2. Si tiene éxito, lo guarda en el historial
  3. Limpia la pila de rehacer
- **Datos que toma:**
  - `Command comando` - Comando a ejecutar (AsignarRolCommand o SwapRolesCommand)
- **Métodos que llama:**
  - `comando.ejecutar()` → Método específico del comando

---

### 10. **Mostrar Estado Final** (`App.java` - línea 141)
```java
private static void mostrarEstadoFinal(MatchingContext context, ListaEsperaManager listaEspera)
```
- **Dónde está:** `src/App.java` líneas 141-161
- **Qué hace:** Muestra el estado final del sistema:
  - Estrategia actual
  - Equipos parciales formados
  - Partidas encontradas
  - Estado de lista de espera
- **Datos que toma:**
  - `MatchingContext` - Para obtener equipos y partidas
  - `ListaEsperaManager` - Para mostrar estado de espera

---

## 📊 Resumen: Métodos que Ejecutan la Simulación Final

| Método | Ubicación | Función | Datos que Toma |
|--------|-----------|---------|----------------|
| `main()` | `App.java:8` | Punto de entrada | Ninguno |
| `crearJugadoresSimulados()` | `App.java:47` | Crea 10 jugadores | Ninguno (hardcodeados) |
| `simularEmparejamiento()` | `App.java:67` | Inicia búsqueda de partidas | `List<Jugador>`, `MatchingContext` |
| `buscarEmparejamiento()` | `MatchingContext.java:39` | Busca equipo para jugador | `Jugador` |
| `buscarEmparejamiento()` | `PorRangoMMRStrategy.java:17` | Estrategia MMR | `Jugador`, `List<Jugador>`, `List<Equipo>` |
| `buscarEquipoOponente()` | `MatchingContext.java:82` | Busca rival | `Equipo` |
| `simularGestionRoles()` | `App.java:94` | Simula cambios de roles | `List<Jugador>`, `CommandInvoker` |
| `ejecutarComando()` | `CommandInvoker.java:20` | Ejecuta comandos | `Command` |
| `mostrarEstadoFinal()` | `App.java:141` | Muestra estado final | `MatchingContext`, `ListaEsperaManager` |

---

## 🎮 Datos de los 10 Jugadores Inventados

### 📍 Ubicación: `src/App.java` - Método `crearJugadoresSimulados()` (líneas 50-61)

### Jugadores actuales:

```java
J1: "ProGamer99"    - MMR: 2500, Zona: NA-EAST,  Lat: 45ms,  Rol: DPS
J2: "ElitePlayer"   - MMR: 2400, Zona: NA-EAST,  Lat: 50ms,  Rol: TANQUE
J3: "MasterMage"    - MMR: 2550, Zona: NA-EAST,  Lat: 48ms,  Rol: MAGE
J4: "SupportKing"   - MMR: 2450, Zona: NA-WEST,  Lat: 120ms, Rol: SOPORTE
J5: "NinjaAssassin" - MMR: 2600, Zona: NA-WEST,  Lat: 110ms, Rol: ASESINO
J6: "TankWarrior"   - MMR: 2300, Zona: EU-WEST,   Lat: 85ms,  Rol: TANQUE
J7: "MagicWizard"   - MMR: 2500, Zona: EU-WEST,   Lat: 90ms,  Rol: MAGE
J8: "SniperPro"     - MMR: 2400, Zona: SA-BRAZIL, Lat: 150ms, Rol: DPS
J9: "HealerAura"    - MMR: 2350, Zona: SA-BRAZIL, Lat: 145ms, Rol: SOPORTE
J10: "ShadowKiller" - MMR: 2480, Zona: ASIA-JAPAN, Lat: 200ms, Rol: ASESINO
```

### ✏️ Cómo Editar los Jugadores:

1. Abre `src/App.java`
2. Ve al método `crearJugadoresSimulados()` (línea 47)
3. Modifica los parámetros de cualquier jugador en las líneas 50-61

**Ejemplo de edición:**
```java
// Cambiar el primer jugador
new Jugador("J1", "NuevoNombre", 2700, 300, "EU-WEST", 30, Rol.MAGE, 200, 100)
```

---

## 🏗️ Arquitectura del Sistema

```
App (main)
├── crearJugadoresSimulados() → List<Jugador>
├── simularEmparejamiento()
│   └── MatchingContext.buscarEmparejamiento()
│       ├── MatchingStrategy.buscarEmparejamiento() [Strategy Pattern]
│       │   ├── PorRangoMMRStrategy
│       │   ├── PorLatenciaStrategy
│       │   └── PorCompatibilidadStrategy
│       └── buscarEquipoOponente()
│           └── new Partida()
├── simularGestionRoles()
│   └── CommandInvoker.ejecutarComando() [Command Pattern]
│       ├── AsignarRolCommand.ejecutar()
│       └── SwapRolesCommand.ejecutar()
└── mostrarEstadoFinal()
```

---

## 🔑 Conceptos Clave

### Patrón Strategy
- Permite cambiar la estrategia de emparejamiento sin modificar el código cliente
- Las estrategias son intercambiables: MMR, Latencia, Compatibilidad

### Patrón Command
- Encapsula operaciones (cambiar roles) como objetos
- Permite deshacer/rehacer operaciones
- Mantiene historial de comandos

### Flujo de Emparejamiento
1. Jugador busca partida → `MatchingContext.buscarEmparejamiento()`
2. Se usa la estrategia actual → `estrategia.buscarEmparejamiento()`
3. Se busca equipo parcial o se crea uno nuevo
4. Si equipo completo → buscar oponente
5. Si oponente encontrado → crear `Partida`

---

## 📝 Notas Importantes

1. **Lista de Espera:** El `MatchingContext` tiene su propia lista de espera interna. El `ListaEsperaManager` está preparado para uso futuro pero se usa poco actualmente.

2. **Estrategia por Defecto:** Es `PorRangoMMRStrategy` (se establece en `MatchingContext` constructor, línea 25).

3. **Cambio de Estrategia:** Se puede cambiar dinámicamente con `matchingContext.cambiarEstrategia(new PorLatenciaStrategy())` (línea 30 de App.java).

4. **Roles Disponibles:** 5 roles definidos en `Rol.java`: TANQUE, DPS, SOPORTE, ASESINO, MAGE.

5. **Equipos Completos:** Un equipo está completo cuando tiene exactamente 5 jugadores (uno por cada rol).

---

## 🚀 Próximos Pasos Sugeridos

1. **Conectar a Base de Datos:** Reemplazar `crearJugadoresSimulados()` con consultas a BD
2. **Interfaz Gráfica:** Crear GUI para visualizar el emparejamiento
3. **Persistencia:** Guardar partidas y estadísticas
4. **API REST:** Exponer el sistema como servicio web
5. **Mejorar ListaEsperaManager:** Integrar mejor con el sistema de emparejamiento

---

**Documentación generada:** Sistema de Emparejamiento - Integrante 4
**Patrones implementados:** Strategy, Command

