import {
  useEffect,
  useState,
} from 'react'

import {
  consultarHistorialAdministrativo,
} from '../../api/citasApi'

import './HistorialCitaModal.css'

function HistorialCitaAdminModal({
  cita,
  onCerrar,
}) {
  const [historial, setHistorial] = useState([])
  const [cargando, setCargando] = useState(true)
  const [mensaje, setMensaje] = useState('')

  useEffect(() => {
    const cargarHistorial = async () => {
      try {
        const datos =
          await consultarHistorialAdministrativo(
            cita.id,
          )

        setHistorial(datos)
      } catch (error) {
        setMensaje(error.message)
      } finally {
        setCargando(false)
      }
    }

    cargarHistorial()
  }, [cita.id])

  return (
    <div className="historial-modal-overlay">
      <section
        className="historial-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="historial-admin-titulo"
      >
        <header className="historial-modal-header">
          <div>
            <p className="eyebrow">
              Historial administrativo
            </p>

            <h2 id="historial-admin-titulo">
              {cita.nombreServicio}
            </h2>

            <p>
              {cita.nombreClienta}
              {' · '}
              {cita.fecha}
              {' · '}
              {cita.hora}
            </p>
          </div>

          <button
            type="button"
            onClick={onCerrar}
            aria-label="Cerrar historial"
          >
            ×
          </button>
        </header>

        {cargando && (
          <p className="historial-modal-status">
            Cargando historial...
          </p>
        )}

        {mensaje && (
          <p className="historial-modal-status">
            {mensaje}
          </p>
        )}

        {!cargando &&
          !mensaje &&
          historial.length === 0 && (
            <p className="historial-modal-status">
              Esta cita todavía no tiene movimientos registrados.
            </p>
          )}

        <div className="historial-lista">
          {historial.map((movimiento) => (
            <article
              className="historial-movimiento"
              key={movimiento.id}
            >
              <div className="historial-estados">
                <span>
                  {movimiento.estadoAnterior || 'CREADA'}
                </span>

                <strong>→</strong>

                <span>
                  {movimiento.estadoNuevo}
                </span>
              </div>

              <p>
                {movimiento.detalle}
              </p>

              <small>
                {movimiento.nombreActor || 'Sistema'}
                {' · '}
                {movimiento.origen}
              </small>

              <small>
                {new Date(
                  movimiento.fechaCambio,
                ).toLocaleString('es-MX')}
              </small>
            </article>
          ))}
        </div>

        <button
          className="historial-cerrar-button"
          type="button"
          onClick={onCerrar}
        >
          Cerrar
        </button>
      </section>
    </div>
  )
}

export default HistorialCitaAdminModal