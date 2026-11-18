import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;

import 'auth_service.dart';

class SoporteService {
  static const String baseUrl =
      'https://lashbook-backend.onrender.com';

  final AuthService authService = AuthService();

  // ==================================================
  // UTILIDADES
  // ==================================================

  Future<String> _obtenerToken() async {
    final token = await authService.obtenerToken();

    if (token == null || token.trim().isEmpty) {
      throw Exception(
        'No existe una sesión activa.',
      );
    }

    return token;
  }

  Map<String, String> _headers(
    String token, {
    bool json = false,
  }) {
    return {
      'Accept': 'application/json',
      'Authorization': 'Bearer $token',
      if (json)
        'Content-Type': 'application/json',
    };
  }

  String _obtenerMensajeError(
    http.Response response,
    String mensajePorDefecto,
  ) {
    try {
      if (response.body.trim().isEmpty) {
        return mensajePorDefecto;
      }

      final data = jsonDecode(
        response.body,
      );

      if (data is Map) {
        if (data['message'] != null) {
          return data['message'].toString();
        }

        if (data['mensaje'] != null) {
          return data['mensaje'].toString();
        }

        if (data['error'] != null) {
          return data['error'].toString();
        }
      }
    } catch (_) {
      // Si el backend no devuelve JSON,
      // utilizamos el mensaje por defecto.
    }

    return mensajePorDefecto;
  }

  void _validarRespuesta(
    http.Response response, {
    required String mensajePorDefecto,
  }) {
    if (response.statusCode == 401) {
      throw Exception(
        'La sesión ha expirado.',
      );
    }

    if (response.statusCode == 403) {
      throw Exception(
        'No tienes permiso para realizar esta operación.',
      );
    }

    if (response.statusCode == 404) {
      throw Exception(
        _obtenerMensajeError(
          response,
          'La conversación no fue encontrada.',
        ),
      );
    }

    if (response.statusCode == 400) {
      throw Exception(
        _obtenerMensajeError(
          response,
          mensajePorDefecto,
        ),
      );
    }

    if (response.statusCode < 200 ||
        response.statusCode >= 300) {
      throw Exception(
        _obtenerMensajeError(
          response,
          mensajePorDefecto,
        ),
      );
    }
  }

  // ==================================================
  // LASHISTA - LISTAR CONVERSACIONES
  // ==================================================

