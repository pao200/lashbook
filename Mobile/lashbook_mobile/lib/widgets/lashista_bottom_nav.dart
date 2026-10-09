import 'package:flutter/material.dart';

import '../core/routes/app_routes.dart';

class LashistaBottomNav
    extends StatelessWidget {
  final int currentIndex;

  const LashistaBottomNav({
    super.key,
    required this.currentIndex,
  });

  void cambiarPantalla(
    BuildContext context,
    int index,
  ) {
    if (index == currentIndex) {
      return;
    }

    switch (index) {
      case 0:
        Navigator.pushReplacementNamed(
          context,
          AppRoutes.homeLashista,
        );
        break;

      case 1:
        Navigator.pushReplacementNamed(
          context,
          AppRoutes.agenda,
        );
        break;

      case 2:
        Navigator.pushReplacementNamed(
          context,
          AppRoutes.servicios,
        );
        break;

      case 3:
        Navigator.pushReplacementNamed(
          context,
          AppRoutes.perfil,
        );
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return NavigationBar(
      selectedIndex: currentIndex,
      onDestinationSelected: (index) {
        cambiarPantalla(
          context,
          index,
        );
      },
      destinations: const [
        NavigationDestination(
          icon:
              Icon(Icons.home_outlined),
          selectedIcon:
              Icon(Icons.home),
          label: 'Inicio',
        ),

        NavigationDestination(
          icon: Icon(
            Icons
                .calendar_month_outlined,
          ),
          selectedIcon: Icon(
            Icons.calendar_month,
          ),
          label: 'Agenda',
        ),

        NavigationDestination(
          icon: Icon(
            Icons
                .design_services_outlined,
          ),
          selectedIcon: Icon(
            Icons.design_services,
          ),
          label: 'Servicios',
        ),

        NavigationDestination(
          icon:
              Icon(Icons.person_outline),
          selectedIcon:
              Icon(Icons.person),
          label: 'Perfil',
        ),
      ],
    );
  }
}