const TOKEN_KEY = 'lashbook_token'

export function guardarToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function obtenerToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function eliminarToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function haySesion() {
  return Boolean(obtenerToken())
}