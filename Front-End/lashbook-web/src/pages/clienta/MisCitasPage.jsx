import {
  useEffect,
  useState,
} from 'react'

import { listarMisCitas } from '../../api/citasApi'
import HistorialCitaModal from '../../components/citas/HistorialCitaModal'
import TarjetaCita from '../../components/citas/TarjetaCita'
import DashboardLayout from '../../layouts/DashboardLayout'
import './MisCitasPage.css'

function MisCitasPage() {
  const [citas, setCitas] = useState([])
  const [citaSeleccionada, setCitaSeleccionada] =
    useState(null)

  const [cargando, setCargando] = useState(true)
  const [mensaje, setMensaje] = useState('')

  useEffect(() => {
    const cargarCitas = async () => {
      try {
        const datos = await listarMisCitas()
        setCitas(datos)
      } catch (error) {
        setMensaje(error.message)
      } finally {
        setCargando(false)
      }
    }

    cargarCitas()
  }, [])

  const actualizarCitaEnLista = (citaActualizada) => {
    setCitas((citasActuales) =>
      citasActuales.map((cita) =>
        cita.id === citaActualizada.id
          ? citaActualizada
          : cita,
      ),
    )
  }

  return (
    <DashboardLayout>
      <section>
        <header className="mis-citas-header">
          <p className="eyebrow">
            Tu agenda
          </p>

          <h1>
            Mis citas
          </h1>

          <p className="mis-citas-description">
            Consulta tus reservaciones y administra el estado
            de cada cita.
          </p>
        </header>

        {cargando && (
          <p className="mis-citas-status">
            Cargando tus citas...
          </p>
        )}

        {mensaje && (
          <p className="mis-citas-status">
            {mensaje}
          </p>
        )}

        {!cargando &&
          !mensaje &&
          citas.length === 0 && (
            <p className="mis-citas-status">
              Todavía no tienes citas registradas.
            </p>
          )}

        <div className="citas-grid">
          {citas.map((cita) => (
            <TarjetaCita
              key={cita.id}
              cita={cita}
              onVerHistorial={
                setCitaSeleccionada
              }
              onCitaActualizada={
                actualizarCitaEnLista
              }
            />
          ))}
        </div>
      </section>

      {citaSeleccionada && (
        <HistorialCitaModal
          cita={citaSeleccionada}
          onCerrar={() =>
            setCitaSeleccionada(null)
          }
        />
      )}
    </DashboardLayout>
  )
}

export default MisCitasPage