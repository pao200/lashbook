import { useAuth } from '../../context/AuthContext'
import AdminLayout from '../../layouts/AdminLayout'

function DashboardAdminPage() {
  const { usuario } = useAuth()

  return (
    <AdminLayout>
      <section>
        <p className="eyebrow">
          Administración
        </p>

        <h1>
          Hola, {usuario?.nombre}
        </h1>

        <p>
          Desde aquí podrás administrar la agenda,
          reagendar citas, modificar servicios y
          consultar estadísticas.
        </p>
      </section>
    </AdminLayout>
  )
}

export default DashboardAdminPage