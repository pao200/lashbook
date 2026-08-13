import { Navigate } from 'react-router-dom'

import { useAuth } from '../context/AuthContext'

function RutaProtegida({
  children,
  rolesPermitidos = [],
}) {
  const {
    usuario,
    cargandoSesion,
    estaAutenticado,
  } = useAuth()

  if (cargandoSesion) {
    return (
      <main>
        <p>Cargando sesión...</p>
      </main>
    )
  }

  if (!estaAutenticado) {
    return (
      <Navigate
        to="/login"
        replace
      />
    )
  }

  if (
    rolesPermitidos.length > 0 &&
    !rolesPermitidos.includes(usuario?.rol)
  ) {
    return (
      <Navigate
        to="/login"
        replace
      />
    )
  }

  return children
}

export default RutaProtegida