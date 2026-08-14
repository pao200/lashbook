import { obtenerToken } from '../utils/almacenamiento'

const API_URL =
  import.meta.env.VITE_API_URL ||
  'http://localhost:8080/api'

async function leerRespuesta(respuesta) {
  const texto = await respuesta.text()

  if (!texto) {
    return null
  }

  return JSON.parse(texto)
}

export async function listarServicios() {
  const respuesta = await fetch(
    `${API_URL}/servicios`,
  )

  const datos = await leerRespuesta(respuesta)

  if (!respuesta.ok) {
    throw new Error(
      datos?.mensaje ||
        'No fue posible cargar los servicios',
    )
  }

  return datos || []
}

export async function buscarServiciosPredictivos(
  texto,
) {
  const termino = texto.trim()

  if (termino.length < 2) {
    return []
  }

  const respuesta = await fetch(
    `${API_URL}/busqueda/servicios?q=${encodeURIComponent(
      termino,
    )}`,
     {
    headers: {
      Accept: 'application/json',
    },
    },
  )

  const datos = await leerRespuesta(respuesta)

  if (!respuesta.ok) {
    throw new Error(
      datos?.mensaje ||
        'No fue posible realizar la búsqueda',
    )
  }

  return datos || []
}

export async function crearServicio({
  nombre,
  descripcion,
  precio,
  duracionMinutos,
  imagenUrl,
}) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/admin/servicios`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nombre,
        descripcion,
        precio,
        duracionMinutos,
        imagenUrl,
      }),
    },
  )

  const datos = await leerRespuesta(respuesta)

  if (!respuesta.ok) {
    throw new Error(
      datos?.mensaje ||
        'No fue posible crear el servicio',
    )
  }

  return datos
}

export async function actualizarServicio(
  servicioId,
  {
    nombre,
    descripcion,
    precio,
    duracionMinutos,
    imagenUrl,
  },
) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/admin/servicios/${servicioId}`,
    {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nombre,
        descripcion,
        precio,
        duracionMinutos,
        imagenUrl,
      }),
    },
  )

  const datos = await leerRespuesta(respuesta)

  if (!respuesta.ok) {
    throw new Error(
      datos?.mensaje ||
        'No fue posible actualizar el servicio',
    )
  }

  return datos
}

export async function eliminarServicio(
  servicioId,
) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/admin/servicios/${servicioId}`,
    {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  )

  const datos = await leerRespuesta(respuesta)

  if (!respuesta.ok) {
    throw new Error(
      datos?.mensaje ||
        'No fue posible desactivar el servicio',
    )
  }

  return datos
}

export async function subirImagenServicio(archivo) {
  const token = obtenerToken()
  const formulario = new FormData()

  formulario.append('archivo', archivo)

  const respuesta = await fetch(
    `${API_URL}/admin/servicios/imagen`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formulario,
    },
  )

  const datos = await leerRespuesta(respuesta)

  if (!respuesta.ok) {
    throw new Error(
      datos?.mensaje ||
        'No fue posible subir la imagen',
    )
  }

  return datos
}