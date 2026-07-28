package com.lashbook.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import java.util.NoSuchElementException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>>
    manejarArgumentoInvalido(
            IllegalArgumentException exception
    ) {
        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", HttpStatus.CONFLICT.value());
        respuesta.put("mensaje", exception.getMessage());

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(respuesta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>>
    manejarValidaciones(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errores = new LinkedHashMap<>();

        exception.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                errores.put(
                    error.getField(),
                    error.getDefaultMessage()
                )
            );

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", HttpStatus.BAD_REQUEST.value());
        respuesta.put("errores", errores);

        return ResponseEntity
            .badRequest()
            .body(respuesta);
    }
    @ExceptionHandler(BadCredentialsException.class)
      public ResponseEntity<Map<String, Object>>
       manejarCredencialesIncorrectas(
        BadCredentialsException exception
    ) {
    Map<String, Object> respuesta = new LinkedHashMap<>();

    respuesta.put("fecha", LocalDateTime.now());
    respuesta.put("estado", HttpStatus.UNAUTHORIZED.value());
    respuesta.put("mensaje", exception.getMessage());

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(respuesta);
    }

    @ExceptionHandler(DisabledException.class)
       public ResponseEntity<Map<String, Object>>
        manejarUsuarioDesactivado(
        DisabledException exception
    ) {
      Map<String, Object> respuesta = new LinkedHashMap<>();

    respuesta.put("fecha", LocalDateTime.now());
    respuesta.put("estado", HttpStatus.FORBIDDEN.value());
    respuesta.put("mensaje", exception.getMessage());

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(respuesta);
    }

    @ExceptionHandler(NoSuchElementException.class)
      public ResponseEntity<Map<String, Object>>
      manejarNoEncontrado(
        NoSuchElementException exception
    ) {
       Map<String, Object> respuesta =
        new LinkedHashMap<>();

        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put(
        "estado",
        HttpStatus.NOT_FOUND.value()
    );
        respuesta.put("mensaje", exception.getMessage());

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(respuesta);
    }

    @ExceptionHandler(AccessDeniedException.class)
     public ResponseEntity<Map<String, Object>>
     manejarAccesoDenegado(
        AccessDeniedException exception
    ) {
    Map<String, Object> respuesta =
        new LinkedHashMap<>();

    respuesta.put("fecha", LocalDateTime.now());
    respuesta.put(
        "estado",
        HttpStatus.FORBIDDEN.value()
    );
    respuesta.put("mensaje", exception.getMessage());

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(respuesta);
    }
 


}