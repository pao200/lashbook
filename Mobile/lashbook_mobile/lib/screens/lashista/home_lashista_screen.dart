import 'package:flutter/material.dart';

import '../../core/routes/app_routes.dart';
import '../../services/auth_service.dart';
import '../../services/cita_service.dart';
import '../../widgets/lashista_bottom_nav.dart';

class HomeLashistaScreen extends StatefulWidget {
  const HomeLashistaScreen({super.key});

  @override
  State<HomeLashistaScreen> createState() =>
      _HomeLashistaScreenState();
}

class _HomeLashistaScreenState
    extends State<HomeLashistaScreen> {
  final AuthService authService = AuthService();
  final CitaService citaService = CitaService();

  String nombre = 'Lashista';

  List<Map<String, dynamic>> citasHoy = [];

  bool cargandoCitas = true;
  String? errorCitas;

  @override
  void initState() {
    super.initState();

    cargarDatos();
  }

  Future<void> cargarDatos() async {
    await cargarNombre();
    await cargarCitas();
  }

  Future<void> cargarNombre() async {
    final nombreGuardado =
        await authService.obtenerNombre();

    if (!mounted) return;

    setState(() {
      nombre =
          nombreGuardado?.isNotEmpty == true
              ? nombreGuardado!
              : 'Lashista';
    });
  }

  Future<void> cargarCitas() async {
    setState(() {
      cargandoCitas = true;
      errorCitas = null;
    });

    try {
      final resultado =
          await citaService.obtenerAgendaHoy();

      if (!mounted) return;

      setState(() {
        citasHoy = resultado;
        cargandoCitas = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() {
        cargandoCitas = false;

        errorCitas = e
            .toString()
            .replaceFirst(
              'Exception: ',
              '',
            );
      });
    }
  }

  String obtenerPrimerNombre() {
    final partes =
        nombre.trim().split(' ');

    if (partes.isEmpty) {
      return nombre;
    }

    return partes.first;
  }

  bool citaActiva(
    Map<String, dynamic> cita,
  ) {
    final estado =
        cita['estado']
            ?.toString()
            .toUpperCase() ??
        '';

    return estado != 'CANCELADA' &&
        estado != 'COMPLETADA';
  }

  List<Map<String, dynamic>>
      obtenerCitasActivas() {
    return citasHoy
        .where(citaActiva)
        .toList();
  }

  Map<String, dynamic>?
      obtenerProximaCita() {
    final ahora = DateTime.now();

    final citasActivas =
        obtenerCitasActivas();

    for (final cita in citasActivas) {
      final hora =
          cita['hora']?.toString();

      if (hora == null ||
          hora.isEmpty) {
        continue;
      }

      try {
        final partes =
            hora.split(':');

        final horaCita =
            DateTime(
          ahora.year,
          ahora.month,
          ahora.day,
          int.parse(partes[0]),
          int.parse(partes[1]),
        );

        if (horaCita.isAfter(ahora) ||
            horaCita.isAtSameMomentAs(
              ahora,
            )) {
          return cita;
        }
      } catch (_) {
        continue;
      }
    }

    return null;
  }

  String formatearHora(
    String hora,
  ) {
    try {
      final partes =
          hora.split(':');

      final hora24 =
          int.parse(partes[0]);

      final minutos =
          partes.length > 1
              ? partes[1]
              : '00';

      final periodo =
          hora24 >= 12
              ? 'PM'
              : 'AM';

      var hora12 =
          hora24 % 12;

      if (hora12 == 0) {
        hora12 = 12;
      }

      return '$hora12:$minutos $periodo';
    } catch (_) {
      return hora;
    }
  }

  String formatearEstado(
    String estado,
  ) {
    switch (estado.toUpperCase()) {
      case 'PENDIENTE':
        return 'Pendiente';

      case 'CONFIRMADA':
        return 'Confirmada';

      case 'CANCELADA':
        return 'Cancelada';

      case 'REAGENDAR':
        return 'Reagendar';

      case 'REAGENDADA':
        return 'Reagendada';

      case 'COMPLETADA':
        return 'Completada';

      default:
        return estado;
    }
  }

  @override
  Widget build(BuildContext context) {
    final citasActivas =
        obtenerCitasActivas();

    final proximaCita =
        obtenerProximaCita();

    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'LashBook Mobile',
        ),
      ),

      body: RefreshIndicator(
        onRefresh: cargarCitas,
        child: SingleChildScrollView(
          physics:
              const AlwaysScrollableScrollPhysics(),
          padding:
              const EdgeInsets.all(22),

          child: Column(
            crossAxisAlignment:
                CrossAxisAlignment.start,
            children: [
              Text(
                'Hola, ${obtenerPrimerNombre()}',
                style:
                    const TextStyle(
                  fontSize: 28,
                  fontWeight:
                      FontWeight.bold,
                  color:
                      Color(
                    0xFF3D2926,
                  ),
                ),
              ),

              const SizedBox(
                height: 6,
              ),

              const Text(
                'Resumen de hoy',
                style: TextStyle(
                  fontSize: 16,
                  color:
                      Colors.black54,
                ),
              ),

              const SizedBox(
                height: 24,
              ),

              if (cargandoCitas)
                const Center(
                  child:
                      CircularProgressIndicator(),
                )
              else if (errorCitas != null)
                Card(
                  child: Padding(
                    padding:
                        const EdgeInsets.all(
                      18,
                    ),
                    child: Column(
                      children: [
                        const Icon(
                          Icons
                              .error_outline,
                        ),

                        const SizedBox(
                          height: 8,
                        ),

                        Text(
                          errorCitas!,
                          textAlign:
                              TextAlign
                                  .center,
                        ),

                        const SizedBox(
                          height: 10,
                        ),

                        TextButton(
                          onPressed:
                              cargarCitas,
                          child:
                              const Text(
                            'Reintentar',
                          ),
                        ),
                      ],
                    ),
                  ),
                )
              else ...[
                Card(
                  child: Padding(
                    padding:
                        const EdgeInsets.all(
                      20,
                    ),
                    child: Row(
                      children: [
                        const Icon(
                          Icons
                              .calendar_month,
                          size: 40,
                          color:
                              Color(
                            0xFF7A514B,
                          ),
                        ),

                        const SizedBox(
                          width: 18,
                        ),

                        Column(
                          crossAxisAlignment:
                              CrossAxisAlignment
                                  .start,
                          children: [
                            Text(
                              '${citasActivas.length} ${citasActivas.length == 1 ? 'cita' : 'citas'}',
                              style:
                                  const TextStyle(
                                fontSize:
                                    24,
                                fontWeight:
                                    FontWeight
                                        .bold,
                              ),
                            ),

                            Text(
                              citasActivas.length ==
                                      1
                                  ? 'Programada para hoy'
                                  : 'Programadas para hoy',
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                ),

                const SizedBox(
                  height: 28,
                ),

                const Text(
                  'Próxima cita',
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight:
                        FontWeight.bold,
                  ),
                ),

                const SizedBox(
                  height: 12,
                ),

                if (proximaCita == null)
                  const Card(
                    child: Padding(
                      padding:
                          EdgeInsets.all(
                        20,
                      ),
                      child: Row(
                        children: [
                          Icon(
                            Icons
                                .event_available_outlined,
                            color:
                                Colors
                                    .black45,
                          ),

                          SizedBox(
                            width: 12,
                          ),

                          Expanded(
                            child: Text(
                              'No hay próximas citas para hoy.',
                              style:
                                  TextStyle(
                                color:
                                    Colors
                                        .black54,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  )
                else
                  Card(
                    child: Padding(
                      padding:
                          const EdgeInsets.all(
                        20,
                      ),
                      child: Column(
                        crossAxisAlignment:
                            CrossAxisAlignment
                                .start,
                        children: [
                          Text(
                            proximaCita[
                                        'nombreClienta']
                                    ?.toString() ??
                                'Clienta',
                            style:
                                const TextStyle(
                              fontSize:
                                  20,
                              fontWeight:
                                  FontWeight
                                      .bold,
                            ),
                          ),

                          const SizedBox(
                            height: 8,
                          ),

                          Text(
                            proximaCita[
                                        'nombreServicio']
                                    ?.toString() ??
                                'Servicio',
                          ),

                          const SizedBox(
                            height: 6,
                          ),

                          Row(
                            children: [
                              const Icon(
                                Icons
                                    .access_time,
                                size: 18,
                              ),

                              const SizedBox(
                                width: 6,
                              ),

                              Text(
                                formatearHora(
                                  proximaCita[
                                              'hora']
                                          ?.toString() ??
                                      '',
                                ),
                              ),
                            ],
                          ),

                          const SizedBox(
                            height: 12,
                          ),

                          Chip(
                            label: Text(
                              formatearEstado(
                                proximaCita[
                                            'estado']
                                        ?.toString() ??
                                    '',
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
              ],

              const SizedBox(
                height: 24,
              ),

              SizedBox(
                width:
                    double.infinity,
                child:
                    ElevatedButton(
                  onPressed: () {
                    Navigator
                        .pushReplacementNamed(
                      context,
                      AppRoutes.agenda,
                    );
                  },
                  child:
                      const Text(
                    'Ver agenda',
                  ),
                ),
              ),

              const SizedBox(
                height: 14,
              ),

              SizedBox(
                width:
                    double.infinity,
                child:
                    OutlinedButton.icon(
                  onPressed: () {
                    Navigator
                        .pushNamed(
                      context,
                      AppRoutes.conexion,
                    );
                  },
                  icon:
                      const Icon(
                    Icons
                        .cloud_outlined,
                  ),
                  label:
                      const Text(
                    'Probar conexión con LashBook',
                  ),
                ),
              ),
            ],
          ),
        ),
      ),

      bottomNavigationBar:
          const LashistaBottomNav(
        currentIndex: 0,
      ),
    );
  }
}