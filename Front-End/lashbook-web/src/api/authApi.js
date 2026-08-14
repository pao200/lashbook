const API_URL =
  import.meta.env.VITE_API_URL ||
  'http://localhost:8080/api'

export async function registrarCliente(
  nombre,
  correo,
  password,
) {
  const respuesta = await fetch(
    `${API_URL}/auth/registro`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nombre,
        correo,
        password,
      }),
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'No fue posible crear la cuenta',
    )
  }

  return datos
}

export async function iniciarSesion(
  correo,
  password,
) {
  const respuesta = await fetch(
    `${API_URL}/auth/login`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        correo,
        password,
      }),
    },
  )

  const datos = await respuesta.json()

  if (!respuesta.ok) {
    throw new Error(
      datos.mensaje ||
        'Correo o contraseña incorrectos',
    )
  }

  return datos
}

export async function obtenerPerfil(token) {
  const respuesta = await fetch(
    `${API_URL}/auth/me`,
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
        'No fue posible obtener el perfil',
    )
  }

  return datos
}