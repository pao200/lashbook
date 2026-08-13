import { useState } from 'react'

import { cambiarEstadoCita } from '../../api/citasApi'

function TarjetaCita({
  cita,
  onVerHistorial,
  onCitaActualizada,
}) {
  const [cargando, setCargando] = useState(false)
  const [mensaje, setMensaje] = useState('')

  const cambiarEstado = async (nuevoEstado) => {
    setMensaje('')
    setCargando(true)

    try {
      const citaActualizada = await cambiarEstadoCita(
        cita.id,
        nuevoEstado,
      )

      onCitaActualizada(citaActualizada)

      setMensaje(
        'La cita se actualizó correctamente',
      )
    } catch (error) {
      setMensaje(error.message)
    } finally {
      setCargando(false)
    }
  }

  /*
   * Obtiene la fecha y hora actuales usando
   * la zona horaria del negocio.
   */
  const obtenerAhoraMexico = () => {
    const partes =
      new Intl.DateTimeFormat(
        'en-CA',
        {
          timeZone:
            'America/Mexico_City',
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
          hourCycle: 'h23',
        },
      ).formatToParts(new Date())

    const valores = {}

    partes.forEach((parte) => {
      if (parte.type !== 'literal') {
        valores[parte.type] =
          parte.value
      }
    })

    return (
      `${valores.year}-` +
      `${valores.month}-` +
      `${valores.day}T` +
      `${valores.hour}:` +
      `${valores.minute}:` +
      `${valores.second}`
    )
  }

  const horaNormalizada =
    cita.hora?.length === 5
      ? `${cita.hora}:00`
      : cita.hora

  const fechaHoraCita =
    `${cita.fecha}T${horaNormalizada}`

  const estaVencida =
    fechaHoraCita <= obtenerAhoraMexico()

  const estaFinalizada =
    cita.estado === 'CANCELADA' ||
    cita.estado === 'COMPLETADA'

  /*
   * Solo permitimos acciones cuando:
   *
   * - La cita no está finalizada.
   * - La fecha y hora todavía no han pasado.
   */
  const puedeModificar =
    !estaFinalizada &&
    !estaVencida

  return (
    <article className="cita-card">
      <h2>
        {cita.nombreServicio}
      </h2>

      <span className="cita-estado">
        {cita.estado}
      </span>

      <div className="cita-info">
        <p>
          <strong>
            Fecha:
          </strong>{' '}
          {cita.fecha}
        </p>

        <p>
          <strong>
            Hora:
          </strong>{' '}
          {cita.hora}
        </p>
      </div>

      {cita.comentarios && (
        <p className="cita-comentarios">
          <strong>
            Comentarios:
          </strong>{' '}
          {cita.comentarios}
        </p>
      )}

      {mensaje && (
        <p className="cita-accion-mensaje">
          {mensaje}
        </p>
      )}

      {estaVencida &&
        !estaFinalizada && (
          <p className="cita-espera">
            Esta cita ya pasó.
            Consulta el historial para
            ver sus movimientos.
          </p>
        )}

      {puedeModificar && (
        <div className="cita-acciones">
          {cita.estado ===
            'PENDIENTE' && (
            <button
              type="button"
              disabled={cargando}
              onClick={() =>
                cambiarEstado(
                  'CONFIRMADA',
                )
              }
            >
              Confirmar
            </button>
          )}

          {cita.estado !==
            'REAGENDAR' && (
            <button
              type="button"
              disabled={cargando}
              onClick={() =>
                cambiarEstado(
                  'REAGENDAR',
                )
              }
            >
              Solicitar reagendar
            </button>
          )}

          <button
            type="button"
            disabled={cargando}
            onClick={() =>
              cambiarEstado(
                'CANCELADA',
              )
            }
          >
            Cancelar
          </button>
        </div>
      )}

      {cita.estado ===
        'REAGENDAR' &&
        !estaVencida && (
          <p className="cita-espera">
            Esperando que la lashista
            asigne una nueva fecha.
          </p>
        )}

      <button
        type="button"
        onClick={() =>
          onVerHistorial(cita)
        }
      >
        Ver historial
      </button>
    </article>
  )
}

export default TarjetaCita