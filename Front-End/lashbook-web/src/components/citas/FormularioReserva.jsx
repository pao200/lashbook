import { useState } from 'react'

import { crearCita } from '../../api/citasApi'
import './FormularioReserva.css'

function FormularioReserva({
  servicio,
  onCancelar,
  onReservaCreada,
}) {
  const [fecha, setFecha] = useState('')
  const [hora, setHora] = useState('')
  const [comentarios, setComentarios] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [cargando, setCargando] = useState(false)

  const manejarReserva = async (evento) => {
    evento.preventDefault()
    setMensaje('')
    setCargando(true)

    try {
      const cita = await crearCita({
        servicioId: servicio.id,
        fecha,
        hora: hora.length === 5
          ? `${hora}:00`
          : hora,
        comentarios,
      })

      setMensaje('La cita se reservó correctamente')

      if (onReservaCreada) {
        onReservaCreada(cita)
      }
    } catch (error) {
      setMensaje(error.message)
    } finally {
      setCargando(false)
    }
  }

  return (
    <form
      className="reserva-form"
      onSubmit={manejarReserva}
    >
      <header className="reserva-form-header">
        <p className="eyebrow">
          Servicio seleccionado
        </p>

        <h2>{servicio.nombre}</h2>
      </header>

      <label htmlFor="fecha">
        Fecha
      </label>

      <input
        id="fecha"
        type="date"
        value={fecha}
        onChange={(evento) =>
          setFecha(evento.target.value)
        }
        required
      />

      <label htmlFor="hora">
        Hora
      </label>

      <input
        id="hora"
        type="time"
        value={hora}
        onChange={(evento) =>
          setHora(evento.target.value)
        }
        required
      />

      <label htmlFor="comentarios">
        Comentarios
      </label>

      <textarea
        id="comentarios"
        placeholder="Agrega alguna indicación para tu cita"
        value={comentarios}
        onChange={(evento) =>
          setComentarios(evento.target.value)
        }
      />

      {mensaje && (
        <p className="reserva-form-message">
          {mensaje}
        </p>
      )}

      <div className="reserva-form-actions">
        <button
          className="reserva-confirm-button"
          type="submit"
          disabled={cargando}
        >
          {cargando
            ? 'Reservando...'
            : 'Confirmar reservación'}
        </button>

        <button
          className="reserva-cancel-button"
          type="button"
          onClick={onCancelar}
          disabled={cargando}
        >
          Elegir otro servicio
        </button>
      </div>
    </form>
  )
}

export default FormularioReserva