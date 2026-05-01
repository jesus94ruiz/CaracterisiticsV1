# Plan de Desarrollo - Cochemon Go

## Estado Actual: FASE 1 COMPLETADA ✅

### Resumen de la Aplicación
"Cochemon Go" es una transformación de la app original de reconocimiento de coches en un juego tipo Pokémon GO donde los usuarios pueden:
- Capturar coches tomando fotos en ubicaciones reales
- Ver un mapa con sus capturas y ubicación actual
- Descubrir puntos de interés con coches especiales
- Coleccionar y mejorar coches
- Subir de nivel según coches capturados

---

## FASE 1: Sistema de Mapa y Ubicación ✅ COMPLETADO

### Archivos Creados/Modificados:

1. **Base de Datos Actualizada**
   - `modelEntity.kt` - Añadidos campos `latitude`, `longitude`, `captureDate`

2. **Repositorio de Ubicación**
   - `LocationRepository.kt` - Gestión de permisos y obtención de ubicación GPS

3. **ViewModel del Mapa**
   - `MapViewModel.kt` - Lógica del mapa con marcadores de coches capturados

4. **Pantalla del Mapa**
   - `MapScreen.kt` - Interfaz del mapa con Google Maps y estilo personalizado

5. **Configuración**
   - `build.gradle` - Dependencias de Google Maps y Play Services
   - `AndroidManifest.xml` - Permisos de ubicación y API key
   - `.env.example` - Template para Google Maps API Key

6. **Navegación y UI**
   - `AppScreens.kt` - Nueva pantalla MapScreen
   - `AppNavigation.kt` - Ruta al mapa
   - `MainScreen.kt` - Botón de acceso al mapa
   - `AppModule.kt` - Inyección de dependencias para ubicación

### Características Implementadas:
- ✅ Mapa con ubicación del usuario en tiempo real
- ✅ Marcadores mostrando dónde se capturaron coches
- ✅ Estilo de mapa personalizado (oscuro)
- ✅ Sistema de permisos de ubicación
- ✅ Base de datos actualizada con coordenadas
- ✅ Navegación al mapa desde menú principal

