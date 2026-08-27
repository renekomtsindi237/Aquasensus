import 'dart:convert';

import 'package:aquasensus_mobile/api/api_client.dart';
import 'package:http/http.dart' as http;

/// Miroir des scénarios SQ1–SQ6 pour les tests widget (aucun backend).
class FakeApiClient implements ApiClient {
  @override
  bool enLigne = true;

  @override
  String? jeton;

  final Set<String> _dejaTraites = {};

  void _reseau() {
    if (!enLigne) {
      throw const HorsLigneException();
    }
  }

  @override
  Future<http.Response> get(String path) async {
    _reseau();
    if (path.endsWith('/water-points')) {
      return _json(200, {
        'elements': [
          {
            'id': 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
            'code': 'YDE-001',
            'nomUsage': 'Forage Nkolbisson Marché',
            'etat': 'OPERATIONNEL',
          }
        ],
      });
    }
    return _json(404, {'titre': 'Non mocké'});
  }

  @override
  Future<http.Response> post(String path, {Map<String, String>? headers, Object? body}) async {
    _reseau();
    final map = _map(body);
    final clientId = headers?['X-Client-Request-Id'] ?? '';

    if (path.endsWith('/auth/login')) {
      final identifiant = '${map['identifiant']}';
      final mdp = '${map['motDePasse']}';
      if (identifiant.contains('verrouille')) {
        return _json(423, {'titre': 'Compte verrouillé'});
      }
      if (mdp == 'mauvais' || mdp.isEmpty) {
        return _json(401, {'titre': 'Identifiants invalides'});
      }
      return _json(200, {
        'jetonAcces': 'e2e-mobile',
        'roles': identifiant.contains('admin') ? ['ADMIN'] : ['TECHNICIEN'],
        'doitChangerMotDePasse': identifiant.contains('tempo'),
      });
    }

    if (path.endsWith('/reports')) {
      return _signalement(map, clientId);
    }

    if (path.contains('/transitions') || path.contains('/parts')) {
      return _commandeTerrain(clientId);
    }

    return _json(404, {'titre': 'Non mocké'});
  }

  @override
  Future<http.Response> put(String path, {Map<String, String>? headers, Object? body}) async {
    _reseau();
    return _commandeTerrain(headers?['X-Client-Request-Id'] ?? '');
  }

  http.Response _signalement(Map<String, dynamic> b, String clientId) {
    if (clientId.isNotEmpty && _dejaTraites.contains(clientId)) {
      return _json(200, _prise('Signalement reçu. Le comité va en prendre connaissance.', 'YDE-REJEU'));
    }
    final code = '${b['pointEauCode']}';
    final otp = '${b['codeOtp']}';
    final tel = '${b['declarantTelephone']}';
    if (code == 'INCONNU') {
      return _json(404, {'titre': 'Ouvrage introuvable'});
    }
    if (otp.isNotEmpty && otp != '123456') {
      return _json(422, {'codeRegle': 'EF-11', 'titre': 'Code de confirmation incorrect.'});
    }
    if (tel.endsWith('9999')) {
      return _json(429, {'titre': 'Quota dépassé'});
    }
    if (clientId.isNotEmpty) {
      _dejaTraites.add(clientId);
    }
    final corroboration = code == 'YDE-DUP';
    final msg = corroboration
        ? 'Déjà signalé par 2 personne(s). Le comité a été averti.'
        : b['gravite'] == 'HAUTE'
            ? 'Signalement grave : le comité a été notifié.'
            : 'Signalement reçu. Le comité va en prendre connaissance.';
    return _json(code == 'YDE-REJEU' ? 200 : 201, _prise(msg, code));
  }

  Map<String, dynamic> _prise(String message, String code) => {
        'priseEnCharge': {'dejaSignale': code == 'YDE-DUP', 'interventionEnCours': false, 'message': message},
        'pointEau': {'code': code, 'etat': code == 'YDE-PANNE' ? 'EN_PANNE' : 'OPERATIONNEL'},
      };

  http.Response _commandeTerrain(String clientId) {
    if (clientId.startsWith('conflit')) {
      return _json(422, {'titre': 'Transition refusée (état serveur différent)'});
    }
    if (clientId.isNotEmpty && _dejaTraites.contains(clientId)) {
      return _json(200, {'statut': 'deja_traite'});
    }
    if (clientId.isNotEmpty) {
      _dejaTraites.add(clientId);
    }
    return _json(201, {'statut': 'applique'});
  }

  Map<String, dynamic> _map(Object? body) {
    if (body is Map<String, dynamic>) {
      return body;
    }
    if (body is String && body.isNotEmpty) {
      return jsonDecode(body) as Map<String, dynamic>;
    }
    return {};
  }

  http.Response _json(int status, Object corps) {
    return http.Response(jsonEncode(corps), status, headers: {'content-type': 'application/json'});
  }
}
