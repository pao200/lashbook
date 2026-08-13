import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { registrarCliente } from '../../api/authApi'
import '../../App.css'

function RegistroPage() {
  const navigate = useNavigate()

  const [nombre, setNombre] = useState('')
  const [correo, setCorreo] = useState('')
  const [password, setPassword] = useState('')
  const [confirmarPassword, setConfirmarPassword] =
    useState('')

  const [mostrarPassword, setMostrarPassword] =
    useState(false)

  const [
    mostrarConfirmarPassword,
    setMostrarConfirmarPassword,
  ] = useState(false)

  const [mensaje, setMensaje] = useState('')
  const [registroExitoso, setRegistroExitoso] =
    useState(false)
  const [cargando, setCargando] = useState(false)

  const manejarRegistro = async (evento) => {
    evento.preventDefault()

    setMensaje('')
    setRegistroExitoso(false)

    if (password !== confirmarPassword) {
      setMensaje(
        'Las contraseñas no coinciden',
      )
      return
    }

    if (password.length < 6) {
      setMensaje(
        'La contraseña debe tener al menos 6 caracteres',
      )
      return
    }

    setCargando(true)

    try {
      await registrarCliente(
        nombre.trim(),
        correo.trim().toLowerCase(),
        password,
      )

      setRegistroExitoso(true)

      setMensaje(
        'Cuenta creada correctamente. Ya puedes iniciar sesión.',
      )

      setNombre('')
      setCorreo('')
      setPassword('')
      setConfirmarPassword('')

      setMostrarPassword(false)
      setMostrarConfirmarPassword(false)
    } catch (error) {
      setMensaje(error.message)
    } finally {
      setCargando(false)
    }
  }

  const estiloContenedorPassword = {
    position: 'relative',
    width: '100%',
  }

  const estiloInputPassword = {
    width: '100%',
    paddingRight: '55px',
  }

  const estiloBotonOjo = {
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

    transform: 'translateY(-50%)',

    boxShadow: 'none',
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
          Reserva tus citas fácilmente
        </p>

        <h1>
          LashBook
        </h1>

        <p className="intro-text">
          Crea tu cuenta para conocer nuestros
          servicios, reservar citas y consultar
          todos tus movimientos desde un solo lugar.
        </p>
      </section>

      <section className="login-panel">
        <form
          className="login-form"
          onSubmit={manejarRegistro}
        >
          <div>
            <p className="eyebrow">
              Nueva clienta
            </p>

            <h2>
              Crear cuenta
            </h2>

            <p className="form-description">
              Completa tus datos para comenzar
              a reservar.
            </p>
          </div>

          <label htmlFor="nombre">
            Nombre completo
          </label>

          <input
            id="nombre"
            name="nombre"
            type="text"
            placeholder="Tu nombre"
            autoComplete="name"
            value={nombre}
            onChange={(evento) =>
              setNombre(evento.target.value)
            }
            required
          />

          <label htmlFor="correo">
            Correo electrónico
          </label>

          <input
            id="correo"
            name="correo"
            type="email"
            placeholder="nombre@correo.com"
            autoComplete="email"
            value={correo}
            onChange={(evento) =>
              setCorreo(evento.target.value)
            }
            required
          />

          <label htmlFor="password">
            Contraseña
          </label>

          <div style={estiloContenedorPassword}>
            <input
              id="password"
              name="password"
              type={
                mostrarPassword
                  ? 'text'
                  : 'password'
              }
              placeholder="Mínimo 6 caracteres"
              autoComplete="new-password"
              value={password}
              onChange={(evento) =>
                setPassword(
                  evento.target.value,
                )
              }
              minLength={6}
              required
              style={estiloInputPassword}
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
              style={estiloBotonOjo}
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

          <label htmlFor="confirmarPassword">
            Confirmar contraseña
          </label>

          <div style={estiloContenedorPassword}>
            <input
              id="confirmarPassword"
              name="confirmarPassword"
              type={
                mostrarConfirmarPassword
                  ? 'text'
                  : 'password'
              }
              placeholder="Repite tu contraseña"
              autoComplete="new-password"
              value={confirmarPassword}
              onChange={(evento) =>
                setConfirmarPassword(
                  evento.target.value,
                )
              }
              minLength={6}
              required
              style={estiloInputPassword}
            />

            <button
              type="button"
              onClick={() =>
                setMostrarConfirmarPassword(
                  (actual) => !actual,
                )
              }
              aria-label={
                mostrarConfirmarPassword
                  ? 'Ocultar confirmación de contraseña'
                  : 'Mostrar confirmación de contraseña'
              }
              title={
                mostrarConfirmarPassword
                  ? 'Ocultar contraseña'
                  : 'Mostrar contraseña'
              }
              style={estiloBotonOjo}
            >
              {mostrarConfirmarPassword ? (
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
            <p
              className="form-message"
              role="status"
            >
              {mensaje}
            </p>
          )}

          {!registroExitoso && (
            <button
              type="submit"
              disabled={cargando}
            >
              {cargando
                ? 'Creando cuenta...'
                : 'Crear cuenta'}
            </button>
          )}

          {registroExitoso && (
            <button
              type="button"
              onClick={() =>
                navigate('/login')
              }
            >
              Ir a iniciar sesión
            </button>
          )}

          <p className="register-text">
            ¿Ya tienes una cuenta?{' '}

            <button
              className="text-button"
              type="button"
              onClick={() =>
                navigate('/login')
              }
            >
              Iniciar sesión
            </button>
          </p>
        </form>
      </section>
    </main>
  )
}

export default RegistroPage