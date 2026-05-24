package com.miapp.software.service;

import com.miapp.software.model.Documento;
import com.miapp.software.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    // Directorio donde se guardarán físicamente los archivos
    private final String UPLOAD_DIR = "uploads/documentos/";

    // RF-15 - Subir documento (MO-03, C.U.4)
    public Documento subirDocumento(Documento documento, MultipartFile archivo) throws IOException {
        // RNF-09: Validar formato y tamaño
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        if (archivo.getSize() > 5 * 1024 * 1024) { // 5MB máx
            throw new IllegalArgumentException("El archivo no puede superar los 5MB");
        }

        // Validar formato (PDF, JPG, PNG)
        String formato = obtenerExtension(archivo.getOriginalFilename());
        if (!formato.equals("pdf") && !formato.equals("jpg") && !formato.equals("png")) {
            throw new IllegalArgumentException("Formato no permitido. Solo PDF, JPG, PNG");
        }

        // Guardar archivo en servidor físico
        String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
        Path ruta = Paths.get(UPLOAD_DIR + nombreArchivo);

        // Crear las carpetas si no existen
        Files.createDirectories(ruta.getParent());
        archivo.transferTo(ruta);

        // Guardar metadata en la Base de Datos
        documento.setNombreArchivo(archivo.getOriginalFilename());
        documento.setRutaArchivo(ruta.toString());
        documento.setFormato(formato.toUpperCase());
        documento.setTamañoBytes((int) archivo.getSize());
        documento.setFechaSubida(LocalDateTime.now());
        // Nota: Asegúrate que en tu modelo 'Documento' el campo sea 'isValidado' o 'validado'
        documento.setValidado(false);

        return documentoRepository.save(documento);
    }

    // Obtener extensión del archivo
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) return "";
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
    }

    // Listar documentos de una solicitud
    public List<Documento> listarPorSolicitud(Integer idSolicitud) {
        return documentoRepository.findBySolicitud_IdSolicitud(idSolicitud);
    }

    // C.U.4 - Validar documento individual
    public Documento validarDocumento(Integer id, boolean valido, String observaciones) {
        return documentoRepository.findById(id).map(documento -> {
            documento.setValidado(valido);
            documento.setObservaciones(observaciones);
            return documentoRepository.save(documento);
        }).orElse(null);
    }

    // RNF-10 - Obtener metadata de documento
    public Documento obtenerDocumento(Integer id) {
        return documentoRepository.findById(id).orElse(null);
    }
}