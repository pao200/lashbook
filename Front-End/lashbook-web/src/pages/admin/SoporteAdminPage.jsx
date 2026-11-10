import { useEffect, useState } from 'react'

import AdminLayout from '../../layouts/AdminLayout'

import {
  cerrarConversacionSoporte,
  enviarMensajeSoporte,
  obtenerConversacionSoporte,
  obtenerConversacionesSoporteAdmin,
} from '../../api/soporteApi'

import './SoporteAdmin.css'

function SoporteAdminPage() {
  const [conversaciones, setConversaciones] = useState([])
  const [conversacionSeleccionada, setConversacionSeleccionada] =
    useState(null)

  const [nuevoMensaje, setNuevoMensaje] = useState('')

  const [cargando, setCargando] = useState(true)
  const [enviando, setEnviando] = useState(false)
  const [cerrando, setCerrando] = useState(false)

  const [error, setError] = useState('')
  const [mensajeExito, setMensajeExito] = useState('')


  // ======================================================
  // CARGAR TODAS LAS CONVERSACIONES
  // ======================================================

  const cargarConversaciones = async () => {
    try {
      setError('')

      const datos =
        await obtenerConversacionesSoporteAdmin()

      

      const lista =
        Array.isArray(datos)
          ? datos
          : []

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
        'ERROR SOPORTE ADMIN:',
        err
      )

      setError(
        err?.message ||
          'No se pudieron cargar las conversaciones.'
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
    } catch (err) {
      console.error(
        'ERROR AL ABRIR CONVERSACIÓN:',
        err
      )

      setError(
        err?.message ||
          'No se pudo abrir la conversación.'
      )
    }
  }


  // ======================================================
  // RESPONDER
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

      setMensajeExito(
        'Respuesta enviada correctamente.'
      )

      await cargarConversaciones()
    } catch (err) {
      console.error(
        'ERROR AL ENVIAR MENSAJE:',
        err
      )

      setError(
        err?.message ||
          'No se pudo enviar el mensaje.'
      )
    } finally {
      setEnviando(false)
    }
  }


  // ======================================================
  // CERRAR CONVERSACIÓN
  // ======================================================

  const cerrarConversacion = async () => {
    if (!conversacionSeleccionada?.id) {
      return
    }

    const confirmar = window.confirm(
      '¿Deseas cerrar esta conversación? La clienta ya no podrá enviar mensajes.'
    )

    if (!confirmar) {
      return
    }

    try {
      setCerrando(true)
      setError('')
      setMensajeExito('')

      const actualizada =
        await cerrarConversacionSoporte(
          conversacionSeleccionada.id
        )

      if (actualizada) {
        setConversacionSeleccionada(
          actualizada
        )
      }

      setMensajeExito(
        'La conversación fue cerrada correctamente.'
      )

      await cargarConversaciones()
    } catch (err) {
      console.error(
        'ERROR AL CERRAR CONVERSACIÓN:',
        err
      )

      setError(
        err?.message ||
          'No se pudo cerrar la conversación.'
      )
    } finally {
      setCerrando(false)
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

  const obtenerNombreClienta = (
    conversacion
  ) => {
    return (
      conversacion?.clientaNombre ||
      'Clienta'
    )
  }


  const obtenerCorreoClienta = (
    conversacion
  ) => {
    return (
      conversacion?.clientaCorreo ||
      ''
    )
  }


  const obtenerEstado = (
    conversacion
  ) => {
    return (
      conversacion?.estado ||
      'ABIERTA'
    )
  }


  const obtenerMensajes = (
    conversacion
  ) => {
    return Array.isArray(
      conversacion?.mensajes
    )
      ? conversacion.mensajes
      : []
  }


  return (
    <AdminLayout>

      <div className="soporte-admin-page">

        {/* ================================================== */}
        {/* ENCABEZADO */}
        {/* ================================================== */}

        <div className="soporte-admin-header">

          <div>
            <span className="soporte-admin-etiqueta">
              LASHBOOK
            </span>

            <h1>
              Soporte
            </h1>

            <p>
              Consulta y responde los mensajes
              de las clientas.
            </p>
          </div>

          <button
            type="button"
            className="soporte-admin-actualizar"
            onClick={cargarConversaciones}
          >
            Actualizar
          </button>

        </div>


        {/* ================================================== */}
        {/* ALERTAS */}
        {/* ================================================== */}

        {error && (
          <div className="soporte-admin-alerta error">
            {error}
          </div>
        )}

        {mensajeExito && (
          <div className="soporte-admin-alerta exito">
            {mensajeExito}
          </div>
        )}


        {/* ================================================== */}
        {/* CONTENIDO PRINCIPAL */}
        {/* ================================================== */}

        <div className="soporte-admin-layout">


          {/* ================================================== */}
          {/* LISTA DE CONVERSACIONES */}
          {/* ================================================== */}

          <aside className="soporte-admin-sidebar">

            <div className="soporte-admin-sidebar-header">

              <div>
                <h2>
                  Conversaciones
                </h2>

                <span>
                  {conversaciones.length}
                  {' '}
                  registradas
                </span>
              </div>

            </div>


            {cargando ? (

              <div className="soporte-admin-vacio">
                Cargando conversaciones...
              </div>

            ) : conversaciones.length === 0 ? (

              <div className="soporte-admin-vacio">
                No existen conversaciones
                de soporte.
              </div>

            ) : (

              <div className="soporte-admin-lista">

                {conversaciones.map(
                  (conversacion) => {

                    const estado =
                      obtenerEstado(
                        conversacion
                      )

                    return (
                      <button
                        type="button"
                        key={
                          conversacion?.id
                        }
                        className={
                          conversacionSeleccionada?.id ===
                          conversacion?.id
                            ? 'soporte-admin-item activo'
                            : 'soporte-admin-item'
                        }
                        onClick={() =>
                          abrirConversacion(
                            conversacion?.id
                          )
                        }
                      >

                        <div className="soporte-admin-item-top">

                          <strong>
                            {
                              conversacion?.asunto ||
                              'Sin asunto'
                            }
                          </strong>

                          <span
                            className={`soporte-admin-estado ${estado.toLowerCase()}`}
                          >
                            {estado}
                          </span>

                        </div>


                        <p>
                          {
                            obtenerNombreClienta(
                              conversacion
                            )
                          }
                        </p>


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
          {/* CHAT */}
          {/* ================================================== */}

          <section className="soporte-admin-contenido">

            {conversacionSeleccionada ? (

              <div className="soporte-admin-chat">


                {/* ============================================ */}
                {/* HEADER DEL CHAT */}
                {/* ============================================ */}

                <div className="soporte-admin-chat-header">

                  <div>

                    <span className="soporte-admin-clienta">
                      {
                        obtenerNombreClienta(
                          conversacionSeleccionada
                        )
                      }
                    </span>

                    {obtenerCorreoClienta(
                      conversacionSeleccionada
                    ) && (
                      <small className="soporte-admin-clienta">
                        {
                          obtenerCorreoClienta(
                            conversacionSeleccionada
                          )
                        }
                      </small>
                    )}

                    <h2>
                      {
                        conversacionSeleccionada?.asunto ||
                        'Sin asunto'
                      }
                    </h2>

                    <span
                      className={`soporte-admin-estado ${obtenerEstado(
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


                  <div className="soporte-admin-chat-acciones">

                    <button
                      type="button"
                      onClick={() =>
                        abrirConversacion(
                          conversacionSeleccionada?.id
                        )
                      }
                    >
                      Actualizar
                    </button>


                    {obtenerEstado(
                      conversacionSeleccionada
                    ) === 'ABIERTA' && (

                      <button
                        type="button"
                        className="cerrar"
                        disabled={cerrando}
                        onClick={
                          cerrarConversacion
                        }
                      >
                        {
                          cerrando
                            ? 'Cerrando...'
                            : 'Cerrar conversación'
                        }
                      </button>

                    )}

                  </div>

                </div>


                {/* ============================================ */}
                {/* MENSAJES */}
                {/* ============================================ */}

                <div className="soporte-admin-mensajes">

                  {obtenerMensajes(
                    conversacionSeleccionada
                  ).length > 0 ? (

                    obtenerMensajes(
                      conversacionSeleccionada
                    ).map(
                      (mensaje) => {

                        const esLashista =
                          mensaje?.remitenteRol ===
                            'LASHISTA' ||
                          mensaje?.remitenteRol ===
                            'ADMIN'

                        return (
                          <div
                            key={mensaje?.id}
                            className={
                              esLashista
                                ? 'soporte-admin-mensaje-fila lashista'
                                : 'soporte-admin-mensaje-fila clienta'
                            }
                          >

                            <div className="soporte-admin-burbuja">

                              <strong>
                                {
                                  esLashista
                                    ? 'Tú'
                                    : mensaje?.remitenteNombre ||
                                      obtenerNombreClienta(
                                        conversacionSeleccionada
                                      )
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

                    <div className="soporte-admin-vacio">
                      Esta conversación todavía
                      no tiene mensajes.
                    </div>

                  )}

                </div>


                {/* ============================================ */}
                {/* RESPUESTA */}
                {/* ============================================ */}

                {obtenerEstado(
                  conversacionSeleccionada
                ) === 'ABIERTA' ? (

                  <form
                    className="soporte-admin-form"
                    onSubmit={
                      enviarMensaje
                    }
                  >

                    <textarea
                      value={nuevoMensaje}
                      maxLength={2000}
                      rows={3}
                      placeholder="Escribe una respuesta..."
                      onChange={(event) =>
                        setNuevoMensaje(
                          event.target.value
                        )
                      }
                    />

                    <button
                      type="submit"
                      disabled={
                        enviando ||
                        !nuevoMensaje.trim()
                      }
                    >
                      {
                        enviando
                          ? 'Enviando...'
                          : 'Enviar respuesta'
                      }
                    </button>

                  </form>

                ) : (

                  <div className="soporte-admin-cerrada">
                    Esta conversación está
                    cerrada. Ya no se pueden
                    enviar mensajes.
                  </div>

                )}

              </div>

            ) : (

              <div className="soporte-admin-placeholder">

                <h2>
                  Selecciona una conversación
                </h2>

                <p>
                  Elige una conversación de la
                  lista para consultar los
                  mensajes de la clienta y
                  responder.
                </p>

              </div>

            )}

          </section>

        </div>

      </div>

    </AdminLayout>
  )
}

export default SoporteAdminPage