# ❓ ¿Por qué hay cambios de roles en la simulación?

## 📋 **Respuesta Corta:**

Los cambios de roles están ahí **SOLO para DEMOSTRAR el patrón Command**. No son necesarios para el emparejamiento ni afectan a la partida 5 vs 5.

---

## 🔍 **Explicación Detallada:**

### **1. ¿Dónde están los cambios de roles?**

En el método `simularGestionRoles()` en `src/App.java` (líneas 113-158).

### **2. ¿Por qué están ahí?**

Es una **DEMOSTRACIÓN EDUCATIVA** del patrón Command que muestra:
- ✅ Cómo se pueden cambiar roles usando comandos
- ✅ Cómo se puede deshacer/rehacer operaciones
- ✅ Cómo se mantiene un historial de comandos

### **3. ¿Afectan a la partida 5 vs 5?**

**❌ NO.** Los cambios de roles se hacen en un **equipo separado** llamado "Equipo-Demo" que:
- Se crea específicamente para la demostración
- **NO es parte** de los equipos de la partida 5 vs 5
- Es solo una demostración técnica

---

## 📊 **Flujo Actual de la Simulación:**

```
1. Crear 10 jugadores
   ↓
2. Formar 2 equipos completos (Equipo-A y Equipo-B)
   ↓
3. Crear partida 5 vs 5 con esos 2 equipos
   ↓
4. [DEMOSTRACIÓN] Crear "Equipo-Demo" separado
   ↓
5. [DEMOSTRACIÓN] Cambiar roles en "Equipo-Demo" (para mostrar Command Pattern)
   ↓
6. Mostrar estado final (partida 5 vs 5 + demostración)
```

### **Equipos involucrados:**

| Equipo | Propósito | ¿Afecta partida 5 vs 5? |
|--------|-----------|-------------------------|
| **Equipo-A** | Equipo real de la partida | ✅ SÍ |
| **Equipo-B** | Equipo real de la partida | ✅ SÍ |
| **Equipo-Demo** | Solo para demostración | ❌ NO |

---

## 🎯 **¿Son necesarios los cambios de roles?**

### **Para la funcionalidad principal:**
❌ **NO son necesarios** - La partida 5 vs 5 funciona perfectamente sin ellos.

### **Para demostrar el patrón Command:**
✅ **SÍ son útiles** - Muestran cómo funciona el patrón Command en la práctica.

---

## 💡 **Opciones:**

### **Opción 1: Eliminar los cambios de roles**
Si solo quieres ver la partida 5 vs 5 sin demostraciones:

```java
// En main(), comentar o eliminar esta línea:
// simularGestionRoles(jugadores, commandInvoker);
```

### **Opción 2: Mantenerlos como demostración**
Mantenerlos para mostrar cómo funciona el patrón Command (recomendado para fines educativos).

### **Opción 3: Aplicar cambios a los equipos reales**
Si quieres que los cambios de roles afecten a los equipos de la partida 5 vs 5, hay que modificar el código para que trabaje con `equipo1` y `equipo2` en lugar de crear un "Equipo-Demo".

---

## 📝 **Código Actual (simularGestionRoles):**

```java
private static void simularGestionRoles(List<Jugador> jugadores, CommandInvoker invoker) {
    // Crear equipo SEPARADO solo para demostración
    Equipo equipo = new Equipo("Equipo-Demo");  // ← Este equipo NO es parte de la partida 5 vs 5
    
    // Agregar jugadores al equipo demo
    equipo.agregarJugador(jugadores.get(0), Rol.DPS);
    // ...
    
    // DEMOSTRACIÓN: Cambiar rol usando comando
    AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, Rol.TANQUE);
    invoker.ejecutarComando(cmd);
    
    // DEMOSTRACIÓN: Intercambiar roles
    SwapRolesCommand swapCmd = new SwapRolesCommand(equipo, jugador1, jugador2);
    invoker.ejecutarComando(swapCmd);
    
    // Mostrar historial de comandos
    invoker.mostrarHistorial();
}
```

---

## 🎓 **Resumen:**

| Pregunta | Respuesta |
|----------|-----------|
| ¿Por qué hay cambios de roles? | Para **demostrar el patrón Command** |
| ¿Son necesarios? | No, son solo una demostración educativa |
| ¿Afectan la partida 5 vs 5? | No, trabajan en un equipo separado |
| ¿Puedo eliminarlos? | Sí, sin problemas |
| ¿Debo mantenerlos? | Recomendado si quieres mostrar el patrón Command |

---

**Documentación generada:** Explicación de Cambios de Roles - Integrante 4

