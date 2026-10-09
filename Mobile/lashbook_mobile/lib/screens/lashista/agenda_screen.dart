import 'package:flutter/material.dart';

import '../../services/cita_service.dart';
import '../../widgets/lashista_bottom_nav.dart';

class AgendaScreen extends StatefulWidget {
  const AgendaScreen({
    super.key,
  });

  @override
  State<AgendaScreen> createState() =>
      _AgendaScreenState();
}

class _AgendaScreenState
    extends State<AgendaScreen> {
  final CitaService citaService =
      CitaService();

  List<Map<String, dynamic>> citas = [];

  bool cargando = true;
  String? error;

  @override
  void initState() {
    super.initState();

    cargarAgenda();
  }

  Future<void> cargarAgenda() async {
    setState(() {
      cargando = true;
      error = null;
    });

    try {
      final resultado =
          await citaService.obtenerAgendaHoy();

      if (!mounted) return;

      setState(() {
        citas = resultado;
        cargando = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() {
        cargando = false;

        error = e
            .toString()
            .replaceFirst(
              'Exception: ',
              '',
            );
      });
    }
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

  String obtenerHoraCirculo(
    String hora,
  ) {
    try {
      final hora24 =
          int.parse(
        hora.split(':')[0],
      );

      var hora12 =
          hora24 % 12;

      if (hora12 == 0) {
        hora12 = 12;
      }

      return hora12
          .toString()
          .padLeft(2, '0');
    } catch (_) {
      return '--';
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
    return Scaffold(
      appBar: AppBar(
        title:
            const Text('Agenda'),
      ),

      body: cargando
          ? const Center(
              child:
                  CircularProgressIndicator(),
            )
          : error != null
              ? Center(
                  child: Padding(
                    padding:
                        const EdgeInsets.all(
                      24,
                    ),
                    child: Column(
                      mainAxisSize:
                          MainAxisSize.min,
                      children: [
                        const Icon(
                          Icons
                              .error_outline,
                          size: 55,
                        ),

                        const SizedBox(
                          height: 16,
                        ),

                        Text(
                          error!,
                          textAlign:
                              TextAlign.center,
                        ),

                        const SizedBox(
                          height: 20,
                        ),

                        ElevatedButton(
                          onPressed:
                              cargarAgenda,
                          child:
                              const Text(
                            'Reintentar',
                          ),
                        ),
                      ],
                    ),
                  ),
                )
              : RefreshIndicator(
                  onRefresh:
                      cargarAgenda,
                  child: ListView(
                    physics:
                        const AlwaysScrollableScrollPhysics(),
                    padding:
                        const EdgeInsets.all(
                      20,
                    ),
                    children: [
                      const Text(
                        'Agenda de hoy',
                        style:
                            TextStyle(
                          fontSize: 26,
                          fontWeight:
                              FontWeight.bold,
                        ),
                      ),

                      const SizedBox(
                        height: 8,
                      ),

                      Text(
                        citas.isEmpty
                            ? 'No hay citas programadas'
                            : '${citas.length} ${citas.length == 1 ? 'cita programada' : 'citas programadas'}',
                        style:
                            const TextStyle(
                          color:
                              Colors.black54,
                        ),
                      ),

                      const SizedBox(
                        height: 24,
                      ),

                      if (citas.isEmpty)
                        const Padding(
                          padding:
                              EdgeInsets.only(
                            top: 60,
                          ),
                          child: Column(
                            children: [
                              Icon(
                                Icons
                                    .event_available_outlined,
                                size: 70,
                                color:
                                    Colors.black38,
                              ),

                              SizedBox(
                                height: 16,
                              ),

                              Text(
                                'No tienes citas para hoy.',
                                textAlign:
                                    TextAlign
                                        .center,
                                style:
                                    TextStyle(
                                  fontSize:
                                      17,
                                  color:
                                      Colors
                                          .black54,
                                ),
                              ),
                            ],
                          ),
                        ),

                      ...citas.map(
                        (cita) {
                          final nombreClienta =
                              cita['nombreClienta']
                                      ?.toString() ??
                                  'Clienta';

                          final nombreServicio =
                              cita['nombreServicio']
                                      ?.toString() ??
                                  'Servicio';

                          final hora =
                              cita['hora']
                                      ?.toString() ??
                                  '';

                          final estado =
                              cita['estado']
                                      ?.toString() ??
                                  '';

                          return Card(
                            margin:
                                const EdgeInsets
                                    .only(
                              bottom: 14,
                            ),
                            child:
                                ListTile(
                              contentPadding:
                                  const EdgeInsets
                                      .all(
                                14,
                              ),

                              leading:
                                  CircleAvatar(
                                child: Text(
                                  obtenerHoraCirculo(
                                    hora,
                                  ),
                                ),
                              ),

                              title: Text(
                                nombreClienta,
                                style:
                                    const TextStyle(
                                  fontWeight:
                                      FontWeight
                                          .bold,
                                ),
                              ),

                              subtitle: Text(
                                '$nombreServicio · ${formatearHora(hora)}',
                              ),

                              trailing:
                                  Chip(
                                label: Text(
                                  formatearEstado(
                                    estado,
                                  ),
                                ),
                              ),
                            ),
                          );
                        },
                      ),
                    ],
                  ),
                ),

      bottomNavigationBar:
          const LashistaBottomNav(
        currentIndex: 1,
      ),
    );
  }
}