import {
  useEffect,
  useState,
} from 'react'

import { Link } from 'react-router-dom'

import {
  buscarServiciosPredictivos,
  listarServicios,
} from '../api/serviciosApi'

import '../App.css'
const API_URL =
  import.meta.env.VITE_API_URL ||
  'http://localhost:8080/api'
function InicioPublicoPage() {
  const [mensajeEnviado, setMensajeEnviado] =
    useState(false)

  const [servicios, setServicios] =
    useState([])

  const [
    cargandoServicios,
    setCargandoServicios,
  ] = useState(true)

  const [
    errorServicios,
    setErrorServicios,
  ] = useState('')

  const [
    textoBusqueda,
    setTextoBusqueda,
  ] = useState('')

  const [
    sugerencias,
    setSugerencias,
  ] = useState([])

  const [
    cargandoBusqueda,
    setCargandoBusqueda,
  ] = useState(false)

  const [
    errorBusqueda,
    setErrorBusqueda,
  ] = useState('')

  const imagenesCarrusel = [
  '/carrusel/lash-1.png',
  '/carrusel/lash-2.png',
  '/carrusel/lash-3.png',
]

const [imagenActual, setImagenActual] =
  useState(0)

useEffect(() => {
  const intervalo = setInterval(() => {
    setImagenActual((actual) =>
      actual === imagenesCarrusel.length - 1
        ? 0
        : actual + 1,
    )
  }, 4500)

  return () => clearInterval(intervalo)
}, [])

const imagenAnterior = () => {
  setImagenActual((actual) =>
    actual === 0
      ? imagenesCarrusel.length - 1
      : actual - 1,
  )
}

const imagenSiguiente = () => {
  setImagenActual((actual) =>
    actual === imagenesCarrusel.length - 1
      ? 0
      : actual + 1,
  )
}

  useEffect(() => {
    const cargarServicios = async () => {
      try {
        setCargandoServicios(true)
        setErrorServicios('')

        const datos =
          await listarServicios()

        setServicios(datos)
      } catch (error) {
        setErrorServicios(error.message)
      } finally {
        setCargandoServicios(false)
      }
    }

    cargarServicios()
  }, [])

  useEffect(() => {
    const termino = textoBusqueda.trim()

    if (termino.length < 2) {
      setSugerencias([])
      setErrorBusqueda('')
      setCargandoBusqueda(false)
      return undefined
    }

    let busquedaActiva = true

    const temporizador = setTimeout(
      async () => {
        try {
          setCargandoBusqueda(true)
          setErrorBusqueda('')

          const resultados =
            await buscarServiciosPredictivos(
              termino,
            )

          if (busquedaActiva) {
            setSugerencias(resultados)
          }
        } catch (error) {
          if (busquedaActiva) {
            setSugerencias([])
            setErrorBusqueda(error.message)
          }
        } finally {
          if (busquedaActiva) {
            setCargandoBusqueda(false)
          }
        }
      },
      300,
    )

    return () => {
      busquedaActiva = false
      clearTimeout(temporizador)
    }
  }, [textoBusqueda])

  const seleccionarServicio = (
    servicio,
  ) => {
    setTextoBusqueda(servicio.nombre)
    setSugerencias([])
    setErrorBusqueda('')

    setTimeout(() => {
      document
        .getElementById(
          `servicio-${servicio.id}`,
        )
        ?.scrollIntoView({
          behavior: 'smooth',
          block: 'center',
        })
    }, 100)
  }

  const limpiarBusqueda = () => {
    setTextoBusqueda('')
    setSugerencias([])
    setErrorBusqueda('')
  }

  const manejarContacto = async (
    evento,
  ) => {
    evento.preventDefault()

    const formulario =
      evento.currentTarget

    const datosFormulario =
      new FormData(formulario)

    const datos = {
      nombre:
        datosFormulario.get('nombre'),
      correo:
        datosFormulario.get('correo'),
      mensaje:
        datosFormulario.get('mensaje'),
    }

    try {
      setMensajeEnviado(false)

      const respuesta = await fetch(
        `${API_URL}/public/contacto`,
        {
          method: 'POST',
          headers: {
            'Content-Type':
              'application/json',
          },
          body: JSON.stringify(datos),
        },
      )

      if (!respuesta.ok) {
        throw new Error(
          'No fue posible enviar el mensaje',
        )
      }

      setMensajeEnviado(true)
      formulario.reset()
    } catch (error) {
      console.error(error)

      alert(
        'No fue posible enviar el mensaje. Intenta nuevamente.',
      )
    }
  }

  return (
    <main className="public-page">
      <header className="public-header">
        <a
          className="public-brand"
          href="#inicio"
        >
          <span className="public-brand-mark">
            <img
             src="/logo/lashbook-logo.png"
             alt="Logo de LashBook"
             className="public-brand-logo"
           />
          </span>

          <span className="public-brand-name">
            LashBook
          </span>
        </a>

        <nav
          className="public-nav"
          aria-label="Navegación principal"
        >
          <a href="#inicio">
            Inicio
          </a>

          <a href="#servicios">
            Servicios
          </a>

          <a href="#nosotros">
            Nosotros
          </a>

          <a href="#contacto">
            Contacto
          </a>
        </nav>

        <div className="public-header-actions">
          <Link
            className="public-button secondary"
            to="/login"
          >
            Iniciar sesión
          </Link>

          <Link
            className="public-button"
            to="/registro"
          >
            Crear cuenta
          </Link>
        </div>
      </header>

      <section
        id="inicio"
        className="public-hero"
      >
        <div className="public-hero-content">
          <p className="eyebrow">
            Belleza, organización y comodidad
          </p>

          <h1>
            Tus citas de pestañas, en un solo lugar
          </h1>

          <p className="public-hero-description">
            Conoce nuestros servicios, reserva tu cita,
            recibe recordatorios y consulta cualquier
            cambio desde LashBook.
          </p>

          <div className="public-hero-actions">
            <Link
              className="public-button"
              to="/registro"
            >
              Crear cuenta y reservar
            </Link>

            <a
              className="public-button secondary"
              href="#servicios"
            >
              Ver servicios
            </a>
          </div>
        </div>

        <div className="public-hero-visual">
           <div className="public-carousel">
                <img
                 className="public-carousel-image"
                 src={imagenesCarrusel[imagenActual]}
                 alt={`Trabajo de pestañas ${imagenActual + 1}`}
               />

              <button
                 className="public-carousel-arrow previous"
                 type="button"
                 onClick={imagenAnterior}
                  aria-label="Imagen anterior"
               >
               ‹
               </button>
                <button
                   className="public-carousel-arrow next"
                   type="button"
                  onClick={imagenSiguiente}
                  aria-label="Imagen siguiente"
                >
                 ›
                </button>
                <div className="public-carousel-dots">
      {imagenesCarrusel.map((imagen, indice) => (
        <button
          key={imagen}
          type="button"
          className={
            indice === imagenActual
              ? 'public-carousel-dot active'
              : 'public-carousel-dot'
          }
          onClick={() =>
            setImagenActual(indice)
          }
          aria-label={`Mostrar imagen ${indice + 1}`}
        />
      ))}
    </div>
  </div>
   <article className="public-hero-card">
    <h2>
      Reserva fácilmente
    </h2>

    <p>
      Selecciona tu servicio, fecha y hora.
      Consulta después el estado de tu cita
      desde tu panel personal.
    </p>
  </article>   
        </div>
      </section>

      <section
        id="servicios"
        className="public-section"
      >
        <div className="public-section-heading centered">
          <p className="eyebrow">
            Nuestros servicios
          </p>

          <h2>
            Una mirada para cada estilo
          </h2>

          <p>
            Explora nuestros servicios o utiliza el
            buscador inteligente para encontrar
            rápidamente lo que necesitas.
          </p>
        </div>

        <div className="public-search-wrapper">
          <div className="public-search-box">
            <span
              className="public-search-icon"
              aria-hidden="true"
            >
              ⌕
            </span>

            <input
              className="public-search-input"
              type="search"
              value={textoBusqueda}
              onChange={(evento) =>
                setTextoBusqueda(
                  evento.target.value,
                )
              }
              placeholder="Busca un servicio, por ejemplo: extensiones..."
              aria-label="Buscar servicios"
              autoComplete="off"
            />

            {textoBusqueda && (
              <button
                className="public-search-clear"
                type="button"
                onClick={limpiarBusqueda}
                aria-label="Limpiar búsqueda"
              >
                ×
              </button>
            )}
          </div>

          <p className="public-search-caption">
            Búsqueda predictiva con Elasticsearch
          </p>

          {cargandoBusqueda && (
            <div className="public-search-results">
              <p className="public-search-status">
                Buscando...
              </p>
            </div>
          )}

          {!cargandoBusqueda &&
            errorBusqueda && (
              <div className="public-search-results">
                <p className="public-search-status">
                  {errorBusqueda}
                </p>
              </div>
            )}

          {!cargandoBusqueda &&
            !errorBusqueda &&
            textoBusqueda.trim().length >= 2 &&
            sugerencias.length === 0 && (
              <div className="public-search-results">
                <p className="public-search-status">
                  No encontramos servicios con ese
                  término.
                </p>
              </div>
            )}

          {!cargandoBusqueda &&
            sugerencias.length > 0 && (
              <div
                className="public-search-results"
                role="listbox"
                aria-label="Sugerencias de servicios"
              >
                {sugerencias.map(
                  (servicio) => (
                    <button
                      key={servicio.id}
                      className="public-search-result"
                      type="button"
                      onClick={() =>
                        seleccionarServicio(
                          servicio,
                        )
                      }
                    >
                      {servicio.imagenUrl ? (
                        <img
                          src={servicio.imagenUrl}
                          alt=""
                        />
                      ) : (
                        <span className="public-search-result-placeholder">
                          LB
                        </span>
                      )}

                      <span className="public-search-result-info">
                        <strong>
                          {servicio.nombre}
                        </strong>

                        <span>
                          {servicio.duracionMinutos}{' '}
                          min · $
                          {Number(
                            servicio.precio,
                          ).toLocaleString(
                            'es-MX',
                            {
                              minimumFractionDigits: 2,
                              maximumFractionDigits: 2,
                            },
                          )}
                        </span>
                      </span>

                      <span
                        className="public-search-result-arrow"
                        aria-hidden="true"
                      >
                        →
                      </span>
                    </button>
                  ),
                )}
              </div>
            )}
        </div>

        <div className="public-card-grid">
          {cargandoServicios && (
            <p className="form-message">
              Cargando servicios...
            </p>
          )}

          {!cargandoServicios &&
            errorServicios && (
              <p className="form-message">
                {errorServicios}
              </p>
            )}

          {!cargandoServicios &&
            !errorServicios &&
            servicios.length === 0 && (
              <p className="form-message">
                No hay servicios disponibles en este
                momento.
              </p>
            )}

          {!cargandoServicios &&
            !errorServicios &&
            servicios.map((servicio) => (
              <article
                id={`servicio-${servicio.id}`}
                className="public-card"
                key={servicio.id}
              >
                {servicio.imagenUrl ? (
                  <img
                    className="public-card-image"
                    src={servicio.imagenUrl}
                    alt={`Servicio ${servicio.nombre}`}
                    loading="lazy"
                  />
                ) : (
                  <div className="public-card-image public-hero-placeholder">
                    Imagen próximamente
                  </div>
                )}

                <div className="public-card-body">
                  <h3>
                    {servicio.nombre}
                  </h3>

                  <p>
                    {servicio.descripcion ||
                      'Servicio disponible para reservación.'}
                  </p>

                  <div className="public-card-meta">
                    <span className="public-chip">
                      Precio: $
                      {Number(
                        servicio.precio,
                      ).toLocaleString(
                        'es-MX',
                        {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2,
                        },
                      )}
                    </span>

                    <span className="public-chip">
                      {servicio.duracionMinutos}{' '}
                      minutos
                    </span>
                  </div>

                  <Link
                    className="public-button"
                    to="/registro"
                    style={{
                      width: '100%',
                      marginTop: '22px',
                    }}
                  >
                    Crear cuenta para reservar
                  </Link>
                </div>
              </article>
            ))}
        </div>
      </section>

      <section
        id="nosotros"
        className="public-section accent"
      >
        <div className="public-section-heading centered">
          <p className="eyebrow">
            ¿Cómo funciona?
          </p>

          <h2>
            Tu cita en tres pasos
          </h2>

          <p>
            LashBook reúne el proceso de registro,
            reservación y seguimiento en una experiencia
            sencilla.
          </p>
        </div>

        <div className="public-steps">
          <article className="public-step">
            <span className="public-step-number">
              1
            </span>

            <h3>
              Crea tu cuenta
            </h3>

            <p>
              Regístrate como clienta usando tu nombre,
              correo y contraseña.
            </p>
          </article>

          <article className="public-step">
            <span className="public-step-number">
              2
            </span>

            <h3>
              Reserva tu servicio
            </h3>

            <p>
              Elige el servicio, la fecha y la hora que
              mejor se adapten a ti.
            </p>
          </article>

          <article className="public-step">
            <span className="public-step-number">
              3
            </span>

            <h3>
              Recibe seguimiento
            </h3>

            <p>
              Consulta cambios, historial y recordatorios
              desde la web y el reloj.
            </p>
          </article>
        </div>
      </section>

      <section
        id="contacto"
        className="public-section soft"
      >
        <div className="public-section-heading">
          <p className="eyebrow">
            Contacto
          </p>

          <h2>
            Estamos para ayudarte
          </h2>

          <p>
            Ponte en contacto con LashBook para resolver
            dudas sobre servicios, citas y disponibilidad.
          </p>
        </div>
        <div className="public-contact-grid">
  <article className="public-contact-card">
    <h3>
      Información
    </h3>

    <div className="public-contact-list">
      <div className="public-contact-item">
        <strong>
          Teléfono
        </strong>

        <a href="tel:+524421230996">
          +52 442 123 0996
        </a>
      </div>

      <div className="public-contact-item">
        <strong>
          Correo
        </strong>

        <a href="mailto:lashbook@gmail.com">
          lashbook@gmail.com
        </a>
      </div>

      <div className="public-contact-item">
        <strong>
          Horario
        </strong>

        <span>
          Lunes a viernes: 11:00 a 19:00
          <br />
          Sábado: 11:00 a 16:00
          <br />
          Domingo: Cerrado
        </span>
      </div>

      <div className="public-contact-item">
        <strong>
          Ubicación
        </strong>

        <a
          href="https://maps.app.goo.gl/m7KKRbNm3yDcFmju5"
          target="_blank"
          rel="noreferrer"
        >
          Ver ubicación en Google Maps
        </a>
      </div>
    </div>

    <div className="public-social-links">
      <a
        href="https://www.instagram.com/pao_mrlss/"
        target="_blank"
        rel="noreferrer"
        aria-label="Instagram de LashBook"
      >
         <img
            src="https://cdn.simpleicons.org/instagram/6e4e4a"
            alt=""
            className="social-icon"
       />
        
        <span>
          Instagram
        </span>
      </a>

      <a
        href="https://www.facebook.com/share/19YVVqWiUD/?mibextid=wwXIfr"
        target="_blank"
        rel="noreferrer"
        aria-label="Facebook de LashBook"
      >
      <img
         src="https://cdn.simpleicons.org/facebook/6e4e4a"
         alt=""
         className="social-icon"
       />

        <span>
          Facebook
        </span>
      </a>

      <a
        href="https://wa.me/524421230996?text=Hola%2C%20vi%20LashBook%20y%20me%20gustar%C3%ADa%20recibir%20informaci%C3%B3n%20sobre%20los%20servicios."
        target="_blank"
        rel="noreferrer"
        aria-label="WhatsApp de LashBook"
      >
       <img
          src="https://cdn.simpleicons.org/whatsapp/6e4e4a"
          alt=""
         className="social-icon"
        />
       

        <span>
          WhatsApp
        </span>
      </a>
    </div>
  </article>

  <form
    className="public-contact-form"
    onSubmit={manejarContacto}
  >
    <h3>
      Envíanos un mensaje
    </h3>

    <label htmlFor="contactoNombre">
      Nombre
    </label>

    <input
      id="contactoNombre"
      name="nombre"
      type="text"
      placeholder="Tu nombre"
      required
    />

    <label htmlFor="contactoCorreo">
      Correo electrónico
    </label>

    <input
      id="contactoCorreo"
      name="correo"
      type="email"
      placeholder="nombre@correo.com"
      required
    />

    <label htmlFor="contactoMensaje">
      Mensaje
    </label>

    <textarea
      id="contactoMensaje"
      name="mensaje"
      placeholder="Escribe tu mensaje"
      required
    />

    <button
      className="public-button"
      type="submit"
    >
      Enviar mensaje
    </button>

    {mensajeEnviado && (
      <p
        className="form-message"
        role="status"
      >
        ¡Gracias! Tu mensaje fue enviado
        correctamente.
      </p>
    )}
  </form>
</div>

<div className="public-map">
  <iframe
    title="Ubicación de LashBook"
    src="https://www.google.com/maps?q=20.6513471,-100.4723743&output=embed"
    loading="lazy"
    referrerPolicy="no-referrer-when-downgrade"
    allowFullScreen
  />
</div>
</section>   
            
          
      

      <footer className="public-footer">
        <div className="public-footer-grid">
          <div>
            <h3>
              LashBook
            </h3>

            <p>
              Plataforma para reservar, organizar y
              consultar citas de servicios de pestañas.
            </p>
          </div>

          <div>
            <h3>
              Navegación
            </h3>

            <a href="#inicio">
              Inicio
            </a>

            <a href="#servicios">
              Servicios
            </a>

            <a href="#nosotros">
              Nosotros
            </a>

            <a href="#contacto">
              Contacto
            </a>
          </div>

          <div>
            <h3>
              Cuenta
            </h3>

            <Link to="/login">
              Iniciar sesión
            </Link>

            <Link to="/registro">
              Crear cuenta
            </Link>
          </div>
        </div>

        <div className="public-footer-bottom">
          © 2026 LashBook.
        </div>
      </footer>
    </main>
  )
}

export default InicioPublicoPage