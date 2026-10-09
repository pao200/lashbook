import 'package:flutter/material.dart';

import '../core/routes/app_routes.dart';
import '../services/auth_service.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() =>
      _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController correoController =
      TextEditingController();

  final TextEditingController passwordController =
      TextEditingController();

  final AuthService authService = AuthService();

  bool ocultarPassword = true;
  bool cargando = false;

  String? error;

  Future<void> iniciarSesion() async {
    final correo =
        correoController.text.trim();

    final password =
        passwordController.text;

    if (correo.isEmpty || password.isEmpty) {
      setState(() {
        error =
            'Ingresa tu correo y contraseña.';
      });

      return;
    }

    setState(() {
      cargando = true;
      error = null;
    });

    try {
      await authService.iniciarSesion(
        correo: correo,
        password: password,
      );

      if (!mounted) return;

      Navigator.pushReplacementNamed(
        context,
        AppRoutes.homeLashista,
      );
    } catch (e) {
      if (!mounted) return;

      String mensaje =
          e.toString();

      mensaje = mensaje.replaceFirst(
        'Exception: ',
        '',
      );

      setState(() {
        error = mensaje;
        cargando = false;
      });
    }
  }

  @override
  void dispose() {
    correoController.dispose();
    passwordController.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding:
              const EdgeInsets.all(28),
          child: Column(
            children: [
              const SizedBox(height: 70),

              Container(
                width: 85,
                height: 85,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  border: Border.all(
                    color:
                        const Color(
                      0xFF7A514B,
                    ),
                    width: 2,
                  ),
                ),
                child: const Center(
                  child: Text(
                    'LB',
                    style: TextStyle(
                      fontSize: 26,
                      fontWeight:
                          FontWeight.bold,
                      color:
                          Color(
                        0xFF7A514B,
                      ),
                    ),
                  ),
                ),
              ),

              const SizedBox(height: 20),

              const Text(
                'LashBook',
                style: TextStyle(
                  fontSize: 30,
                  fontWeight:
                      FontWeight.bold,
                  color:
                      Color(
                    0xFF3D2926,
                  ),
                ),
              ),

              const SizedBox(height: 6),

              const Text(
                'Acceso para lashista',
                style: TextStyle(
                  fontSize: 16,
                  color: Colors.black54,
                ),
              ),

              const SizedBox(height: 40),

              TextField(
                controller:
                    correoController,
                keyboardType:
                    TextInputType
                        .emailAddress,
                enabled: !cargando,
                decoration:
                    InputDecoration(
                  labelText:
                      'Correo electrónico',
                  prefixIcon:
                      const Icon(
                    Icons.email_outlined,
                  ),
                  border:
                      OutlineInputBorder(
                    borderRadius:
                        BorderRadius
                            .circular(14),
                  ),
                ),
              ),

              const SizedBox(height: 20),

              TextField(
                controller:
                    passwordController,
                obscureText:
                    ocultarPassword,
                enabled: !cargando,
                onSubmitted: (_) {
                  if (!cargando) {
                    iniciarSesion();
                  }
                },
                decoration:
                    InputDecoration(
                  labelText:
                      'Contraseña',
                  prefixIcon:
                      const Icon(
                    Icons.lock_outline,
                  ),
                  suffixIcon:
                      IconButton(
                    onPressed: cargando
                        ? null
                        : () {
                            setState(() {
                              ocultarPassword =
                                  !ocultarPassword;
                            });
                          },
                    icon: Icon(
                      ocultarPassword
                          ? Icons
                              .visibility_off
                          : Icons
                              .visibility,
                    ),
                  ),
                  border:
                      OutlineInputBorder(
                    borderRadius:
                        BorderRadius
                            .circular(14),
                  ),
                ),
              ),

              const SizedBox(height: 20),

              if (error != null)
                Container(
                  width:
                      double.infinity,
                  padding:
                      const EdgeInsets
                          .all(14),
                  decoration:
                      BoxDecoration(
                    color:
                        Colors.red
                            .withValues(
                      alpha: 0.08,
                    ),
                    borderRadius:
                        BorderRadius
                            .circular(12),
                    border:
                        Border.all(
                      color:
                          Colors.red
                              .withValues(
                        alpha: 0.30,
                      ),
                    ),
                  ),
                  child: Text(
                    error!,
                    textAlign:
                        TextAlign.center,
                    style:
                        const TextStyle(
                      color:
                          Colors.red,
                    ),
                  ),
                ),

              if (error != null)
                const SizedBox(
                  height: 20,
                ),

              SizedBox(
                width: double.infinity,
                child:
                    ElevatedButton(
                  onPressed:
                      cargando
                          ? null
                          : iniciarSesion,
                  child: cargando
                      ? const SizedBox(
                          width: 22,
                          height: 22,
                          child:
                              CircularProgressIndicator(
                            strokeWidth: 2,
                          ),
                        )
                      : const Text(
                          'Iniciar sesión',
                        ),
                ),
              ),

              const SizedBox(height: 16),

              const Text(
                'Utiliza tu cuenta existente de LashBook',
                textAlign:
                    TextAlign.center,
                style: TextStyle(
                  fontSize: 13,
                  color: Colors.black54,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}