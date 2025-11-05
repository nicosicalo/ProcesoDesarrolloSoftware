# 🎮 CÓMO MODIFICAR EL ROL DE UN USUARIO/JUGADOR

## 📍 **Hay DOS lugares donde se maneja el rol de un jugador:**

---

## 1️⃣ **ROL PREFERIDO DEL JUGADOR** (Al crear el jugador)

### 📂 **Ubicación:** `src/App.java` - Método `crearJugadoresSimulados()` (líneas 47-65)

### 📝 **¿Qué es?**
Es el **rol que el jugador prefiere jugar** cuando se crea su perfil. Este rol se establece cuando se crea el objeto `Jugador`.

### 🔧 **Cómo modificarlo:**

Abre el archivo `src/App.java` y busca el método `crearJugadoresSimulados()`:

```java
private static List<Jugador> crearJugadoresSimulados() {
    List<Jugador> jugadores = List.of(
        new Jugador("J1", "ProGamer99", 2500, 150, "NA-EAST", 45, Rol.DPS, 85, 65),
        //                                                                    ^^^
        //                                                                    Aquí está el rol preferido
        new Jugador("J2", "ElitePlayer", 2400, 200, "NA-EAST", 50, Rol.TANQUE, 120, 80),
        //                                                                     ^^^^^^^^
        //                                                                     Aquí está el rol preferido
        // ... más jugadores
    );
}
```

### ✏️ **Ejemplo de cambio:**

**ANTES:**
```java
new Jugador("J1", "ProGamer99", 2500, 150, "NA-EAST", 45, Rol.DPS, 85, 65)
```

**DESPUÉS (cambiar a TANQUE):**
```java
new Jugador("J1", "ProGamer99", 2500, 150, "NA-EAST", 45, Rol.TANQUE, 85, 65)
```

### 📋 **Roles disponibles:**
- `Rol.TANQUE` - Tanque
- `Rol.DPS` - DPS
- `Rol.SOPORTE` - Soporte
- `Rol.ASESINO` - Asesino
- `Rol.MAGE` - Mago

### ⚠️ **Nota importante:**
Este es el **rol preferido** del jugador. No significa que **siempre** jugará ese rol en un equipo. El rol asignado en un equipo puede ser diferente.

---

## 2️⃣ **ROL ASIGNADO EN UN EQUIPO** (Durante el juego/simulación)

### 📂 **Ubicación 1:** `src/App.java` - Método `formarEquiposCompletos()` (líneas 76-94)

### 📝 **¿Qué es?**
Es el **rol que el jugador tiene asignado dentro de un equipo específico**. Puede ser diferente de su rol preferido.

### 🔧 **Cómo modificarlo en la formación de equipos:**

Cuando se forman los equipos en `formarEquiposCompletos()`, puedes cambiar qué rol se asigna a cada jugador:

```java
// Equipo 1
equipo1.agregarJugador(jugadores.get(0), Rol.DPS);      // ProGamer99
//                                      ^^^^^^^^
//                                      Cambia este rol aquí

equipo1.agregarJugador(jugadores.get(1), Rol.TANQUE);   // ElitePlayer
//                                      ^^^^^^^^^^
//                                      Cambia este rol aquí
```

### ✏️ **Ejemplo de cambio:**

**ANTES:**
```java
equipo1.agregarJugador(jugadores.get(0), Rol.DPS);      // ProGamer99 es DPS
```

**DESPUÉS (cambiar a MAGE):**
```java
equipo1.agregarJugador(jugadores.get(0), Rol.MAGE);     // ProGamer99 ahora es MAGE
```

---

### 📂 **Ubicación 2:** `src/App.java` - Método `simularGestionRoles()` (líneas 113-158)

### 📝 **¿Qué es?**
Este método simula **cambios de roles durante el juego** usando el patrón Command.

### 🔧 **Cómo modificarlo usando comandos:**

Puedes cambiar el rol de un jugador en un equipo usando comandos:

