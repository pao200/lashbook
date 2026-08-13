import { NavLink, useNavigate } from 'react-router-dom'

import { useAuth } from '../context/AuthContext'
import './AdminLayout.css'

function AdminLayout({ children }) {
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
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <div className="admin-brand">
          <span className="admin-brand-mark">
          <img
             src="/logo/lashbook-logo.png"
             alt="Logo de LashBook"
            className="admin-brand-logo"
          />
         </span>

          <div>
            <strong>LashBook</strong>
            <span>Panel administrativo</span>
          </div>
        </div>

        <nav className="admin-navigation">
          <NavLink
            to="/admin/inicio"
            className={({ isActive }) =>
              isActive
                ? 'admin-link active'
                : 'admin-link'
            }
          >
            Inicio
          </NavLink>

          <NavLink
            to="/admin/agenda"
            className={({ isActive }) =>
              isActive
                ? 'admin-link active'
                : 'admin-link'
            }
          >
            Agenda
          </NavLink>

          <NavLink
            to="/admin/servicios"
            className={({ isActive }) =>
              isActive
                ? 'admin-link active'
                : 'admin-link'
            }
          >
            Servicios
          </NavLink>

          <NavLink
            to="/admin/estadisticas"
            className={({ isActive }) =>
              isActive
                ? 'admin-link active'
                : 'admin-link'
            }
          >
            Estadísticas
          </NavLink>
        </nav>

        <div className="admin-user">
          <div>
            <strong>{usuario?.nombre}</strong>
            <span>{usuario?.correo}</span>
            <small>{usuario?.rol}</small>
          </div>

          <button
            type="button"
            onClick={manejarCierreSesion}
          >
            Cerrar sesión
          </button>
        </div>
      </aside>

      <main className="admin-content">
        {children}
      </main>
    </div>
  )
}

export default AdminLayout