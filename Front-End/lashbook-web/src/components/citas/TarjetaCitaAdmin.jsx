import { useState } from 'react'

import {
  cambiarEstadoCitaAdministrativa,
  reagendarCitaAdministrativa,
} from '../../api/citasApi'

function TarjetaCitaAdmin({
  cita,
  onCitaActualizada,
  onVerHistorial,
}) {
  const [mostrarReagendar, setMostrarReagendar] =
    useState(false)

  const [fecha, setFecha] = useState('')
  const [hora, setHora] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [cargando, setCargando] = useState(false)

  const cambiarEstado = async (nuevoEstado) => {
    setMensaje('')
    setCargando(true)

    try {
      const citaActualizada =
        await cambiarEstadoCitaAdministrativa(
          cita.id,
          nuevoEstado,
        )

      onCitaActualizada(citaActualizada)
      setMensaje('La cita se actualizó correctamente')
    } catch (error) {
      setMensaje(error.message)
    } finally {
      setCargando(false)
    }
  }

  const manejarReagendado = async (evento) => {
    evento.preventDefault()
    setMensaje('')
    setCargando(true)

    try {
      const citaActualizada =
        await reagendarCitaAdministrativa(
          cita.id,
          fecha,
          hora,
        )

      onCitaActualizada(citaActualizada)

      setMensaje('La cita se reagendó correctamente')
      setMostrarReagendar(false)
      setFecha('')
      setHora('')
    } catch (error) {
      setMensaje(error.message)
    } finally {
      setCargando(false)
    }
  }

  const estaFinalizada =
    cita.estado === 'CANCELADA' ||
    cita.estado === 'COMPLETADA'

  return (
    <article className="agenda-card">
      <h2>{cita.nombreServicio}</h2>

      <p className="agenda-clienta">
        <strong>Clienta:</strong>{' '}
        {cita.nombreClienta}
      </p>

      <span className="agenda-estado">
        {cita.estado}
      </span>

      <div className="agenda-info">
        <p>
          <strong>Fecha:</strong>{' '}
          {cita.fecha}
        </p>

        <p>
          <strong>Hora:</strong>{' '}
          {cita.hora}
        </p>
      </div>

      {cita.comentarios && (
        <p className="agenda-comentarios">
          <strong>Comentarios:</strong>{' '}
          {cita.comentarios}
        </p>
      )}

      {mensaje && (
        <p className="agenda-accion-mensaje">
          {mensaje}
        </p>
      )}

      {!estaFinalizada && (
        <div className="agenda-acciones">
          {cita.estado === 'PENDIENTE' && (
            <button
              type="button"
              disabled={cargando}
              onClick={() =>
                cambiarEstado('CONFIRMADA')
              }
            >
              Confirmar cita
            </button>
          )}

          {cita.estado === 'CONFIRMADA' && (
            <button
              type="button"
              disabled={cargando}
              onClick={() =>
                cambiarEstado('COMPLETADA')
              }
            >
              Marcar como completada
            </button>
          )}

          {cita.estado === 'REAGENDAR' && (
            <button
              type="button"
              disabled={cargando}
              onClick={() =>
                setMostrarReagendar(
                  (valorActual) => !valorActual,
                )
              }
            >
              Asignar nueva fecha
            </button>
          )}

          <button
            type="button"
            disabled={cargando}
            onClick={() =>
              cambiarEstado('CANCELADA')
            }
          >
            Cancelar cita
          </button>
        </div>
      )}

      {mostrarReagendar && (
        <form
          className="agenda-reagendar-form"
          onSubmit={manejarReagendado}
        >
          <label htmlFor={`fecha-${cita.id}`}>
            Nueva fecha
          </label>

          <input
            id={`fecha-${cita.id}`}
            type="date"
            value={fecha}
            onChange={(evento) =>
              setFecha(evento.target.value)
            }
            required
          />

          <label htmlFor={`hora-${cita.id}`}>
            Nueva hora
          </label>

          <input
            id={`hora-${cita.id}`}
            type="time"
            value={hora}
            onChange={(evento) =>
              setHora(evento.target.value)
            }
            required
          />

          <button
            type="submit"
            disabled={cargando}
          >
            {cargando
              ? 'Guardando...'
              : 'Confirmar nueva fecha'}
          </button>
        </form>
      )}

      <button
        className="agenda-historial-button"
        type="button"
        onClick={() => onVerHistorial(cita)}
      >
        Ver historial
      </button>
    </article>
  )
}

export default TarjetaCitaAdmin