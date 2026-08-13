package com.lashbook.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "servicios_busqueda")
public class ServicioBusquedaDocument {

    @Id
    private String id;

    @Field(type = FieldType.Search_As_You_Type)
    private String nombre;

    @Field(type = FieldType.Text)
    private String descripcion;

    @Field(type = FieldType.Double)
    private Double precio;

    @Field(type = FieldType.Integer)
    private Integer duracionMinutos;

    @Field(type = FieldType.Keyword)
    private String imagenUrl;

    @Field(type = FieldType.Boolean)
    private Boolean activo;

    public ServicioBusquedaDocument() {
    }

    public ServicioBusquedaDocument(
        String id,
        String nombre,
        String descripcion,
        Double precio,
        Integer duracionMinutos,
        String imagenUrl,
        Boolean activo
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}