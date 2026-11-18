import 'dart:io';

import 'package:android_intent_plus/android_intent.dart';

import 'package:flutter/material.dart';

import 'package:image_picker/image_picker.dart';

import 'package:url_launcher/url_launcher.dart';

import '../../services/cita_service.dart';

class DetalleCitaScreen extends StatefulWidget {

  final Map<String, dynamic> cita;

  const DetalleCitaScreen({

    super.key,

    required this.cita,

  });

  @override

  State<DetalleCitaScreen> createState() =>

      _DetalleCitaScreenState();

}

class _DetalleCitaScreenState

    extends State<DetalleCitaScreen> {

  final CitaService citaService =

      CitaService();

  final ImagePicker imagePicker =

      ImagePicker();

  late Map<String, dynamic> citaActual;

  XFile? fotoServicio;

  bool actualizando = false;
  bool guardandoFoto = false;

  static const Color colorPrincipal =

      Color(0xFF9D4F43);

  static const Color colorFondo =

      Color(0xFFFAF7F4);

  static const Color colorTarjeta =

      Color(0xFFFFF4EF);

  static const Color colorBorde =

      Color(0xFFE9D8D1);

  static const Color colorTexto =

      Color(0xFF352D2B);

  @override

  void initState() {

    super.initState();

    citaActual =

        Map<String, dynamic>.from(

      widget.cita,

    );

  }

  // ==================================================

  // 2.3 FUNCIÓN DE LLAMADA

  // ==================================================

  Future<void> llamarClienta() async {

    final telefono =

        citaActual['telefonoClienta']

            ?.toString()

            .trim();

    if (telefono == null ||

        telefono.isEmpty) {

      if (!mounted) return;

      ScaffoldMessenger.of(context)

          .showSnackBar(

        const SnackBar(

          content: Text(

            'La clienta no tiene un teléfono registrado.',

          ),

        ),

      );

      return;

    }

    final telefonoLimpio =

        telefono.replaceAll(

      RegExp(r'[^0-9+]'),

      '',

    );

    final uri = Uri(

      scheme: 'tel',

      path: telefonoLimpio,

    );

    try {

      final abierto =

          await launchUrl(

        uri,

        mode:

            LaunchMode.externalApplication,

      );

      if (!abierto && mounted) {

        ScaffoldMessenger.of(context)

            .showSnackBar(

          const SnackBar(

            content: Text(

              'No se pudo abrir el marcador telefónico.',

            ),

          ),

        );

      }

    } catch (_) {

      if (!mounted) return;

      ScaffoldMessenger.of(context)

          .showSnackBar(

        const SnackBar(

          content: Text(

            'No se pudo abrir el marcador telefónico.',

          ),

        ),

      );

    }

  }

  // ==================================================

  // 2.4 GUARDAR CONTACTO

  // ==================================================

  Future<void> guardarContacto() async {

    final nombre =

        citaActual['nombreClienta']

            ?.toString()

            .trim();

    final telefono =

        citaActual['telefonoClienta']

            ?.toString()

            .trim();

    if (nombre == null ||

        nombre.isEmpty ||

        telefono == null ||

        telefono.isEmpty) {

      if (!mounted) return;

      ScaffoldMessenger.of(context)

          .showSnackBar(

        const SnackBar(

          content: Text(

            'No hay datos suficientes para crear el contacto.',

          ),

        ),

      );

      return;

    }

    final telefonoLimpio =

        telefono.replaceAll(

      RegExp(r'[^0-9+]'),

      '',

    );

    try {

      final intent = AndroidIntent(

        action:

            'android.intent.action.INSERT',

        type:

            'vnd.android.cursor.dir/contact',

        arguments: {

          'name': nombre,

          'phone': telefonoLimpio,

        },

      );

      await intent.launch();

    } catch (_) {

      if (!mounted) return;

      ScaffoldMessenger.of(context)

          .showSnackBar(

        const SnackBar(

          content: Text(

            'No se pudo abrir la aplicación de contactos.',

          ),

        ),

      );

    }

  }

  // ==================================================

  // 2.5 USO DE CÁMARA

  // ==================================================

  Future<void> tomarFotografia() async {

    try {

      final XFile? fotografia =

          await imagePicker.pickImage(

        source: ImageSource.camera,

        imageQuality: 85,

      );

      if (fotografia == null) {

        return;

      }

      if (!mounted) return;

      setState(() {

        fotoServicio = fotografia;

      });

      ScaffoldMessenger.of(context)

          .showSnackBar(

        const SnackBar(

          content: Text(

            'Fotografía capturada correctamente.',

          ),

        ),

      );

    } catch (_) {

      if (!mounted) return;

      ScaffoldMessenger.of(context)

          .showSnackBar(

        const SnackBar(

          content: Text(

            'No se pudo abrir la cámara del dispositivo.',

          ),

        ),

      );

    }

  }

  // ==================================================

  // 2.6 ASOCIAR FOTOGRAFÍA CON LA CITA

  // ==================================================

  Future<void> guardarFotografia() async {
    if (guardandoFoto || fotoServicio == null) {
      return;
    }

    final citaId =
        citaActual['id']
            ?.toString();

    if (citaId == null ||
        citaId.isEmpty) {
      if (!mounted) return;

      ScaffoldMessenger.of(context)
          .showSnackBar(
        const SnackBar(
          content: Text(
            'No fue posible identificar la cita.',
          ),
        ),
      );

      return;
    }

    setState(() {
      guardandoFoto = true;
    });

    try {
      final citaActualizada =
          await citaService
              .subirFotoResultado(
        citaId: citaId,
        rutaFoto: fotoServicio!.path,
      );

      if (!mounted) return;

      setState(() {
        citaActual =
            citaActualizada;

        fotoServicio = null;
        guardandoFoto = false;
      });

      ScaffoldMessenger.of(context)
          .showSnackBar(
        const SnackBar(
          content: Text(
            'Fotografía asociada a la cita correctamente.',
          ),
        ),
      );
    } catch (e) {
      if (!mounted) return;

      setState(() {
        guardandoFoto = false;
      });

      ScaffoldMessenger.of(context)
          .showSnackBar(
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
    }
  }

  // ==================================================

  // FORMATEAR HORA

  // ==================================================

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

  // ==================================================

  // FORMATEAR FECHA

  // ==================================================

  String formatearFecha(

    String fecha,

  ) {

    try {

      final partes =

          fecha.split('-');

      if (partes.length == 3) {

        return '${partes[2]}/${partes[1]}/${partes[0]}';

      }

      return fecha;

    } catch (_) {

      return fecha;

    }

  }

  // ==================================================

  // FORMATEAR ESTADO

  // ==================================================

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

  // ==================================================

  // 2.2 ACTUALIZAR ESTADO

  // ==================================================

  Future<void> cambiarEstado(

    String nuevoEstado,

  ) async {

    if (actualizando) {

      return;

    }

    final citaId =

        citaActual['id']

            ?.toString();

    if (citaId == null ||

        citaId.isEmpty) {

      return;

    }

    final confirmar =

        await showDialog<bool>(

      context: context,

      builder: (context) {

        return AlertDialog(

          title: const Text(

            'Actualizar estado',

          ),

          content: Text(

            '¿Deseas cambiar la cita a '

            '${formatearEstado(nuevoEstado)}?',

          ),

          actions: [

            TextButton(

              onPressed: () {

                Navigator.pop(

                  context,

                  false,

                );

              },

              child: const Text(

                'Cancelar',

              ),

            ),

            FilledButton(

              onPressed: () {

                Navigator.pop(

                  context,

                  true,

                );

              },

              child: const Text(

                'Confirmar',

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

      actualizando = true;

    });

    try {

      final citaActualizada =

          await citaService

              .cambiarEstado(

        citaId: citaId,

        nuevoEstado: nuevoEstado,

      );

      if (!mounted) return;

      setState(() {

        citaActual =

            citaActualizada;

        actualizando = false;

      });

      ScaffoldMessenger.of(context)

          .showSnackBar(

        SnackBar(

          content: Text(

            'Estado actualizado a '

            '${formatearEstado(nuevoEstado)}.',

          ),

        ),

      );

    } catch (e) {

      if (!mounted) return;

      setState(() {

        actualizando = false;

      });

      ScaffoldMessenger.of(context)

          .showSnackBar(

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

    }

  }

  // ==================================================

  // TÍTULO DE SECCIÓN

  // ==================================================

  Widget tituloSeccion({

    required IconData icono,

    required String titulo,

  }) {

    return Padding(

      padding:

          const EdgeInsets.only(

        top: 8,

        bottom: 10,

      ),

      child: Row(

        children: [

          Icon(

            icono,

            size: 21,

            color: colorPrincipal,

          ),

          const SizedBox(

            width: 8,

          ),

          Text(

            titulo,

            style:

                const TextStyle(

              fontSize: 18,

              fontWeight:

                  FontWeight.bold,

              color: colorTexto,

            ),

          ),

        ],

      ),

    );

  }

  // ==================================================

  // TARJETA PEQUEÑA DE INFORMACIÓN

  // ==================================================

  Widget datoCompacto({

    required IconData icono,

    required String titulo,

    required String valor,

  }) {

    return Container(

      padding:

          const EdgeInsets.all(

        14,

      ),

      decoration: BoxDecoration(

        color: colorTarjeta,

        borderRadius:

            BorderRadius.circular(

          18,

        ),

        border: Border.all(

          color: colorBorde,

        ),

      ),

      child: Row(

        children: [

          Container(

            width: 40,

            height: 40,

            decoration:

                const BoxDecoration(

              color:

                  Color(

                0xFFF4DDD7,

              ),

              shape:

                  BoxShape.circle,

            ),

            child: Icon(

              icono,

              size: 21,

              color: colorPrincipal,

            ),

          ),

          const SizedBox(

            width: 10,

          ),

          Expanded(

            child: Column(

              crossAxisAlignment:

                  CrossAxisAlignment

                      .start,

              children: [

                Text(

                  titulo,

                  style:

                      const TextStyle(

                    fontSize: 12,

                    color:

                        Colors.black54,

                  ),

                ),

                const SizedBox(

                  height: 2,

                ),

                Text(

                  valor,

                  style:

                      const TextStyle(

                    fontSize: 15,

                    fontWeight:

                        FontWeight

                            .w600,

                    color: colorTexto,

                  ),

                ),

              ],

            ),

          ),

        ],

      ),

    );

  }

  // ==================================================

  // ACCIONES SEGÚN ESTADO

  // ==================================================

  Widget accionesEstado(

    String estado,

  ) {

    final estadoActual =

        estado.toUpperCase();

    if (estadoActual ==

            'CANCELADA' ||

        estadoActual ==

            'COMPLETADA') {

      return Container(

        width: double.infinity,

        padding:

            const EdgeInsets.all(

          16,

        ),

        decoration:

            BoxDecoration(

          color:

              const Color(

            0xFFF1E6E2,

          ),

          borderRadius:

              BorderRadius.circular(

            18,

          ),

        ),

        child: const Text(

          'Esta cita ya se encuentra en un estado final.',

          textAlign:

              TextAlign.center,

          style: TextStyle(

            color:

                Colors.black54,

          ),

        ),

      );

    }

    if (estadoActual ==

        'PENDIENTE') {

      return Column(

        children: [

          SizedBox(

            width:

                double.infinity,

            child:

                FilledButton.icon(

              style:

                  FilledButton.styleFrom(

                backgroundColor:

                    colorPrincipal,

                foregroundColor:

                    Colors.white,

                padding:

                    const EdgeInsets

                        .symmetric(

                  vertical: 15,

                ),

              ),

              onPressed:

                  actualizando

                      ? null

                      : () =>

                          cambiarEstado(

                            'CONFIRMADA',

                          ),

              icon:

                  const Icon(

                Icons

                    .check_circle_outline,

              ),

              label:

                  const Text(

                'Confirmar cita',

              ),

            ),

          ),

          const SizedBox(

            height: 10,

          ),

          SizedBox(

            width:

                double.infinity,

            child:

                OutlinedButton.icon(

              style:

                  OutlinedButton.styleFrom(

                foregroundColor:

                    colorPrincipal,

                side:

                    const BorderSide(

                  color:

                      colorPrincipal,

                ),

                padding:

                    const EdgeInsets

                        .symmetric(

                  vertical: 15,

                ),

              ),

              onPressed:

                  actualizando

                      ? null

                      : () =>

                          cambiarEstado(

                            'CANCELADA',

                          ),

              icon:

                  const Icon(

                Icons

                    .cancel_outlined,

              ),

              label:

                  const Text(

                'Cancelar cita',

              ),

            ),

          ),

        ],

      );

    }

    if (estadoActual ==

        'CONFIRMADA') {

      return Column(

        children: [

          SizedBox(

            width:

                double.infinity,

            child:

                FilledButton.icon(

              style:

                  FilledButton.styleFrom(

                backgroundColor:

                    colorPrincipal,

                foregroundColor:

                    Colors.white,

                padding:

                    const EdgeInsets

                        .symmetric(

                  vertical: 15,

                ),

              ),

              onPressed:

                  actualizando

                      ? null

                      : () =>

                          cambiarEstado(

                            'COMPLETADA',

                          ),

              icon:

                  const Icon(

                Icons.task_alt,

              ),

              label:

                  const Text(

                'Marcar como completada',

              ),

            ),

          ),

          const SizedBox(

            height: 10,

          ),

          SizedBox(

            width:

                double.infinity,

            child:

                OutlinedButton.icon(

              style:

                  OutlinedButton.styleFrom(

                foregroundColor:

                    colorPrincipal,

                side:

                    const BorderSide(

                  color:

                      colorPrincipal,

                ),

                padding:

                    const EdgeInsets

                        .symmetric(

                  vertical: 15,

                ),

              ),

              onPressed:

                  actualizando

                      ? null

                      : () =>

                          cambiarEstado(

                            'CANCELADA',

                          ),

              icon:

                  const Icon(

                Icons

                    .cancel_outlined,

              ),

              label:

                  const Text(

                'Cancelar cita',

              ),

            ),

          ),

        ],

      );

    }

    if (estadoActual ==

        'REAGENDAR') {

      return Column(

        children: [

          Container(

            width:

                double.infinity,

            padding:

                const EdgeInsets.all(

              14,

            ),

            decoration:

                BoxDecoration(

              color:

                  const Color(

                0xFFF1E6E2,

              ),

              borderRadius:

                  BorderRadius

                      .circular(

                16,

              ),

            ),

            child:

                const Text(

              'La clienta solicitó reagendar esta cita.',

              textAlign:

                  TextAlign.center,

            ),

          ),

          const SizedBox(

            height: 10,

          ),

          SizedBox(

            width:

                double.infinity,

            child:

                OutlinedButton.icon(

              style:

                  OutlinedButton.styleFrom(

                foregroundColor:

                    colorPrincipal,

                side:

                    const BorderSide(

                  color:

                      colorPrincipal,

                ),

              ),

              onPressed:

                  actualizando

                      ? null

                      : () =>

                          cambiarEstado(

                            'CANCELADA',

                          ),

              icon:

                  const Icon(

                Icons

                    .cancel_outlined,

              ),

              label:

                  const Text(

                'Cancelar cita',

              ),

            ),

          ),

        ],

      );

    }

    return const SizedBox.shrink();

  }

  // ==================================================

  // BUILD

  // ==================================================

  @override

  Widget build(

    BuildContext context,

  ) {

    final nombreClienta =

        citaActual['nombreClienta']

                ?.toString() ??

            'Clienta';

    final telefonoReal =

        citaActual['telefonoClienta']

            ?.toString()

            .trim();

    final telefonoDisponible =

        telefonoReal != null &&

            telefonoReal.isNotEmpty;

    final telefono =

        telefonoDisponible

            ? telefonoReal

            : 'Sin teléfono registrado';

    final servicio =

        citaActual['nombreServicio']

                ?.toString() ??

            'Servicio';

    final fecha =

        citaActual['fecha']

                ?.toString() ??

            '';

    final hora =

        citaActual['hora']

                ?.toString() ??

            '';

    final estado =

        citaActual['estado']

                ?.toString() ??

            '';

    final fotoResultadoUrl =
        citaActual['fotoResultadoUrl']
            ?.toString()
            .trim();

    final tieneFotoGuardada =
        fotoResultadoUrl != null &&
            fotoResultadoUrl.isNotEmpty;

    return Scaffold(

      backgroundColor:

          colorFondo,

      appBar: AppBar(

        backgroundColor:

            const Color(

          0xFFF4E8E3,

        ),

        surfaceTintColor:

            Colors.transparent,

        title:

            const Text(

          'Detalle de cita',

        ),

      ),

      body:

          SingleChildScrollView(

        padding:

            const EdgeInsets.fromLTRB(

          16,

          18,

          16,

          35,

        ),

        child: Column(

          crossAxisAlignment:

              CrossAxisAlignment

                  .stretch,

          children: [

            // ========================================

            // RESUMEN DE CITA

            // ========================================

            Container(

              padding:

                  const EdgeInsets.all(

                18,

              ),

              decoration:

                  BoxDecoration(

                color: colorTarjeta,

                borderRadius:

                    BorderRadius

                        .circular(

                  22,

                ),

                border:

                    Border.all(

                  color: colorBorde,

                ),

              ),

              child: Row(

                children: [

                  Container(

                    width: 56,

                    height: 56,

                    decoration:

                        const BoxDecoration(

                      color:

                          Color(

                        0xFFF3D7D0,

                      ),

                      shape:

                          BoxShape

                              .circle,

                    ),

                    child:

                        const Icon(

                      Icons.person_outline,

                      size: 29,

                      color:

                          colorPrincipal,

                    ),

                  ),

                  const SizedBox(

                    width: 14,

                  ),

                  Expanded(

                    child: Column(

                      crossAxisAlignment:

                          CrossAxisAlignment

                              .start,

                      children: [

                        Text(

                          nombreClienta,

                          style:

                              const TextStyle(

                            fontSize:

                                21,

                            fontWeight:

                                FontWeight

                                    .bold,

                            color:

                                colorTexto,

                          ),

                        ),

                        const SizedBox(

                          height: 4,

                        ),

                        Text(

                          servicio,

                          style:

                              const TextStyle(

                            fontSize:

                                14,

                            color:

                                Colors

                                    .black54,

                          ),

                        ),

                        const SizedBox(

                          height: 9,

                        ),

                        Container(

                          padding:

                              const EdgeInsets

                                  .symmetric(

                            horizontal:

                                11,

                            vertical:

                                5,

                          ),

                          decoration:

                              BoxDecoration(

                            color:

                                const Color(

                              0xFFF1DDD7,

                            ),

                            borderRadius:

                                BorderRadius

                                    .circular(

                              20,

                            ),

                          ),

                          child: Text(

                            formatearEstado(

                              estado,

                            ),

                            style:

                                const TextStyle(

                              color:

                                  colorPrincipal,

                              fontWeight:

                                  FontWeight

                                      .w600,

                            ),

                          ),

                        ),

                      ],

                    ),

                  ),

                ],

              ),

            ),

            const SizedBox(

              height: 18,

            ),

            // ========================================

            // DETALLES

            // ========================================

            tituloSeccion(

              icono:

                  Icons.event_note,

              titulo:

                  'Detalles de la cita',

            ),

            Row(

              children: [

                Expanded(

                  child:

                      datoCompacto(

                    icono: Icons

                        .calendar_today_outlined,

                    titulo:

                        'Fecha',

                    valor:

                        formatearFecha(

                      fecha,

                    ),

                  ),

                ),

                const SizedBox(

                  width: 10,

                ),

                Expanded(

                  child:

                      datoCompacto(

                    icono:

                        Icons

                            .access_time,

                    titulo:

                        'Hora',

                    valor:

                        formatearHora(

                      hora,

                    ),

                  ),

                ),

              ],

            ),

            const SizedBox(

              height: 18,

            ),

            // ========================================

            // CONTACTO

            // ========================================

            tituloSeccion(

              icono:

                  Icons

                      .contact_phone_outlined,

              titulo:

                  'Contacto',

            ),

            Container(

              padding:

                  const EdgeInsets.all(

                16,

              ),

              decoration:

                  BoxDecoration(

                color: colorTarjeta,

                borderRadius:

                    BorderRadius

                        .circular(

                  20,

                ),

                border:

                    Border.all(

                  color: colorBorde,

                ),

              ),

              child: Column(

                crossAxisAlignment:

                    CrossAxisAlignment

                        .start,

                children: [

                  Row(

                    children: [

                      const Icon(

                        Icons

                            .phone_outlined,

                        color:

                            colorPrincipal,

                      ),

                      const SizedBox(

                        width: 10,

                      ),

                      Expanded(

                        child: Text(

                          telefono,

                          style:

                              const TextStyle(

                            fontSize:

                                17,

                            fontWeight:

                                FontWeight

                                    .w600,

                            color:

                                colorTexto,

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

                      Expanded(

                        child:

                            FilledButton.icon(

                          style:

                              FilledButton

                                  .styleFrom(

                            backgroundColor:

                                colorPrincipal,

                            foregroundColor:

                                Colors.white,

                            padding:

                                const EdgeInsets

                                    .symmetric(

                              vertical:

                                  13,

                            ),

                          ),

                          onPressed:

                              telefonoDisponible

                                  ? llamarClienta

                                  : null,

                          icon:

                              const Icon(

                            Icons.phone,

                            size: 19,

                          ),

                          label:

                              const Text(

                            'Llamar',

                          ),

                        ),

                      ),

                      const SizedBox(

                        width: 8,

                      ),

                      Expanded(

                        child:

                            OutlinedButton.icon(

                          style:

                              OutlinedButton

                                  .styleFrom(

                            foregroundColor:

                                colorPrincipal,

                            side:

                                const BorderSide(

                              color:

                                  colorPrincipal,

                            ),

                            padding:

                                const EdgeInsets

                                    .symmetric(

                              vertical:

                                  13,

                            ),

                          ),

                          onPressed:

                              telefonoDisponible

                                  ? guardarContacto

                                  : null,

                          icon:

                              const Icon(

                            Icons

                                .person_add_alt_1,

                            size: 19,

                          ),

                          label:

                              const Text(

                            'Guardar',

                          ),

                        ),

                      ),

                    ],

                  ),

                ],

              ),

            ),

            const SizedBox(

              height: 18,

            ),

            // ========================================

            // 2.5 Y 2.6 RESULTADO DEL SERVICIO

            // ========================================

            tituloSeccion(
              icono:
                  Icons
                      .photo_camera_outlined,
              titulo:
                  'Resultado del servicio',
            ),

            Container(
              padding:
                  const EdgeInsets.all(
                16,
              ),
              decoration:
                  BoxDecoration(
                color: colorTarjeta,
                borderRadius:
                    BorderRadius
                        .circular(
                  20,
                ),
                border:
                    Border.all(
                  color: colorBorde,
                ),
              ),
              child: Column(
                crossAxisAlignment:
                    CrossAxisAlignment
                        .stretch,
                children: [
                  if (fotoServicio == null &&
                      !tieneFotoGuardada) ...[
                    Container(
                      height: 145,
                      decoration:
                          BoxDecoration(
                        color:
                            const Color(
                          0xFFF5E9E5,
                        ),
                        borderRadius:
                            BorderRadius
                                .circular(
                          16,
                        ),
                      ),
                      child:
                          const Column(
                        mainAxisAlignment:
                            MainAxisAlignment
                                .center,
                        children: [
                          Icon(
                            Icons
                                .photo_camera_outlined,
                            size: 45,
                            color:
                                colorPrincipal,
                          ),
                          SizedBox(
                            height: 8,
                          ),
                          Text(
                            'Captura una fotografía del resultado',
                            textAlign:
                                TextAlign
                                    .center,
                            style:
                                TextStyle(
                              color:
                                  Colors
                                      .black54,
                            ),
                          ),
                        ],
                      ),
                    ),

                    const SizedBox(
                      height: 12,
                    ),

                    FilledButton.icon(
                      style:
                          FilledButton
                              .styleFrom(
                        backgroundColor:
                            colorPrincipal,
                        foregroundColor:
                            Colors.white,
                        padding:
                            const EdgeInsets
                                .symmetric(
                          vertical: 15,
                        ),
                      ),
                      onPressed:
                          guardandoFoto
                              ? null
                              : tomarFotografia,
                      icon:
                          const Icon(
                        Icons
                            .camera_alt_outlined,
                      ),
                      label:
                          const Text(
                        'Tomar fotografía',
                      ),
                    ),
                  ],

                  if (fotoServicio !=
                      null) ...[
                    ClipRRect(
                      borderRadius:
                          BorderRadius
                              .circular(
                        16,
                      ),
                      child:
                          Image.file(
                        File(
                          fotoServicio!
                              .path,
                        ),
                        width:
                            double.infinity,
                        height: 250,
                        fit:
                            BoxFit.cover,
                      ),
                    ),

                    const SizedBox(
                      height: 12,
                    ),

                    const Row(
                      children: [
                        Icon(
                          Icons
                              .check_circle,
                          color:
                              colorPrincipal,
                        ),
                        SizedBox(
                          width: 8,
                        ),
                        Expanded(
                          child: Text(
                            'Fotografía capturada',
                            style:
                                TextStyle(
                              fontWeight:
                                  FontWeight
                                      .w600,
                            ),
                          ),
                        ),
                      ],
                    ),

                    const SizedBox(
                      height: 12,
                    ),

                    FilledButton.icon(
                      style:
                          FilledButton
                              .styleFrom(
                        backgroundColor:
                            colorPrincipal,
                        foregroundColor:
                            Colors.white,
                        padding:
                            const EdgeInsets
                                .symmetric(
                          vertical: 14,
                        ),
                      ),
                      onPressed:
                          guardandoFoto
                              ? null
                              : guardarFotografia,
                      icon:
                          guardandoFoto
                              ? const SizedBox(
                                  width: 18,
                                  height: 18,
                                  child:
                                      CircularProgressIndicator(
                                    strokeWidth: 2,
                                    color:
                                        Colors.white,
                                  ),
                                )
                              : const Icon(
                                  Icons
                                      .cloud_upload_outlined,
                                ),
                      label: Text(
                        guardandoFoto
                            ? 'Guardando fotografía...'
                            : 'Guardar fotografía',
                      ),
                    ),

                    const SizedBox(
                      height: 10,
                    ),

                    OutlinedButton.icon(
                      style:
                          OutlinedButton
                              .styleFrom(
                        foregroundColor:
                            colorPrincipal,
                        side:
                            const BorderSide(
                          color:
                              colorPrincipal,
                        ),
                        padding:
                            const EdgeInsets
                                .symmetric(
                          vertical: 14,
                        ),
                      ),
                      onPressed:
                          guardandoFoto
                              ? null
                              : tomarFotografia,
                      icon:
                          const Icon(
                        Icons.refresh,
                      ),
                      label:
                          const Text(
                        'Tomar otra fotografía',
                      ),
                    ),
                  ],

                  if (fotoServicio == null &&
                      tieneFotoGuardada) ...[
                    ClipRRect(
                      borderRadius:
                          BorderRadius
                              .circular(
                        16,
                      ),
                      child:
                          Image.network(
                        fotoResultadoUrl,
                        width:
                            double.infinity,
                        height: 250,
                        fit:
                            BoxFit.cover,
                        errorBuilder:
                            (
                          context,
                          error,
                          stackTrace,
                        ) {
                          return Container(
                            height: 180,
                            alignment:
                                Alignment
                                    .center,
                            color:
                                const Color(
                              0xFFF5E9E5,
                            ),
                            child:
                                const Column(
                              mainAxisAlignment:
                                  MainAxisAlignment
                                      .center,
                              children: [
                                Icon(
                                  Icons
                                      .broken_image_outlined,
                                  size: 42,
                                  color:
                                      colorPrincipal,
                                ),
                                SizedBox(
                                  height: 8,
                                ),
                                Text(
                                  'No se pudo cargar la fotografía',
                                  textAlign:
                                      TextAlign
                                          .center,
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),

                    const SizedBox(
                      height: 12,
                    ),

                    const Row(
                      children: [
                        Icon(
                          Icons
                              .cloud_done_outlined,
                          color:
                              colorPrincipal,
                        ),
                        SizedBox(
                          width: 8,
                        ),
                        Expanded(
                          child: Text(
                            'Fotografía asociada a la cita',
                            style:
                                TextStyle(
                              fontWeight:
                                  FontWeight
                                      .w600,
                            ),
                          ),
                        ),
                      ],
                    ),

                    const SizedBox(
                      height: 12,
                    ),

                    OutlinedButton.icon(
                      style:
                          OutlinedButton
                              .styleFrom(
                        foregroundColor:
                            colorPrincipal,
                        side:
                            const BorderSide(
                          color:
                              colorPrincipal,
                        ),
                        padding:
                            const EdgeInsets
                                .symmetric(
                          vertical: 14,
                        ),
                      ),
                      onPressed:
                          guardandoFoto
                              ? null
                              : tomarFotografia,
                      icon:
                          const Icon(
                        Icons
                            .photo_camera_outlined,
                      ),
                      label:
                          const Text(
                        'Tomar nueva fotografía',
                      ),
                    ),
                  ],
                ],
              ),
            ),

            const SizedBox(
              height: 20,
            ),

            // ========================================

            // ACCIONES DE LA CITA

            // ========================================

            tituloSeccion(

              icono:

                  Icons

                      .task_alt_outlined,

              titulo:

                  'Acciones de la cita',

            ),

            if (actualizando)

              const Padding(

                padding:

                    EdgeInsets.only(

                  bottom: 16,

                ),

                child: Center(

                  child:

                      CircularProgressIndicator(),

                ),

              ),

            accionesEstado(

              estado,

            ),

            const SizedBox(

              height: 20,

            ),

          ],

        ),

      ),

    );

  }

}