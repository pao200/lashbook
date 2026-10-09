import 'package:flutter/material.dart';

import '../services/api_service.dart';

class ConexionScreen extends StatefulWidget {
  const ConexionScreen({super.key});

  @override
  State<ConexionScreen> createState() =>
      _ConexionScreenState();
}

class _ConexionScreenState
    extends State<ConexionScreen> {
  final ApiService apiService = ApiService();

  bool consultando = false;

  String respuesta =
      'Presiona CONSULTAR para comprobar la conexión.';

  Future<void> consultar() async {
    setState(() {
      consultando = true;
      respuesta = 'Consultando...';
    });

    try {
      final resultado =
          await apiService.consultarBackend();

      if (!mounted) return;

      setState(() {
        respuesta = resultado;
        consultando = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() {
        respuesta =
            'No fue posible obtener la información.';
        consultando = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Conexión con LashBook',
        ),
      ),

      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment:
              CrossAxisAlignment.stretch,
          children: [
            const SizedBox(height: 30),

            const Icon(
              Icons.cloud_outlined,
              size: 75,
              color: Color(0xFF7A514B),
            ),

            const SizedBox(height: 20),

            const Text(
              'Consulta de información',
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 26,
                fontWeight: FontWeight.bold,
              ),
            ),

            const SizedBox(height: 10),

            const Text(
              'Prueba de comunicación entre LashBook Mobile y el backend.',
              textAlign: TextAlign.center,
              style: TextStyle(
                color: Colors.black54,
              ),
            ),

            const SizedBox(height: 30),

            ElevatedButton(
              onPressed:
                  consultando ? null : consultar,
              child: const Text(
                'CONSULTAR',
              ),
            ),

            const SizedBox(height: 25),

            if (consultando)
              const Center(
                child: Column(
                  children: [
                    CircularProgressIndicator(),

                    SizedBox(height: 12),

                    Text(
                      'Consultando...',
                    ),
                  ],
                ),
              ),

            if (!consultando)
              Container(
                padding:
                    const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  border: Border.all(
                    color: Colors.black12,
                  ),
                  borderRadius:
                      BorderRadius.circular(14),
                ),
                child: Text(
                  respuesta,
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontSize: 16,
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}