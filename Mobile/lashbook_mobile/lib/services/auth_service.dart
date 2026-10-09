import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class AuthService {
  static const String baseUrl =
      'https://lashbook-backend.onrender.com';

  Future<Map<String, dynamic>> iniciarSesion({
    required String correo,
    required String password,
  }) async {
    final loginUrl = Uri.parse(
      '$baseUrl/api/auth/login',
    );

    try {
      final response = await http
          .post(
            loginUrl,
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json',
            },
            body: jsonEncode({
              'correo': correo,
              'password': password,
            }),
          )
          .timeout(
            const Duration(seconds: 120),
          );

      if (response.statusCode != 200) {
        throw Exception(
          'Correo o contraseña incorrectos.',
        );
      }

      final data =
          jsonDecode(response.body)
              as Map<String, dynamic>;

      final token =
          data['token']?.toString();

      if (token == null || token.isEmpty) {
        throw Exception(
          'El servidor no devolvió un token.',
        );
      }

      final usuario =
          data['usuario']
              as Map<String, dynamic>?;

      if (usuario == null) {
        throw Exception(
          'El servidor no devolvió la información del usuario.',
        );
      }

      final String rol =
          usuario['rol']
                  ?.toString()
                  .toUpperCase() ??
              '';

      if (rol != 'LASHISTA' &&
          rol != 'ROLE_LASHISTA') {
        throw Exception(
          'Esta aplicación es exclusiva para lashistas.',
        );
      }

      final prefs =
          await SharedPreferences.getInstance();

      await prefs.setString(
        'token',
        token,
      );

      await prefs.setString(
        'nombre',
        usuario['nombre']?.toString() ?? '',
      );

      await prefs.setString(
        'correo',
        usuario['correo']?.toString() ?? '',
      );

      await prefs.setString(
        'rol',
        rol,
      );

      return {
        'token': token,
        'perfil': usuario,
      };
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en responder. Intenta nuevamente.',
      );
    } catch (e) {
      if (e is Exception) {
        rethrow;
      }

      throw Exception(
        'No fue posible iniciar sesión.',
      );
    }
  }

  Future<Map<String, dynamic>> obtenerPerfil(
    String token,
  ) async {
    final url = Uri.parse(
      '$baseUrl/api/auth/me',
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

      if (response.statusCode != 200) {
        throw Exception(
          'No fue posible obtener el perfil.',
        );
      }

      return jsonDecode(response.body)
          as Map<String, dynamic>;
    } on TimeoutException {
      throw Exception(
        'El servidor está tardando en responder.',
      );
    }
  }

  Future<String?> obtenerToken() async {
    final prefs =
        await SharedPreferences.getInstance();

    return prefs.getString('token');
  }

  Future<String?> obtenerNombre() async {
    final prefs =
        await SharedPreferences.getInstance();

    return prefs.getString('nombre');
  }

  Future<String?> obtenerCorreo() async {
    final prefs =
        await SharedPreferences.getInstance();

    return prefs.getString('correo');
  }

  Future<String?> obtenerRol() async {
    final prefs =
        await SharedPreferences.getInstance();

    return prefs.getString('rol');
  }

  Future<void> cerrarSesion() async {
    final prefs =
        await SharedPreferences.getInstance();

    await prefs.remove('token');
    await prefs.remove('nombre');
    await prefs.remove('correo');
    await prefs.remove('rol');
  }
}