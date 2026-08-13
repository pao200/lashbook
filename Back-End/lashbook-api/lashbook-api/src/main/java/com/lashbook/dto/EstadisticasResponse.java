package com.lashbook.dto;

import java.math.BigDecimal;

public class EstadisticasResponse {

    private final long totalCitas;
    private final long pendientes;
    private final long confirmadas;
    private final long canceladas;
    private final long porReagendar;
    private final long completadas;
    private final BigDecimal ingresosTotales;

    public EstadisticasResponse(
            long totalCitas,
            long pendientes,
            long confirmadas,
            long canceladas,
            long porReagendar,
            long completadas,
            BigDecimal ingresosTotales
    ) {
        this.totalCitas = totalCitas;
        this.pendientes = pendientes;
        this.confirmadas = confirmadas;
        this.canceladas = canceladas;
        this.porReagendar = porReagendar;
        this.completadas = completadas;
        this.ingresosTotales = ingresosTotales;
    }

    public long getTotalCitas() {
        return totalCitas;
    }

    public long getPendientes() {
        return pendientes;
    }

    public long getConfirmadas() {
        return confirmadas;
    }

    public long getCanceladas() {
        return canceladas;
    }

    public long getPorReagendar() {
        return porReagendar;
    }

    public long getCompletadas() {
        return completadas;
    }

    public BigDecimal getIngresosTotales() {
        return ingresosTotales;
    }
}