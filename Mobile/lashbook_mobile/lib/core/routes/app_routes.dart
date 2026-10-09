import 'package:flutter/material.dart';
import '../../screens/conexion_screen.dart';
import '../../screens/welcome_screen.dart';
import '../../screens/setup_complete_screen.dart';
import '../../screens/login_screen.dart';
import '../../screens/lashista/home_lashista_screen.dart';
import '../../screens/lashista/agenda_screen.dart';
import '../../screens/lashista/perfil_screen.dart';
import '../../screens/lashista/servicios_screen.dart';

class AppRoutes {
  static const String welcome = '/';
  static const String setupComplete = '/setup-complete';
  static const String conexion = '/conexion';
  static const String login = '/login';
  static const String homeLashista = '/home-lashista';
  static const String agenda = '/agenda';
  static const String perfil = '/perfil';
  static const String servicios = '/servicios';
  static Map<String, WidgetBuilder> get routes {
    return {
      welcome: (context) => const WelcomeScreen(),
      setupComplete: (context) => const SetupCompleteScreen(),
      login: (context) => const LoginScreen(),
      homeLashista: (context) => const HomeLashistaScreen(),
      agenda: (context) => const AgendaScreen(),
      perfil: (context) => const PerfilScreen(),
      conexion: (context) => const ConexionScreen(),
      servicios: (context) =>const ServiciosScreen(),
    };
  }
}