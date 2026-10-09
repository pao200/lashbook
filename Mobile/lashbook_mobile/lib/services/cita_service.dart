import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;

import 'auth_service.dart';

class CitaService {
  static const String baseUrl =
      'https://lashbook-backend.onrender.com';

  final AuthService authService = AuthService();

  Future<List<Map<String, dynamic>>> obtenerTodas() async {
    final token =
        await authService.obtenerToken();

    if (token == null || token.isEmpty) {
      throw Exception(
        'No existe una sesión activa.',
      );
    }

    final url = Uri.parse(
      '$baseUrl/api/admin/citas',
    );

    try {
      final response = await http
          .get(
            url,
            headers: {
              'Accept': 'application/json',
              'Authorization':
                  'Bearer $token',
            },
          )
          .timeout(
            const Duration(seconds: 120),
          );

      if (response.statusCode == 401) {
        throw Exception(
          'La sesión ha expirado.',
        );
      }

      if (response.statusCode == 403) {
        throw Exception(
          'La cuenta no tiene permiso para consultar la agenda.',
        );
      }

      if (response.statusCode != 200) {
        throw Exception(
          'Error al consultar las citas: ${response.statusCode}',
        );
      }

      final data =
          jsonDecode(response.body);

      if (data is! List) {
        throw Exception(
          'La respuesta del servidor no contiene una lista de citas.',
        );
      }

      return data
          .map(
            (cita) =>
                Map<String, dynamic>.from(
              cita as Map,
            ),
          )
          .toList();
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en responder.',
      );
    }
  }

  Future<List<Map<String, dynamic>>>
      obtenerAgendaHoy() async {
    final citas = await obtenerTodas();

    final ahora = DateTime.now();

    final fechaHoy =
        '${ahora.year.toString().padLeft(4, '0')}-'
        '${ahora.month.toString().padLeft(2, '0')}-'
        '${ahora.day.toString().padLeft(2, '0')}';

    final citasHoy = citas.where(
      (cita) {
        return cita['fecha']?.toString() ==
            fechaHoy;
      },
    ).toList();

    citasHoy.sort(
      (a, b) {
        final horaA =
            a['hora']?.toString() ?? '';

        final horaB =
            b['hora']?.toString() ?? '';

        return horaA.compareTo(horaB);
      },
    );

    return citasHoy;
  }
}