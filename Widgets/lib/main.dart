import 'dart:convert';
import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:web/web.dart' as web;

const apiUrl =
    'http://localhost:8080/api/admin/estadisticas';

const tokenKey = 'lashbook_token';

void main() {
  runApp(const LashBookStatsApp());
}

class LashBookStatsApp extends StatelessWidget {
  const LashBookStatsApp({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Estadísticas LashBook',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor:
            const Color(0xFFFFFDFC),
        colorScheme: ColorScheme.fromSeed(
          seedColor:
              const Color(0xFF6E4E4A),
          brightness: Brightness.light,
        ),
        fontFamily: 'Arial',
      ),
      home: const EstadisticasWidgetPage(),
    );
  }
}

class EstadisticasWidgetPage
    extends StatefulWidget {
  const EstadisticasWidgetPage({
    super.key,
  });

  @override
  State<EstadisticasWidgetPage>
      createState() =>
          _EstadisticasWidgetPageState();
}

class _EstadisticasWidgetPageState
    extends State<EstadisticasWidgetPage> {
  Estadisticas? estadisticas;
  String mensajeError = '';
  bool cargando = true;

  @override
  void initState() {
    super.initState();
    cargarEstadisticas();
  }

  Future<void> cargarEstadisticas() async {
    setState(() {
      cargando = true;
      mensajeError = '';
    });

    try {
      final token =
          web.window.localStorage
              .getItem(tokenKey);

      if (
          token == null ||
          token.trim().isEmpty
      ) {
        throw Exception(
          'No existe una sesión administrativa activa.',
        );
      }

      final respuesta =
          await http.get(
        Uri.parse(apiUrl),
        headers: {
          'Authorization':
              'Bearer $token',
          'Accept':
              'application/json',
        },
      );

      final contenido =
          respuesta.body.trim();

      final Map<String, dynamic>
          datos =
          contenido.isEmpty
              ? <String, dynamic>{}
              : jsonDecode(contenido)
                  as Map<String, dynamic>;

      if (
          respuesta.statusCode < 200 ||
          respuesta.statusCode >= 300
      ) {
        throw Exception(
          datos['mensaje']?.toString() ??
              'No fue posible cargar las estadísticas.',
        );
      }

      setState(() {
        estadisticas =
            Estadisticas.fromJson(datos);
      });
    } catch (error) {
      setState(() {
        mensajeError =
            limpiarMensajeError(error);
      });
    } finally {
      if (mounted) {
        setState(() {
          cargando = false;
        });
      }
    }
  }

  String limpiarMensajeError(
    Object error,
  ) {
    return error
        .toString()
        .replaceFirst(
          'Exception: ',
          '',
        );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: cargando
            ? const EstadoCargando()
            : mensajeError.isNotEmpty
                ? EstadoError(
                    mensaje:
                        mensajeError,
                    onReintentar:
                        cargarEstadisticas,
                  )
                : ContenidoEstadisticas(
                    estadisticas:
                        estadisticas!,
                    onActualizar:
                        cargarEstadisticas,
                  ),
      ),
    );
  }
}

