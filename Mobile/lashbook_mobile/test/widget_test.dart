import 'package:flutter_test/flutter_test.dart';
import 'package:lashbook_mobile/main.dart';

void main() {
  testWidgets(
    'LashBook Mobile inicia correctamente',
    (WidgetTester tester) async {
      await tester.pumpWidget(
        const LashBookApp(),
      );

      expect(
        find.text('LashBook'),
        findsOneWidget,
      );

      expect(
        find.text('Acceso para lashista'),
        findsOneWidget,
      );
    },
  );
}