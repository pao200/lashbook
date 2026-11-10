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
  const [cargandoSesion, setCargandoSesion] =
    useState(true)

  useEffect(() => {
    let activo = true

    const cargarSesion = async () => {
      const tokenInicial = obtenerToken()

      if (!tokenInicial) {
        if (activo) {
          setUsuario(null)
          setCargandoSesion(false)
        }

        return
      }

      try {
        const perfil =
          await obtenerPerfil(tokenInicial)

        if (!activo) {
          return
        }

        /*
         * Si mientras se validaba este token
         * el usuario inició otra sesión,
         * no debemos sobrescribir la sesión nueva.
         */
        const tokenActual = obtenerToken()

        if (tokenActual !== tokenInicial) {
          return
        }

        setUsuario(perfil)
      } catch (error) {
        if (!activo) {
          return
        }

        /*
         * Solo eliminamos el token si sigue siendo
         * exactamente el mismo que intentábamos validar.
         *
         * Esto evita que una petición vieja elimine
         * una sesión que acaba de iniciarse.
         */
        const tokenActual = obtenerToken()

        if (tokenActual === tokenInicial) {
          eliminarToken()
          setUsuario(null)
        }

        console.error(
          'Error al restaurar la sesión:',
          error
        )
      } finally {
        if (activo) {
          setCargandoSesion(false)
        }
      }
    }

    cargarSesion()

    return () => {
      activo = false
    }
  }, [])


  const ingresar = async (
    correo,
    password
  ) => {
    setCargandoSesion(true)

    try {
      const datosLogin =
        await iniciarSesion(
          correo,
          password
        )

      const nuevoToken =
        datosLogin.token

      guardarToken(nuevoToken)

      const perfil =
        await obtenerPerfil(nuevoToken)

      /*
       * Verificamos que el token todavía sea
       * el de este login antes de guardar
       * el usuario en el contexto.
       */
      if (
        obtenerToken() !== nuevoToken
      ) {
        throw new Error(
          'La sesión cambió durante el inicio de sesión.'
        )
      }

      setUsuario(perfil)

      return perfil
    } catch (error) {
      /*
       * Solo borramos si el token actual
       * pertenece al intento que falló.
       */
      const tokenActual =
        obtenerToken()

      if (tokenActual) {
        eliminarToken()
      }

      setUsuario(null)

      throw error
    } finally {
      setCargandoSesion(false)
    }
  }


  const cerrarSesion = () => {
    eliminarToken()
    setUsuario(null)
    setCargandoSesion(false)
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
  const contexto =
    useContext(AuthContext)

  if (!contexto) {
    throw new Error(
      'useAuth debe utilizarse dentro de AuthProvider'
    )
  }

  return contexto
}