```java
// Obtener un jugador del equipo
Jugador jugador = equipo.getJugador(Rol.DPS);  // Jugador que tiene rol DPS

// Crear comando para asignar nuevo rol
AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, Rol.TANQUE);
//                                                              ^^^^^^^^^^
//                                                              Nuevo rol asignado

// Ejecutar el comando
invoker.ejecutarComando(cmd);
```

### ✏️ **Ejemplo de cambio:**

**Para cambiar el rol de "ProGamer99" de DPS a TANQUE:**

```java
// 1. Obtener el jugador
Jugador proGamer = equipo.getJugador(Rol.DPS);  // Si actualmente es DPS

// 2. Crear comando para cambiar su rol
AsignarRolCommand cmd = new AsignarRolCommand(equipo, proGamer, Rol.TANQUE);

// 3. Ejecutar
invoker.ejecutarComando(cmd);
```

### ✅ **Ventajas de usar comandos:**
- Puedes **deshacer** el cambio con `invoker.deshacer()`
- Puedes **rehacer** el cambio con `invoker.rehacer()`
- Se guarda en el **historial** de comandos

---

## 📊 **Resumen: Dónde modificar roles**

| Tipo de Rol | Ubicación | Método/Archivo | Línea Aprox. |
|-------------|-----------|----------------|--------------|
| **Rol Preferido** | Al crear jugador | `App.java` → `crearJugadoresSimulados()` | 51-60 |
| **Rol en Equipo** | Al formar equipos | `App.java` → `formarEquiposCompletos()` | 76-94 |
| **Rol en Equipo** | Durante simulación | `App.java` → `simularGestionRoles()` | 130-134 |

---

## 🎯 **Ejemplo Completo: Cambiar rol de "ProGamer99"**

### **Opción 1: Cambiar rol preferido (al crear jugador)**

```java
// src/App.java - línea 51
new Jugador("J1", "ProGamer99", 2500, 150, "NA-EAST", 45, Rol.TANQUE, 85, 65)
//                                                                     ^^^^^^^^^^
//                                                                     Cambiado de DPS a TANQUE
```

### **Opción 2: Cambiar rol en equipo (al formar equipo)**

```java
// src/App.java - línea 76
equipo1.agregarJugador(jugadores.get(0), Rol.TANQUE);  // ProGamer99
//                                      ^^^^^^^^^^
//                                      Cambiado de DPS a TANQUE
```

### **Opción 3: Cambiar rol durante simulación (usando comandos)**

```java
// src/App.java - en simularGestionRoles()
Jugador proGamer = equipo.getJugador(Rol.DPS);
AsignarRolCommand cmd = new AsignarRolCommand(equipo, proGamer, Rol.TANQUE);
invoker.ejecutarComando(cmd);
```

---

## ⚠️ **Diferencia entre Rol Preferido y Rol Asignado**

### **Rol Preferido:**
- Se establece **una vez** al crear el jugador
- Es parte del **perfil del jugador**
- Se almacena en `Jugador.rolPreferido`
- Puede ser diferente del rol que juega en un equipo

### **Rol Asignado:**
- Se establece **cada vez** que se forma un equipo
- Es **específico para ese equipo**
- Se almacena en `Equipo.jugadoresPorRol`
- Puede cambiar durante la partida usando comandos

---

## 🔍 **Archivos relacionados:**

1. **`src/App.java`** - Donde se crean jugadores y se asignan roles
2. **`src/model/Jugador.java`** - Clase que almacena el rol preferido
3. **`src/model/Equipo.java`** - Clase que almacena roles asignados en equipos
4. **`src/command/AsignarRolCommand.java`** - Comando para cambiar roles
5. **`src/model/Rol.java`** - Enum con los roles disponibles

---

## 📝 **Nota Final:**

Si quieres cambiar el rol de un jugador **permanentemente** en la simulación:
- Modifica el **rol preferido** en `crearJugadoresSimulados()`
- Modifica el **rol asignado** en `formarEquiposCompletos()`

Si quieres cambiar el rol **durante la simulación** (con capacidad de deshacer):
- Usa **comandos** en `simularGestionRoles()`

---

**Documentación generada:** Cómo Modificar Roles - Integrante 4

