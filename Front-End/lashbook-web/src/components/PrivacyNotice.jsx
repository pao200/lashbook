import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import './PrivacyNotice.css'

function PrivacyNotice() {
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    const aceptado = localStorage.getItem(
      'lashbook_privacidad_aceptada'
    )

    if (!aceptado) {
      setVisible(true)
    }
  }, [])

  const aceptarAviso = () => {
    localStorage.setItem(
      'lashbook_privacidad_aceptada',
      'true'
    )

    setVisible(false)
  }

  if (!visible) {
    return null
  }

  return (
    <aside
      className="privacy-notice"
      aria-label="Aviso de privacidad"
    >
      <div className="privacy-notice-text">
        <strong>
          Privacidad en LashBook
        </strong>

        <p>
          Utilizamos tus datos para gestionar tu cuenta,
          citas, recordatorios y las funciones de LashBook.
        </p>
      </div>

      <div className="privacy-notice-actions">
        <Link to="/politicas">
          Ver aviso de privacidad
        </Link>

        <button
          type="button"
          onClick={aceptarAviso}
        >
          Entendido
        </button>
      </div>
    </aside>
  )
}

export default PrivacyNotice