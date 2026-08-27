import 'dart:convert';

import 'package:aquasensus_mobile/api/api_client.dart';
import 'package:aquasensus_mobile/ecrans/coquille.dart';
import 'package:aquasensus_mobile/ecrans/presentation.dart';
import 'package:aquasensus_mobile/session.dart';
import 'package:aquasensus_mobile/theme/tokens.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

Widget corpsAccueil(List<String> roles, BuildContext context) {
  void signaler() {
    Navigator.of(context).push(MaterialPageRoute(builder: (_) => const EcranSignaler()));
  }

  if (roles.contains('ADMIN')) {
    return const EcranDashAdmin();
  }
  if (roles.contains('DELEGUE')) {
    return const EcranDashDelegue();
  }
  if (roles.contains('PARTENAIRE')) {
    return const EcranDashPartenaire();
  }
  if (roles.contains('TECHNICIEN')) {
    return const EcranTerrain();
  }
  return EcranDashUsager(onSignaler: signaler);
}

Widget coquilleSession(SessionTerrain session, BuildContext context) {
  return EcranCoquille(
    corps: corpsAccueil(session.roles, context),
    onSignaler: () {
      Navigator.of(context).push(MaterialPageRoute(builder: (_) => const EcranSignaler()));
    },
    onDeconnexion: () {
      session.api.jeton = null;
      session.roles = const [];
      Navigator.of(context).pushAndRemoveUntil(
        MaterialPageRoute(builder: (_) => const EcranPresentation()),
        (_) => false,
      );
    },
  );
}

class EcranConnexion extends StatefulWidget {
  const EcranConnexion({super.key});

  @override
  State<EcranConnexion> createState() => _EcranConnexionState();
}

class _EcranConnexionState extends State<EcranConnexion> {
  final _identifiant = TextEditingController();
  final _motDePasse = TextEditingController();
  final _nom = TextEditingController();
  bool _inscription = false;
  String? _message;

  @override
  void dispose() {
    _identifiant.dispose();
    _motDePasse.dispose();
    _nom.dispose();
    super.dispose();
  }

  Future<void> _appliquerAuth(SessionTerrain session, Map<String, dynamic> corps) async {
    session.api.jeton = corps['jetonAcces'] as String?;
    session.roles = List<String>.from((corps['roles'] as List<dynamic>?) ?? const []);
    session.doitChangerMotDePasse = corps['doitChangerMotDePasse'] == true;
    if (!mounted) {
      return;
    }
    Navigator.of(context).pushReplacement(
      MaterialPageRoute(
        builder: (_) => session.doitChangerMotDePasse
            ? const EcranMotDePasse()
            : coquilleSession(session, context),
      ),
    );
  }

  Future<void> _entrer() async {
    final session = AqsScope.lire(context);
    setState(() => _message = null);
    try {
      final res = await session.api.post(
        '/api/v1/auth/login',
        body: {
          'identifiant': _identifiant.text,
          'motDePasse': _motDePasse.text,
        },
      );
      if (!mounted) {
        return;
      }
      if (res.statusCode == 423) {
        setState(() => _message = 'Compte verrouillé. Réessayez plus tard.');
        return;
      }
      if (res.statusCode != 200) {
        setState(() => _message = 'Identifiant ou mot de passe incorrect.');
        return;
      }
      await _appliquerAuth(session, jsonDecode(res.body) as Map<String, dynamic>);
    } on HorsLigneException {
      setState(() => _message = 'Hors ligne : connexion impossible.');
    }
  }

