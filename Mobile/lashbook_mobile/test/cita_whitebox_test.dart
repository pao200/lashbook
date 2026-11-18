
import 'package:flutter_test/flutter_test.dart';
import 'package:lashbook_mobile/services/cita_service.dart';

// Simula las citas del servidor sin realizar solicitudes HTTP.
class CitaServicePrueba extends CitaService {
  final List<Map<String, dynamic>> citasSimuladas;
  final bool provocarError;

  CitaServicePrueba(
    this.citasSimuladas, {
    this.provocarError = false,
  });

  @override
  Future<List<Map<String, dynamic>>> obtenerTodas() async {
    if (provocarError) {
      throw Exception('Error de conexión simulado');
    }

    return citasSimuladas;
  }
}

void main() {
  // Fecha actual en el mismo formato que utiliza LashBook.
  final ahora = DateTime.now();

  String fecha(DateTime valor) {
    return '${valor.year.toString().padLeft(4, '0')}-'
        '${valor.month.toString().padLeft(2, '0')}-'
        '${valor.day.toString().padLeft(2, '0')}';
  }

  final hoy = fecha(ahora);
  final ayer = fecha(ahora.subtract(const Duration(days: 1)));
  final manana = fecha(ahora.add(const Duration(days: 1)));

  group('Practica 11 - White Box Agenda LashBook', () {
    test('CITA-01 Lista vacía', () async {
      final servicio = CitaServicePrueba([]);

      final resultado = await servicio.obtenerAgendaHoy();

      expect(resultado, isEmpty);
    });

    test('CITA-02 Una cita de hoy', () async {
      final servicio = CitaServicePrueba([
        {'id': '1', 'fecha': hoy, 'hora': '10:00'},
      ]);

      final resultado = await servicio.obtenerAgendaHoy();

      expect(resultado.length, 1);
      expect(resultado.first['id'], '1');
    });

    test('CITA-03 Excluir cita de ayer', () async {
      final servicio = CitaServicePrueba([
        {'id': '1', 'fecha': ayer, 'hora': '10:00'},
      ]);

      final resultado = await servicio.obtenerAgendaHoy();

      expect(resultado, isEmpty);
    });

    test('CITA-04 Excluir cita de mañana', () async {
      final servicio = CitaServicePrueba([
        {'id': '1', 'fecha': manana, 'hora': '10:00'},
      ]);

      final resultado = await servicio.obtenerAgendaHoy();

      expect(resultado, isEmpty);
    });

    test('CITA-05 Ordenar citas por hora', () async {
      final servicio = CitaServicePrueba([
        {'id': '1', 'fecha': hoy, 'hora': '15:00'},
        {'id': '2', 'fecha': hoy, 'hora': '09:00'},
        {'id': '3', 'fecha': hoy, 'hora': '12:00'},
      ]);

      final resultado = await servicio.obtenerAgendaHoy();

      expect(
        resultado.map((cita) => cita['id']).toList(),
        ['2', '3', '1'],
      );
    });

    test('CITA-06 Filtrar fechas mezcladas', () async {
      final servicio = CitaServicePrueba([
        {'id': '1', 'fecha': ayer, 'hora': '08:00'},
        {'id': '2', 'fecha': hoy, 'hora': '10:00'},
        {'id': '3', 'fecha': manana, 'hora': '11:00'},
        {'id': '4', 'fecha': hoy, 'hora': '12:00'},
      ]);

      final resultado = await servicio.obtenerAgendaHoy();

      expect(resultado.length, 2);
      expect(
        resultado.map((cita) => cita['id']).toList(),
        ['2', '4'],
      );
    });

    test('CITA-07 Cita sin hora', () async {
      final servicio = CitaServicePrueba([
        {'id': '1', 'fecha': hoy, 'hora': '10:00'},
        {'id': '2', 'fecha': hoy},
      ]);

      final resultado = await servicio.obtenerAgendaHoy();

      expect(resultado.length, 2);
      expect(resultado.first['id'], '2');
    });

    test('CITA-08 Error de conexión simulado', () async {
      final servicio = CitaServicePrueba(
        [],
        provocarError: true,
      );

      await expectLater(
        servicio.obtenerAgendaHoy(),
        throwsException,
      );
    });
  });
}
