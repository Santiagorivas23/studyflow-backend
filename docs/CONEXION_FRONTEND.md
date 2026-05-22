# INSTRUCCIONES DE CONEXIÓN FRONTEND-BACKEND

## Configuración del Frontend

El frontend necesita conocer la URL base del backend. Editar el archivo `.env` en la raíz del frontend:

```env
VITE_API_URL=http://localhost:8080/api
```

Para producción:
```env
VITE_API_URL=https://tu-dominio-backend.com/api
```

## Configuración del Backend

El backend expone la API REST en todos los puertos por CORS. Asegúrate de que la configuración CORS en `config/CorsConfig.java` incluya las URLs de tu frontend.

### Para Desarrollo Local

```bash
# Terminal 1: Backend
cd backend
mvn spring-boot:run
# El backend estará en http://localhost:8080/api

# Terminal 2: Frontend
cd frontend
npm run dev
# El frontend estará en http://localhost:5173
```

### Variables de Entorno Necesarias

#### Backend (.env o variables del sistema)

```
GEMINI_API_KEY=sk-proj-xxx (tu clave de Gemini)
DATABASE_URL=jdbc:postgresql://localhost:5432/studyflow
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=tu_contraseña
```

#### Frontend (.env)

```
VITE_API_URL=http://localhost:8080/api
```

## Autenticación

El backend actualmente usa un header `X-Usuario-Id` para identificar al usuario:

```javascript
// En el cliente (Frontend)
const usuarioId = "usuario-123";
fetch("http://localhost:8080/api/mazos", {
    headers: {
        "X-Usuario-Id": usuarioId,
        "Content-Type": "application/json"
    }
})
```

**Nota**: En producción, implementar autenticación completa con JWT.

## Testing de la API

Usar Postman, Thunder Client o similar:

### Ejemplo: Crear un mazo

```bash
curl -X POST http://localhost:8080/api/mazos \
  -H "Content-Type: application/json" \
  -H "X-Usuario-Id: usuario-123" \
  -d '{
    "nombre": "Patrones de Diseño",
    "descripcion": "Aprende los 12 patrones principales",
    "algoritmo": "SM2"
  }'
```

### Ejemplo: Obtener mazos

```bash
curl http://localhost:8080/api/mazos \
  -H "X-Usuario-Id: usuario-123"
```

## Flujo de Datos

1. **Frontend** solicita `/api/mazos` con header `X-Usuario-Id`
2. **MazoController** recibe la petición
3. **MazoService** obtiene los mazos del usuario
4. **MazoRepository** consulta la base de datos
5. **EntityMapper** convierte entidades a DTOs
6. **Backend** retorna JSON con los DTOs
7. **Frontend** recibe y renderiza los datos

## Solución de Problemas

### CORS Error

Si ves: `Access to XMLHttpRequest has been blocked by CORS policy`

Solución: Verificar que la URL del frontend está en `CorsConfig.java`

```java
.allowedOrigins(
    "http://localhost:5173",  // frontend dev
    "https://tu-dominio.vercel.app"  // frontend prod
)
```

### Database Connection Error

Si ves: `Connection refused on localhost:5432`

Solución:
1. Asegurate que PostgreSQL está corriendo: `psql -U postgres`
2. Verifica la contraseña en `application.properties`
3. Crea la base de datos: `CREATE DATABASE studyflow;`

### Gemini API Error

Si ves: `API key not configured`

Solución: Agregar `GEMINI_API_KEY` a las variables de entorno

```bash
export GEMINI_API_KEY="tu_clave_aqui"
```

## Deployment

### Backend (Railway, Heroku, Render, etc.)

```bash
git push heroku main
```

Configurar variables:
```
DATABASE_URL=postgresql://...
GEMINI_API_KEY=tu_clave
```

### Frontend (Vercel, Netlify)

```bash
git push origin main
```

Configurar variables:
```
VITE_API_URL=https://tu-backend.herokuapp.com/api
```
