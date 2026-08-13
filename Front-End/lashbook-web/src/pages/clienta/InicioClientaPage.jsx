import { useAuth } from '../../context/AuthContext'
import DashboardLayout from '../../layouts/DashboardLayout'

function InicioClientaPage() {
  const { usuario } = useAuth()

  return (
    <DashboardLayout>
      <section>
        <p className="eyebrow">
          Panel de clienta
        </p>

        <h1>
          Hola, {usuario?.nombre}
        </h1>

        <p>
          Desde aquí podrás reservar servicios,
          consultar tus citas y revisar su historial.
        </p>
      </section>
    </DashboardLayout>
  )
}

export default InicioClientaPage