class ContenidoEstadisticas
    extends StatelessWidget {
  const ContenidoEstadisticas({
    required this.estadisticas,
    required this.onActualizar,
    super.key,
  });

  final Estadisticas estadisticas;
  final Future<void> Function()
      onActualizar;

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (
        context,
        constraints,
      ) {
        final esCompacto =
            constraints.maxWidth < 760;

        return SingleChildScrollView(
          padding: EdgeInsets.symmetric(
            horizontal:
                esCompacto ? 18 : 30,
            vertical:
                esCompacto ? 20 : 28,
          ),
          child: Center(
            child: ConstrainedBox(
              constraints:
                  const BoxConstraints(
                maxWidth: 1120,
              ),
              child: Column(
                crossAxisAlignment:
                    CrossAxisAlignment.start,
                children: [
                  EncabezadoWidget(
                    onActualizar:
                        onActualizar,
                    esCompacto:
                        esCompacto,
                  ),
                  const SizedBox(
                    height: 24,
                  ),
                  ResumenPrincipal(
                    estadisticas:
                        estadisticas,
                    esCompacto:
                        esCompacto,
                  ),
                  const SizedBox(
                    height: 24,
                  ),
                  DistribucionCitas(
                    estadisticas:
                        estadisticas,
                    esCompacto:
                        esCompacto,
                  ),
                  const SizedBox(
                    height: 24,
                  ),
                  TarjetaIngresos(
                    estadisticas:
                        estadisticas,
                    esCompacto:
                        esCompacto,
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}

class EncabezadoWidget
    extends StatelessWidget {
  const EncabezadoWidget({
    required this.onActualizar,
    required this.esCompacto,
    super.key,
  });

  final Future<void> Function()
      onActualizar;
  final bool esCompacto;

  @override
  Widget build(BuildContext context) {
    final titulo = Column(
      crossAxisAlignment:
          CrossAxisAlignment.start,
      children: [
        const Text(
          'WIDGET INTERACTIVO',
          style: TextStyle(
            color: Color(0xFF8A625D),
            fontSize: 12,
            fontWeight: FontWeight.w800,
            letterSpacing: 1.5,
          ),
        ),
        const SizedBox(
          height: 7,
        ),
        Text(
          'Resumen de LashBook',
          style: TextStyle(
            color:
                const Color(0xFF2B2424),
            fontSize:
                esCompacto ? 28 : 36,
            fontFamily: 'Georgia',
            fontWeight: FontWeight.w500,
          ),
        ),
        const SizedBox(
          height: 8,
        ),
        const Text(
          'Información actualizada desde Spring Boot y Supabase.',
          style: TextStyle(
            color: Color(0xFF746765),
            fontSize: 15,
            height: 1.45,
          ),
        ),
      ],
    );

    final boton = FilledButton.icon(
      onPressed: onActualizar,
      icon: const Icon(
        Icons.refresh_rounded,
      ),
      label: const Text(
        'Actualizar',
      ),
      style: FilledButton.styleFrom(
        backgroundColor:
            const Color(0xFF6E4E4A),
        foregroundColor:
            Colors.white,
        padding:
            const EdgeInsets.symmetric(
          horizontal: 20,
          vertical: 16,
        ),
      ),
    );

    if (esCompacto) {
      return Column(
        crossAxisAlignment:
            CrossAxisAlignment.stretch,
        children: [
          titulo,
          const SizedBox(
            height: 16,
          ),
          boton,
        ],
      );
    }

    return Row(
      mainAxisAlignment:
          MainAxisAlignment.spaceBetween,
      crossAxisAlignment:
          CrossAxisAlignment.start,
      children: [
        Expanded(
          child: titulo,
        ),
        const SizedBox(
          width: 24,
        ),
        boton,
      ],
    );
  }
}

class ResumenPrincipal
    extends StatelessWidget {
  const ResumenPrincipal({
    required this.estadisticas,
    required this.esCompacto,
    super.key,
  });

  final Estadisticas estadisticas;
  final bool esCompacto;

  @override
  Widget build(BuildContext context) {
    final tarjetas = [
      DatoResumen(
        titulo: 'Total de citas',
        valor:
            estadisticas.totalCitas,
        icono:
            Icons.calendar_month_rounded,
        destacado: true,
      ),
      DatoResumen(
        titulo: 'Pendientes',
        valor:
            estadisticas.pendientes,
        icono:
            Icons.schedule_rounded,
      ),
      DatoResumen(
        titulo: 'Confirmadas',
        valor:
            estadisticas.confirmadas,
        icono:
            Icons.verified_rounded,
      ),
      DatoResumen(
        titulo: 'Completadas',
        valor:
            estadisticas.completadas,
        icono:
            Icons.done_all_rounded,
      ),
      DatoResumen(
        titulo: 'Canceladas',
        valor:
            estadisticas.canceladas,
        icono:
            Icons.cancel_outlined,
      ),
      DatoResumen(
        titulo: 'Por reagendar',
        valor:
            estadisticas.porReagendar,
        icono:
            Icons.event_repeat_rounded,
      ),
    ];

    return GridView.builder(
      shrinkWrap: true,
      physics:
          const NeverScrollableScrollPhysics(),
      itemCount: tarjetas.length,
      gridDelegate:
          SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount:
            esCompacto ? 2 : 3,
        crossAxisSpacing: 14,
        mainAxisSpacing: 14,
        childAspectRatio:
            esCompacto ? 1.25 : 1.55,
      ),
      itemBuilder: (
        context,
        index,
      ) {
        return TarjetaResumen(
          dato: tarjetas[index],
        );
      },
    );
  }
}

class TarjetaResumen
    extends StatelessWidget {
  const TarjetaResumen({
    required this.dato,
    super.key,
  });

  final DatoResumen dato;

  @override
  Widget build(BuildContext context) {
    final fondo =
        dato.destacado
            ? const Color(0xFF6E4E4A)
            : Colors.white;

    final textoPrincipal =
        dato.destacado
            ? Colors.white
            : const Color(0xFF2B2424);

    final textoSecundario =
        dato.destacado
            ? const Color(0xFFF1E4E0)
            : const Color(0xFF746765);

    return Container(
      padding:
          const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: fondo,
        borderRadius:
            BorderRadius.circular(22),
        border: Border.all(
          color: dato.destacado
              ? const Color(0xFF6E4E4A)
              : const Color(0xFFE3D7D4),
        ),
        boxShadow: const [
          BoxShadow(
            color: Color(0x137B5B57),
            blurRadius: 25,
            offset: Offset(0, 10),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment:
            CrossAxisAlignment.start,
        mainAxisAlignment:
            MainAxisAlignment.spaceBetween,
        children: [
          Icon(
            dato.icono,
            color: dato.destacado
                ? const Color(0xFFF1E4E0)
                : const Color(0xFF8A625D),
          ),
          Column(
            crossAxisAlignment:
                CrossAxisAlignment.start,
            children: [
              Text(
                dato.valor.toString(),
                style: TextStyle(
                  color: textoPrincipal,
                  fontSize: 32,
                  fontWeight:
                      FontWeight.w800,
                ),
              ),
              const SizedBox(
                height: 4,
              ),
              Text(
                dato.titulo,
                style: TextStyle(
                  color: textoSecundario,
                  fontSize: 13,
                  fontWeight:
                      FontWeight.w700,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class DistribucionCitas
    extends StatelessWidget {
  const DistribucionCitas({
    required this.estadisticas,
    required this.esCompacto,
    super.key,
  });

  final Estadisticas estadisticas;
  final bool esCompacto;

  @override
  Widget build(BuildContext context) {
    final segmentos =
        estadisticas.segmentos;

    final grafica = SizedBox(
      width: 250,
      height: 250,
      child: CustomPaint(
        painter: GraficaCircularPainter(
          segmentos: segmentos,
        ),
        child: Center(
          child: Column(
            mainAxisAlignment:
                MainAxisAlignment.center,
            children: [
              Text(
                estadisticas
                    .totalCitas
                    .toString(),
                style: const TextStyle(
                  color:
                      Color(0xFF2B2424),
                  fontSize: 38,
                  fontWeight:
                      FontWeight.w800,
                ),
              ),
              const Text(
                'citas',
                style: TextStyle(
                  color:
                      Color(0xFF746765),
                  fontSize: 14,
                ),
              ),
            ],
          ),
        ),
      ),
    );

    final leyenda = Column(
      children: segmentos
          .map(
            (
              segmento,
            ) =>
                LeyendaEstado(
              segmento: segmento,
              total:
                  estadisticas.totalCitas,
            ),
          )
          .toList(),
    );

    return Container(
      width: double.infinity,
      padding:
          const EdgeInsets.all(26),
      decoration: BoxDecoration(
        color:
            const Color(0xFFF7F2F0),
        borderRadius:
            BorderRadius.circular(24),
        border: Border.all(
          color:
              const Color(0xFFE3D7D4),
        ),
      ),
      child: Column(
        crossAxisAlignment:
            CrossAxisAlignment.start,
        children: [
          const Text(
            'Distribución por estado',
            style: TextStyle(
              color: Color(0xFF2B2424),
              fontFamily: 'Georgia',
              fontSize: 25,
              fontWeight: FontWeight.w500,
            ),
          ),
          const SizedBox(
            height: 8,
          ),
          const Text(
            'Comparación visual de las citas registradas.',
            style: TextStyle(
              color: Color(0xFF746765),
              fontSize: 14,
            ),
          ),
          const SizedBox(
            height: 24,
          ),
          if (esCompacto)
            Column(
              children: [
                grafica,
                const SizedBox(
                  height: 24,
                ),
                leyenda,
              ],
            )
          else
            Row(
              children: [
                grafica,
                const SizedBox(
                  width: 46,
                ),
                Expanded(
                  child: leyenda,
                ),
              ],
            ),
        ],
      ),
    );
  }
}

class LeyendaEstado
    extends StatelessWidget {
  const LeyendaEstado({
    required this.segmento,
    required this.total,
    super.key,
  });

  final SegmentoEstadistica segmento;
  final int total;

  @override
  Widget build(BuildContext context) {
    final porcentaje =
        total <= 0
            ? 0.0
            : segmento.valor /
                total *
                100;

    return Padding(
      padding:
          const EdgeInsets.symmetric(
        vertical: 7,
      ),
      child: Row(
        children: [
          Container(
            width: 13,
            height: 13,
            decoration: BoxDecoration(
              color: segmento.color,
              borderRadius:
                  BorderRadius.circular(4),
            ),
          ),
          const SizedBox(
            width: 11,
          ),
          Expanded(
            child: Text(
              segmento.nombre,
              style: const TextStyle(
                color:
                    Color(0xFF493D3B),
                fontWeight:
                    FontWeight.w700,
              ),
            ),
          ),
          Text(
            '${segmento.valor} · '
            '${porcentaje.toStringAsFixed(1)}%',
            style: const TextStyle(
              color:
                  Color(0xFF746765),
              fontSize: 13,
            ),
          ),
        ],
      ),
    );
  }
}

class TarjetaIngresos
    extends StatelessWidget {
  const TarjetaIngresos({
    required this.estadisticas,
    required this.esCompacto,
    super.key,
  });

  final Estadisticas estadisticas;
  final bool esCompacto;

  @override
  Widget build(BuildContext context) {
    final descripcion = Column(
      crossAxisAlignment:
          CrossAxisAlignment.start,
      children: [
        const Text(
          'CITAS COMPLETADAS',
          style: TextStyle(
            color:
                Color(0xFFEAD7D2),
            fontSize: 12,
            fontWeight:
                FontWeight.w800,
            letterSpacing: 1.4,
          ),
        ),
        const SizedBox(
          height: 8,
        ),
        const Text(
          'Ingresos acumulados',
          style: TextStyle(
            color: Colors.white,
            fontFamily: 'Georgia',
            fontSize: 28,
            fontWeight: FontWeight.w500,
          ),
        ),
        const SizedBox(
          height: 8,
        ),
        Text(
          '${estadisticas.completadas} citas completadas',
          style: const TextStyle(
            color:
                Color(0xFFF1E4E0),
            fontSize: 14,
          ),
        ),
      ],
    );

    final cantidad = Text(
      formatearDinero(
        estadisticas.ingresosTotales,
      ),
      style: TextStyle(
        color: Colors.white,
        fontSize:
            esCompacto ? 31 : 42,
        fontWeight: FontWeight.w800,
      ),
    );

    return Container(
      width: double.infinity,
      padding:
          const EdgeInsets.all(28),
      decoration: BoxDecoration(
        color:
            const Color(0xFF2C2423),
        borderRadius:
            BorderRadius.circular(24),
        boxShadow: const [
          BoxShadow(
            color:
                Color(0x2A2C2423),
            blurRadius: 30,
            offset: Offset(0, 14),
          ),
        ],
      ),
      child: esCompacto
          ? Column(
              crossAxisAlignment:
                  CrossAxisAlignment.start,
              children: [
                descripcion,
                const SizedBox(
                  height: 20,
                ),
                cantidad,
              ],
            )
          : Row(
              mainAxisAlignment:
                  MainAxisAlignment
                      .spaceBetween,
              children: [
                descripcion,
                const SizedBox(
                  width: 24,
                ),
                cantidad,
              ],
            ),
    );
  }
}

class EstadoCargando
    extends StatelessWidget {
  const EstadoCargando({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Column(
        mainAxisSize:
            MainAxisSize.min,
        children: [
          CircularProgressIndicator(
            color:
                Color(0xFF6E4E4A),
          ),
          SizedBox(
            height: 16,
          ),
          Text(
            'Cargando estadísticas...',
            style: TextStyle(
              color:
                  Color(0xFF746765),
            ),
          ),
        ],
      ),
    );
  }
}

class EstadoError
    extends StatelessWidget {
  const EstadoError({
    required this.mensaje,
    required this.onReintentar,
    super.key,
  });

  final String mensaje;
  final Future<void> Function()
      onReintentar;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        width: 440,
        margin:
            const EdgeInsets.all(24),
        padding:
            const EdgeInsets.all(28),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius:
              BorderRadius.circular(24),
          border: Border.all(
            color:
                const Color(0xFFE3D7D4),
          ),
        ),
        child: Column(
          mainAxisSize:
              MainAxisSize.min,
          children: [
            const Icon(
              Icons.error_outline_rounded,
              color:
                  Color(0xFF9A4E43),
              size: 42,
            ),
            const SizedBox(
              height: 14,
            ),
            const Text(
              'No fue posible cargar el widget',
              textAlign:
                  TextAlign.center,
              style: TextStyle(
                color:
                    Color(0xFF2B2424),
                fontFamily:
                    'Georgia',
                fontSize: 24,
              ),
            ),
            const SizedBox(
              height: 10,
            ),
            Text(
              mensaje,
              textAlign:
                  TextAlign.center,
              style: const TextStyle(
                color:
                    Color(0xFF746765),
                height: 1.45,
              ),
            ),
            const SizedBox(
              height: 20,
            ),
            FilledButton.icon(
              onPressed: onReintentar,
              icon: const Icon(
                Icons.refresh_rounded,
              ),
              label: const Text(
                'Reintentar',
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class GraficaCircularPainter
    extends CustomPainter {
  GraficaCircularPainter({
    required this.segmentos,
  });

  final List<SegmentoEstadistica>
      segmentos;

  @override
  void paint(
    Canvas canvas,
    Size size,
  ) {
    final centro = Offset(
      size.width / 2,
      size.height / 2,
    );

    final radio =
        math.min(
              size.width,
              size.height,
            ) /
            2 -
            18;

    final rectangulo =
        Rect.fromCircle(
      center: centro,
      radius: radio,
    );

    final total = segmentos.fold<int>(
      0,
      (
        suma,
        segmento,
      ) =>
          suma + segmento.valor,
    );

    final pinturaFondo = Paint()
      ..color =
          const Color(0xFFE9DEDB)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 24
      ..strokeCap = StrokeCap.round;

    canvas.drawCircle(
      centro,
      radio,
      pinturaFondo,
    );

    if (total <= 0) {
      return;
    }

    var anguloInicial =
        -math.pi / 2;

    for (final segmento
        in segmentos) {
      if (segmento.valor <= 0) {
        continue;
      }

      final angulo =
          segmento.valor /
              total *
              math.pi *
              2;

      final pintura = Paint()
        ..color = segmento.color
        ..style =
            PaintingStyle.stroke
        ..strokeWidth = 24
        ..strokeCap =
            StrokeCap.butt;

      canvas.drawArc(
        rectangulo,
        anguloInicial,
        angulo,
        false,
        pintura,
      );

      anguloInicial += angulo;
    }
  }

  @override
  bool shouldRepaint(
    covariant GraficaCircularPainter
        oldDelegate,
  ) {
    return oldDelegate.segmentos !=
        segmentos;
  }
}

class DatoResumen {
  const DatoResumen({
    required this.titulo,
    required this.valor,
    required this.icono,
    this.destacado = false,
  });

  final String titulo;
  final int valor;
  final IconData icono;
  final bool destacado;
}

class SegmentoEstadistica {
  const SegmentoEstadistica({
    required this.nombre,
    required this.valor,
    required this.color,
  });

  final String nombre;
  final int valor;
  final Color color;
}

class Estadisticas {
  const Estadisticas({
    required this.totalCitas,
    required this.pendientes,
    required this.confirmadas,
    required this.completadas,
    required this.canceladas,
    required this.porReagendar,
    required this.ingresosTotales,
  });

  factory Estadisticas.fromJson(
    Map<String, dynamic> json,
  ) {
    return Estadisticas(
      totalCitas:
          convertirEntero(
        json['totalCitas'],
      ),
      pendientes:
          convertirEntero(
        json['pendientes'],
      ),
      confirmadas:
          convertirEntero(
        json['confirmadas'],
      ),
      completadas:
          convertirEntero(
        json['completadas'],
      ),
      canceladas:
          convertirEntero(
        json['canceladas'],
      ),
      porReagendar:
          convertirEntero(
        json['porReagendar'],
      ),
      ingresosTotales:
          convertirDecimal(
        json['ingresosTotales'],
      ),
    );
  }

  final int totalCitas;
  final int pendientes;
  final int confirmadas;
  final int completadas;
  final int canceladas;
  final int porReagendar;
  final double ingresosTotales;

  List<SegmentoEstadistica>
      get segmentos => [
        SegmentoEstadistica(
          nombre: 'Pendientes',
          valor: pendientes,
          color:
              const Color(0xFFC69B6D),
        ),
        SegmentoEstadistica(
          nombre: 'Confirmadas',
          valor: confirmadas,
          color:
              const Color(0xFF59705A),
        ),
        SegmentoEstadistica(
          nombre: 'Completadas',
          valor: completadas,
          color:
              const Color(0xFF7A554F),
        ),
        SegmentoEstadistica(
          nombre: 'Canceladas',
          valor: canceladas,
          color:
              const Color(0xFF9A4E43),
        ),
        SegmentoEstadistica(
          nombre: 'Por reagendar',
          valor: porReagendar,
          color:
              const Color(0xFFB7849A),
        ),
      ];
}

int convertirEntero(
  dynamic valor,
) {
  if (valor is int) {
    return valor;
  }

  if (valor is num) {
    return valor.toInt();
  }

  return int.tryParse(
        valor?.toString() ?? '',
      ) ??
      0;
}

double convertirDecimal(
  dynamic valor,
) {
  if (valor is num) {
    return valor.toDouble();
  }

  return double.tryParse(
        valor?.toString() ?? '',
      ) ??
      0;
}

String formatearDinero(
  double cantidad,
) {
  final texto =
      cantidad.toStringAsFixed(2);

  final partes =
      texto.split('.');

  final entero =
      partes.first;

  final decimal =
      partes.length > 1
          ? partes.last
          : '00';

  final caracteres =
      entero.split('').reversed.toList();

  final grupos =
      <String>[];

  for (
    var indice = 0;
    indice < caracteres.length;
    indice += 3
  ) {
    final fin =
        math.min(
          indice + 3,
          caracteres.length,
        );

    grupos.add(
      caracteres
          .sublist(
            indice,
            fin,
          )
          .reversed
          .join(),
    );
  }

  return '\$${grupos.reversed.join(',')}.$decimal MXN';
}