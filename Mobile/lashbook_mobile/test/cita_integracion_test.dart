
import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:http/testing.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:lashbook_mobile/services/cita_service.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('Practicas 13 y 14 - Integracion LashBook', () {
    setUp(() {
      SharedPreferences.setMockInitialValues({
        'token': 'jwt_prueba',
        'rol': 'LASHISTA',
      });
    });

    test('AUT-04 GET citas responde HTTP 200', () async {
      final cliente = MockClient((request) async {
        expect(request.method, 'GET');
        expect(request.url.path, '/api/admin/citas');
        expect(
          request.headers['Authorization'],
          'Bearer jwt_prueba',
        );

        return http.Response(
          jsonEncode([
            {
              'id': 'cita-001',
              'fecha': '2026-11-18',
              'hora': '10:00',
              'estado': 'PENDIENTE',
            },
          ]),
          200,
          headers: {
            'content-type': 'application/json',
          },
        );
      });

      final servicio = CitaService(
        clienteHttp: cliente,
      );

      final resultado = await servicio.obtenerTodas();

      expect(resultado.length, 1);
      expect(resultado.first['id'], 'cita-001');
      expect(resultado.first['estado'], 'PENDIENTE');

      cliente.close();
    });

    test('AUT-05 GET sin permiso devuelve error 401', () async {
      final cliente = MockClient((request) async {
        expect(request.url.path, '/api/admin/citas');

        return http.Response(
          '{"message":"No autorizado"}',
          401,
        );
      });

      final servicio = CitaService(
        clienteHttp: cliente,
      );

      await expectLater(
        servicio.obtenerTodas(),
        throwsA(
          predicate(
            (error) => error.toString().contains(
              'La sesión ha expirado',
            ),
          ),
        ),
      );

      cliente.close();
    });

    test('AUT-06 PATCH actualiza estado correctamente', () async {
      var cantidadPeticiones = 0;

      final cliente = MockClient((request) async {
        cantidadPeticiones++;

        expect(request.method, 'PATCH');

        expect(
          request.url.path,
          '/api/admin/citas/cita-001/estado',
        );

        expect(
          request.headers['Authorization'],
          'Bearer jwt_prueba',
        );

        final cuerpo = jsonDecode(request.body);

        expect(cuerpo['estado'], 'CONFIRMADA');

        return http.Response(
          jsonEncode({
            'id': 'cita-001',
            'estado': 'CONFIRMADA',
          }),
          200,
          headers: {
            'content-type': 'application/json',
          },
        );
      });

      final servicio = CitaService(
        clienteHttp: cliente,
      );

      final resultado = await servicio.cambiarEstado(
        citaId: 'cita-001',
        nuevoEstado: 'CONFIRMADA',
      );

      expect(resultado['estado'], 'CONFIRMADA');
      expect(cantidadPeticiones, 1);

      cliente.close();
    });

    test('INT-04 GET devuelve lista vacia', () async {
      final cliente = MockClient((request) async {
        return http.Response('[]', 200);
      });

      final servicio = CitaService(
        clienteHttp: cliente,
      );

      final resultado = await servicio.obtenerTodas();

      expect(resultado, isEmpty);

      cliente.close();
    });

    test('INT-05 GET rechaza formato JSON incorrecto', () async {
      final cliente = MockClient((request) async {
        return http.Response(
          '{"mensaje":"No es una lista"}',
          200,
        );
      });

      final servicio = CitaService(
        clienteHttp: cliente,
      );

      await expectLater(
        servicio.obtenerTodas(),
        throwsA(isA<Exception>()),
      );

      cliente.close();
    });

    test('INT-06 PATCH maneja error del servidor', () async {
      final cliente = MockClient((request) async {
        return http.Response(
          jsonEncode({
            'message': 'Error al actualizar la cita',
          }),
          500,
          headers: {
            'content-type': 'application/json',
          },
        );
      });

      final servicio = CitaService(
        clienteHttp: cliente,
      );

      await expectLater(
        servicio.cambiarEstado(
          citaId: 'cita-001',
          nuevoEstado: 'CONFIRMADA',
        ),
        throwsA(
          predicate(
            (error) => error.toString().contains(
              'Error al actualizar la cita',
            ),
          ),
        ),
      );

      cliente.close();
    });
  });
}
