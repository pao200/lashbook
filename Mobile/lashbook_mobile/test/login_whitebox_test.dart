
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:lashbook_mobile/screens/login_screen.dart';

void main() {
  group('Practica 11 - White Box Login LashBook', () {

    // WB-01: Ambos campos vacios
    testWidgets('WB-01 Ambos campos vacios', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: LoginScreen()),
      );

      await tester.tap(find.text('Iniciar sesión'));
      await tester.pump();

      expect(
        find.text('Ingresa tu correo y contraseña.'),
        findsOneWidget,
      );
    });

    // WB-02: Correo vacio
    testWidgets('WB-02 Correo vacio', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: LoginScreen()),
      );

      await tester.enterText(
        find.byType(TextField).at(1),
        '123456',
      );

      await tester.tap(find.text('Iniciar sesión'));
      await tester.pump();

      expect(
        find.text('Ingresa tu correo y contraseña.'),
        findsOneWidget,
      );
    });

    // WB-03: Password vacio
    testWidgets('WB-03 Password vacio', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: LoginScreen()),
      );

      await tester.enterText(
        find.byType(TextField).at(0),
        'lashista@ejemplo.com',
      );

      await tester.tap(find.text('Iniciar sesión'));
      await tester.pump();

      expect(
        find.text('Ingresa tu correo y contraseña.'),
        findsOneWidget,
      );
    });

    // WB-04: Correo con espacios
    testWidgets('WB-04 Correo con espacios', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: LoginScreen()),
      );

      await tester.enterText(
        find.byType(TextField).at(0),
        '    ',
      );

      await tester.enterText(
        find.byType(TextField).at(1),
        '123456',
      );

      await tester.tap(find.text('Iniciar sesión'));
      await tester.pump();

      expect(
        find.text('Ingresa tu correo y contraseña.'),
        findsOneWidget,
      );
    });

    // WB-05: Visibilidad de contraseña
    testWidgets('WB-05 Mostrar y ocultar contraseña', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: LoginScreen()),
      );

      TextField passwordField =
          tester.widget<TextField>(
        find.byType(TextField).at(1),
      );

      expect(passwordField.obscureText, isTrue);

      await tester.tap(
        find.byIcon(Icons.visibility_off),
      );

      await tester.pump();

      passwordField = tester.widget<TextField>(
        find.byType(TextField).at(1),
      );

      expect(passwordField.obscureText, isFalse);

      await tester.tap(
        find.byIcon(Icons.visibility),
      );

      await tester.pump();

      passwordField = tester.widget<TextField>(
        find.byType(TextField).at(1),
      );

      expect(passwordField.obscureText, isTrue);
    });

  });
}
