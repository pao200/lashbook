import { useEffect, useState } from 'react'

import DashboardLayout from '../../layouts/DashboardLayout'

import {
  crearConversacionSoporte,
  enviarMensajeSoporte,
  obtenerConversacionSoporte,
  obtenerMisConversacionesSoporte,
} from '../../api/soporteApi'

import './SoporteClienta.css'

function SoporteClientaPage() {
  const [conversaciones, setConversaciones] = useState([])
  const [conversacionSeleccionada, setConversacionSeleccionada] =
    useState(null)

  const [mostrarNueva, setMostrarNueva] = useState(false)

  const [asunto, setAsunto] = useState('')
  const [mensajeInicial, setMensajeInicial] = useState('')
  const [nuevoMensaje, setNuevoMensaje] = useState('')

  const [cargando, setCargando] = useState(true)
  const [enviando, setEnviando] = useState(false)

  const [error, setError] = useState('')
  const [mensajeExito, setMensajeExito] = useState('')


  // ======================================================
  // CARGAR CONVERSACIONES
  // ======================================================

  const cargarConversaciones = async () => {
    try {
      setError('')

      const datos =
        await obtenerMisConversacionesSoporte()

      

      let lista = []

      if (Array.isArray(datos)) {
        lista = datos
      } else if (
        Array.isArray(datos?.conversaciones)
      ) {
        lista = datos.conversaciones
      } else if (
        Array.isArray(datos?.content)
      ) {
        lista = datos.content
      }

      setConversaciones(lista)

      if (conversacionSeleccionada) {
        const actualizada = lista.find(
          (item) =>
            item?.id ===
            conversacionSeleccionada?.id
        )

        if (actualizada) {
          setConversacionSeleccionada(
            actualizada
          )
        }
      }
    } catch (err) {
      console.error(
        'ERROR SOPORTE CLIENTA:',
        err
      )

      setError(
        err?.message ||
          'No se pudieron cargar las conversaciones'
      )
    } finally {
      setCargando(false)
    }
  }


  useEffect(() => {
    cargarConversaciones()
  }, [])


  // ======================================================
  // ABRIR CONVERSACIÓN
  // ======================================================

  const abrirConversacion = async (id) => {
    if (!id) {
      return
    }

    try {
      setError('')
      setMensajeExito('')

      const datos =
        await obtenerConversacionSoporte(id)

      if (!datos) {
        throw new Error(
          'No se recibió información de la conversación.'
        )
      }

      setConversacionSeleccionada(datos)
      setMostrarNueva(false)
    } catch (err) {
      console.error(
        'ERROR AL ABRIR CONVERSACIÓN:',
        err
      )

      setError(
        err?.message ||
          'No se pudo abrir la conversación'
      )
    }
  }


  // ======================================================
  // CREAR CONVERSACIÓN
  // ======================================================

  const crearConversacion = async (event) => {
    event.preventDefault()

    if (!asunto.trim() || !mensajeInicial.trim()) {
      setError(
        'Escribe un asunto y un mensaje.'
      )
      return
    }

    try {
      setEnviando(true)
      setError('')
      setMensajeExito('')

      const nueva =
        await crearConversacionSoporte(
          asunto.trim(),
          mensajeInicial.trim()
        )

      setAsunto('')
      setMensajeInicial('')
      setMostrarNueva(false)

      if (nueva) {
        setConversacionSeleccionada(nueva)
      }

      setMensajeExito(
        'Tu conversación de soporte fue creada.'
      )

      await cargarConversaciones()
    } catch (err) {
      console.error(
        'ERROR AL CREAR CONVERSACIÓN:',
        err
      )

      setError(
        err?.message ||
          'No se pudo crear la conversación'
      )
    } finally {
      setEnviando(false)
    }
  }


  // ======================================================
  // ENVIAR MENSAJE
  // ======================================================

  const enviarMensaje = async (event) => {
    event.preventDefault()

    if (!nuevoMensaje.trim()) {
      return
    }

    if (!conversacionSeleccionada?.id) {
      return
    }

    try {
      setEnviando(true)
      setError('')
      setMensajeExito('')

      const actualizada =
        await enviarMensajeSoporte(
          conversacionSeleccionada.id,
          nuevoMensaje.trim()
        )

      setNuevoMensaje('')

      if (actualizada) {
        setConversacionSeleccionada(
          actualizada
        )
      }

      await cargarConversaciones()
    } catch (err) {
      console.error(
        'ERROR AL ENVIAR MENSAJE:',
        err
      )

      setError(
        err?.message ||
          'No se pudo enviar el mensaje'
      )
    } finally {
      setEnviando(false)
    }
  }


  // ======================================================
  // FORMATO DE FECHA
  // ======================================================

  const formatearFecha = (fecha) => {
    if (!fecha) {
      return ''
    }

    const fechaObjeto = new Date(fecha)

    if (
      Number.isNaN(
        fechaObjeto.getTime()
      )
    ) {
      return ''
    }

    return fechaObjeto.toLocaleString(
      'es-MX',
      {
        dateStyle: 'short',
        timeStyle: 'short',
      }
    )
  }


  // ======================================================
  // DATOS SEGUROS
  // ======================================================

  const conversacionesSeguras =
    Array.isArray(conversaciones)
      ? conversaciones
      : []


  const obtenerEstado = (conversacion) => {
    return (
      conversacion?.estado ||
      'ABIERTA'
    )
  }


  const obtenerMensajes = (conversacion) => {
    return Array.isArray(
      conversacion?.mensajes
    )
      ? conversacion.mensajes
      : []
  }


  return (
    <DashboardLayout>

      <div className="soporte-page">

        {/* ================================================== */}
        {/* ENCABEZADO */}
        {/* ================================================== */}

        <div className="soporte-header">

          <div>
            <span className="soporte-etiqueta">
              LASHBOOK
            </span>

            <h1>
              Soporte
            </h1>

            <p>
              Escríbenos si tienes alguna duda sobre
              tus citas o servicios.
            </p>
          </div>

          <button
            type="button"
            className="btn-nueva-conversacion"
            onClick={() => {
              setMostrarNueva(true)
              setConversacionSeleccionada(null)
              setError('')
              setMensajeExito('')
            }}
          >
            + Nueva conversación
          </button>

        </div>


        {/* ================================================== */}
        {/* ALERTAS */}
        {/* ================================================== */}

        {error && (
          <div className="soporte-alerta soporte-error">
            {error}
          </div>
        )}

        {mensajeExito && (
          <div className="soporte-alerta soporte-exito">
            {mensajeExito}
          </div>
        )}


        {/* ================================================== */}
        {/* CONTENIDO */}
        {/* ================================================== */}

        <div className="soporte-layout">


          {/* ================================================== */}
          {/* LISTA DE CONVERSACIONES */}
          {/* ================================================== */}

          <aside className="soporte-sidebar">

            <div className="soporte-sidebar-header">

              <h2>
                Mis conversaciones
              </h2>

              <button
                type="button"
                className="btn-actualizar"
                onClick={cargarConversaciones}
              >
                Actualizar
              </button>

            </div>


            {cargando ? (

              <p className="soporte-vacio">
                Cargando conversaciones...
              </p>

            ) : conversacionesSeguras.length === 0 ? (

              <div className="soporte-vacio">

                <p>
                  Todavía no tienes conversaciones.
                </p>

                <button
                  type="button"
                  onClick={() =>
                    setMostrarNueva(true)
                  }
                >
                  Crear conversación
                </button>

              </div>

            ) : (

              <div className="conversaciones-lista">

                {conversacionesSeguras.map(
                  (conversacion) => {

                    const estado =
                      obtenerEstado(
                        conversacion
                      )

                    return (
                      <button
                        type="button"
                        key={conversacion?.id}
                        className={
                          conversacionSeleccionada?.id ===
                          conversacion?.id
                            ? 'conversacion-item activa'
                            : 'conversacion-item'
                        }
                        onClick={() =>
                          abrirConversacion(
                            conversacion?.id
                          )
                        }
                      >

                        <div className="conversacion-item-top">

                          <strong>
                            {
                              conversacion?.asunto ||
                              'Sin asunto'
                            }
                          </strong>

                          <span
                            className={`estado-soporte ${estado.toLowerCase()}`}
                          >
                            {estado}
                          </span>

                        </div>


                        <small>
                          {
                            formatearFecha(
                              conversacion?.actualizadoEn ||
                              conversacion?.creadoEn
                            )
                          }
                        </small>

                      </button>
                    )
                  }
                )}

              </div>
            )}

          </aside>


          {/* ================================================== */}
          {/* PANEL DERECHO */}
          {/* ================================================== */}

          <section className="soporte-contenido">


            {/* ================================================== */}
            {/* NUEVA CONVERSACIÓN */}
            {/* ================================================== */}

            {mostrarNueva ? (

              <form
                className="nueva-conversacion"
                onSubmit={crearConversacion}
              >

                <h2>
                  Nueva conversación
                </h2>

                <p>
                  Cuéntanos cómo podemos ayudarte.
                </p>


                <label>
                  Asunto
                </label>

                <input
                  type="text"
                  value={asunto}
                  maxLength={150}
                  onChange={(event) =>
                    setAsunto(
                      event.target.value
                    )
                  }
                  placeholder="Ej. Duda sobre mi cita"
                />


                <label>
                  Mensaje
                </label>

                <textarea
                  value={mensajeInicial}
                  maxLength={2000}
                  onChange={(event) =>
                    setMensajeInicial(
                      event.target.value
                    )
                  }
                  placeholder="Escribe tu mensaje..."
                  rows={7}
                />


                <div className="nueva-conversacion-acciones">

                  <button
                    type="button"
                    className="btn-secundario"
                    onClick={() => {
                      setMostrarNueva(false)
                      setError('')
                    }}
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn-enviar"
                    disabled={enviando}
                  >
                    {
                      enviando
                        ? 'Enviando...'
                        : 'Crear conversación'
                    }
                  </button>

                </div>

              </form>


            ) : conversacionSeleccionada ? (

              /* ================================================== */
              /* CHAT */
              /* ================================================== */

              <div className="chat-soporte">

                <div className="chat-header">

                  <div>

                    <h2>
                      {
                        conversacionSeleccionada?.asunto ||
                        'Sin asunto'
                      }
                    </h2>

                    <span
                      className={`estado-soporte ${obtenerEstado(
                        conversacionSeleccionada
                      ).toLowerCase()}`}
                    >
                      {
                        obtenerEstado(
                          conversacionSeleccionada
                        )
                      }
                    </span>

                  </div>


                  <button
                    type="button"
                    className="btn-actualizar"
                    onClick={() =>
                      abrirConversacion(
                        conversacionSeleccionada?.id
                      )
                    }
                  >
                    Actualizar
                  </button>

                </div>


                {/* ================================================== */}
                {/* MENSAJES */}
                {/* ================================================== */}

                <div className="chat-mensajes">

                  {obtenerMensajes(
                    conversacionSeleccionada
                  ).length > 0 ? (

                    obtenerMensajes(
                      conversacionSeleccionada
                    ).map(
                      (mensaje) => {

                        const esClienta =
                          mensaje?.remitenteRol ===
                          'CLIENTA'

                        return (
                          <div
                            key={mensaje?.id}
                            className={
                              esClienta
                                ? 'mensaje-fila clienta'
                                : 'mensaje-fila lashista'
                            }
                          >

                            <div className="mensaje-burbuja">

                              <strong>
                                {
                                  esClienta
                                    ? 'Tú'
                                    : mensaje?.remitenteNombre ||
                                      'LashBook'
                                }
                              </strong>

                              <p>
                                {
                                  mensaje?.mensaje ||
                                  ''
                                }
                              </p>

                              <small>
                                {
                                  formatearFecha(
                                    mensaje?.enviadoEn
                                  )
                                }
                              </small>

                            </div>

                          </div>
                        )
                      }
                    )

                  ) : (

                    <div className="soporte-vacio">
                      Esta conversación todavía no
                      tiene mensajes.
                    </div>

                  )}

                </div>


                {/* ================================================== */}
                {/* FORMULARIO DE MENSAJE */}
                {/* ================================================== */}

                {obtenerEstado(
                  conversacionSeleccionada
                ) === 'ABIERTA' ? (

                  <form
                    className="chat-form"
                    onSubmit={enviarMensaje}
                  >

                    <textarea
                      value={nuevoMensaje}
                      maxLength={2000}
                      onChange={(event) =>
                        setNuevoMensaje(
                          event.target.value
                        )
                      }
                      placeholder="Escribe un mensaje..."
                      rows={3}
                    />

                    <button
                      type="submit"
                      className="btn-enviar"
                      disabled={
                        enviando ||
                        !nuevoMensaje.trim()
                      }
                    >
                      {
                        enviando
                          ? 'Enviando...'
                          : 'Enviar'
                      }
                    </button>

                  </form>

                ) : (

                  <div className="conversacion-cerrada">
                    Esta conversación fue cerrada.
                    Ya no se pueden enviar mensajes.
                  </div>

                )}

              </div>


            ) : (

              /* ================================================== */
              /* PLACEHOLDER */
              /* ================================================== */

              <div className="soporte-placeholder">

                <h2>
                  ¿Necesitas ayuda?
                </h2>

                <p>
                  Selecciona una conversación o crea
                  una nueva para comunicarte con
                  LashBook.
                </p>

                <button
                  type="button"
                  className="btn-nueva-conversacion"
                  onClick={() => {
                    setMostrarNueva(true)
                    setError('')
                    setMensajeExito('')
                  }}
                >
                  Nueva conversación
                </button>

              </div>

            )}

          </section>

        </div>

      </div>

    </DashboardLayout>
  )
}

export default SoporteClientaPage