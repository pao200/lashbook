
import 'dart:async';
import 'dart:convert';
import 'package:http_parser/http_parser.dart';
import 'package:http/http.dart' as http;

import 'auth_service.dart';

class CitaService {
  static const String baseUrl =
      'https://lashbook-backend.onrender.com';

  final AuthService authService;
  final http.Client? clienteHttp;

  CitaService({
    AuthService? authService,
    this.clienteHttp,
  }) : authService = authService ?? AuthService();

  Future<List<Map<String, dynamic>>>
      obtenerTodas() async {
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
      // CAMBIO PRACTICAS 13 Y 14:
      // Permite usar MockClient durante las pruebas.
      // En la aplicacion sigue usando http.get.
      final headers = {
        'Accept': 'application/json',
        'Authorization': 'Bearer $token',
      };

      final response = await (
        clienteHttp?.get(
          url,
          headers: headers,
        ) ??
        http.get(
          url,
          headers: headers,
        )
      ).timeout(
        const Duration(
          seconds: 120,
        ),
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
          jsonDecode(
        response.body,
      );

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
    final citas =
        await obtenerTodas();

    final ahora =
        DateTime.now();

    final fechaHoy =
        '${ahora.year.toString().padLeft(4, '0')}-'
        '${ahora.month.toString().padLeft(2, '0')}-'
        '${ahora.day.toString().padLeft(2, '0')}';

    final citasHoy =
        citas.where(
      (cita) {
        return cita['fecha']
                ?.toString() ==
            fechaHoy;
      },
    ).toList();

    citasHoy.sort(
      (a, b) {
        final horaA =
            a['hora']
                    ?.toString() ??
                '';

        final horaB =
            b['hora']
                    ?.toString() ??
                '';

        return horaA.compareTo(
          horaB,
        );
      },
    );

    return citasHoy;
  }

  Future<Map<String, dynamic>>
      cambiarEstado({
    required String citaId,
    required String nuevoEstado,
  }) async {
    final token =
        await authService.obtenerToken();

    if (token == null || token.isEmpty) {
      throw Exception(
        'No existe una sesión activa.',
      );
    }

    final url = Uri.parse(
      '$baseUrl/api/admin/citas/$citaId/estado',
    );

    try {
      // CAMBIO PRACTICAS 13 Y 14:
      // Permite simular PATCH con MockClient.
      // En la aplicacion sigue usando http.patch.
      final headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      };

      final cuerpo = jsonEncode({
        'estado': nuevoEstado,
      });

      final response = await (
        clienteHttp?.patch(
          url,
          headers: headers,
          body: cuerpo,
        ) ??
        http.patch(
          url,
          headers: headers,
          body: cuerpo,
        )
      ).timeout(
        const Duration(
          seconds: 120,
        ),
      );

      if (response.statusCode == 401) {
        throw Exception(
          'La sesión ha expirado.',
        );
      }

      if (response.statusCode == 403) {
        throw Exception(
          'No tienes permiso para modificar esta cita.',
        );
      }

      if (response.statusCode != 200) {
        String mensaje =
            'No se pudo actualizar la cita.';

        try {
          final data =
              jsonDecode(
            response.body,
          );

          if (data is Map &&
              data['message'] != null) {
            mensaje =
                data['message']
                    .toString();
          }
        } catch (_) {}

        throw Exception(
          mensaje,
        );
      }

      final data =
          jsonDecode(
        response.body,
      );

      return Map<String, dynamic>.from(
        data as Map,
      );
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en responder.',
      );
    }
  }

  // ==================================================
  // 2.6 SUBIR FOTOGRAFÍA DEL RESULTADO
  // ==================================================

  Future<Map<String, dynamic>>
      subirFotoResultado({
    required String citaId,
    required String rutaFoto,
  }) async {
    final token =
        await authService.obtenerToken();

    if (token == null || token.isEmpty) {
      throw Exception(
        'No existe una sesión activa.',
      );
    }

    final url = Uri.parse(
      '$baseUrl/api/admin/citas/$citaId/foto',
    );

    try {
      final request =
          http.MultipartRequest(
        'POST',
        url,
      );

      request.headers.addAll(
        {
          'Accept':
              'application/json',
          'Authorization':
              'Bearer $token',
        },
      );

      request.files.add(
        await http.MultipartFile.fromPath(
          'foto',
          rutaFoto,
          contentType: MediaType(
            'image',
            'jpeg',
          ),
        ),
      );

      final streamedResponse =
          await request
              .send()
              .timeout(
                const Duration(
                  seconds: 120,
                ),
              );

      final response =
          await http.Response
              .fromStream(
        streamedResponse,
      );

      if (response.statusCode == 401) {
        throw Exception(
          'La sesión ha expirado.',
        );
      }

      if (response.statusCode == 403) {
        throw Exception(
          'No tienes permiso para guardar fotografías de esta cita.',
        );
      }

      if (response.statusCode != 200) {
        String mensaje =
            'No se pudo guardar la fotografía.';

        try {
          final data =
              jsonDecode(
            response.body,
          );

          if (data is Map &&
              data['message'] != null) {
            mensaje =
                data['message']
                    .toString();
          }
        } catch (_) {}

        throw Exception(
          mensaje,
        );
      }

      final data =
          jsonDecode(
        response.body,
      );

      return Map<String, dynamic>.from(
        data as Map,
      );
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en guardar la fotografía.',
      );
    }
  }
}
