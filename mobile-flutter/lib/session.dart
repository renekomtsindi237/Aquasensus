import 'package:aquasensus_mobile/api/api_client.dart';
import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:aquasensus_mobile/sync/synchroniseur.dart';
import 'package:flutter/material.dart';

class SessionTerrain extends ChangeNotifier {
  SessionTerrain({required this.api, required this.file})
      : sync = Synchroniseur(api: api, file: file) {
    file.addListener(notifyListeners);
  }

  final ApiClient api;
  final FileLocale file;
  final Synchroniseur sync;

  bool get horsLigne => !api.enLigne;

  void couperReseau() {
    api.enLigne = false;
    notifyListeners();
  }

  Future<void> retablirReseau() async {
    api.enLigne = true;
    notifyListeners();
    await sync.rejouer();
  }

  @override
  void dispose() {
    file.removeListener(notifyListeners);
    super.dispose();
  }
}

class AqsScope extends InheritedNotifier<SessionTerrain> {
  const AqsScope({super.key, required SessionTerrain session, required super.child})
      : super(notifier: session);

  static SessionTerrain of(BuildContext context) {
    final scope = context.dependOnInheritedWidgetOfExactType<AqsScope>();
    assert(scope != null, 'AqsScope manquant');
    return scope!.notifier!;
  }

  /// Sans s’abonner : à utiliser dans les callbacks (évite un rebuild qui efface l’état local).
  static SessionTerrain lire(BuildContext context) {
    final scope = context.getInheritedWidgetOfExactType<AqsScope>();
    assert(scope != null, 'AqsScope manquant');
    return scope!.notifier!;
  }
}
