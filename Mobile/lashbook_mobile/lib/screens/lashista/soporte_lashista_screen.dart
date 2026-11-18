import 'package:flutter/material.dart';

import '../../services/soporte_service.dart';
import 'detalle_soporte_screen.dart';

class SoporteLashistaScreen extends StatefulWidget {
  const SoporteLashistaScreen({
    super.key,
  });

  @override
  State<SoporteLashistaScreen> createState() =>
      _SoporteLashistaScreenState();
}

class _SoporteLashistaScreenState
    extends State<SoporteLashistaScreen> {
  final SoporteService soporteService =
      SoporteService();

  List<Map<String, dynamic>> conversaciones = [];

  bool cargando = true;
  bool actualizando = false;

  String? error;

  @override
  void initState() {
    super.initState();

    cargarConversaciones();
  }

  Future<void> cargarConversaciones({
    bool silencioso = false,
  }) async {
    if (actualizando) {
      return;
    }

    actualizando = true;

    if (!silencioso && mounted) {
      setState(() {
        cargando = true;
        error = null;
      });
    }

    try {
      final resultado =
          await soporteService
              .obtenerConversaciones();

      if (!mounted) {
        return;
      }

      setState(() {
        conversaciones = resultado;
        cargando = false;
        error = null;
      });
    } catch (e) {
      if (!mounted) {
        return;
      }

      if (!silencioso) {
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
    } finally {
      actualizando = false;
    }
  }

  String obtenerTexto(
    Map<String, dynamic> datos,
    String llave, {
    String valorDefecto = '',
  }) {
    final valor =
        datos[llave]?.toString().trim();

    if (valor == null || valor.isEmpty) {
      return valorDefecto;
    }

    return valor;
  }

  int obtenerNoLeidos(
    Map<String, dynamic> conversacion,
  ) {
    final valor =
        conversacion['mensajesNoLeidos'];

    if (valor is int) {
      return valor;
    }

    return int.tryParse(
          valor?.toString() ?? '',
        ) ??
        0;
  }

  String formatearFecha(
    dynamic fecha,
  ) {
    if (fecha == null) {
      return '';
    }

    try {
      final dateTime =
          DateTime.parse(
        fecha.toString(),
      ).toLocal();

      final dia =
          dateTime.day
              .toString()
              .padLeft(2, '0');

      final mes =
          dateTime.month
              .toString()
              .padLeft(2, '0');

      final hora =
          dateTime.hour
              .toString()
              .padLeft(2, '0');

      final minuto =
          dateTime.minute
              .toString()
              .padLeft(2, '0');

      return '$dia/$mes/${dateTime.year} · $hora:$minuto';
    } catch (_) {
      return fecha.toString();
    }
  }

  Color colorEstado(
    String estado,
  ) {
    switch (estado.toUpperCase()) {
      case 'ABIERTA':
        return const Color(
          0xFFE8F5E9,
        );

      case 'CERRADA':
        return const Color(
          0xFFF1F1F1,
        );

      default:
        return const Color(
          0xFFFFF3E0,
        );
    }
  }

  Color colorTextoEstado(
    String estado,
  ) {
    switch (estado.toUpperCase()) {
      case 'ABIERTA':
        return const Color(
          0xFF2E7D32,
        );

      case 'CERRADA':
        return const Color(
          0xFF616161,
        );

      default:
        return const Color(
          0xFF8D6E00,
        );
    }
  }

  Future<void> abrirConversacion(
    Map<String, dynamic> conversacion,
  ) async {
    final id =
        conversacion['id'];

    if (id == null) {
      return;
    }

    await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) =>
            DetalleSoporteScreen(
          conversacionId: id,
        ),
      ),
    );

    if (mounted) {
      await cargarConversaciones(
        silencioso: true,
      );
    }
  }

  Widget construirContenido() {
    if (cargando) {
      return const Center(
        child:
            CircularProgressIndicator(),
      );
    }

    if (error != null) {
      return Center(
        child: Padding(
          padding:
              const EdgeInsets.all(
            24,
          ),
          child: Card(
            child: Padding(
              padding:
                  const EdgeInsets.all(
                20,
              ),
              child: Column(
                mainAxisSize:
                    MainAxisSize.min,
                children: [
                  const Icon(
                    Icons.error_outline,
                    size: 42,
                    color:
                        Color(
                      0xFF7A514B,
                    ),
                  ),
                  const SizedBox(
                    height: 12,
                  ),
                  Text(
                    error!,
                    textAlign:
                        TextAlign.center,
                  ),
                  const SizedBox(
                    height: 14,
                  ),
                  FilledButton(
                    onPressed: () {
                      cargarConversaciones();
                    },
                    child:
                        const Text(
                      'Reintentar',
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      );
    }

    if (conversaciones.isEmpty) {
      return RefreshIndicator(
        onRefresh:
            cargarConversaciones,
        child: ListView(
          physics:
              const AlwaysScrollableScrollPhysics(),
          padding:
              const EdgeInsets.all(
            24,
          ),
          children: const [
            SizedBox(
              height: 100,
            ),
            Icon(
              Icons
                  .support_agent_outlined,
              size: 64,
              color:
                  Color(
                0xFFB59A94,
              ),
            ),
            SizedBox(
              height: 18,
            ),
            Text(
              'No hay conversaciones de soporte.',
              textAlign:
                  TextAlign.center,
              style: TextStyle(
                fontSize: 18,
                fontWeight:
                    FontWeight.bold,
              ),
            ),
            SizedBox(
              height: 8,
            ),
            Text(
              'Cuando una clienta envíe un mensaje desde LashBook Web aparecerá aquí.',
              textAlign:
                  TextAlign.center,
              style: TextStyle(
                color:
                    Colors.black54,
              ),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh:
          cargarConversaciones,
      child: ListView.builder(
        physics:
            const AlwaysScrollableScrollPhysics(),
        padding:
            const EdgeInsets.all(
          18,
        ),
        itemCount:
            conversaciones.length,
        itemBuilder:
            (context, index) {
          final conversacion =
              conversaciones[index];

          final asunto =
              obtenerTexto(
            conversacion,
            'asunto',
            valorDefecto:
                'Sin asunto',
          );

          final clienta =
              obtenerTexto(
            conversacion,
            'clientaNombre',
            valorDefecto:
                'Clienta',
          );

          final correo =
              obtenerTexto(
            conversacion,
            'clientaCorreo',
          );

          final estado =
              obtenerTexto(
            conversacion,
            'estado',
            valorDefecto:
                'ABIERTA',
          );

          final noLeidos =
              obtenerNoLeidos(
            conversacion,
          );

          final fecha =
              formatearFecha(
            conversacion[
                'actualizadoEn'],
          );

          return Card(
            margin:
                const EdgeInsets.only(
              bottom: 14,
            ),
            child: InkWell(
              borderRadius:
                  BorderRadius.circular(
                12,
              ),
              onTap: () =>
                  abrirConversacion(
                conversacion,
              ),
              child: Padding(
                padding:
                    const EdgeInsets.all(
                  16,
                ),
                child: Column(
                  crossAxisAlignment:
                      CrossAxisAlignment
                          .start,
                  children: [
                    Row(
                      crossAxisAlignment:
                          CrossAxisAlignment
                              .start,
                      children: [
                        const CircleAvatar(
                          backgroundColor:
                              Color(
                            0xFFF0DEDA,
                          ),
                          child: Icon(
                            Icons
                                .chat_bubble_outline,
                            color:
                                Color(
                              0xFF7A514B,
                            ),
                          ),
                        ),
                        const SizedBox(
                          width: 12,
                        ),
                        Expanded(
                          child: Column(
                            crossAxisAlignment:
                                CrossAxisAlignment
                                    .start,
                            children: [
                              Text(
                                asunto,
                                style:
                                    const TextStyle(
                                  fontSize:
                                      17,
                                  fontWeight:
                                      FontWeight
                                          .bold,
                                ),
                              ),
                              const SizedBox(
                                height: 4,
                              ),
                              Text(
                                clienta,
                                style:
                                    const TextStyle(
                                  fontWeight:
                                      FontWeight
                                          .w600,
                                ),
                              ),
                              if (correo
                                  .isNotEmpty)
                                Text(
                                  correo,
                                  style:
                                      const TextStyle(
                                    fontSize:
                                        12,
                                    color:
                                        Colors
                                            .black54,
                                  ),
                                ),
                            ],
                          ),
                        ),
                        const SizedBox(
                          width: 8,
                        ),
                        Container(
                          padding:
                              const EdgeInsets
                                  .symmetric(
                            horizontal:
                                9,
                            vertical: 5,
                          ),
                          decoration:
                              BoxDecoration(
                            color:
                                colorEstado(
                              estado,
                            ),
                            borderRadius:
                                BorderRadius
                                    .circular(
                              20,
                            ),
                          ),
                          child: Text(
                            estado,
                            style:
                                TextStyle(
                              fontSize: 11,
                              fontWeight:
                                  FontWeight
                                      .bold,
                              color:
                                  colorTextoEstado(
                                estado,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(
                      height: 14,
                    ),
                    Row(
                      children: [
                        if (fecha
                            .isNotEmpty)
                          Expanded(
                            child: Text(
                              fecha,
                              style:
                                  const TextStyle(
                                fontSize:
                                    12,
                                color:
                                    Colors
                                        .black54,
                              ),
                            ),
                          )
                        else
                          const Spacer(),
                        if (noLeidos >
                            0)
                          Container(
                            margin:
                                const EdgeInsets
                                    .only(
                              right: 8,
                            ),
                            padding:
                                const EdgeInsets
                                    .symmetric(
                              horizontal:
                                  9,
                              vertical: 4,
                            ),
                            decoration:
                                BoxDecoration(
                              color:
                                  const Color(
                                0xFF7A514B,
                              ),
                              borderRadius:
                                  BorderRadius
                                      .circular(
                                20,
                              ),
                            ),
                            child: Text(
                              '$noLeidos nuevo${noLeidos == 1 ? '' : 's'}',
                              style:
                                  const TextStyle(
                                color:
                                    Colors.white,
                                fontSize:
                                    11,
                                fontWeight:
                                    FontWeight
                                        .bold,
                              ),
                            ),
                          ),
                        const Icon(
                          Icons
                              .chevron_right,
                          color:
                              Colors.black45,
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  @override
  Widget build(
    BuildContext context,
  ) {
    return Scaffold(
      appBar: AppBar(
        title:
            const Text(
          'Soporte',
        ),
        actions: [
          IconButton(
            tooltip: 'Actualizar',
            onPressed:
                actualizando
                    ? null
                    : () {
                        cargarConversaciones();
                      },
            icon:
                const Icon(
              Icons.refresh,
            ),
          ),
        ],
      ),
      body:
          construirContenido(),
    );
  }
}