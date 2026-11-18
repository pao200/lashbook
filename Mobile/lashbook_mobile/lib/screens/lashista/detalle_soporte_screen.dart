import 'package:flutter/material.dart';

import '../../services/soporte_service.dart';

class DetalleSoporteScreen
    extends StatefulWidget {
  const DetalleSoporteScreen({
    required this.conversacionId,
    super.key,
  });

  final dynamic conversacionId;

  @override
  State<DetalleSoporteScreen>
      createState() =>
          _DetalleSoporteScreenState();
}

class _DetalleSoporteScreenState
    extends State<DetalleSoporteScreen> {
  final SoporteService soporteService =
      SoporteService();

  final TextEditingController
      mensajeController =
      TextEditingController();

  final ScrollController
      scrollController =
      ScrollController();

  Map<String, dynamic>? conversacion;

  bool cargando = true;
  bool enviando = false;
  bool cerrando = false;

  String? error;

  @override
  void initState() {
    super.initState();

    cargarConversacion();
  }

  @override
  void dispose() {
    mensajeController.dispose();
    scrollController.dispose();

    super.dispose();
  }

  Future<void> cargarConversacion() async {
    try {
      final resultado =
          await soporteService
              .obtenerConversacion(
        widget.conversacionId,
      );

      if (!mounted) {
        return;
      }

      setState(() {
        conversacion = resultado;
        cargando = false;
        error = null;
      });

      WidgetsBinding.instance
          .addPostFrameCallback(
        (_) {
          irAlFinal();
        },
      );
    } catch (e) {
      if (!mounted) {
        return;
      }

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

  void irAlFinal() {
    if (!scrollController
        .hasClients) {
      return;
    }

    scrollController.animateTo(
      scrollController
          .position
          .maxScrollExtent,
      duration:
          const Duration(
        milliseconds: 250,
      ),
      curve:
          Curves.easeOut,
    );
  }

  bool get conversacionAbierta {
    final estado =
        conversacion?['estado']
            ?.toString()
            .toUpperCase();

    return estado == 'ABIERTA';
  }

  List<Map<String, dynamic>>
      obtenerMensajes() {
    final datos =
        conversacion?['mensajes'];

    if (datos is! List) {
      return [];
    }

    return datos
        .whereType<Map>()
        .map(
          (item) =>
              Map<String, dynamic>.from(
            item,
          ),
        )
        .toList();
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

  Future<void> enviarMensaje() async {
    final texto =
        mensajeController.text.trim();

    if (texto.isEmpty ||
        enviando ||
        !conversacionAbierta) {
      return;
    }

    setState(() {
      enviando = true;
    });

    try {
      await soporteService
          .enviarMensaje(
        conversacionId:
            widget.conversacionId,
        mensaje: texto,
      );

      mensajeController.clear();

      await cargarConversacion();

      if (!mounted) {
        return;
      }

      WidgetsBinding.instance
          .addPostFrameCallback(
        (_) {
          irAlFinal();
        },
      );
    } catch (e) {
      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(
        context,
      ).showSnackBar(
        SnackBar(
          content: Text(
            e
                .toString()
                .replaceFirst(
                  'Exception: ',
                  '',
                ),
          ),
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          enviando = false;
        });
      }
    }
  }

  Future<void> cerrarConversacion() async {
    if (!conversacionAbierta ||
        cerrando) {
      return;
    }

    final confirmar =
        await showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title:
              const Text(
            'Cerrar conversación',
          ),
          content:
              const Text(
            'Después de cerrarla ya no se podrán enviar más mensajes.',
          ),
          actions: [
            TextButton(
              onPressed: () =>
                  Navigator.pop(
                context,
                false,
              ),
              child:
                  const Text(
                'Cancelar',
              ),
            ),
            FilledButton(
              onPressed: () =>
                  Navigator.pop(
                context,
                true,
              ),
              child:
                  const Text(
                'Cerrar',
              ),
            ),
          ],
        );
      },
    );

    if (confirmar != true) {
      return;
    }

    setState(() {
      cerrando = true;
    });

    try {
      await soporteService
          .cerrarConversacion(
        widget.conversacionId,
      );

      await cargarConversacion();

      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(
        context,
      ).showSnackBar(
        const SnackBar(
          content: Text(
            'Conversación cerrada correctamente.',
          ),
        ),
      );
    } catch (e) {
      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(
        context,
      ).showSnackBar(
        SnackBar(
          content: Text(
            e
                .toString()
                .replaceFirst(
                  'Exception: ',
                  '',
                ),
          ),
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          cerrando = false;
        });
      }
    }
  }

  Widget construirMensaje(
    Map<String, dynamic> mensaje,
  ) {
    final rol =
        mensaje['remitenteRol']
            ?.toString()
            .toUpperCase() ??
        '';

    final esLashista =
        rol == 'LASHISTA' ||
        rol == 'ADMIN';

    final nombre =
        mensaje['remitenteNombre']
            ?.toString() ??
        (esLashista
            ? 'Tú'
            : 'Clienta');

    final texto =
        mensaje['mensaje']
            ?.toString() ??
        '';

    final fecha =
        formatearFecha(
      mensaje['enviadoEn'],
    );

    return Align(
      alignment:
          esLashista
              ? Alignment.centerRight
              : Alignment.centerLeft,
      child: Container(
        constraints:
            const BoxConstraints(
          maxWidth: 310,
        ),
        margin:
            const EdgeInsets.only(
          bottom: 12,
        ),
        padding:
            const EdgeInsets.all(
          14,
        ),
        decoration:
            BoxDecoration(
          color:
              esLashista
                  ? const Color(
                      0xFF3D2926,
                    )
                  : const Color(
                      0xFFF2E2DE,
                    ),
          borderRadius:
              BorderRadius.circular(
            16,
          ),
        ),
        child: Column(
          crossAxisAlignment:
              CrossAxisAlignment.start,
          children: [
            Text(
              esLashista
                  ? 'Tú'
                  : nombre,
              style:
                  TextStyle(
                fontSize: 12,
                fontWeight:
                    FontWeight.bold,
                color:
                    esLashista
                        ? Colors.white70
                        : const Color(
                            0xFF7A514B,
                          ),
              ),
            ),
            const SizedBox(
              height: 5,
            ),
            Text(
              texto,
              style:
                  TextStyle(
                fontSize: 15,
                color:
                    esLashista
                        ? Colors.white
                        : const Color(
                            0xFF2F2422,
                          ),
              ),
            ),
            if (fecha.isNotEmpty) ...[
              const SizedBox(
                height: 7,
              ),
              Text(
                fecha,
                style:
                    TextStyle(
                  fontSize: 10,
                  color:
                      esLashista
                          ? Colors.white60
                          : Colors.black45,
                ),
              ),
            ],
          ],
        ),
      ),
    );
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
          child: Column(
            mainAxisSize:
                MainAxisSize.min,
            children: [
              const Icon(
                Icons.error_outline,
                size: 48,
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
                  setState(() {
                    cargando = true;
                    error = null;
                  });

                  cargarConversacion();
                },
                child:
                    const Text(
                  'Reintentar',
                ),
              ),
            ],
          ),
        ),
      );
    }

    final datos =
        conversacion!;

    final mensajes =
        obtenerMensajes();

    final asunto =
        datos['asunto']
                ?.toString() ??
            'Soporte';

    final clienta =
        datos['clientaNombre']
                ?.toString() ??
            'Clienta';

    final correo =
        datos['clientaCorreo']
                ?.toString() ??
            '';

    return Column(
      children: [
        Container(
          width:
              double.infinity,
          padding:
              const EdgeInsets.all(
            18,
          ),
          color:
              const Color(
            0xFFF8EFEC,
          ),
          child: Column(
            crossAxisAlignment:
                CrossAxisAlignment.start,
            children: [
              Text(
                asunto,
                style:
                    const TextStyle(
                  fontSize: 20,
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
              Text(
                clienta,
                style:
                    const TextStyle(
                  fontWeight:
                      FontWeight.w600,
                ),
              ),
              if (correo.isNotEmpty)
                Text(
                  correo,
                  style:
                      const TextStyle(
                    color:
                        Colors.black54,
                  ),
                ),
            ],
          ),
        ),
        Expanded(
          child: mensajes.isEmpty
              ? const Center(
                  child: Text(
                    'Esta conversación todavía no tiene mensajes.',
                  ),
                )
              : ListView.builder(
                  controller:
                      scrollController,
                  padding:
                      const EdgeInsets.all(
                    16,
                  ),
                  itemCount:
                      mensajes.length,
                  itemBuilder:
                      (context, index) {
                    return construirMensaje(
                      mensajes[index],
                    );
                  },
                ),
        ),
        if (conversacionAbierta)
          SafeArea(
            top: false,
            child: Container(
              padding:
                  const EdgeInsets.fromLTRB(
                12,
                10,
                12,
                12,
              ),
              decoration:
                  const BoxDecoration(
                color:
                    Colors.white,
                border:
                    Border(
                  top: BorderSide(
                    color:
                        Color(
                      0xFFE3D7D4,
                    ),
                  ),
                ),
              ),
              child: Row(
                crossAxisAlignment:
                    CrossAxisAlignment.end,
                children: [
                  Expanded(
                    child: TextField(
                      controller:
                          mensajeController,
                      minLines: 1,
                      maxLines: 4,
                      maxLength: 2000,
                      decoration:
                          const InputDecoration(
                        hintText:
                            'Escribe un mensaje...',
                        counterText: '',
                        border:
                            OutlineInputBorder(),
                      ),
                    ),
                  ),
                  const SizedBox(
                    width: 8,
                  ),
                  IconButton.filled(
                    onPressed:
                        enviando
                            ? null
                            : enviarMensaje,
                    icon:
                        enviando
                            ? const SizedBox(
                                width: 20,
                                height: 20,
                                child:
                                    CircularProgressIndicator(
                                  strokeWidth:
                                      2,
                                ),
                              )
                            : const Icon(
                                Icons.send,
                              ),
                  ),
                ],
              ),
            ),
          )
        else
          SafeArea(
            top: false,
            child: Container(
              width:
                  double.infinity,
              padding:
                  const EdgeInsets.all(
                16,
              ),
              color:
                  const Color(
                0xFFF3F3F3,
              ),
              child:
                  const Text(
                'Esta conversación está cerrada. Ya no se pueden enviar mensajes.',
                textAlign:
                    TextAlign.center,
                style:
                    TextStyle(
                  color:
                      Colors.black54,
                ),
              ),
            ),
          ),
      ],
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
          'Conversación',
        ),
        actions: [
          IconButton(
            tooltip: 'Actualizar',
            onPressed:
                cargarConversacion,
            icon:
                const Icon(
              Icons.refresh,
            ),
          ),
          if (!cargando &&
              error == null &&
              conversacionAbierta)
            IconButton(
              tooltip:
                  'Cerrar conversación',
              onPressed:
                  cerrando
                      ? null
                      : cerrarConversacion,
              icon:
                  const Icon(
                Icons.lock_outline,
              ),
            ),
        ],
      ),
      body:
          construirContenido(),
    );
  }
}