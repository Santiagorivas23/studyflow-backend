package co.studyflow.controller;

import co.studyflow.dto.MazoDTO;
import co.studyflow.exception.ResourceNotFoundException;
import co.studyflow.model.Mazo;
import co.studyflow.model.Tarjeta;
import co.studyflow.patterns.factory.Exportador;
import co.studyflow.patterns.factory.ExportadorFactory;
import co.studyflow.service.MazoService;
import co.studyflow.service.TarjetaService;
import co.studyflow.util.EntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de mazos
 */
@RestController
@RequestMapping("/mazos")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://*.vercel.app"})
public class MazoController {

    private static final Logger logger = LoggerFactory.getLogger(MazoController.class);

    @Autowired
    private MazoService mazoService;
    @Autowired
    private TarjetaService tarjetaService;

    /**
     * GET /api/mazos - Obtener todos los mazos del usuario
     */
    @GetMapping
    public ResponseEntity<List<MazoDTO>> obtenerTodos(Authentication auth) {
        String usuarioId = auth.getName();
        List<Mazo> mazos = mazoService.obtenerMazosDelUsuario(usuarioId);
        List<MazoDTO> dtos = mazos.stream()
                .map(EntityMapper::toMazoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/mazos/{id} - Obtener mazo específico
     */
    @GetMapping("/{id}")
    public ResponseEntity<MazoDTO> obtenerPorId(@PathVariable String id, Authentication auth) {
        Mazo mazo = mazoService.obtenerPorId(id, auth.getName());
        return ResponseEntity.ok(EntityMapper.toMazoDTO(mazo));
    }

    /**
     * POST /api/mazos - Crear nuevo mazo
     */
    @PostMapping
    public ResponseEntity<MazoDTO> crear(@RequestBody MazoDTO mazoDTO, Authentication auth) {
        Mazo mazo = mazoService.crear(mazoDTO, auth.getName());
        return ResponseEntity.status(201).body(EntityMapper.toMazoDTO(mazo));
    }

    /**
     * PUT /api/mazos/{id} - Actualizar mazo
     */
    @PutMapping("/{id}")
    public ResponseEntity<MazoDTO> actualizar(
            @PathVariable String id,
            @RequestBody MazoDTO mazoDTO,
            Authentication auth
    ) {
        Mazo mazo = mazoService.actualizar(id, mazoDTO, auth.getName());
        return ResponseEntity.ok(EntityMapper.toMazoDTO(mazo));
    }

    /**
     * DELETE /api/mazos/{id} - Eliminar mazo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id, Authentication auth) {
        mazoService.eliminar(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/mazos/{id}/duplicar - Duplicar mazo
     */
    @PostMapping("/{id}/duplicar")
    public ResponseEntity<MazoDTO> duplicar(@PathVariable String id, Authentication auth) {
        Mazo mazoDuplicado = mazoService.duplicar(id, auth.getName());
        return ResponseEntity.status(201).body(EntityMapper.toMazoDTO(mazoDuplicado));
    }

    /**
     * GET/POST /api/mazos/{id}/exportar - Exportar mazo
     */
    @GetMapping("/{id}/exportar")
    public ResponseEntity<?> exportarGet(
            @PathVariable String id,
            @RequestParam(defaultValue = "json") String formato,
            Authentication auth
    ) {
        return exportarMazo(id, formato, auth);
    }

    @PostMapping("/{id}/exportar")
    public ResponseEntity<?> exportar(
            @PathVariable String id,
            @RequestParam(defaultValue = "json") String formato,
            Authentication auth
    ) {
        return exportarMazo(id, formato, auth);
    }

    private ResponseEntity<?> exportarMazo(String id, String formato, Authentication auth) {
        String usuarioId = (auth != null) ? auth.getName() : null;
        logger.info("Exportar mazo {} formato={} usuario={}", id, formato, usuarioId);

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token JWT ausente o inválido");
        }

        try {
            Mazo mazo = mazoService.obtenerPorId(id, usuarioId);

            // Cargar tarjetas explícitamente para evitar LazyInitializationException
            // (la colección Mazo.tarjetas es LAZY y la sesión Hibernate ya está cerrada)
            List<Tarjeta> tarjetas = tarjetaService.obtenerPorMazo(mazo.getId());

            Exportador exportador = ExportadorFactory.crearExportador(formato);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            exportador.exportar(tarjetas, output);

            byte[] data = output.toByteArray();
            String nombreArchivo = exportador.obtenerNombreArchivo(mazo.getNombre());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                    .contentType(MediaType.parseMediaType(exportador.obtenerTipoContenido()))
                    .body(data);
        } catch (ResourceNotFoundException e) {
            logger.warn("Mazo no encontrado al exportar {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetro inválido al exportar mazo {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al exportar mazo {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al exportar: " + e.getMessage());
        }
    }

    /**
     * POST /api/mazos/{id}/importar - Importar tarjetas a un mazo (body JSON)
     * Acepta el mismo formato que produce el endpoint /exportar.
     */
    @PostMapping(value = "/{id}/importar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importarPorMazo(
            @PathVariable String id,
            @RequestBody String body,
            Authentication auth
    ) {
        String usuarioId = (auth != null) ? auth.getName() : null;
        logger.info("Importar tarjetas a mazo {} usuario={}", id, usuarioId);
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT ausente o inválido");
        }
        try {
            return ResponseEntity.status(201).body(procesarImportacion(id, body, usuarioId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al importar mazo {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al importar: " + e.getMessage());
        }
    }

    private MazoDTO procesarImportacion(String mazoId, String content, String usuarioId) throws IOException {
        Mazo mazo = mazoService.obtenerPorId(mazoId, usuarioId);
        List<co.studyflow.dto.TarjetaDTO> tarjetasDto = parsearTarjetasJson(content);
        if (tarjetasDto.isEmpty()) {
            throw new IOException("No se encontraron tarjetas en el archivo");
        }
        tarjetasDto.forEach(t -> {
            t.setMazoId(mazo.getId());
            if (t.getConPista() == null) t.setConPista(false);
            if (t.getEtiquetas() == null) t.setEtiquetas(new java.util.ArrayList<>());
        });
        tarjetaService.crearMultiples(mazo.getId(), tarjetasDto);
        return EntityMapper.toMazoDTO(mazoService.obtenerPorId(mazo.getId(), usuarioId));
    }

    private List<co.studyflow.dto.TarjetaDTO> parsearTarjetasJson(String content) throws IOException {
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapter(java.time.LocalDateTime.class, new co.studyflow.util.LocalDateTimeAdapter())
                .create();
        List<co.studyflow.dto.TarjetaDTO> tarjetasDto = new java.util.ArrayList<>();
        try {
            com.google.gson.JsonElement el = gson.fromJson(content.trim(), com.google.gson.JsonElement.class);
            com.google.gson.JsonArray arr = null;
            if (el.isJsonArray()) {
                arr = el.getAsJsonArray();
            } else if (el.isJsonObject()) {
                com.google.gson.JsonObject obj = el.getAsJsonObject();
                if (obj.has("tarjetas") && obj.get("tarjetas").isJsonArray()) {
                    arr = obj.getAsJsonArray("tarjetas");
                }
            }
            if (arr != null) {
                for (com.google.gson.JsonElement e : arr) {
                    tarjetasDto.add(gson.fromJson(e, co.studyflow.dto.TarjetaDTO.class));
                }
            }
        } catch (Exception ex) {
            throw new IOException("Formato de archivo inválido: " + ex.getMessage(), ex);
        }
        return tarjetasDto;
    }

    /**
     * POST /api/mazos/importar - Importar mazo desde archivo (multipart)
     */
    @PostMapping("/importar")
    public ResponseEntity<MazoDTO> importar(
            @RequestParam MultipartFile archivo,
            Authentication auth
    ) throws IOException {
        String usuarioId = auth.getName();
        String content = new String(archivo.getBytes(), StandardCharsets.UTF_8).trim();
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
            .registerTypeAdapter(java.time.LocalDateTime.class, new co.studyflow.util.LocalDateTimeAdapter())
            .create();

        java.util.List<co.studyflow.dto.TarjetaDTO> tarjetasDto;
        try {
            tarjetasDto = new java.util.ArrayList<>();
            com.google.gson.JsonElement el = gson.fromJson(content, com.google.gson.JsonElement.class);
            if (el.isJsonArray()) {
                com.google.gson.JsonArray arr = el.getAsJsonArray();
                for (com.google.gson.JsonElement e : arr) {
                    co.studyflow.dto.TarjetaDTO dto = gson.fromJson(e, co.studyflow.dto.TarjetaDTO.class);
                    tarjetasDto.add(dto);
                }
            } else if (el.isJsonObject()) {
                com.google.gson.JsonObject obj = el.getAsJsonObject();
                if (obj.has("tarjetas") && obj.get("tarjetas").isJsonArray()) {
                    com.google.gson.JsonArray arr = obj.getAsJsonArray("tarjetas");
                    for (com.google.gson.JsonElement e : arr) {
                        co.studyflow.dto.TarjetaDTO dto = gson.fromJson(e, co.studyflow.dto.TarjetaDTO.class);
                        tarjetasDto.add(dto);
                    }
                }
            }
        } catch (Exception ex) {
            throw new IOException("Formato de archivo inválido: " + ex.getMessage(), ex);
        }

        if (tarjetasDto.isEmpty()) {
            throw new IOException("No se encontraron tarjetas en el archivo");
        }

        String mazoId = tarjetasDto.get(0).getMazoId();
        if (mazoId == null || mazoId.isBlank()) {
            throw new IllegalArgumentException("El archivo debe contener 'mazoId' en las tarjetas o indicar el mazo destino");
        }

        co.studyflow.model.Mazo mazo = mazoService.obtenerPorId(mazoId, usuarioId);

        tarjetasDto.forEach(t -> t.setMazoId(mazo.getId()));
        tarjetasDto.forEach(t -> {
            if (t.getConPista() == null) t.setConPista(false);
            if (t.getEtiquetas() == null) t.setEtiquetas(new java.util.ArrayList<>());
        });

        tarjetaService.crearMultiples(mazo.getId(), tarjetasDto);

        MazoDTO actualizado = EntityMapper.toMazoDTO(mazoService.obtenerPorId(mazo.getId(), usuarioId));
        return ResponseEntity.status(201).body(actualizado);
    }
}
