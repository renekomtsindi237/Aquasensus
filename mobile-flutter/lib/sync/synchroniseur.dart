import 'dart:io';

import 'package:aquasensus_mobile/api/api_client.dart';
import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:http/http.dart' as http;

/// Rejeu FIFO. Le serveur reste l’autorité (EF-99, SQ6).
class Synchroniseur {
  Synchroniseur({required this.api, required this.file});

  final ApiClient api;
  final FileLocale file;

  Future<void> rejouer() async {
    final attente = file.elements.where((e) => e.statut == StatutSync.enAttente).toList();
    for (final e in attente) {
      try {
        final res = await _envoyer(e);
        if (res.statusCode == 200 || res.statusCode == 201) {
          file.marquerEnvoye(e.id);
        } else if (res.statusCode == 422 || res.statusCode == 409) {
          file.marquerConflit(e.id, 'Le serveur a refusé la copie locale (autorité serveur).');
        }
      } on HorsLigneException {
        return;
      } on SocketException {
        return;
      } on http.ClientException {
        return;
      }
    }
  }

  Future<http.Response> _envoyer(ElementFile e) {
    final headers = {'X-Client-Request-Id': e.id};
    switch (e.type) {
      case 'SIGNALEMENT':
        return api.post('/api/v1/reports', headers: headers, body: e.corps);
      case 'COMPTE_RENDU':
        return api.put(
          '/api/v1/interventions/${e.corps['interventionId']}/report',
          headers: headers,
          body: {
            'diagnostic': e.corps['diagnostic'],
            'causeRacine': e.corps['causeRacine'],
            'actions': e.corps['actions'],
          },
        );
      case 'PIECE':
        return api.post(
          '/api/v1/interventions/${e.corps['interventionId']}/parts',
          headers: headers,
          body: {
            'reference': e.corps['reference'],
            'libelle': e.corps['libelle'],
            'quantite': e.corps['quantite'] ?? 1,
            'coutUnitaire': e.corps['coutUnitaire'] ?? 0,
          },
        );
      case 'TRANSITION':
        return api.post(
          '/api/v1/interventions/${e.corps['interventionId']}/transitions',
          headers: headers,
          body: {
            'cible': e.corps['cible'],
            'version': e.corps['version'] ?? 0,
            'diagnostic': e.corps['diagnostic'],
            'causeRacine': e.corps['causeRacine'],
            'actions': e.corps['actions'],
          },
        );
      default:
        return Future.value(http.Response('{"titre":"type inconnu"}', 400));
    }
  }
}
