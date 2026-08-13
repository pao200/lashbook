package com.lashbook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private static final long TAMANO_MAXIMO =
        5L * 1024L * 1024L;

    private static final Map<String, String>
        EXTENSIONES_PERMITIDAS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
        );

    private final HttpClient httpClient;
    private final String supabaseUrl;
    private final String secretKey;
    private final String bucket;

    public SupabaseStorageService(
            @Value("${supabase.url}")
            String supabaseUrl,

            @Value("${supabase.secret-key}")
            String secretKey,

            @Value("${supabase.storage.bucket}")
            String bucket
    ) {
        this.supabaseUrl =
            quitarDiagonalFinal(supabaseUrl);

        this.secretKey = secretKey;
        this.bucket = bucket;

        this.httpClient = HttpClient
            .newBuilder()
            .connectTimeout(
                Duration.ofSeconds(15)
            )
            .build();
    }

    public String subirImagen(
            MultipartFile archivo
    ) {
        validarArchivo(archivo);

        String tipoContenido =
            archivo.getContentType();

        String extension =
            EXTENSIONES_PERMITIDAS.get(
                tipoContenido
            );

        String rutaArchivo =
            "imagenes/" +
            UUID.randomUUID() +
            "." +
            extension;

        URI uriSubida = URI.create(
            supabaseUrl +
            "/storage/v1/object/" +
            bucket +
            "/" +
            rutaArchivo
        );

        try {
            HttpRequest request =
                HttpRequest.newBuilder()
                    .uri(uriSubida)
                    .timeout(
                        Duration.ofSeconds(30)
                    )
                    .header(
                        "apikey",
                        secretKey
                    )
                    .header(
                        "Content-Type",
                        tipoContenido
                    )
                    .header(
                        "x-upsert",
                        "false"
                    )
                    .header(
                        "cache-control",
                        "3600"
                    )
                    .POST(
                        HttpRequest.BodyPublishers
                            .ofByteArray(
                                archivo.getBytes()
                            )
                    )
                    .build();

            HttpResponse<String> respuesta =
                httpClient.send(
                    request,
                    HttpResponse.BodyHandlers
                        .ofString()
                );

            if (
                respuesta.statusCode() < 200 ||
                respuesta.statusCode() >= 300
            ) {
                throw new IllegalStateException(
                    "Supabase rechazó la imagen: " +
                    obtenerMensajeSeguro(
                        respuesta.body()
                    )
                );
            }

            return construirUrlPublica(
                rutaArchivo
            );

        } catch (IOException error) {
            throw new IllegalStateException(
                "No fue posible leer o subir la imagen",
                error
            );

        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                "La subida de la imagen fue interrumpida",
                error
            );
        }
    }

    private void validarArchivo(
            MultipartFile archivo
    ) {
        if (
            archivo == null ||
            archivo.isEmpty()
        ) {
            throw new IllegalArgumentException(
                "Debes seleccionar una imagen"
            );
        }

        if (
            archivo.getSize() >
            TAMANO_MAXIMO
        ) {
            throw new IllegalArgumentException(
                "La imagen no puede superar 5 MB"
            );
        }

        String tipoContenido =
            archivo.getContentType();

        if (
            tipoContenido == null ||
            !EXTENSIONES_PERMITIDAS
                .containsKey(tipoContenido)
        ) {
            throw new IllegalArgumentException(
                "Solo se permiten imágenes JPG, PNG o WEBP"
            );
        }
    }

    private String construirUrlPublica(
            String rutaArchivo
    ) {
        return supabaseUrl +
            "/storage/v1/object/public/" +
            bucket +
            "/" +
            rutaArchivo;
    }

    private String obtenerMensajeSeguro(
            String cuerpo
    ) {
        if (
            cuerpo == null ||
            cuerpo.isBlank()
        ) {
            return "respuesta vacía";
        }

        return cuerpo.length() > 300
            ? cuerpo.substring(0, 300)
            : cuerpo;
    }

    private static String quitarDiagonalFinal(
            String valor
    ) {
        if (valor.endsWith("/")) {
            return valor.substring(
                0,
                valor.length() - 1
            );
        }

        return valor;
    }
}