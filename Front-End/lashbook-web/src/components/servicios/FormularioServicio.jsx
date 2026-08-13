import {
  useEffect,
  useState,
} from 'react'

import {
  subirImagenServicio,
} from '../../api/serviciosApi'

function FormularioServicio({
  servicio,
  cargando,
  onGuardar,
  onCancelar,
}) {
  const [nombre, setNombre] = useState('')
  const [descripcion, setDescripcion] = useState('')
  const [precio, setPrecio] = useState('')
  const [duracionMinutos, setDuracionMinutos] =
    useState('')

  const [imagenUrl, setImagenUrl] = useState('')
  const [archivoImagen, setArchivoImagen] =
    useState(null)

  const [vistaPrevia, setVistaPrevia] =
    useState('')

  const [mensajeImagen, setMensajeImagen] =
    useState('')

  const [subiendoImagen, setSubiendoImagen] =
    useState(false)

  useEffect(() => {
    if (servicio) {
      setNombre(servicio.nombre || '')
      setDescripcion(servicio.descripcion || '')
      setPrecio(servicio.precio ?? '')
      setDuracionMinutos(
        servicio.duracionMinutos ?? '',
      )
      setImagenUrl(servicio.imagenUrl || '')
      setVistaPrevia(servicio.imagenUrl || '')
    } else {
      setNombre('')
      setDescripcion('')
      setPrecio('')
      setDuracionMinutos('')
      setImagenUrl('')
      setVistaPrevia('')
    }

    setArchivoImagen(null)
    setMensajeImagen('')
  }, [servicio])

  useEffect(() => {
    if (!archivoImagen) {
      return undefined
    }

    const urlTemporal =
      URL.createObjectURL(archivoImagen)

    setVistaPrevia(urlTemporal)

    return () => {
      URL.revokeObjectURL(urlTemporal)
    }
  }, [archivoImagen])

  const seleccionarImagen = (evento) => {
    const archivo =
      evento.target.files?.[0]

    setMensajeImagen('')

    if (!archivo) {
      setArchivoImagen(null)
      setVistaPrevia(imagenUrl)
      return
    }

    const tiposPermitidos = [
      'image/jpeg',
      'image/png',
      'image/webp',
    ]

    if (!tiposPermitidos.includes(archivo.type)) {
      setMensajeImagen(
        'Solo se permiten imágenes JPG, PNG o WEBP',
      )
      evento.target.value = ''
      return
    }

    const tamanoMaximo =
      5 * 1024 * 1024

    if (archivo.size > tamanoMaximo) {
      setMensajeImagen(
        'La imagen no puede superar 5 MB',
      )
      evento.target.value = ''
      return
    }

    setArchivoImagen(archivo)
  }

  const manejarEnvio = async (evento) => {
    evento.preventDefault()
    setMensajeImagen('')

    let nuevaImagenUrl = imagenUrl

    try {
      if (archivoImagen) {
        setSubiendoImagen(true)

        const respuesta =
          await subirImagenServicio(
            archivoImagen,
          )

        nuevaImagenUrl =
          respuesta.imagenUrl

        setImagenUrl(nuevaImagenUrl)
      }

      await onGuardar({
        nombre: nombre.trim(),
        descripcion: descripcion.trim(),
        precio: Number(precio),
        duracionMinutos:
          Number(duracionMinutos),
        imagenUrl: nuevaImagenUrl || null,
      })
    } catch (error) {
      setMensajeImagen(error.message)
    } finally {
      setSubiendoImagen(false)
    }
  }

  const procesando =
    cargando || subiendoImagen

  return (
    <form
      className="servicio-form"
      onSubmit={manejarEnvio}
    >
      <header>
        <p className="eyebrow">
          {servicio
            ? 'Editar servicio'
            : 'Nuevo servicio'}
        </p>

        <h2>
          {servicio
            ? servicio.nombre
            : 'Agregar servicio'}
        </h2>
      </header>

      <label htmlFor="servicio-nombre">
        Nombre
      </label>

      <input
        id="servicio-nombre"
        type="text"
        value={nombre}
        onChange={(evento) =>
          setNombre(evento.target.value)
        }
        required
      />

      <label htmlFor="servicio-descripcion">
        Descripción
      </label>

      <textarea
        id="servicio-descripcion"
        rows="4"
        value={descripcion}
        onChange={(evento) =>
          setDescripcion(
            evento.target.value,
          )
        }
        required
      />

      <label htmlFor="servicio-precio">
        Precio
      </label>

      <input
        id="servicio-precio"
        type="number"
        min="0.01"
        step="0.01"
        value={precio}
        onChange={(evento) =>
          setPrecio(evento.target.value)
        }
        required
      />

      <label htmlFor="servicio-duracion">
        Duración en minutos
      </label>

      <input
        id="servicio-duracion"
        type="number"
        min="15"
        value={duracionMinutos}
        onChange={(evento) =>
          setDuracionMinutos(
            evento.target.value,
          )
        }
        required
      />

      <label htmlFor="servicio-imagen">
        Imagen del servicio
      </label>

      <input
        id="servicio-imagen"
        type="file"
        accept="image/jpeg,image/png,image/webp"
        onChange={seleccionarImagen}
      />

      {vistaPrevia && (
        <div className="servicio-imagen-preview">
          <img
            src={vistaPrevia}
            alt={`Vista previa de ${nombre || 'servicio'}`}
          />
        </div>
      )}

      {mensajeImagen && (
        <p className="servicio-imagen-mensaje">
          {mensajeImagen}
        </p>
      )}

      <div className="servicio-form-actions">
        <button
          type="submit"
          disabled={procesando}
        >
          {subiendoImagen
            ? 'Subiendo imagen...'
            : cargando
              ? 'Guardando...'
              : servicio
                ? 'Guardar cambios'
                : 'Crear servicio'}
        </button>

        <button
          type="button"
          onClick={onCancelar}
          disabled={procesando}
        >
          Cancelar
        </button>
      </div>
    </form>
  )
}

export default FormularioServicio