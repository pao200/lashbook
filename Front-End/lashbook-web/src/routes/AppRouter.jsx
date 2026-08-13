import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from 'react-router-dom'

import AgendaPage from '../pages/admin/AgendaPage'
import DashboardAdminPage from '../pages/admin/DashboardAdminPage'
import EstadisticasAdminPage from '../pages/admin/EstadisticasAdminPage'
import ServiciosAdminPage from '../pages/admin/ServiciosAdminPage'
import LoginPage from '../pages/auth/LoginPage'
import RegistroPage from '../pages/auth/RegistroPage'
import InicioClientaPage from '../pages/clienta/InicioClientaPage'
import MisCitasPage from '../pages/clienta/MisCitasPage'
import ReservarCitaPage from '../pages/clienta/ReservarCitaPage'
import RutaProtegida from './RutaProtegida'
import InicioPublicoPage from '../pages/InicioPublicoPage'
function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={<InicioPublicoPage />}
        />
        <Route
          path="/login"
          element={<LoginPage />}
        />

        <Route
          path="/registro"
          element={<RegistroPage />}
        />

        <Route
          path="/clienta/inicio"
          element={
            <RutaProtegida rolesPermitidos={['CLIENTA']}>
              <InicioClientaPage />
            </RutaProtegida>
          }
        />

        <Route
          path="/clienta/reservar"
          element={
            <RutaProtegida rolesPermitidos={['CLIENTA']}>
              <ReservarCitaPage />
            </RutaProtegida>
          }
        />

        <Route
          path="/clienta/mis-citas"
          element={
            <RutaProtegida rolesPermitidos={['CLIENTA']}>
              <MisCitasPage />
            </RutaProtegida>
          }
        />

        <Route
          path="/admin/inicio"
          element={
            <RutaProtegida
              rolesPermitidos={['LASHISTA', 'ADMIN']}
            >
              <DashboardAdminPage />
            </RutaProtegida>
          }
        />

        <Route
          path="/admin/agenda"
          element={
            <RutaProtegida
              rolesPermitidos={['LASHISTA', 'ADMIN']}
            >
              <AgendaPage />
            </RutaProtegida>
          }
        />

        <Route
          path="/admin/servicios"
          element={
            <RutaProtegida
              rolesPermitidos={['LASHISTA', 'ADMIN']}
            >
              <ServiciosAdminPage />
            </RutaProtegida>
          }
        />

        <Route
          path="/admin/estadisticas"
          element={
            <RutaProtegida
              rolesPermitidos={['LASHISTA', 'ADMIN']}
            >
              <EstadisticasAdminPage />
            </RutaProtegida>
          }
        />

        <Route
          path="*"
          element={
            <Navigate
              to="/"
              replace
            />
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

export default AppRouter