package com.lashbook.dto;

public class ImagenServicioResponse {

    private final String imagenUrl;

    public ImagenServicioResponse(
            String imagenUrl
    ) {
        this.imagenUrl = imagenUrl;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }
}