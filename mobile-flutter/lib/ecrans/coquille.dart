import 'package:aquasensus_mobile/theme/tokens.dart';
import 'package:flutter/material.dart';

class EcranCoquille extends StatelessWidget {
  const EcranCoquille({
    super.key,
    required this.corps,
    required this.onSignaler,
    required this.onDeconnexion,
  });

  final Widget corps;
  final VoidCallback onSignaler;
  final VoidCallback onDeconnexion;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AqsColors.neutral50,
      appBar: AppBar(
        backgroundColor: AqsColors.chrome,
        foregroundColor: AqsColors.chromeText,
        title: const Text('AquaSensus'),
        actions: [
          TextButton(
            onPressed: onDeconnexion,
            child: Text('Déconnexion', style: TextStyle(color: AqsColors.chromeText)),
          ),
        ],
      ),
      drawer: Drawer(
        backgroundColor: AqsColors.chrome,
        child: SafeArea(
          child: ListView(
            padding: const EdgeInsets.all(AqsSpacing.s4),
            children: [
              Text('Menu', style: TextStyle(color: AqsColors.chromeText, fontWeight: FontWeight.w700)),
              ListTile(
                title: Text('Tableau de bord', style: TextStyle(color: AqsColors.chromeText)),
                onTap: () => Navigator.pop(context),
              ),
              ListTile(
                title: Text('Signaler', style: TextStyle(color: AqsColors.chromeText)),
                onTap: () {
                  Navigator.pop(context);
                  onSignaler();
                },
              ),
            ],
          ),
        ),
      ),
      body: corps,
    );
  }
}

class EcranDashUsager extends StatelessWidget {
  const EcranDashUsager({super.key, required this.onSignaler});

  final VoidCallback onSignaler;

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(AqsSpacing.s6),
      children: [
        Text('Mon quartier', style: AqsTypography.h2),
        const SizedBox(height: AqsSpacing.s2),
        Text('Aucun volume d’eau. Signalez si la pompe pose problème.', style: AqsTypography.bodySmall),
        const SizedBox(height: AqsSpacing.s6),
        FilledButton(onPressed: onSignaler, child: const Text('Signaler un incident')),
      ],
    );
  }
}

class EcranDashDelegue extends StatelessWidget {
  const EcranDashDelegue({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(AqsSpacing.s6),
      children: [
        Text('File du comité', style: AqsTypography.h2),
        const SizedBox(height: AqsSpacing.s2),
        Text('Qualifier et rétablir — pas le cockpit partenaire.', style: AqsTypography.bodySmall),
      ],
    );
  }
}

class EcranDashPartenaire extends StatelessWidget {
  const EcranDashPartenaire({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(AqsSpacing.s6),
      children: [
        Text('Pilotage association / mairie', style: AqsTypography.h2),
        const SizedBox(height: AqsSpacing.s2),
        Text('Temps de rétablissement. Aucun volume d’eau.', style: AqsTypography.bodySmall),
      ],
    );
  }
}

class EcranDashAdmin extends StatelessWidget {
  const EcranDashAdmin({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(AqsSpacing.s6),
      children: [
        Text('Pilotage plateforme', style: AqsTypography.h2),
        const SizedBox(height: AqsSpacing.s2),
        Text('Comptes, canal simulé, parc. Aucun volume d’eau.', style: AqsTypography.bodySmall),
      ],
    );
  }
}
