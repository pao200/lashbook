import 'package:flutter/material.dart';

import '../../services/api_service.dart';
import '../../widgets/lashista_bottom_nav.dart';

class ServiciosScreen
    extends StatefulWidget {
  const ServiciosScreen({
    super.key,
  });

  @override
  State<ServiciosScreen> createState() =>
      _ServiciosScreenState();
}

class _ServiciosScreenState
    extends State<ServiciosScreen> {
  final ApiService apiService =
      ApiService();

  bool cargando = true;
  String? error;

  List<dynamic> servicios = [];

  @override
  void initState() {
    super.initState();
    cargarServicios();
  }

  Future<void> cargarServicios() async {
    setState(() {
      cargando = true;
      error = null;
    });

    try {
      final resultado =
          await apiService
              .consultarServicios();

      if (!mounted) return;

      setState(() {
        servicios = resultado;
        cargando = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() {
        cargando = false;
        error =
            'No fue posible cargar los servicios.';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title:
            const Text('Servicios'),
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
                        const EdgeInsets
                            .all(24),
                    child: Column(
                      mainAxisSize:
                          MainAxisSize.min,
                      children: [
                        const Icon(
                          Icons
                              .cloud_off_outlined,
                          size: 55,
                        ),

                        const SizedBox(
                          height: 16,
                        ),

                        Text(
                          error!,
                          textAlign:
                              TextAlign
                                  .center,
                        ),

                        const SizedBox(
                          height: 20,
                        ),

                        ElevatedButton(
                          onPressed:
                              cargarServicios,
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
                      cargarServicios,
                  child: ListView(
                    padding:
                        const EdgeInsets
                            .all(18),
                    children: [
                      const Text(
                        'Servicios disponibles',
                        style:
                            TextStyle(
                          fontSize: 26,
                          fontWeight:
                              FontWeight
                                  .bold,
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
                        'Información actualizada desde LashBook.',
                        style:
                            TextStyle(
                          color:
                              Colors
                                  .black54,
                        ),
                      ),

                      const SizedBox(
                        height: 22,
                      ),

                      ...servicios.map(
                        (servicio) {
                          final nombre =
                              servicio[
                                      'nombre']
                                  ?.toString() ??
                              'Servicio';

                          final descripcion =
                              servicio[
                                      'descripcion']
                                  ?.toString() ??
                              '';

                          final precio =
                              servicio[
                                      'precio']
                                  ?.toString() ??
                              '';

                          final duracion =
                              servicio[
                                      'duracionMinutos']
                                  ?.toString() ??
                              '';

                          final imagenUrl =
                              servicio[
                                      'imagenUrl']
                                  ?.toString() ??
                              '';

                          return Card(
                            clipBehavior:
                                Clip
                                    .antiAlias,
                            margin:
                                const EdgeInsets
                                    .only(
                              bottom: 20,
                            ),
                            child: Column(
                              crossAxisAlignment:
                                  CrossAxisAlignment
                                      .start,
                              children: [
                                if (imagenUrl
                                    .isNotEmpty)
                                  Image.network(
                                    imagenUrl,
                                    width:
                                        double
                                            .infinity,
                                    height:
                                        190,
                                    fit:
                                        BoxFit
                                            .cover,
                                    errorBuilder:
                                        (
                                      context,
                                      error,
                                      stackTrace,
                                    ) {
                                      return Container(
                                        height:
                                            190,
                                        width:
                                            double
                                                .infinity,
                                        color:
                                            Colors
                                                .black12,
                                        child:
                                            const Icon(
                                          Icons
                                              .image_not_supported_outlined,
                                          size:
                                              55,
                                        ),
                                      );
                                    },
                                  ),

                                Padding(
                                  padding:
                                      const EdgeInsets
                                          .all(
                                    18,
                                  ),
                                  child:
                                      Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment
                                            .start,
                                    children: [
                                      Text(
                                        nombre,
                                        style:
                                            const TextStyle(
                                          fontSize:
                                              21,
                                          fontWeight:
                                              FontWeight
                                                  .bold,
                                        ),
                                      ),

                                      if (descripcion
                                          .isNotEmpty) ...[
                                        const SizedBox(
                                          height:
                                              8,
                                        ),
                                        Text(
                                          descripcion,
                                          style:
                                              const TextStyle(
                                            color:
                                                Colors
                                                    .black54,
                                          ),
                                        ),
                                      ],

                                      const SizedBox(
                                        height:
                                            14,
                                      ),

                                      Row(
                                        children: [
                                          const Icon(
                                            Icons
                                                .payments_outlined,
                                            size:
                                                20,
                                            color:
                                                Color(
                                              0xFF7A514B,
                                            ),
                                          ),

                                          const SizedBox(
                                            width:
                                                6,
                                          ),

                                          Text(
                                            '\$$precio',
                                            style:
                                                const TextStyle(
                                              fontSize:
                                                  18,
                                              fontWeight:
                                                  FontWeight.bold,
                                            ),
                                          ),

                                          const Spacer(),

                                          if (duracion
                                              .isNotEmpty) ...[
                                            const Icon(
                                              Icons
                                                  .schedule,
                                              size:
                                                  19,
                                            ),
                                            const SizedBox(
                                              width:
                                                  5,
                                            ),
                                            Text(
                                              '$duracion min',
                                            ),
                                          ],
                                        ],
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ],
                  ),
                ),

      bottomNavigationBar:
          const LashistaBottomNav(
        currentIndex: 2,
      ),
    );
  }
}