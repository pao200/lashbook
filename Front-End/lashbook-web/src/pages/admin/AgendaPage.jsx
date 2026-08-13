import {
  useEffect,
  useState,
} from 'react'

import { listarCitasAdministrativas } from '../../api/citasApi'
import HistorialCitaAdminModal from '../../components/citas/HistorialCitaAdminModal'
import TarjetaCitaAdmin from '../../components/citas/TarjetaCitaAdmin'
import AdminLayout from '../../layouts/AdminLayout'
import './AgendaPage.css'

function AgendaPage() {
  const [citas, setCitas] = useState([])
  const [
    citaSeleccionada,
    setCitaSeleccionada,
  ] = useState(null)

  const [cargando, setCargando] = useState(true)
  const [mensaje, setMensaje] = useState('')

  useEffect(() => {
    const cargarAgenda = async () => {
      try {
        const datos =
          await listarCitasAdministrativas()

        setCitas(datos)
      } catch (error) {
        setMensaje(error.message)
      } finally {
        setCargando(false)
      }
    }

    cargarAgenda()
  }, [])

  const actualizarCitaEnLista = (
    citaActualizada,
  ) => {
    setCitas((citasActuales) =>
      citasActuales.map((cita) =>
        cita.id === citaActualizada.id
          ? citaActualizada
          : cita,
      ),
    )
  }

  return (
    <AdminLayout>
      <section>
        <header className="agenda-header">
          <p className="eyebrow">
            Administración
          </p>

          <h1>
            Agenda de citas
          </h1>

          <p className="agenda-description">
            Consulta todas las citas registradas,
            revisa sus datos y administra su estado.
          </p>
        </header>

        {cargando && (
          <p className="agenda-status">
            Cargando agenda...
          </p>
        )}

        {mensaje && (
          <p className="agenda-status">
            {mensaje}
          </p>
        )}

        {!cargando &&
          !mensaje &&
          citas.length === 0 && (
            <p className="agenda-status">
              No hay citas registradas.
            </p>
          )}

        <div className="agenda-grid">
          {citas.map((cita) => (
            <TarjetaCitaAdmin
              key={cita.id}
              cita={cita}
              onCitaActualizada={
                actualizarCitaEnLista
              }
              onVerHistorial={
                setCitaSeleccionada
              }
            />
          ))}
        </div>
      </section>

      {citaSeleccionada && (
        <HistorialCitaAdminModal
          cita={citaSeleccionada}
          onCerrar={() =>
            setCitaSeleccionada(null)
          }
        />
      )}
    </AdminLayout>
  )
}

export default AgendaPage