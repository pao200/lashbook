import { Link } from 'react-router-dom'
import './PoliticasPage.css'

function PoliticasPage() {
  return (
    <main className="politicas-page">
      <header className="politicas-header">
        <Link
          className="politicas-brand"
          to="/"
        >
          LashBook
        </Link>

        <Link
          className="politicas-back"
          to="/"
        >
          ← Volver al inicio
        </Link>
      </header>

      <section className="politicas-container">
        <div className="politicas-intro">
          <p className="politicas-eyebrow">
          
          </p>

          <h1>
            Aviso de privacidad
          </h1>

          <p>
            En LashBook valoramos la privacidad de nuestras
            clientas y protegemos la información utilizada
            para proporcionar nuestros servicios.
          </p>

          <span>
            Última actualización: agosto de 2026
          </span>
        </div>

        <article className="politicas-card">
          <section>
            <h2>1. Datos que recopilamos</h2>

            <p>
              LashBook puede recopilar información necesaria
              para crear  una cuenta, como nombre,
              correo electrónico y datos relacionados con las
              citas reservadas dentro de la plataforma.
            </p>
          </section>

          <section>
            <h2>2. Uso de la información</h2>

            <p>
              Los datos proporcionados se utilizan para
              administrar cuentas, registrar citas, consultar
              servicios, realizar cambios en reservaciones y
              enviar recordatorios relacionados con las citas.
            </p>
          </section>

          

          <section>
            <h2>3. Protección de datos</h2>

            <p>
              La plataforma utiliza mecanismos de
              autenticación y control de acceso para proteger
              las cuentas y limitar el acceso a la información
              según el tipo de usuario.
            </p>
          </section>

          

          <section>
            <h2>4. Derechos de la persona </h2>

            <p>
              La cliente  puede solicitar información
              sobre sus datos personales, así como solicitar
              su actualización, corrección o eliminación
              cuando corresponda.
            </p>
          </section>

          <section>
            <h2>5. Uso de la plataforma</h2>

            <p>
              La información proporcionada por la persona
              clienta  debe ser correcta. Las reservaciones,
              cancelaciones y solicitudes de cambio deben
              realizarse mediante las funciones disponibles
              dentro de LashBook.
            </p>
          </section>

          <section>
            <h2>6. Contacto</h2>

            <p>
              Para dudas relacionadas con este aviso de
              privacidad puedes comunicarte mediante los
              medios de contacto disponibles en LashBook.
            </p>

            <a href="mailto:lashbook@gmail.com">
              lashbook@gmail.com
            </a>
          </section>
        </article>

        <div className="politicas-bottom">
          <Link to="/">
            Volver a LashBook
          </Link>
        </div>
      </section>
    </main>
  )
}

export default PoliticasPage