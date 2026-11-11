const API_URL = import.meta.env.DEV
  ? 'http://localhost:8080/api'
  : import.meta.env.VITE_API_URL

function obtenerToken() {
  return (
    localStorage.getItem('lashbook_token') ||
    localStorage.getItem('token') ||
    localStorage.getItem('authToken') ||
    localStorage.getItem('accessToken') ||
    sessionStorage.getItem('lashbook_token') ||
    sessionStorage.getItem('token')
  )
}

async function peticionSoporte(ruta, opciones = {}) {
  const token = obtenerToken()

  if (!token) {
    throw new Error('No hay una sesión activa')
  }

  const respuesta = await fetch(`${API_URL}${ruta}`, {
    ...opciones,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      ...(opciones.headers || {}),
    },
  })

  let datos = null

  const texto = await respuesta.text()

  if (texto) {
    try {
      datos = JSON.parse(texto)
    } catch {
      datos = texto
    }
  }

  if (!respuesta.ok) {
    let mensaje = 'Ocurrió un error al comunicarse con soporte'

    if (respuesta.status === 400) {
      mensaje =
        datos?.message ||
        'La operación no puede realizarse en esta conversación'
    }

    if (respuesta.status === 401) {
      mensaje =
        'Tu sesión ha expirado. Inicia sesión nuevamente.'
    }

    if (respuesta.status === 403) {
      mensaje =
        'No tienes permisos para realizar esta operación.'
    }

    if (respuesta.status === 404) {
      mensaje =
        datos?.message ||
        'La conversación no fue encontrada.'
    }

    if (respuesta.status >= 500) {
      mensaje =
        'El servidor de soporte no está disponible en este momento.'
    }

    throw new Error(mensaje)
  }

  return datos
}


// ======================================================
// CLIENTA
// ======================================================

export async function crearConversacionSoporte(
  asunto,
  mensaje
) {
  return peticionSoporte(
    '/soporte/conversaciones',
    {
      method: 'POST',
      body: JSON.stringify({
        asunto,
        mensaje,
      }),
    }
  )
}

export async function obtenerMisConversacionesSoporte() {
  return peticionSoporte(
    '/soporte/mis-conversaciones',
    {
      method: 'GET',
    }
  )
}


// ======================================================
// CLIENTA / LASHISTA
// ======================================================

export async function obtenerConversacionSoporte(id) {
  return peticionSoporte(
    `/soporte/conversaciones/${id}`,
    {
      method: 'GET',
    }
  )
}

export async function enviarMensajeSoporte(
  id,
  mensaje
) {
  return peticionSoporte(
    `/soporte/conversaciones/${id}/mensajes`,
    {
      method: 'POST',
      body: JSON.stringify({
        mensaje,
      }),
    }
  )
}


// ======================================================
// LASHISTA / ADMIN
// ======================================================

export async function obtenerConversacionesSoporteAdmin() {
  return peticionSoporte(
    '/soporte/admin/conversaciones',
    {
      method: 'GET',
    }
  )
}

export async function obtenerConversacionesPorEstado(
  estado
) {
  return peticionSoporte(
    `/soporte/admin/conversaciones/estado/${estado}`,
    {
      method: 'GET',
    }
  )
}

export async function cerrarConversacionSoporte(id) {
  return peticionSoporte(
    `/soporte/admin/conversaciones/${id}/cerrar`,
    {
      method: 'PUT',
    }
  )
}