### Próximos Pasos para Usar la Fase 1:
1. Obtener Google Maps API Key desde [Google Cloud Console](https://console.cloud.google.com/)
2. Crear archivo `.env` con: `GOOGLE_MAPS_API_KEY=tu_api_key_aqui`
3. Sincronizar Gradle
4. Compilar y ejecutar la app
5. Dar permisos de ubicación cuando se soliciten
6. Tomar fotos de coches - se guardarán con ubicación
7. Ir al mapa para ver dónde capturaste cada coche

---

## FASE 2: Sistema de Puntos de Interés (POI)

### Objetivos:
Implementar puntos especiales en el mapa donde los usuarios pueden obtener recompensas.

### Tareas:

#### 2.1 Entidades y Base de Datos
- [ ] Crear entidad `PoiEntity` con campos:
  - id, nombre, descripción, tipo (SPECIAL_CAR, UPGRADE_PARTS, BONUS_XP)
  - latitude, longitude, radius
  - rewardType, rewardId, isActive, cooldownMinutes
- [ ] Crear DAO `PoiDao` con queries básicas
- [ ] Actualizar `ModelDatabase` con nueva tabla

#### 2.2 Modelos y Repositorio
- [ ] Crear `data class PoiModel` en domain
- [ ] Crear `PoiRepository` con métodos:
  - getNearbyPois(userLat, userLng, radiusKm)
  - checkPoiInteraction(poiId, userLat, userLng)
  - collectPoiReward(poiId)
  - updatePoiCooldown(poiId)

#### 2.3 Integración con Mapa
- [ ] Actualizar `MapViewModel`:
  - Cargar POIs cercanos
  - Verificar proximidad del usuario
  - Manejar interacción con POIs
- [ ] Actualizar `MapScreen`:
  - Mostrar marcadores de POI con iconos diferentes según tipo
  - Añadir diálogo al tocar POI mostrando recompensa
  - Animación cuando usuario está cerca de POI
  - Botón "Recolectar" cuando está en rango

#### 2.4 Sistema de Recompensas
- [ ] Crear `RewardManager` para gestionar tipos de recompensa
- [ ] Implementar lógica de cooldown para POIs
- [ ] Notificación cuando POI está disponible cerca

#### 2.5 Datos de Prueba
- [ ] Crear POIs de ejemplo en ubicaciones fijas para testing
- [ ] Script para generar POIs aleatorios en área de prueba

### Estimación: 8-10 horas

---

## FASE 3: Sistema de Experiencia y Niveles

### Objetivos:
Gamificar la captura de coches con sistema de XP y niveles.

### Tareas:

#### 3.1 Base de Datos de Usuario
- [ ] Crear entidad `UserProfileEntity`:
  - userId (siempre 1 para single-user)
  - username, level, currentXp, totalXp
  - carsCollected, totalCaptures, distance traveled
  - achievements, createdDate
- [ ] Crear DAO `UserProfileDao`
- [ ] Actualizar database

#### 3.2 Sistema de XP
- [ ] Crear `XpManager` con reglas:
  - Coche común: +10 XP
  - Coche raro: +25 XP
  - Coche épico: +50 XP
  - Primera captura de modelo: +20 XP bonus
  - POI visitado: +15 XP
  - Completar colección de marca: +100 XP
- [ ] Calcular XP necesario por nivel: `requiredXp = baseXp * (level ^ 1.5)`
- [ ] Métodos para añadir XP y subir nivel

#### 3.3 UI de Progreso
- [ ] Crear `ProfileScreen` mostrando:
  - Avatar/icono de usuario
  - Nivel actual y barra de progreso XP
  - Estadísticas (coches capturados, distancia, etc.)
  - Logros desbloqueados
- [ ] Añadir barra de XP mini en pantalla principal
- [ ] Animación de "Level Up" cuando sube nivel
- [ ] Diálogo mostrando recompensas de nuevo nivel

#### 3.4 Sistema de Logros
- [ ] Crear lista de logros:
  - "Primera Captura", "Coleccionista" (10, 50, 100 coches)
  - "Explorador" (visitar 5, 10, 20 POIs)
  - "Especialista" (10 coches de misma marca)
  - "Velocista" (capturar coche deportivo)
- [ ] UI para ver logros y progreso

#### 3.5 Integración
- [ ] Actualizar `CameraViewModel` para añadir XP tras captura
- [ ] Actualizar `MapViewModel` para XP de POIs
- [ ] Notificaciones de logros desbloqueados

### Estimación: 10-12 horas

---

## FASE 4: Sistema de Rareza y Coches Especiales

### Objetivos:
Añadir sistema de rareza para hacer coches más valiosos.

### Tareas:

#### 4.1 Sistema de Rareza
- [ ] Añadir campo `rarity` a `modelEntity`: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
- [ ] Añadir campo `specialAttributes` (JSON):
  - powerLevel, speed, handling, acceleration
  - specialAbility (ej: "turbo", "drift master")
- [ ] Actualizar DAO y migraciones

#### 4.2 Algoritmo de Rareza
- [ ] Crear `RarityCalculator`:
  - Por marca (Ferrari/Lamborghini = más raras)
  - Por edad del modelo (clásicos raros)
  - Por ubicación (coches exóticos en ciertas zonas)
  - Por horario (coches nocturnos)
  - Factor aleatorio (5% épico, 1% legendario)

#### 4.3 POIs de Coches Especiales
- [ ] Implementar POIs tipo "Concesionario Premium"
- [ ] Spawns temporales de coches legendarios en mapa
- [ ] Sistema de "Evento" con coches especiales por tiempo limitado

#### 4.4 UI de Rareza
- [ ] Colores por rareza en tarjetas de garaje
- [ ] Efectos visuales al capturar coche raro
- [ ] Filtros en garaje por rareza
- [ ] "Pokedex" mostrando rareza y stats

#### 4.5 Coches Únicos
- [ ] Implementar "Coches Legendarios" únicos:
  - Solo 1 captura posible
  - Requisitos especiales (nivel mínimo, logros)
  - Aparecen en ubicaciones/eventos específicos

### Estimación: 12-15 horas

---

## FASE 5: Sistema de Mejoras y Piezas

### Objetivos:
Permitir mejorar coches capturados con piezas obtenidas.

### Tareas:

#### 5.1 Entidad de Piezas
- [ ] Crear `CarPartEntity`:
  - id, name, type (ENGINE, TIRES, TURBO, PAINT, etc.)
  - rarity, statBoost (JSON con mejoras)
  - quantity, description, iconResource
- [ ] Crear DAO y actualizar database

#### 5.2 Sistema de Inventario
- [ ] Crear `InventoryRepository`
- [ ] Métodos para añadir/gastar piezas
- [ ] UI de inventario mostrando piezas disponibles

#### 5.3 Sistema de Upgrade
- [ ] Añadir campos a `modelEntity`:
  - upgradeLevel, installedParts (JSON)
  - customizations
- [ ] Crear `UpgradeManager`:
  - Aplicar pieza a coche
  - Calcular nuevos stats
  - Validar compatibilidad
  - Costo en piezas

#### 5.4 UI de Mejoras
- [ ] Pantalla de detalle de coche con:
  - Stats actuales vs potenciales
  - Slots de piezas instaladas
  - Botón "Mejorar"
- [ ] Diálogo de selección de pieza
- [ ] Animación al instalar pieza
- [ ] Preview de stats tras mejora

#### 5.5 Obtención de Piezas
- [ ] POIs tipo "Taller Mecánico" dando piezas
- [ ] Recompensa por subir nivel
- [ ] Recompensa por logros
- [ ] Sistema de "desmantelar" coches duplicados → piezas

### Estimación: 10-12 horas

---

## FASE 6: Funcionalidades Sociales y Competitivas

### Objetivos:
Añadir elementos sociales básicos.

### Tareas:

#### 6.1 Sistema de Comparación
- [ ] Crear `LeaderboardRepository` (local primero)
- [ ] Pantalla de clasificación mostrando:
  - Top por nivel
  - Top por coches capturados
  - Top por coches raros
  - Top por distancia recorrida

#### 6.2 Compartir Capturas
- [ ] Botón para compartir captura:
  - Imagen del coche
  - Stats y rareza
  - Ubicación (opcional)
  - Texto generado automáticamente
- [ ] Integración con Android Share Intent

#### 6.3 Desafíos Diarios
- [ ] Sistema de misiones diarias:
  - "Captura 3 coches hoy"
  - "Visita 2 POIs"
  - "Captura un coche deportivo"
- [ ] Recompensas por completar
- [ ] UI mostrando progreso

#### 6.4 Eventos Temporales
- [ ] Sistema de eventos:
  - Fin de semana con XP x2
  - Spawn aumentado de coches raros
  - POIs especiales de evento
- [ ] Notificaciones de eventos activos

### Estimación: 12-15 horas

---

## FASE 7: Optimización y Pulido

### Objetivos:
Mejorar rendimiento y experiencia de usuario.

### Tareas:

#### 7.1 Rendimiento
- [ ] Optimizar queries de base de datos
- [ ] Lazy loading de imágenes en garaje
- [ ] Caché de datos de mapa
- [ ] Reducir consumo de batería en modo mapa

#### 7.2 Animaciones y Feedback
- [ ] Animación de captura exitosa
- [ ] Partículas al recolectar POI
- [ ] Transiciones suaves entre pantallas
- [ ] Haptic feedback en acciones importantes

#### 7.3 Tutorial
- [ ] Crear tutorial inicial:
  - Explicar cómo capturar
  - Mostrar mapa y POIs
  - Explicar sistema de XP
  - Guiar primera mejora
- [ ] Tooltips contextuales

#### 7.4 Configuración Avanzada
- [ ] Opciones de mapa (satélite/terreno)
- [ ] Radio de búsqueda de POIs
- [ ] Notificaciones personalizables
- [ ] Modo de ahorro de datos

#### 7.5 Testing y Bug Fixes
- [ ] Testear permisos en diferentes versiones Android
- [ ] Testear sin conexión (modo offline básico)
- [ ] Verificar consumo de recursos
- [ ] Fix bugs reportados

### Estimación: 15-20 horas

---

## IDEAS ADICIONALES PARA FUTURAS VERSIONES

### Corto Plazo:
1. **Modo AR (Realidad Aumentada)**
   - Ver modelo 3D del coche en AR al capturarlo
   - Requiere: ARCore, modelos 3D

2. **Sistema de Intercambio**
   - Intercambiar coches duplicados entre usuarios
   - Requiere: Backend, autenticación

3. **Garaje Visual Mejorado**
   - Vista 3D del garaje mostrando coches
   - Organizarlos por categorías

### Medio Plazo:
4. **Batallas de Coches**
   - Competir usando stats de coches mejorados
   - Sistema por turnos o automático

5. **Clubes/Equipos**
   - Crear/unirse a clubes
   - Objetivos de equipo
   - Chat de equipo

6. **Misiones y Historia**
   - Campaña con misiones conectadas
   - Desbloquear coches especiales por completar

### Largo Plazo:
7. **Backend Real**
   - Firebase o backend custom
   - Sincronización multi-dispositivo
   - Ranking global

8. **Mapa Colaborativo**
   -
