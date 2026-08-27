import 'dart:convert';

import 'package:http/http.dart' as http;

/// Accès REST uniquement (DA-10) : aucune règle métier ici.
class HorsLigneException implements Exception {
  const HorsLigneException();
}

abstract class ApiClient {
  bool enLigne = true;
  String? jeton;

  Future<http.Response> get(String path);
  Future<http.Response> post(String path, {Map<String, String>? headers, Object? body});
  Future<http.Response> put(String path, {Map<String, String>? headers, Object? body});
}

class HttpApiClient implements ApiClient {
  HttpApiClient({this.base = 'http://127.0.0.1:8080', http.Client? httpClient})
      : _http = httpClient ?? http.Client();

  final String base;
  final http.Client _http;

  @override
  bool enLigne = true;

  @override
  String? jeton;

  Uri _uri(String path) => Uri.parse('$base$path');

  Map<String, String> _entetes([Map<String, String>? extra]) {
    return {
      'Content-Type': 'application/json',
      if (jeton != null) 'Authorization': 'Bearer $jeton',
      ...?extra,
    };
  }

  void _exigerReseau() {
    if (!enLigne) {
      throw const HorsLigneException();
    }
  }

  String _corps(Object? body) {
    if (body == null) {
      return '';
    }
    if (body is String) {
      return body;
    }
    return jsonEncode(body);
  }

  @override
  Future<http.Response> get(String path) async {
    _exigerReseau();
    return _http.get(_uri(path), headers: _entetes());
  }

  @override
  Future<http.Response> post(String path, {Map<String, String>? headers, Object? body}) async {
    _exigerReseau();
    return _http.post(_uri(path), headers: _entetes(headers), body: _corps(body));
  }

  @override
  Future<http.Response> put(String path, {Map<String, String>? headers, Object? body}) async {
    _exigerReseau();
    return _http.put(_uri(path), headers: _entetes(headers), body: _corps(body));
  }
}
