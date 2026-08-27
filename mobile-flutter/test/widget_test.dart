import 'package:aquasensus_mobile/main.dart';
import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'helpers/fake_api_client.dart';

void main() {
  testWidgets('affiche l’écran de connexion sans volume saisi', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    expect(find.text('Connexion'), findsOneWidget);
    expect(find.textContaining('volume'), findsOneWidget);
    expect(find.text('Identifiant'), findsOneWidget);
    expect(find.text('Mot de passe'), findsOneWidget);
    expect(find.text('Litres'), findsNothing);
    expect(find.text('Bidons'), findsNothing);
  });

  testWidgets('SQ1 non passant : mot de passe incorrect (401)', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    await tester.enterText(find.byType(TextField).at(0), 'admin@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'mauvais');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.textContaining('incorrect'), findsOneWidget);
  });

  testWidgets('SQ1 non passant : compte verrouillé (423)', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    await tester.enterText(find.byType(TextField).at(0), 'verrouille@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'ChangeMoi!2026');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.textContaining('verrouillé'), findsOneWidget);
  });

  testWidgets('SQ1 MDP temporaire : pas de navigation', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    await tester.enterText(find.byType(TextField).at(0), 'tempo@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'ChangeMoi!2026');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.textContaining('temporaire'), findsOneWidget);
    expect(find.text('Connexion'), findsOneWidget);
  });

  testWidgets('SQ1 passant : ouvre le terrain', (tester) async {
    await tester.pumpWidget(AquasensusApp(api: FakeApiClient(), file: FileLocale()));
    await tester.enterText(find.byType(TextField).at(0), 'tech@aquasensus.local');
    await tester.enterText(find.byType(TextField).at(1), 'ChangeMoi!2026');
    await tester.tap(find.text('Entrer'));
    await tester.pumpAndSettle();
    expect(find.text('Terrain'), findsOneWidget);
    expect(find.textContaining('YDE-001'), findsOneWidget);
  });
}