  Future<void> _inscrire() async {
    final session = AqsScope.lire(context);
    setState(() => _message = null);
    try {
      final res = await session.api.post(
        '/api/v1/auth/register',
        body: {
          'identifiant': _identifiant.text,
          'nomAffichage': _nom.text,
          'motDePasse': _motDePasse.text,
        },
      );
      if (!mounted) {
        return;
      }
      if (res.statusCode == 409) {
        setState(() => _message = 'Un compte existe déjà pour cet identifiant.');
        return;
      }
      if (res.statusCode != 201) {
        setState(() => _message = 'Inscription impossible (10 caractères min.).');
        return;
      }
      await _appliquerAuth(session, jsonDecode(res.body) as Map<String, dynamic>);
    } on HorsLigneException {
      setState(() => _message = 'Hors ligne : inscription impossible.');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(AqsSpacing.s6),
          child: ListView(
            children: [
              Image.asset('assets/brand/aquasensus-logo.png', height: 72),
              const SizedBox(height: AqsSpacing.s6),
              Text(_inscription ? 'Créer un compte usager' : 'Connexion', style: AqsTypography.h2),
              const SizedBox(height: AqsSpacing.s2),
              Text(
                'Suivi des forages — aucune saisie de volume d’eau.',
                style: AqsTypography.bodySmall,
              ),
              const SizedBox(height: AqsSpacing.s6),
              TextField(
                controller: _identifiant,
                decoration: const InputDecoration(labelText: 'Identifiant'),
              ),
              if (_inscription) ...[
                const SizedBox(height: AqsSpacing.s4),
                TextField(
                  controller: _nom,
                  decoration: const InputDecoration(labelText: 'Nom affiché'),
                ),
              ],
              const SizedBox(height: AqsSpacing.s4),
              TextField(
                controller: _motDePasse,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'Mot de passe'),
              ),
              const SizedBox(height: AqsSpacing.s6),
              FilledButton(
                onPressed: _inscription ? _inscrire : _entrer,
                child: Text(_inscription ? 'S’inscrire' : 'Entrer'),
              ),
              TextButton(
                onPressed: () => setState(() => _inscription = !_inscription),
                child: Text(_inscription ? 'Déjà un compte' : 'Créer un compte'),
              ),
              TextButton(
                onPressed: () {
                  Navigator.of(context).push(
                    MaterialPageRoute(builder: (_) => const EcranSignaler()),
                  );
                },
                child: const Text('Signaler sans compte'),
              ),
              if (_message != null)
                Padding(
                  padding: const EdgeInsets.only(top: AqsSpacing.s4),
                  child: Text(
                    _message!,
                    style: TextStyle(color: AqsColors.statePanne),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

class EcranMotDePasse extends StatefulWidget {
  const EcranMotDePasse({super.key});

  @override
  State<EcranMotDePasse> createState() => _EcranMotDePasseState();
}

class _EcranMotDePasseState extends State<EcranMotDePasse> {
  final _actuel = TextEditingController();
  final _nouveau = TextEditingController();
  String? _message;

  @override
  void dispose() {
    _actuel.dispose();
    _nouveau.dispose();
    super.dispose();
  }

  Future<void> _enregistrer() async {
    final session = AqsScope.lire(context);
    setState(() => _message = null);
    try {
      final res = await session.api.post(
        '/api/v1/auth/password/change',
        body: {'actuel': _actuel.text, 'nouveau': _nouveau.text},
      );
      if (!mounted) {
        return;
      }
      if (res.statusCode != 204 && res.statusCode != 200) {
        setState(() => _message = 'Changement impossible.');
        return;
      }
      session.doitChangerMotDePasse = false;
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => coquilleSession(session, context)),
      );
    } on HorsLigneException {
      setState(() => _message = 'Hors ligne : changement impossible.');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(AqsSpacing.s6),
          child: ListView(
            children: [
              Image.asset('assets/brand/aquasensus-logo.png', height: 72),
              const SizedBox(height: AqsSpacing.s6),
              Text('Changer le mot de passe', style: AqsTypography.h2),
              const SizedBox(height: AqsSpacing.s2),
              Text(
                'Mot de passe temporaire à remplacer (EF-83). Aucun volume d’eau.',
                style: AqsTypography.bodySmall,
              ),
              const SizedBox(height: AqsSpacing.s6),
              TextField(
                controller: _actuel,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'Mot de passe actuel'),
              ),
              const SizedBox(height: AqsSpacing.s4),
              TextField(
                controller: _nouveau,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'Nouveau mot de passe'),
              ),
              const SizedBox(height: AqsSpacing.s6),
              FilledButton(
                onPressed: _enregistrer,
                child: const Text('Enregistrer'),
              ),
              if (_message != null)
                Padding(
                  padding: const EdgeInsets.only(top: AqsSpacing.s4),
                  child: Text(_message!, style: TextStyle(color: AqsColors.statePanne)),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

class EcranTerrain extends StatefulWidget {
  const EcranTerrain({super.key});

  @override
  State<EcranTerrain> createState() => _EcranTerrainState();
}

class _EcranTerrainState extends State<EcranTerrain> {
  Future<http.Response>? _points;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _points ??= AqsScope.of(context).api.get('/api/v1/water-points');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Terrain')),
      body: Column(
        children: [
          const BandeauSync(),
          Expanded(
            child: FutureBuilder<http.Response>(
              future: _points,
              builder: (context, snap) {
                if (snap.hasError) {
                  return const Padding(
                    padding: EdgeInsets.all(AqsSpacing.s4),
                    child: Text('Points déjà en cache indisponibles hors ligne (EF-95).'),
                  );
                }
                if (!snap.hasData) {
                  return const Center(child: CircularProgressIndicator());
                }
                final list = jsonDecode(snap.data!.body)['elements'] as List<dynamic>;
                return ListView(
                  padding: const EdgeInsets.all(AqsSpacing.s4),
                  children: [
                    Text(
                      'Aucun volume d’eau. File et interventions du périmètre.',
                      style: AqsTypography.bodySmall,
                    ),
                    const SizedBox(height: AqsSpacing.s4),
                    FilledButton(
                      onPressed: () {
                        Navigator.of(context).push(
                          MaterialPageRoute(builder: (_) => const EcranSignaler()),
                        );
                      },
                      child: const Text('Signaler'),
                    ),
                    if (AqsScope.of(context).peutIntervention) ...[
                      const SizedBox(height: AqsSpacing.s3),
                      OutlinedButton(
                        key: const Key('btn-intervention'),
                        onPressed: () {
                          Navigator.of(context).push(
                            MaterialPageRoute(builder: (_) => const EcranIntervention()),
                          );
                        },
                        child: const Text('Intervention affectée'),
                      ),
                    ],
                    const SizedBox(height: AqsSpacing.s4),
                    for (final e in list)
                      ListTile(
                        title: Text('${e['code']} — ${e['nomUsage']}'),
                        subtitle: Text('${e['etat']} · aucun volume'),
                      ),
                  ],
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class BandeauSync extends StatelessWidget {
  const BandeauSync({super.key});

  @override
  Widget build(BuildContext context) {
    final session = AqsScope.of(context);
    return Column(
      children: [
        if (session.horsLigne)
          Container(
            width: double.infinity,
            color: AqsColors.neutral100,
            padding: const EdgeInsets.all(AqsSpacing.s3),
            child: Text(
              'Hors ligne — consultation des données déjà chargées. Aucun volume d’eau.',
              textAlign: TextAlign.center,
              style: TextStyle(color: AqsColors.feedbackOffline, fontWeight: FontWeight.w600),
            ),
          ),
        if (session.file.aEnvoyer > 0)
          Container(
            width: double.infinity,
            color: AqsColors.blue100,
            padding: const EdgeInsets.all(AqsSpacing.s3),
            child: Text(
              '${session.file.aEnvoyer} élément(s) à envoyer',
              textAlign: TextAlign.center,
              style: TextStyle(color: AqsColors.action, fontWeight: FontWeight.w600),
            ),
          ),
        if (session.file.conflit != null)
          Container(
            width: double.infinity,
            color: AqsColors.statePanneBg,
            padding: const EdgeInsets.all(AqsSpacing.s3),
            child: Text(
              'Conflit de synchronisation : le serveur fait autorité. ${session.file.conflit}',
              textAlign: TextAlign.center,
              style: TextStyle(color: AqsColors.statePanneText),
            ),
          ),
      ],
    );
  }
}

class EcranSignaler extends StatefulWidget {
  const EcranSignaler({super.key});

  @override
  State<EcranSignaler> createState() => _EcranSignalerState();
}

class _EcranSignalerState extends State<EcranSignaler> {
  final _code = TextEditingController();
  final _tel = TextEditingController();
  final _otp = TextEditingController(text: '123456');
  String _categorie = 'DEBIT_FAIBLE';
  String _gravite = 'MOYENNE';
  String? _statut;

  @override
  void dispose() {
    _code.dispose();
    _tel.dispose();
    _otp.dispose();
    super.dispose();
  }

  String _uuid() {
    final hex = DateTime.now().microsecondsSinceEpoch.toRadixString(16).padLeft(12, '0');
    return 'aaaaaaaa-bbbb-4ccc-8ddd-${hex.substring(hex.length - 12)}';
  }

  Future<void> _envoyer() async {
    final session = AqsScope.lire(context);
    final id = _uuid();
    final corps = {
      'pointEauCode': _code.text,
      'categorie': _categorie,
      'gravite': _gravite,
      'canal': 'MOBILE',
      'declarantTelephone': _tel.text,
      'codeOtp': _otp.text,
    };
    if (!session.api.enLigne) {
      setState(
        () => _statut =
            'Hors ligne : signalement mis en file locale (EN_ATTENTE). Même identifiant au rejeu.',
      );
      session.file.empiler(
        id: id,
        type: 'SIGNALEMENT',
        corps: corps,
        resume: 'Signalement ${_code.text}',
      );
      return;
    }
    try {
      final res = await session.api.post(
        '/api/v1/reports',
        headers: {'X-Client-Request-Id': id},
        body: corps,
      );
      if (!mounted) {
        return;
      }
      if (res.statusCode == 200 || res.statusCode == 201) {
        final msg = jsonDecode(res.body)['priseEnCharge']?['message'] as String?;
        setState(() => _statut = msg ?? 'Signalement envoyé (idempotence $id).');
        return;
      }
      if (res.statusCode == 429) {
        setState(() => _statut = 'Trop de signalements depuis ce numéro. Réessayez plus tard.');
        return;
      }
      if (res.statusCode == 404) {
        setState(() => _statut = 'Ouvrage introuvable. Vérifiez le code du point d’eau.');
        return;
      }
      if (res.statusCode == 422) {
        setState(() => _statut = 'Code de confirmation incorrect.');
        return;
      }
      setState(() => _statut = 'Envoi impossible.');
    } on HorsLigneException {
      if (!mounted) {
        return;
      }
      setState(
        () => _statut = 'Hors ligne : signalement mis en file locale (EN_ATTENTE). Même identifiant au rejeu.',
      );
      session.file.empiler(
        id: id,
        type: 'SIGNALEMENT',
        corps: corps,
        resume: 'Signalement ${_code.text}',
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Signaler')),
      body: Padding(
        padding: const EdgeInsets.all(AqsSpacing.s6),
        child: ListView(
          children: [
            const BandeauSync(),
            Text(
              'Moins d’une minute. Code simulé : 123456. Aucun volume d’eau.',
              style: AqsTypography.bodySmall,
            ),
            const SizedBox(height: AqsSpacing.s4),
            TextField(
              controller: _code,
              decoration: const InputDecoration(labelText: 'Code du point d’eau'),
            ),
            const SizedBox(height: AqsSpacing.s4),
            DropdownButtonFormField<String>(
              initialValue: _categorie,
              decoration: const InputDecoration(labelText: 'Symptôme'),
              items: const [
                DropdownMenuItem(value: 'PANNE_TOTALE', child: Text('Panne totale')),
                DropdownMenuItem(value: 'DEBIT_FAIBLE', child: Text('Débit faible')),
              ],
              onChanged: (v) => setState(() => _categorie = v ?? _categorie),
            ),
            const SizedBox(height: AqsSpacing.s4),
            DropdownButtonFormField<String>(
              initialValue: _gravite,
              decoration: const InputDecoration(labelText: 'Gravité'),
              items: const [
                DropdownMenuItem(value: 'FAIBLE', child: Text('Faible')),
                DropdownMenuItem(value: 'MOYENNE', child: Text('Moyenne')),
                DropdownMenuItem(value: 'HAUTE', child: Text('Haute')),
              ],
              onChanged: (v) => setState(() => _gravite = v ?? _gravite),
            ),
            const SizedBox(height: AqsSpacing.s4),
            TextField(
              controller: _tel,
              decoration: const InputDecoration(labelText: 'Téléphone'),
            ),
            const SizedBox(height: AqsSpacing.s4),
            TextField(
              controller: _otp,
              decoration: const InputDecoration(labelText: 'Code de confirmation'),
            ),
            const SizedBox(height: AqsSpacing.s6),
            FilledButton(onPressed: _envoyer, child: const Text('Envoyer')),
            if (_statut != null)
              Padding(
                padding: const EdgeInsets.only(top: AqsSpacing.s4),
                child: Text(_statut!, key: const Key('aqs-status')),
              ),
          ],
        ),
      ),
    );
  }
}

class EcranIntervention extends StatefulWidget {
  const EcranIntervention({super.key, this.interventionId = 'cccccccc-cccc-cccc-cccc-ccccccccccc1'});

  final String interventionId;

  @override
  State<EcranIntervention> createState() => _EcranInterventionState();
}

class _EcranInterventionState extends State<EcranIntervention> {
  final _diagnostic = TextEditingController();
  final _cause = TextEditingController();
  final _actions = TextEditingController();
  final _pieceRef = TextEditingController();
  String? _message;

  @override
  void dispose() {
    _diagnostic.dispose();
    _cause.dispose();
    _actions.dispose();
    _pieceRef.dispose();
    super.dispose();
  }

  String _uuid() {
    final hex = DateTime.now().microsecondsSinceEpoch.toRadixString(16).padLeft(12, '0');
    return 'bbbbbbbb-cccc-4ddd-8eee-${hex.substring(hex.length - 12)}';
  }

  void _enFile(String type, Map<String, dynamic> corps, String resume) {
    AqsScope.lire(context).file.empiler(id: _uuid(), type: type, corps: corps, resume: resume);
  }

  void _demarrer() {
    _enFile(
      'TRANSITION',
      {'interventionId': widget.interventionId, 'cible': 'EN_COURS', 'version': 0},
      'Démarrer INT',
    );
    setState(() => _message = '1 élément à envoyer');
  }

  void _sauverCompteRendu() {
    _enFile(
      'COMPTE_RENDU',
      {
        'interventionId': widget.interventionId,
        'diagnostic': _diagnostic.text,
        'causeRacine': _cause.text,
        'actions': _actions.text,
      },
      'Compte rendu',
    );
    setState(() => _message = 'Compte rendu mis en file');
  }

  void _ajouterPiece() {
    _enFile(
      'PIECE',
      {
        'interventionId': widget.interventionId,
        'reference': _pieceRef.text,
        'libelle': 'Joint',
        'quantite': 1,
        'coutUnitaire': 0,
      },
      'Pièce ${_pieceRef.text}',
    );
    setState(() => _message = 'Pièce mise en file');
  }

  void _declarerRealisee() {
    if (_diagnostic.text.trim().isEmpty || _actions.text.trim().isEmpty) {
      setState(
        () => _message = 'Diagnostic et action requis avant de déclarer réalisée',
      );
      return;
    }
    _enFile(
      'TRANSITION',
      {
        'interventionId': widget.interventionId,
        'cible': 'REALISEE',
        'version': 1,
        'diagnostic': _diagnostic.text,
        'causeRacine': _cause.text,
        'actions': _actions.text,
      },
      'Réalisée',
    );
    setState(() {
      final n = AqsScope.lire(context).file.aEnvoyer;
      _message = '$n éléments seront envoyés dès le retour du réseau';
    });
  }

  Future<void> _synchroniser() async {
    await AqsScope.lire(context).retablirReseau();
    if (!mounted) {
      return;
    }
    final session = AqsScope.lire(context);
    setState(() {
      if (session.file.aEnvoyer == 0 && session.file.conflit == null) {
        _message = 'Tout est synchronisé';
      } else if (session.file.conflit != null) {
        _message = 'Conflit : le serveur fait autorité';
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Intervention')),
      body: Padding(
        padding: const EdgeInsets.all(AqsSpacing.s4),
        child: ListView(
          children: [
            const BandeauSync(),
            if (_message != null)
              Padding(
                padding: const EdgeInsets.only(bottom: AqsSpacing.s4),
                child: Text(_message!, key: const Key('aqs-status')),
              ),
            Text('INT-2026-0003 · AFFECTEE · aucun volume d’eau', style: AqsTypography.bodySmall),
            const SizedBox(height: AqsSpacing.s3),
            FilledButton(
              key: const Key('btn-demarrer'),
              onPressed: _demarrer,
              child: const Text('Démarrer'),
            ),
            const SizedBox(height: AqsSpacing.s4),
            TextField(
              controller: _diagnostic,
              decoration: const InputDecoration(labelText: 'Diagnostic'),
            ),
            const SizedBox(height: AqsSpacing.s3),
            TextField(
              controller: _cause,
              decoration: const InputDecoration(labelText: 'Cause racine'),
            ),
            const SizedBox(height: AqsSpacing.s3),
            TextField(
              controller: _actions,
              decoration: const InputDecoration(labelText: 'Actions'),
            ),
            const SizedBox(height: AqsSpacing.s3),
            OutlinedButton(onPressed: _sauverCompteRendu, child: const Text('Enregistrer le compte rendu')),
            const SizedBox(height: AqsSpacing.s4),
            TextField(
              controller: _pieceRef,
              decoration: const InputDecoration(labelText: 'Référence pièce'),
            ),
            const SizedBox(height: AqsSpacing.s3),
            OutlinedButton(
              key: const Key('btn-piece'),
              onPressed: _ajouterPiece,
              child: const Text('Ajouter une pièce'),
            ),
            const SizedBox(height: AqsSpacing.s4),
            FilledButton(
              key: const Key('btn-realisee'),
              onPressed: _declarerRealisee,
              child: const Text('Déclarer réalisée'),
            ),
            const SizedBox(height: AqsSpacing.s3),
            OutlinedButton(
              key: const Key('btn-sync'),
              onPressed: _synchroniser,
              child: const Text('Synchroniser'),
            ),
          ],
        ),
      ),
    );
  }
}
