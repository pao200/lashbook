import { NavLink, useNavigate } from 'react-router-dom'

import { useAuth } from '../context/AuthContext'
import './DashboardLayout.css'

function DashboardLayout({ children }) {
  const { usuario, cerrarSesion } = useAuth()
  const navigate = useNavigate()

  const manejarCierreSesion = () => {
    cerrarSesion()

    navigate(
      '/login',
      { replace: true },
    )
  }

  return (
    <div className="dashboard-layout">
      <aside className="dashboard-sidebar">
        <div className="dashboard-brand">
          <div className="dashboard-logo">
            LB
          </div>

          <div>
            <strong>LashBook</strong>
            <span>Panel de clienta</span>
          </div>
        </div>

        <nav className="dashboard-navigation">
          <NavLink
            to="/clienta/inicio"
            className={({ isActive }) =>
              isActive
                ? 'dashboard-link active'
                : 'dashboard-link'
            }
          >
            Inicio
          </NavLink>

          <NavLink
            to="/clienta/reservar"
            className={({ isActive }) =>
              isActive
                ? 'dashboard-link active'
                : 'dashboard-link'
            }
          >
            Reservar cita
          </NavLink>

          <NavLink
            to="/clienta/mis-citas"
            className={({ isActive }) =>
              isActive
                ? 'dashboard-link active'
                : 'dashboard-link'
            }
          >
            Mis citas
          </NavLink>
        </nav>

        <div className="dashboard-user">
          <div>
            <strong>{usuario?.nombre}</strong>
            <span>{usuario?.correo}</span>
          </div>

          <button
            type="button"
            onClick={manejarCierreSesion}
          >
            Cerrar sesión
          </button>
        </div>
      </aside>

      <main className="dashboard-content">
        {children}
      </main>
    </div>
  )
}

export default DashboardLayout