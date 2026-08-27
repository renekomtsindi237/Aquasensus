import 'package:aquasensus_mobile/api/api_client.dart';
import 'package:aquasensus_mobile/ecrans/parcours.dart';
import 'package:aquasensus_mobile/session.dart';
import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:aquasensus_mobile/theme/tokens.dart';
import 'package:flutter/material.dart';

const apiBase = String.fromEnvironment('AQS_API', defaultValue: 'http://127.0.0.1:8080');

void main() {
  runApp(AquasensusApp(api: HttpApiClient(base: apiBase), file: FileLocale()));
}

class AquasensusApp extends StatefulWidget {
  const AquasensusApp({super.key, this.api, this.file});

  final ApiClient? api;
  final FileLocale? file;

  @override
  State<AquasensusApp> createState() => _AquasensusAppState();
}

class _AquasensusAppState extends State<AquasensusApp> {
  late final SessionTerrain session;

  @override
  void initState() {
    super.initState();
    session = SessionTerrain(
      api: widget.api ?? HttpApiClient(base: apiBase),
      file: widget.file ?? FileLocale(),
    );
  }

  @override
  void dispose() {
    session.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AqsScope(
      session: session,
      child: MaterialApp(
        title: 'AquaSensus',
        theme: AqsTheme.light(),
        darkTheme: AqsTheme.dark(),
        home: const EcranConnexion(),
      ),
    );
  }
}
