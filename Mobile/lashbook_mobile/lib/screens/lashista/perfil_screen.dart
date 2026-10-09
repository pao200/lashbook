import 'package:flutter/material.dart';

import '../../core/routes/app_routes.dart';
import '../../services/auth_service.dart';
import '../../widgets/lashista_bottom_nav.dart';

class PerfilScreen extends StatefulWidget {
  const PerfilScreen({super.key});

  @override
  State<PerfilScreen> createState() =>
      _PerfilScreenState();
}

class _PerfilScreenState
    extends State<PerfilScreen> {
  final AuthService authService = AuthService();

  String nombre = 'Cargando...';
  String correo = '';
  String rol = '';

  bool cargando = true;

  @override
  void initState() {
    super.initState();

    cargarPerfil();
  }

  Future<void> cargarPerfil() async {
    final nombreGuardado =
        await authService.obtenerNombre();

    final correoGuardado =
        await authService.obtenerCorreo();

    final rolGuardado =
        await authService.obtenerRol();

    if (!mounted) return;

    setState(() {
      nombre =
          nombreGuardado?.isNotEmpty == true
              ? nombreGuardado!
              : 'Lashista';

      correo =
          correoGuardado ?? '';

      rol =
          rolGuardado ?? '';

      cargando = false;
    });
  }

  String obtenerNombreRol() {
  if (rol == 'LASHISTA' ||
      rol == 'ROLE_LASHISTA') {
    return 'Lashista';
  }

  return rol;
}
    

  Future<void> cerrarSesion() async {
    await authService.cerrarSesion();

    if (!mounted) return;

    Navigator.pushNamedAndRemoveUntil(
      context,
      AppRoutes.login,
      (route) => false,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Perfil'),
      ),

      body: cargando
          ? const Center(
              child:
                  CircularProgressIndicator(),
            )
          : Padding(
              padding:
                  const EdgeInsets.all(24),
              child: Column(
                children: [
                  const SizedBox(
                    height: 30,
                  ),

                  const CircleAvatar(
                    radius: 48,
                    backgroundColor:
                        Color(
                      0xFF7A514B,
                    ),
                    child: Icon(
                      Icons.person,
                      size: 55,
                      color: Colors.white,
                    ),
                  ),

                  const SizedBox(
                    height: 20,
                  ),

                  Text(
                    nombre,
                    style:
                        const TextStyle(
                      fontSize: 24,
                      fontWeight:
                          FontWeight.bold,
                    ),
                  ),

                  const SizedBox(
                    height: 6,
                  ),

                  Text(
                    correo,
                    style:
                        const TextStyle(
                      color:
                          Colors.black54,
                    ),
                  ),

                  const SizedBox(
                    height: 6,
                  ),

                  Chip(
                    label: Text(
                      obtenerNombreRol(),
                    ),
                  ),

                  const SizedBox(
                    height: 40,
                  ),

                  ListTile(
                    leading:
                        const Icon(
                      Icons
                          .settings_outlined,
                    ),
                    title:
                        const Text(
                      'Configuración',
                    ),
                    trailing:
                        const Icon(
                      Icons.chevron_right,
                    ),
                    onTap: () {},
                  ),

                  const Divider(),

                  ListTile(
                    leading:
                        const Icon(
                      Icons.logout,
                    ),
                    title:
                        const Text(
                      'Cerrar sesión',
                    ),
                    onTap:
                        cerrarSesion,
                  ),
                ],
              ),
            ),

      bottomNavigationBar:
          const LashistaBottomNav(
        currentIndex: 3,
      ),
    );
  }
}