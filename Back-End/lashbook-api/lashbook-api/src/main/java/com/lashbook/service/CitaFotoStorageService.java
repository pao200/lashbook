package com.lashbook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.UUID;

@Service
public class CitaFotoStorageService {

    private static final String BUCKET =
        "resultados-citas";

    private static final long MAX_SIZE =
        5 * 1024 * 1024;

    @Value("${SUPABASE_URL}")
    private String supabaseUrl;

    @Value("${SUPABASE_SECRET_KEY}")
    private String supabaseSecretKey;

    private final HttpClient httpClient =
        HttpClient.newHttpClient();

    public String subirFoto(
            UUID citaId,
            MultipartFile foto
    ) {
        validarFoto(foto);

        try {
            String extension =
                obtenerExtension(
                    foto.getOriginalFilename()
                );

            String nombreArchivo =
                UUID.randomUUID()
                    + extension;

            String ruta =
                "citas/"
                    + citaId
                    + "/"
                    + nombreArchivo;

            String urlSubida =
                supabaseUrl
                    + "/storage/v1/object/"
                    + BUCKET
                    + "/"
                    + ruta;

            String contentType =
                foto.getContentType();

            HttpRequest request =
                HttpRequest.newBuilder()
                    .uri(
                        URI.create(
                            urlSubida
                        )
                    )
                    .header(
                        "Authorization",
                        "Bearer "
                            + supabaseSecretKey
                    )
                    .header(
                        "apikey",
                        supabaseSecretKey
                    )
                    .header(
                        "Content-Type",
                        contentType
                    )
                    .header(
                        "x-upsert",
                        "true"
                    )
                    .POST(
                        HttpRequest
                            .BodyPublishers
                            .ofByteArray(
                                foto.getBytes()
                            )
                    )
                    .build();

            HttpResponse<String>
                response =
                    httpClient.send(
                        request,
                        HttpResponse
                            .BodyHandlers
                            .ofString()
                    );

            if (
                response.statusCode() < 200 ||
                response.statusCode() >= 300
            ) {
                throw new IllegalStateException(
                    "Supabase Storage respondió "
                        + response.statusCode()
                        + ": "
                        + response.body()
                );
            }

            return supabaseUrl
                + "/storage/v1/object/public/"
                + BUCKET
                + "/"
                + ruta;

        } catch (Exception error) {
            throw new IllegalStateException(
                "No fue posible guardar la fotografía",
                error
            );
        }
    }

    private void validarFoto(
            MultipartFile foto
    ) {
        if (
            foto == null ||
            foto.isEmpty()
        ) {
            throw new IllegalArgumentException(
                "Debes enviar una fotografía"
            );
        }

        if (
            foto.getSize() >
            MAX_SIZE
        ) {
            throw new IllegalArgumentException(
                "La fotografía no puede superar 5 MB"
            );
        }

        String contentType =
            foto.getContentType();

        if (
            contentType == null ||
            !contentType
                .toLowerCase(
                    Locale.ROOT
                )
                .startsWith(
                    "image/"
                )
        ) {
            throw new IllegalArgumentException(
                "El archivo debe ser una imagen"
            );
        }
    }

    private String obtenerExtension(
            String nombreOriginal
    ) {
        if (
            nombreOriginal == null ||
            !nombreOriginal.contains(".")
        ) {
            return ".jpg";
        }

        String extension =
            nombreOriginal.substring(
                nombreOriginal
                    .lastIndexOf(".")
            );

        if (
            extension.length() > 6
        ) {
            return ".jpg";
        }

        return extension.toLowerCase(
            Locale.ROOT
        );
    }
}