import {
  createContext,
  useContext,
  useEffect,
  useState,
} from 'react'

import {
  iniciarSesion,
  obtenerPerfil,
} from '../api/authApi'

import {
  eliminarToken,
  guardarToken,
  obtenerToken,
} from '../utils/almacenamiento'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [cargandoSesion, setCargandoSesion] = useState(true)

  useEffect(() => {
    const cargarSesion = async () => {
      const token = obtenerToken()

      if (!token) {
        setCargandoSesion(false)
        return
      }

      try {
        const perfil = await obtenerPerfil(token)
        setUsuario(perfil)
      } catch {
        eliminarToken()
        setUsuario(null)
      } finally {
        setCargandoSesion(false)
      }
    }

    cargarSesion()
  }, [])

  const ingresar = async (correo, password) => {
    const datosLogin = await iniciarSesion(
      correo,
      password,
    )

    guardarToken(datosLogin.token)

    try {
      const perfil = await obtenerPerfil(
        datosLogin.token,
      )

      setUsuario(perfil)
      return perfil
    } catch (error) {
      eliminarToken()
      throw error
    }
  }

  const cerrarSesion = () => {
    eliminarToken()
    setUsuario(null)
  }

  const valor = {
    usuario,
    cargandoSesion,
    estaAutenticado: Boolean(usuario),
    ingresar,
    cerrarSesion,
  }

  return (
    <AuthContext.Provider value={valor}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const contexto = useContext(AuthContext)

  if (!contexto) {
    throw new Error(
      'useAuth debe utilizarse dentro de AuthProvider',
    )
  }

  return contexto
}