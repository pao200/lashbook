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

export async function obtenerEstadisticas() {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/admin/estadisticas`,
    {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  )

  const datos = await leerRespuesta(respuesta)

  if (!respuesta.ok) {
    throw new Error(
      datos?.mensaje ||
        'No fue posible cargar las estadísticas',
    )
  }

  return datos
}