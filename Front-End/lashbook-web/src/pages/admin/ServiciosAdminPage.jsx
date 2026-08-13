import {
  useEffect,
  useState,
} from 'react'

import {
  actualizarServicio,
  crearServicio,
  eliminarServicio,
  listarServicios,
} from '../../api/serviciosApi'

import FormularioServicio from '../../components/servicios/FormularioServicio'
import AdminLayout from '../../layouts/AdminLayout'
import './ServiciosAdminPage.css'

function ServiciosAdminPage() {
  const [servicios, setServicios] = useState([])

  const [
    servicioSeleccionado,
    setServicioSeleccionado,
  ] = useState(null)

  const [
    mostrarFormulario,
    setMostrarFormulario,
  ] = useState(false)

  const [cargandoLista, setCargandoLista] =
    useState(true)

  const [guardando, setGuardando] =
    useState(false)

  const [mensaje, setMensaje] =
    useState('')

  useEffect(() => {
    const cargarServicios = async () => {
      try {
        const datos = await listarServicios()
        setServicios(datos)
      } catch (error) {
        setMensaje(error.message)
      } finally {
        setCargandoLista(false)
      }
    }

    cargarServicios()
  }, [])

  const abrirFormularioNuevo = () => {
    setServicioSeleccionado(null)
    setMensaje('')
    setMostrarFormulario(true)
  }

  const abrirFormularioEdicion = (servicio) => {
    setServicioSeleccionado(servicio)
    setMensaje('')
    setMostrarFormulario(true)

    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    })
  }

  const cerrarFormulario = () => {
    setServicioSeleccionado(null)
    setMostrarFormulario(false)
  }

  const guardarServicio = async (
    datosFormulario,
  ) => {
    setMensaje('')
    setGuardando(true)

    try {
      if (servicioSeleccionado) {
        const servicioActualizado =
          await actualizarServicio(
            servicioSeleccionado.id,
            datosFormulario,
          )

        setServicios((serviciosActuales) =>
          serviciosActuales.map((servicio) =>
            servicio.id === servicioActualizado.id
              ? servicioActualizado
              : servicio,
          ),
        )

        setMensaje(
          'El servicio se actualizó correctamente',
        )
      } else {
        const nuevoServicio =
          await crearServicio(datosFormulario)

        setServicios((serviciosActuales) => [
          ...serviciosActuales,
          nuevoServicio,
        ])

        setMensaje(
          'El servicio se creó correctamente',
        )
      }

      cerrarFormulario()
    } catch (error) {
      setMensaje(error.message)
      throw error
    } finally {
      setGuardando(false)
    }
  }

  const desactivarServicio = async (
    servicio,
  ) => {
    const confirmado = window.confirm(
      `¿Deseas desactivar el servicio "${servicio.nombre}"?`,
    )

    if (!confirmado) {
      return
    }

    setMensaje('')

    try {
      await eliminarServicio(servicio.id)

      setServicios((serviciosActuales) =>
        serviciosActuales.filter(
          (elemento) =>
            elemento.id !== servicio.id,
        ),
      )

      setMensaje(
        'El servicio se desactivó correctamente',
      )
    } catch (error) {
      setMensaje(error.message)
    }
  }

  return (
    <AdminLayout>
      <section>
        <header className="servicios-admin-header">
          <div>
            <p className="eyebrow">
              Administración
            </p>

            <h1>
              Servicios
            </h1>

            <p className="servicios-admin-description">
              Crea, modifica o desactiva los servicios
              disponibles para las clientas.
            </p>
          </div>

          <button
            className="servicios-agregar-button"
            type="button"
            onClick={abrirFormularioNuevo}
          >
            Agregar servicio
          </button>
        </header>

        {mensaje && (
          <p className="servicios-admin-mensaje">
            {mensaje}
          </p>
        )}

        {mostrarFormulario && (
          <FormularioServicio
            servicio={servicioSeleccionado}
            cargando={guardando}
            onGuardar={guardarServicio}
            onCancelar={cerrarFormulario}
          />
        )}

        {cargandoLista && (
          <p className="servicios-admin-status">
            Cargando servicios...
          </p>
        )}

        {!cargandoLista &&
          servicios.length === 0 && (
            <p className="servicios-admin-status">
              No hay servicios activos.
            </p>
          )}

        <div className="servicios-admin-grid">
          {servicios.map((servicio) => (
            <article
              className="servicio-admin-card"
              key={servicio.id}
            >
              {servicio.imagenUrl ? (
                <img
                  className="servicio-admin-imagen"
                  src={servicio.imagenUrl}
                  alt={servicio.nombre}
                />
              ) : (
                <div className="servicio-admin-imagen-vacia">
                  Sin imagen
                </div>
              )}

              <div className="servicio-admin-contenido">
                <h2>
                  {servicio.nombre}
                </h2>

                <p className="servicio-admin-descripcion">
                  {servicio.descripcion}
                </p>

                <div className="servicio-admin-detalles">
                  <span>
                    Precio: ${servicio.precio}
                  </span>

                  <span>
                    {servicio.duracionMinutos} minutos
                  </span>
                </div>

                <div className="servicio-admin-acciones">
                  <button
                    className="servicio-admin-editar"
                    type="button"
                    onClick={() =>
                      abrirFormularioEdicion(
                        servicio,
                      )
                    }
                  >
                    Editar
                  </button>

                  <button
                    className="servicio-admin-desactivar"
                    type="button"
                    onClick={() =>
                      desactivarServicio(
                        servicio,
                      )
                    }
                  >
                    Desactivar
                  </button>
                </div>
              </div>
            </article>
          ))}
        </div>
      </section>
    </AdminLayout>
  )
}

export default ServiciosAdminPage