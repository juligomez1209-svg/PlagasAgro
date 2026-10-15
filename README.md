# Simulador de Propagacion de Plagas y Dinamica de Infestacion - Proyecto 3

Java 17+ | Swing | Estructuras propias (sin java.util.*, salvo Random)

## Modelo matematico (por celda, cada dia)
`v' = v + (1 - r) * [ g*v*(1 - v) + d*(promedio_vecinos - v) ]`
v = infestacion [0,1], r = resistencia, g = crecimiento logistico, d = difusion espacial.
Costo por dia: O(n^2).

## Mapa de estructuras obligatorias
| Estructura | Campo | Uso agro |
|---|---|---|
| Matriz 2D | `inf`, `res` | Grilla de infestacion y resistencia por planta |
| Vector 1D | `focos` | Coordenadas (fila,col) de los focos iniciales |
| Lista circular | `enjambres` | Insectos en migracion (un enjambre por dia, round-robin) |
| Lista doble | `resistentes` | Plantas que desarrollan resistencia (recorrido en ambos sentidos) |
| Lista simple | `tratamientos` | Registro de tratamientos aplicados |
| Pila | `snapshots` | Instantaneas historicas (deshacer) |
| Cola | `colaCuarentena` | Cuarentena sanitaria: celdas criticas tratadas en orden FIFO |

## Compilar y ejecutar
```
cd src
mkdir ../out
javac -d ../out estructuras/*.java modelo/*.java gui/*.java
java -cp ../out gui.PlagasApp       # GUI
java -cp ../out modelo.Prueba       # prueba de consola
```
## Uso
Nuevo lote -> Paso o Auto (la plaga se propaga) -> las celdas con infestacion > 70 % entran a la cola de cuarentena (borde azul) -> Tratar cuarentena atiende la mas antigua -> Deshacer restaura el dia anterior. Clic en el lote agrega un foco.
