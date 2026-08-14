import { obtenerToken } from '../utils/almacenamiento'

const API_URL =
  import.meta.env.VITE_API_URL ||
  'http://localhost:8080/api'

export async function crearCita({
  servicioId,
  fecha,
  hora,
  comentarios,
}) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/citas`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        servicioId,
        fecha,
        hora,
        comentarios,
      }),
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible reservar la cita',
    )
  }

  return datos
}

export async function listarMisCitas() {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/citas/mis-citas`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible cargar tus citas',
    )
  }

  return datos
}

export async function consultarHistorialCita(citaId) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/citas/${citaId}/historial`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible consultar el historial',
    )
  }

  return datos
}

export async function cambiarEstadoCita(
  citaId,
  estado,
) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/citas/${citaId}/estado`,
    {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        estado,
      }),
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible cambiar el estado de la cita',
    )
  }

  return datos
}

export async function listarCitasAdministrativas() {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/admin/citas`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible cargar la agenda',
    )
  }

  return datos
}

export async function cambiarEstadoCitaAdministrativa(
  citaId,
  estado,
) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/admin/citas/${citaId}/estado`,
    {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        estado,
      }),
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible actualizar la cita',
    )
  }

  return datos
}

export async function reagendarCitaAdministrativa(
  citaId,
  fecha,
  hora,
) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/admin/citas/${citaId}/reagendar`,
    {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        fecha,
        hora: hora.length === 5
          ? `${hora}:00`
          : hora,
      }),
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible reagendar la cita',
    )
  }

  return datos
}

export async function consultarHistorialAdministrativo(
  citaId,
) {
  const token = obtenerToken()

  const respuesta = await fetch(
    `${API_URL}/admin/citas/${citaId}/historial`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible consultar el historial',
    )
  }

  return datos
}