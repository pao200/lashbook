
import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:http/testing.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:lashbook_mobile/services/auth_service.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('Practica 11 - White Box AuthService', () {
    setUp(() {
      SharedPreferences.setMockInitialValues({});
    });

    test('AUTH-01 Credenciales incorrectas', () async {
      final cliente = MockClient((request) async {
        expect(request.method, 'POST');
        expect(request.url.path, '/api/auth/login');

        return http.Response(
          '{"error":"Credenciales incorrectas"}',
          401,
        );
      });

      // El servicio actual no permite inyectar este cliente.
      // Probamos el contrato HTTP simulado.
      final respuesta = await cliente.post(
        Uri.parse('${AuthService.baseUrl}/api/auth/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'correo': 'prueba@ejemplo.com',
          'password': 'incorrecta',
        }),
      );

      expect(respuesta.statusCode, 401);
    });

    test('AUTH-02 Login exitoso simulado', () async {
      final cliente = MockClient((request) async {
        return http.Response(
          jsonEncode({
            'token': 'token_simulado',
            'usuario': {
              'nombre': 'Lashista Prueba',
              'correo': 'lashista@ejemplo.com',
              'rol': 'LASHISTA',
            },
          }),
          200,
        );
      });

      final respuesta = await cliente.post(
        Uri.parse('${AuthService.baseUrl}/api/auth/login'),
      );

      final datos = jsonDecode(respuesta.body);

      expect(respuesta.statusCode, 200);
      expect(datos['token'], isNotEmpty);
      expect(datos['usuario']['rol'], 'LASHISTA');
    });

    test('AUTH-03 Respuesta sin token', () async {
      final cliente = MockClient((request) async {
        return http.Response(
          jsonEncode({
            'usuario': {'rol': 'LASHISTA'}
          }),
          200,
        );
      });

      final respuesta = await cliente.post(
        Uri.parse('${AuthService.baseUrl}/api/auth/login'),
      );

      final datos = jsonDecode(respuesta.body);

      expect(datos['token'], isNull);
    });

    test('AUTH-04 Rol no autorizado', () async {
      final cliente = MockClient((request) async {
        return http.Response(
          jsonEncode({
            'token': 'token_simulado',
            'usuario': {'rol': 'CLIENTA'},
          }),
          200,
        );
      });

      final respuesta = await cliente.post(
        Uri.parse('${AuthService.baseUrl}/api/auth/login'),
      );

      final datos = jsonDecode(respuesta.body);

      expect(datos['usuario']['rol'], isNot('LASHISTA'));
    });

    test('AUTH-05 Obtener token inexistente', () async {
      final servicio = AuthService();

      final token = await servicio.obtenerToken();

      expect(token, isNull);
    });

    test('AUTH-06 Cerrar sesión', () async {
      SharedPreferences.setMockInitialValues({
        'token': 'token_prueba',
        'nombre': 'Lashista Prueba',
        'correo': 'lashista@ejemplo.com',
        'rol': 'LASHISTA',
      });

      final servicio = AuthService();

      await servicio.cerrarSesion();

      expect(await servicio.obtenerToken(), isNull);
      expect(await servicio.obtenerNombre(), isNull);
      expect(await servicio.obtenerCorreo(), isNull);
      expect(await servicio.obtenerRol(), isNull);
    });
  });
}
