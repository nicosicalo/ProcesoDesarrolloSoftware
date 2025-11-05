# 🎯 EXPLICACIÓN: ¿Por qué son Strategy y Command?

## 📋 ÍNDICE
1. [¿Por qué Matching es Patrón Strategy?](#1-por-qué-matching-es-patrón-strategy)
2. [¿Por qué Command es Patrón Command?](#2-por-qué-command-es-patrón-command)
3. [¿La simulación realmente crea equipos?](#3-la-simulación-realmente-crea-equipos)

---

## 1. ¿Por qué Matching es Patrón Strategy?

### ✅ **Pautas que cumple el Patrón Strategy:**

El patrón Strategy se usa cuando tienes **múltiples formas de realizar la misma tarea** y quieres poder **cambiarlas dinámicamente** sin modificar el código que las usa.

### 🔍 **Estructura del Patrón Strategy:**

```
┌─────────────────────┐
│  Strategy (Interfaz)│
│  + execute()        │
└─────────────────────┘
         ▲
         │ implementa
         │
┌────────┴─────────┐
│                 │
│ ConcreteStrategy │
│  A, B, C...     │
└─────────────────┘
         ▲
         │ usa
         │
┌────────┴─────────┐
│    Context      │
│  - strategy     │
│  + cambiar()    │
└─────────────────┘
```

### 📝 **Cómo lo implementa tu código:**

#### **1. Interfaz Strategy (Contrato Común)**
```java
// src/matching/MatchingStrategy.java
public interface MatchingStrategy {
    Equipo buscarEmparejamiento(Jugador jugador, 
                               List<Jugador> jugadoresDisponibles, 
                               List<Equipo> equiposParciales);
    String getNombre();
}
```
✅ **Cumple:** Define el contrato común que todas las estrategias deben seguir.

---

#### **2. Implementaciones Concretas (Estrategias)**
```java
// PorRangoMMRStrategy.java
public class PorRangoMMRStrategy implements MatchingStrategy {
    @Override
    public Equipo buscarEmparejamiento(...) {
        // Algoritmo: Emparejar por MMR similar
        // Filtra jugadores con diferencia ≤ 200 puntos MMR
    }
}

// PorLatenciaStrategy.java
public class PorLatenciaStrategy implements MatchingStrategy {
    @Override
    public Equipo buscarEmparejamiento(...) {
        // Algoritmo: Emparejar por zona/latencia
        // Prioriza misma zona o latencia similar
    }
}

// PorCompatibilidadStrategy.java
public class PorCompatibilidadStrategy implements MatchingStrategy {
    @Override
    public Equipo buscarEmparejamiento(...) {
        // Algoritmo: Emparejar por historial
        // Filtra por win rate y partidas jugadas similares
    }
}
```
✅ **Cumple:** Múltiples implementaciones que hacen lo mismo (emparejar) pero de forma diferente.

---

#### **3. Context (Contexto)**
```java
// MatchingContext.java
public class MatchingContext {
    private MatchingStrategy estrategia;  // ← Referencia a la interfaz
    
    public MatchingContext() {
        this.estrategia = new PorRangoMMRStrategy();  // Estrategia por defecto
    }
    
    // ← CLAVE: Puede cambiar la estrategia dinámicamente
    public void cambiarEstrategia(MatchingStrategy nuevaEstrategia) {
        this.estrategia = nuevaEstrategia;  // ← Cambio en tiempo de ejecución
    }
    
    public Partida buscarEmparejamiento(Jugador jugador) {
        // ← Usa la estrategia actual sin saber cuál es
        Equipo equipo = estrategia.buscarEmparejamiento(jugador, listaEspera, equiposParciales);
        // ...
    }
}
```
✅ **Cumple:** 
- Tiene una referencia a la interfaz Strategy
- Puede cambiar la estrategia dinámicamente
- Usa la estrategia sin conocer su implementación concreta

---

### 🎯 **¿Por qué es Strategy y no otra cosa?**

#### **❌ NO es simplemente Herencia:**
Si fuera solo herencia, tendrías algo como:
```java
// MALO: Tendrías que cambiar el código cada vez
class MatchingSystem {
    void buscarEmparejamiento() {
        if (tipo == "MMR") {
            // código MMR
        } else if (tipo == "LATENCIA") {
            // código latencia
        } // ... más if-else
    }
}
```
**Problema:** Tendrías que modificar el código cada vez que agregues una estrategia.

#### **✅ SÍ es Strategy porque:**
```java
// BUENO: Puedes cambiar sin modificar código existente
matchingContext.cambiarEstrategia(new PorRangoMMRStrategy());  // Usa MMR
matchingContext.cambiarEstrategia(new PorLatenciaStrategy());  // Cambia a latencia
matchingContext.cambiarEstrategia(new PorCompatibilidadStrategy()); // Cambia a compatibilidad
```
**Ventaja:** Puedes agregar nuevas estrategias sin tocar el código existente.

---

### 📊 **Ejemplo Real en tu Código:**

```java
// App.java línea 30
matchingContext.cambiarEstrategia(new PorLatenciaStrategy());
```
**Lo que hace:**
1. El `MatchingContext` tenía `PorRangoMMRStrategy` (por defecto)
2. Cambia dinámicamente a `PorLatenciaStrategy`
3. A partir de ese momento, todos los emparejamientos usan la estrategia de latencia
4. **Sin modificar ningún código existente**

---

### ✅ **Pautas que cumple (Checklist Strategy Pattern):**

| Pauta | ¿Se cumple? | Evidencia |
|-------|-------------|-----------|
| 1. Interfaz común para algoritmos | ✅ SÍ | `MatchingStrategy` interface |
| 2. Múltiples implementaciones | ✅ SÍ | 3 estrategias diferentes |
| 3. Context mantiene referencia a Strategy | ✅ SÍ | `private MatchingStrategy estrategia` |
| 4. Cambio dinámico de estrategia | ✅ SÍ | `cambiarEstrategia()` método |
| 5. Context usa Strategy sin conocer implementación | ✅ SÍ | Llama `estrategia.buscarEmparejamiento()` |
| 6. Extensible sin modificar código existente | ✅ SÍ | Puedes agregar nuevas estrategias |

---

## 2. ¿Por qué Command es Patrón Command?

### ✅ **Pautas que cumple el Patrón Command:**

El patrón Command **encapsula una solicitud como un objeto**, permitiendo:
- Deshacer/rehacer operaciones
- Colocar solicitudes en cola
- Registrar historial de operaciones
- Separar quién invoca de quién ejecuta

### 🔍 **Estructura del Patrón Command:**

```
┌─────────────────────┐
│  Command (Interfaz) │
│  + execute()        │
│  + undo()          │
└─────────────────────┘
         ▲
         │ implementa
         │
┌────────┴─────────┐
│                 │
│ ConcreteCommand │
│  A, B, C...     │
└─────────────────┘
         ▲
         │ usa
         │
┌────────┴─────────┐
│    Invoker       │
│  - historial     │
│  + execute()     │
│  + undo()        │
└─────────────────┘
```

### 📝 **Cómo lo implementa tu código:**

#### **1. Interfaz Command (Contrato)**
```java
// src/command/Command.java
public interface Command {
    boolean ejecutar();      // ← Ejecutar la operación
    void deshacer();         // ← Deshacer la operación
    String getDescripcion(); // ← Descripción del comando
}
```
✅ **Cumple:** Define el contrato que todos los comandos deben seguir.

---

#### **2. Comandos Concretos (Operaciones Encapsuladas)**
```java
// AsignarRolCommand.java
public class AsignarRolCommand implements Command {
    private Equipo equipo;
    private Jugador jugador;
    private Rol rol;
    private Jugador jugadorAnterior;  // ← Guarda estado para deshacer
    
    @Override
    public boolean ejecutar() {
        jugadorAnterior = equipo.getJugador(rol);  // Guarda estado
        equipo.removerJugador(...);  // Ejecuta operación
        equipo.agregarJugador(jugador, rol);
        return true;
    }
    
    @Override
    public void deshacer() {
        equipo.removerJugador(rol);
        if (jugadorAnterior != null) {
            equipo.agregarJugador(jugadorAnterior, rol);  // Restaura estado
        }
    }
}
```

```java
// SwapRolesCommand.java
public class SwapRolesCommand implements Command {
    private Equipo equipo;
    private Jugador jugador1;
    private Jugador jugador2;
    private Rol rol1;  // ← Guarda estado para deshacer
    private Rol rol2;  // ← Guarda estado para deshacer
    
    @Override
    public boolean ejecutar() {
        // Encuentra roles actuales y los guarda
        rol1 = encontrarRol(jugador1);
        rol2 = encontrarRol(jugador2);
        // Intercambia roles
        equipo.removerJugador(rol1);
        equipo.removerJugador(rol2);
        equipo.agregarJugador(jugador1, rol2);
        equipo.agregarJugador(jugador2, rol1);
        return true;
    }
    
    @Override
    public void deshacer() {
        // Intercambia de vuelta
        equipo.removerJugador(rol1);
        equipo.removerJugador(rol2);
        equipo.agregarJugador(jugador1, rol1);  // Restaura
        equipo.agregarJugador(jugador2, rol2);  // Restaura
    }
}
```
✅ **Cumple:**
- Encapsula operaciones como objetos
- Guarda estado para poder deshacer
- Implementa `ejecutar()` y `deshacer()`

---

#### **3. Invoker (Invocador)**
```java
// CommandInvoker.java
public class CommandInvoker {
    private Stack<Command> historial;      // ← Historial de comandos ejecutados
    private Stack<Command> rehacerPila;   // ← Pila para rehacer
    
    public boolean ejecutarComando(Command comando) {
        boolean exito = comando.ejecutar();  // ← Ejecuta el comando
        if (exito) {
            historial.push(comando);  // ← Guarda en historial
            rehacerPila.clear();
        }
        return exito;
    }
    
    public void deshacer() {
        if (historial.isEmpty()) return;
        Command comando = historial.pop();  // ← Obtiene último comando
        comando.deshacer();  // ← Lo deshace
        rehacerPila.push(comando);  // ← Lo guarda para rehacer
    }
    
    public void rehacer() {
        if (rehacerPila.isEmpty()) return;
        Command comando = rehacerPila.pop();
        comando.ejecutar();  // ← Lo vuelve a ejecutar
        historial.push(comando);
    }
}
```
✅ **Cumple:**
- Separa quién invoca (CommandInvoker) de quién ejecuta (Command)
- Mantiene historial de operaciones
- Permite deshacer/rehacer

---

### 🎯 **¿Por qué es Command y no otra cosa?**

#### **❌ NO es simplemente llamar métodos:**
Si fuera solo llamar métodos directamente:
```java
// MALO: Sin capacidad de deshacer
equipo.agregarJugador(jugador, rol);  // ¿Cómo deshaces esto?
equipo.removerJugador(rol);  // ¿Cómo deshaces esto?
```
**Problema:** No hay manera de deshacer, no hay historial.

#### **✅ SÍ es Command porque:**
```java
// BUENO: Encapsulado como objeto, con capacidad de deshacer
AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, rol);
invoker.ejecutarComando(cmd);  // Ejecuta
invoker.deshacer();  // Deshace
invoker.rehacer();  // Rehace
```
**Ventajas:**
- Puedes deshacer/rehacer
- Tienes historial
- Puedes colocar comandos en cola
- Separación de responsabilidades

---

### 📊 **Ejemplo Real en tu Código:**

```java
// App.java líneas 118-119
AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, Rol.TANQUE);
invoker.ejecutarComando(cmd);  // Ejecuta el comando

// Luego puedes:
invoker.deshacer();  // Deshace el cambio
invoker.rehacer();  // Lo vuelve a aplicar
invoker.mostrarHistorial();  // Muestra todos los comandos ejecutados
```

---

### ✅ **Pautas que cumple (Checklist Command Pattern):**

| Pauta | ¿Se cumple? | Evidencia |
|-------|-------------|-----------|
| 1. Interfaz Command común | ✅ SÍ | `Command` interface |
| 2. Comandos concretos encapsulan operaciones | ✅ SÍ | `AsignarRolCommand`, `SwapRolesCommand` |
| 3. Comandos guardan estado para deshacer | ✅ SÍ | `jugadorAnterior`, `rol1`, `rol2` |
| 4. Invoker separa invocación de ejecución | ✅ SÍ | `CommandInvoker` ejecuta comandos |
| 5. Historial de comandos | ✅ SÍ | `Stack<Command> historial` |
| 6. Capacidad de deshacer/rehacer | ✅ SÍ | `deshacer()`, `rehacer()` métodos |

---

## 3. ¿La Simulación Realmente Crea Equipos?

### ✅ **SÍ, CREA EQUIPOS REALES**

No solo simula la búsqueda, **realmente crea objetos `Equipo` y `Partida`** en memoria.

---

### 🔍 **Evidencia en el Código:**

#### **1. Creación de Equipos (PorRangoMMRStrategy.java línea 36)**
```java
// Cuando busca emparejamiento, CREA un equipo nuevo
Equipo nuevoEquipo = new Equipo("Equipo-" + System.currentTimeMillis());
nuevoEquipo.agregarJugador(jugador, jugador.getRolPreferido());

// Busca otros jugadores y los AGREGA al equipo
for (Jugador candidato : candidatos) {
    nuevoEquipo.agregarJugador(candidato, rolDisponible);  // ← AGREGA jugadores reales
    agregados++;
}

return nuevoEquipo;  // ← RETORNA el equipo creado
```
✅ **Esto crea un objeto `Equipo` real con jugadores reales.**

---

#### **2. Almacenamiento de Equipos (MatchingContext.java líneas 54-77)**
```java
public Partida buscarEmparejamiento(Jugador jugador) {
    Equipo equipo = estrategia.buscarEmparejamiento(...);  // ← Obtiene equipo creado
    
    if (equipo.estaCompleto()) {
        // Equipo completo → buscar oponente
        Equipo oponente = buscarEquipoOponente(equipo);
        
        if (oponente != null) {
            Partida partida = new Partida("PART-" + contadorPartidas++, equipo, oponente);
            partidasEmparejadas.add(partida);  // ← GUARDA la partida en lista
            return partida;
        } else {
            equiposParciales.add(equipo);  // ← GUARDA el equipo completo esperando oponente
        }
    } else {
        equiposParciales.add(equipo);  // ← GUARDA el equipo parcial
    }
}
```
✅ **Los equipos se almacenan en:**
- `equiposParciales` (lista de equipos incompletos o completos esperando rival)
- `partidasEmparejadas` (lista de partidas creadas)

---

#### **3. Visualización de Equipos Creados (App.java líneas 148-158)**
```java
private static void mostrarEstadoFinal(MatchingContext context, ...) {
    // Obtiene los equipos REALES creados
    List<Equipo> equipos = context.getEquiposParciales();
    System.out.println("\n[EQUIPOS] Equipos parciales (" + equipos.size() + "):");
    for (Equipo e : equipos) {
        System.out.println(e.toString());  // ← Muestra equipos REALES
    }

    // Obtiene las partidas REALES creadas
    List<Partida> partidas = context.getPartidasEmparejadas();
    System.out.println("\n[PARTIDAS] Partidas encontradas (" + partidas.size() + "):");
    for (Partida p : partidas) {
        System.out.println(p.toString());  // ← Muestra partidas REALES
    }
}
```
✅ **Muestra los equipos y partidas que realmente se crearon.**

---

### 📊 **Flujo Completo de Creación:**

```
1. Jugador busca partida
   ↓
2. MatchingContext.buscarEmparejamiento(jugador)
   ↓
3. Estrategia.buscarEmparejamiento() 
   → CREA Equipo nuevoEquipo = new Equipo(...)
   → AGREGA jugadores: nuevoEquipo.agregarJugador(...)
   → RETORNA nuevoEquipo
   ↓
4. MatchingContext recibe el equipo
   ↓
5. Si equipo completo:
   → Busca oponente
   → Si encuentra oponente:
      → CREA Partida partida = new Partida(equipo, oponente)
      → GUARDA en partidasEmparejadas.add(partida)
   → Si no encuentra:
      → GUARDA en equiposParciales.add(equipo)
   ↓
6. Si equipo parcial:
   → GUARDA en equiposParciales.add(equipo)
   ↓
7. Al final:
   → Muestra equiposParciales (equipos REALES creados)
   → Muestra partidasEmparejadas (partidas REALES creadas)
```

---

### ✅ **Ejemplo de lo que se crea:**

Si ejecutas la simulación con los 6 primeros jugadores:

**Equipos creados:**
```
Equipo Equipo-1234567890 (MMR Promedio: 2450)
  DPS: ProGamer99
  TANQUE: ElitePlayer
  MAGE: MasterMage
  SOPORTE: SupportKing
  ASESINO: NinjaAssassin
```

**Partidas creadas:**
```
Partida PART-1 - Estado: En espera
Equipo 1:
  Equipo Equipo-1234567890 (MMR Promedio: 2450)
    ...
Equipo 2:
  Equipo Equipo-1234567891 (MMR Promedio: 2400)
    ...
```

---

### 🎯 **Conclusión:**

**NO es solo simulación de búsqueda.** El código:
- ✅ Crea objetos `Equipo` reales
- ✅ Agrega jugadores reales a esos equipos
- ✅ Almacena equipos en listas (`equiposParciales`)
- ✅ Crea objetos `Partida` reales cuando encuentra dos equipos completos
- ✅ Almacena partidas en lista (`partidasEmparejadas`)
- ✅ Muestra los equipos y partidas creados al final

**Es una simulación completa que funciona como un sistema real, solo que con datos inventados en lugar de una base de datos.**

---

## 📝 Resumen Final

### **Strategy Pattern:**
- ✅ Permite cambiar algoritmos de emparejamiento dinámicamente
- ✅ Sin modificar código existente
- ✅ Implementado correctamente con interfaz, múltiples estrategias y contexto

### **Command Pattern:**
- ✅ Encapsula operaciones (cambiar roles) como objetos
- ✅ Permite deshacer/rehacer operaciones
- ✅ Mantiene historial de comandos
- ✅ Separación entre invocador y ejecutor

### **Creación de Equipos:**
- ✅ **SÍ crea equipos reales** en memoria
- ✅ **SÍ crea partidas reales** cuando encuentra dos equipos completos
- ✅ Los equipos se almacenan y se muestran al final

---

**Documentación generada:** Explicación de Patrones de Diseño - Integrante 4

