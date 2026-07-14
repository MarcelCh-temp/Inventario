# Microservicio de Inventario - VetNova

## Descripción
Microservicio encargado de gestionar el inventario de la clínica veterinaria VetNova. Administra el catálogo de productos, el stock por sucursal, los movimientos de bodega (entradas, salidas, traslados y ajustes), proveedores y órdenes de compra. Expone además un endpoint de verificación de disponibilidad consumido internamente por el microservicio de Ventas antes de confirmar cualquier compra.

## Integrantes
- Sebastián Miranda
- Vicente Provoste
- Bastián Chamblas

## Tecnologías
- Java 25
- Spring Boot 4.1.0
- MySQL
- RestTemplate (comunicación HTTP con Autenticación)
- JUnit 5 + Mockito + AssertJ
- JaCoCo (cobertura mínima 100%)
- Swagger / OpenAPI (springdoc-openapi 3.0.3)

## Endpoints principales

### Productos (`/api/productos`)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/productos | Crear producto |
| GET | /api/productos | Listar todos los productos |
| GET | /api/productos/{id} | Obtener producto por ID |
| GET | /api/productos/categoria/{categoria} | Listar por categoría |
| GET | /api/productos/tipo/{tipoUso} | Listar por tipo de uso |
| PUT | /api/productos/{id} | Actualizar producto |
| PATCH | /api/productos/{id}/desactivar | Desactivar producto (soft delete) |

### Stock (`/api/stock`)
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | /api/stock | Listar todo el stock |
| GET | /api/stock/sucursal/{sucursalId} | Stock de una sucursal |
| GET | /api/stock/producto/{productoId} | Stock de un producto |
| GET | /api/stock/bajo-minimo | Productos bajo su stock mínimo |
| POST | /api/stock/verificar-disponibilidad | Verificar si hay stock para una lista de productos |

### Movimientos de bodega (`/api/movimientos`)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/movimientos | Registrar movimiento (ENTRADA / SALIDA / TRASLADO / AJUSTE) |
| GET | /api/movimientos | Listar todos los movimientos |
| GET | /api/movimientos/producto/{productoId} | Movimientos de un producto |
| GET | /api/movimientos/sucursal/{sucursalId} | Movimientos de una sucursal |

### Proveedores (`/api/proveedores`)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/proveedores | Crear proveedor |
| GET | /api/proveedores | Listar todos los proveedores |
| GET | /api/proveedores/{id} | Obtener proveedor por ID |
| PUT | /api/proveedores/{id} | Actualizar proveedor |
| PATCH | /api/proveedores/{id}/desactivar | Desactivar proveedor |

### Pedidos a proveedor (`/api/pedidos`)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/pedidos | Crear orden de compra |
| GET | /api/pedidos | Listar todos los pedidos |
| GET | /api/pedidos/{id} | Obtener pedido por ID |
| PUT | /api/pedidos/{id}/estado | Actualizar estado del pedido |
| POST | /api/pedidos/{id}/recibir | Registrar recepción de mercadería |

## Categorías de producto
`MEDICAMENTO` · `ALIMENTO` · `ACCESORIO` · `HIGIENE` · `EQUIPAMIENTO`

## Tipos de uso
`USO_CLINICO` · `VENTA`

## Tipos de movimiento de stock
| Tipo | Efecto en stock |
|------|----------------|
| `ENTRADA` | Suma al disponible |
| `TRASLADO` | Suma al disponible (origen del traslado) |
| `AJUSTE` | Suma al disponible (corrección de inventario) |
| `SALIDA` | Resta del disponible (falla si no alcanza) |

## Comunicación con otros microservicios
| Destino | Propósito |
|---------|-----------|
| Autenticación `:8081` | Valida el token del usuario en cada movimiento de bodega |

## Documentación Swagger
- Local: http://localhost:8085/swagger-ui/index.html
- JSON: http://localhost:8085/v3/api-docs

## Base de datos
- Nombre: `vetnova_inventario`
- Motor: MySQL
- Tablas principales: `productos`, `stock`, `movimientos_inventario`, `proveedores`, `pedidos_proveedor`, `detalles_pedido_proveedor`

## Ejecución local
1. Tener XAMPP corriendo con MySQL activo
2. Asegurarse de que la base de datos `vetnova_inventario` existe
3. Asegurarse de que el microservicio de **Autenticación** está corriendo en el puerto 8081
4. Ajustar usuario y contraseña en `application.properties`
5. Ejecutar:
```bash
./mvnw spring-boot:run
```
6. El servicio estará disponible en el puerto **8085**

## Pruebas unitarias
```bash
./mvnw test
```

## Cobertura de código
```bash
./mvnw verify
```
El reporte HTML se genera en `target/site/jacoco/index.html`. El build falla si la cobertura de líneas cae por debajo del **100%** en las clases de servicio, controladores y manejo de excepciones.
