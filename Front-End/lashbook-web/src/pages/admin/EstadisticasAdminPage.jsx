import AdminLayout from '../../layouts/AdminLayout'
import './EstadisticasAdminPage.css'

function EstadisticasAdminPage() {
  return (
    <AdminLayout>
      <section>
        <header className="estadisticas-header">
          <p className="eyebrow">
            Administración
          </p>

          <h1>
            Estadísticas
          </h1>

          <p className="estadisticas-description">
            Consulta el rendimiento general de las
            citas y los ingresos de LashBook mediante
            el panel interactivo desarrollado con
            Flutter Web.
          </p>
        </header>

        <section className="flutter-widget-section">
          <div className="flutter-widget-heading">
            <p className="eyebrow">
              Flutter Web
            </p>

            <h2>
              Panel interactivo
            </h2>

            <p>
              Visualización conectada a las estadísticas
              reales de LashBook.
            </p>
          </div>

          <iframe
            className="flutter-widget-frame"
            src="/flutter-widget/index.html"
            title="Widget interactivo de estadísticas LashBook"
          />

          <section
            className="wearable-widget-link"
            id="integracion-wearable"
          >
            <div className="wearable-widget-icon">
              ⌚
            </div>

            <div className="wearable-widget-content">
              <p className="eyebrow">
                Wear OS
              </p>

              <h3>
                Integración con smartwatch
              </h3>

              <p>
                LashBook envía recordatorios de citas
                al smartwatch mediante Firebase Cloud
                Messaging. Desde el reloj la clienta
                puede confirmar, cancelar o solicitar
                reagendar una cita utilizando su NIP.
              </p>

              <a
                className="wearable-widget-button"
                href="https://github.com/pao200/lashbook"
                target="_blank"
                rel="noreferrer"
              >
                Ver módulo Wearable
              </a>
            </div>
          </section>
        </section>
      </section>
    </AdminLayout>
  )
}

export default EstadisticasAdminPage