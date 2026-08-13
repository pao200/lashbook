import {
  useEffect,
  useState,
} from 'react'

import { listarServicios } from '../../api/serviciosApi'
import FormularioReserva from '../../components/citas/FormularioReserva'
import DashboardLayout from '../../layouts/DashboardLayout'
import './ReservarCitaPage.css'

function ReservarCitaPage() {
  const [servicios, setServicios] = useState([])

  const [
    servicioSeleccionado,
    setServicioSeleccionado,
  ] = useState(null)

  const [cargando, setCargando] = useState(true)
  const [mensaje, setMensaje] = useState('')

  useEffect(() => {
    const cargarServicios = async () => {
      try {
        const datos = await listarServicios()
        setServicios(datos)
      } catch (error) {
        setMensaje(error.message)
      } finally {
        setCargando(false)
      }
    }

    cargarServicios()
  }, [])

  return (
    <DashboardLayout>
      <section>
        <header className="reserva-header">
          <p className="eyebrow">
            Nueva reservación
          </p>

          <h1>
            Reservar una cita
          </h1>

          <p className="reserva-description">
            {servicioSeleccionado
              ? 'Selecciona la fecha y hora de tu cita.'
              : 'Selecciona el servicio que deseas reservar.'}
          </p>
        </header>

        {servicioSeleccionado ? (
          <FormularioReserva
            servicio={servicioSeleccionado}
            onCancelar={() =>
              setServicioSeleccionado(null)
            }
          />
        ) : (
          <>
            {cargando && (
              <p className="reserva-status">
                Cargando servicios...
              </p>
            )}

            {mensaje && (
              <p className="reserva-status">
                {mensaje}
              </p>
            )}

            {!cargando &&
              !mensaje &&
              servicios.length === 0 && (
                <p className="reserva-status">
                  No hay servicios disponibles.
                </p>
              )}

            <div className="servicios-grid">
              {servicios.map((servicio) => (
                <article
                  className="servicio-card"
                  key={servicio.id}
                >
                  {servicio.imagenUrl ? (
                    <img
                      className="servicio-card-imagen"
                      src={servicio.imagenUrl}
                      alt={servicio.nombre}
                    />
                  ) : (
                    <div className="servicio-card-imagen-vacia">
                      Sin imagen
                    </div>
                  )}

                  <div className="servicio-card-contenido">
                    <h2>
                      {servicio.nombre}
                    </h2>

                    {servicio.descripcion && (
                      <p className="servicio-card-description">
                        {servicio.descripcion}
                      </p>
                    )}

                    <div className="servicio-card-details">
                      {servicio.precio !== undefined && (
                        <span className="servicio-detail">
                          Precio: ${servicio.precio}
                        </span>
                      )}

                      {servicio.duracionMinutos && (
                        <span className="servicio-detail">
                          {servicio.duracionMinutos} minutos
                        </span>
                      )}
                    </div>

                    <button
                      type="button"
                      onClick={() =>
                        setServicioSeleccionado(
                          servicio,
                        )
                      }
                    >
                      Seleccionar servicio
                    </button>
                  </div>
                </article>
              ))}
            </div>
          </>
        )}
      </section>
    </DashboardLayout>
  )
}

export default ReservarCitaPage