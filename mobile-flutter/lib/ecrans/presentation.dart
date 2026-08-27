import 'package:aquasensus_mobile/ecrans/parcours.dart';
import 'package:aquasensus_mobile/theme/tokens.dart';
import 'package:flutter/material.dart';

class EcranPresentation extends StatelessWidget {
  const EcranPresentation({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AqsColors.neutral50,
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(AqsSpacing.s6),
          children: [
            Image.asset('assets/brand/aquasensus-logo.png', height: 64),
            const SizedBox(height: AqsSpacing.s8),
            Text(
              'Anticiper la panne, garder l’eau.',
              style: AqsTypography.h3.copyWith(color: AqsColors.earth700),
            ),
            const SizedBox(height: AqsSpacing.s4),
            Text('Le suivi partagé des forages communautaires', style: AqsTypography.h2),
            const SizedBox(height: AqsSpacing.s4),
            Text(
              'Signalez, coordonnez, anticipez. Aucun volume d’eau n’est relevé.',
              style: AqsTypography.bodySmall,
            ),
            const SizedBox(height: AqsSpacing.s8),
            FilledButton(
              onPressed: () {
                Navigator.of(context).push(MaterialPageRoute(builder: (_) => const EcranConnexion()));
              },
              child: const Text('Se connecter'),
            ),
            const SizedBox(height: AqsSpacing.s3),
            OutlinedButton(
              onPressed: () {
                Navigator.of(context).push(MaterialPageRoute(builder: (_) => const EcranConnexion()));
              },
              child: const Text('Créer un compte'),
            ),
            TextButton(
              onPressed: () {
                Navigator.of(context).push(MaterialPageRoute(builder: (_) => const EcranSignaler()));
              },
              child: const Text('Signaler sans compte'),
            ),
          ],
        ),
      ),
    );
  }
}