  Future<List<Map<String, dynamic>>>
      obtenerConversaciones() async {
    final token = await _obtenerToken();

    final url = Uri.parse(
      '$baseUrl/api/soporte/admin/conversaciones',
    );

    try {
      final response = await http
          .get(
            url,
            headers: _headers(
              token,
            ),
          )
          .timeout(
            const Duration(
              seconds: 120,
            ),
          );

      _validarRespuesta(
        response,
        mensajePorDefecto:
            'No fue posible consultar las conversaciones de soporte.',
      );

      if (response.body.trim().isEmpty) {
        return [];
      }

      final data = jsonDecode(
        response.body,
      );

      if (data is! List) {
        throw Exception(
          'La respuesta del servidor no contiene una lista de conversaciones.',
        );
      }

      return data
          .map(
            (item) =>
                Map<String, dynamic>.from(
              item as Map,
            ),
          )
          .toList();
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en responder.',
      );
    }
  }

  // ==================================================
  // LASHISTA - FILTRAR POR ESTADO
  // ==================================================

  Future<List<Map<String, dynamic>>>
      obtenerConversacionesPorEstado(
    String estado,
  ) async {
    final token = await _obtenerToken();

    final estadoNormalizado =
        estado.trim().toUpperCase();

    final url = Uri.parse(
      '$baseUrl/api/soporte/admin/conversaciones/estado/$estadoNormalizado',
    );

    try {
      final response = await http
          .get(
            url,
            headers: _headers(
              token,
            ),
          )
          .timeout(
            const Duration(
              seconds: 120,
            ),
          );

      _validarRespuesta(
        response,
        mensajePorDefecto:
            'No fue posible consultar las conversaciones.',
      );

      if (response.body.trim().isEmpty) {
        return [];
      }

      final data = jsonDecode(
        response.body,
      );

      if (data is! List) {
        throw Exception(
          'La respuesta del servidor no contiene una lista de conversaciones.',
        );
      }

      return data
          .map(
            (item) =>
                Map<String, dynamic>.from(
              item as Map,
            ),
          )
          .toList();
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en responder.',
      );
    }
  }

  // ==================================================
  // CLIENTA / LASHISTA - DETALLE CONVERSACIÓN
  // ==================================================

  Future<Map<String, dynamic>>
      obtenerConversacion(
    dynamic conversacionId,
  ) async {
    final token = await _obtenerToken();

    final url = Uri.parse(
      '$baseUrl/api/soporte/conversaciones/$conversacionId',
    );

    try {
      final response = await http
          .get(
            url,
            headers: _headers(
              token,
            ),
          )
          .timeout(
            const Duration(
              seconds: 120,
            ),
          );

      _validarRespuesta(
        response,
        mensajePorDefecto:
            'No fue posible consultar la conversación.',
      );

      if (response.body.trim().isEmpty) {
        throw Exception(
          'El servidor no devolvió información de la conversación.',
        );
      }

      final data = jsonDecode(
        response.body,
      );

      if (data is! Map) {
        throw Exception(
          'La respuesta de la conversación no es válida.',
        );
      }

      return Map<String, dynamic>.from(
        data,
      );
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en responder.',
      );
    }
  }

  // ==================================================
  // CLIENTA / LASHISTA - ENVIAR MENSAJE
  // ==================================================

  Future<Map<String, dynamic>>
      enviarMensaje({
    required dynamic conversacionId,
    required String mensaje,
  }) async {
    final token = await _obtenerToken();

    final mensajeLimpio =
        mensaje.trim();

    if (mensajeLimpio.isEmpty) {
      throw Exception(
        'Escribe un mensaje antes de enviarlo.',
      );
    }

    final url = Uri.parse(
      '$baseUrl/api/soporte/conversaciones/$conversacionId/mensajes',
    );

    try {
      final response = await http
          .post(
            url,
            headers: _headers(
              token,
              json: true,
            ),
            body: jsonEncode(
              {
                'mensaje': mensajeLimpio,
              },
            ),
          )
          .timeout(
            const Duration(
              seconds: 120,
            ),
          );

      _validarRespuesta(
        response,
        mensajePorDefecto:
            'No fue posible enviar el mensaje.',
      );

      if (response.body.trim().isEmpty) {
        return {};
      }

      final data = jsonDecode(
        response.body,
      );

      if (data is! Map) {
        throw Exception(
          'La respuesta del servidor no es válida.',
        );
      }

      return Map<String, dynamic>.from(
        data,
      );
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en enviar el mensaje.',
      );
    }
  }

  // ==================================================
  // LASHISTA - CERRAR CONVERSACIÓN
  // ==================================================

  Future<Map<String, dynamic>>
      cerrarConversacion(
    dynamic conversacionId,
  ) async {
    final token = await _obtenerToken();

    final url = Uri.parse(
      '$baseUrl/api/soporte/admin/conversaciones/$conversacionId/cerrar',
    );

    try {
      final response = await http
          .put(
            url,
            headers: _headers(
              token,
              json: true,
            ),
          )
          .timeout(
            const Duration(
              seconds: 120,
            ),
          );

      _validarRespuesta(
        response,
        mensajePorDefecto:
            'No fue posible cerrar la conversación.',
      );

      if (response.body.trim().isEmpty) {
        return {};
      }

      final data = jsonDecode(
        response.body,
      );

      if (data is! Map) {
        throw Exception(
          'La respuesta del servidor no es válida.',
        );
      }

      return Map<String, dynamic>.from(
        data,
      );
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en cerrar la conversación.',
      );
    }
  }
}