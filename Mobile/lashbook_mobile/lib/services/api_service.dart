import 'dart:convert';

import 'package:http/http.dart' as http;

class ApiService {
  static const String baseUrl =
      'https://lashbook-backend.onrender.com';

  Future<String> consultarBackend() async {
    final url = Uri.parse(
      '$baseUrl/actuator/health',
    );

    try {
      final response = await http
          .get(url)
          .timeout(
            const Duration(seconds: 60),
          );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);

        return 'Conexión exitosa con LashBook\n\n'
            'Estado del servidor: ${data['status']}';
      }

      throw Exception(
        'Código HTTP: ${response.statusCode}',
      );
    } catch (e) {
      throw Exception(
        'No fue posible obtener la información.',
      );
    }
  }

  Future<List<dynamic>> consultarServicios() async {
    final url = Uri.parse(
      '$baseUrl/api/servicios',
    );

    try {
      final response = await http
          .get(
            url,
           headers: {
            'Accept':'application/json',
           },
          )
          .timeout(
          
            const Duration(seconds: 60),
          );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);

        if (data is List) {
          return data;
        }

        throw Exception(
          'La respuesta no contiene una lista de servicios.',
        );
      }

      throw Exception(
        'Error HTTP: ${response.statusCode}',
      );
    } catch (e) {
      throw Exception(
        'No fue posible consultar los servicios.',
      );
    }
  }
}