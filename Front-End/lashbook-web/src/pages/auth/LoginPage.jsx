import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { useAuth } from '../../context/AuthContext'
import '../../App.css'

function LoginPage() {
  const { ingresar } = useAuth()
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [mostrarPassword, setMostrarPassword] =
    useState(false)

  const [mensaje, setMensaje] = useState('')
  const [cargando, setCargando] = useState(false)

  const manejarInicioSesion = async (evento) => {
    evento.preventDefault()

    setMensaje('')
    setCargando(true)

    try {
      const perfil = await ingresar(
        email,
        password,
      )

      setMensaje(
        `Bienvenida, ${perfil.nombre}`,
      )

      setPassword('')

      if (perfil.rol === 'CLIENTA') {
        navigate(
          '/clienta/inicio',
          { replace: true },
        )
      } else if (
        perfil.rol === 'LASHISTA' ||
        perfil.rol === 'ADMIN'
      ) {
        navigate(
          '/admin/inicio',
          { replace: true },
        )
      } else {
        setMensaje(
          'El usuario no tiene un rol válido',
        )
      }
    } catch (error) {
      setMensaje(error.message)
    } finally {
      setCargando(false)
    }
  }

  return (
    <main className="login-page">
      <section className="login-intro">
        <div className="brand-mark">
          <img
            src="/logo/lashbook-logo.png"
            alt="Logo de LashBook"
            className="brand-logo"
          />
        </div>

        <p className="eyebrow">
          Agenda y administra tus citas
        </p>

        <h1>
          LashBook
        </h1>

        <p className="intro-text">
          Reserva tus servicios de pestañas,
          consulta tus próximas citas y recibe
          actualizaciones desde un solo lugar.
        </p>
      </section>

      <section className="login-panel">
        <form
          className="login-form"
          onSubmit={manejarInicioSesion}
        >
          <div>
            <p className="eyebrow">
              Bienvenida
            </p>

            <h2>
              Iniciar sesión
            </h2>

            <p className="form-description">
              Ingresa con tu correo electrónico
              y contraseña.
            </p>
          </div>

          <label htmlFor="email">
            Correo electrónico
          </label>

          <input
            id="email"
            name="email"
            type="email"
            placeholder="nombre@correo.com"
            autoComplete="email"
            value={email}
            onChange={(evento) =>
              setEmail(evento.target.value)
            }
            required
          />

          <label htmlFor="password">
            Contraseña
          </label>

          <div
            style={{
              position: 'relative',
              width: '100%',
            }}
          >
            <input
              id="password"
              name="password"
              type={
                mostrarPassword
                  ? 'text'
                  : 'password'
              }
              placeholder="Tu contraseña"
              autoComplete="current-password"
              value={password}
              onChange={(evento) =>
                setPassword(
                  evento.target.value,
                )
              }
              required
              style={{
                width: '100%',
                paddingRight: '55px',
              }}
            />

            <button
              type="button"
              onClick={() =>
                setMostrarPassword(
                  (actual) => !actual,
                )
              }
              aria-label={
                mostrarPassword
                  ? 'Ocultar contraseña'
                  : 'Mostrar contraseña'
              }
              title={
                mostrarPassword
                  ? 'Ocultar contraseña'
                  : 'Mostrar contraseña'
              }
              style={{
                position: 'absolute',
                top: '50%',
                right: '12px',

                width: '36px',
                height: '36px',
                minWidth: '36px',
                minHeight: '36px',

                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',

                margin: '0',
                padding: '0',

                border: 'none',
                borderRadius: '50%',

                background: 'transparent',
                color: '#7b5b57',

                cursor: 'pointer',

                transform:
                  'translateY(-50%)',

                boxShadow: 'none',
              }}
            >
              {mostrarPassword ? (
                <svg
                  width="21"
                  height="21"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <path
                    d="M3 3l18 18M10.6 10.6a2 2 0 002.8 2.8M9.9 4.2A10.8 10.8 0 0112 4c5 0 9 4.5 10 8a12.7 12.7 0 01-2.2 4.1M6.6 6.6A12.7 12.7 0 002 12c1 3.5 5 8 10 8 1.7 0 3.2-.5 4.5-1.2"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                </svg>
              ) : (
                <svg
                  width="21"
                  height="21"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <path
                    d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinejoin="round"
                  />

                  <circle
                    cx="12"
                    cy="12"
                    r="3"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                  />
                </svg>
              )}
            </button>
          </div>

          {mensaje && (
            <p className="form-message">
              {mensaje}
            </p>
          )}

          <button
            type="submit"
            disabled={cargando}
          >
            {cargando
              ? 'Ingresando...'
              : 'Entrar'}
          </button>

          <p className="register-text">
            ¿Todavía no tienes una cuenta?{' '}

            <button
              className="text-button"
              type="button"
              onClick={() =>
                navigate('/registro')
              }
            >
              Crear cuenta
            </button>
          </p>
        </form>
      </section>
    </main>
  )
}

export default LoginPage