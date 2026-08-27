import 'package:flutter/foundation.dart';

enum StatutSync { enAttente, envoye, enConflit }

/// File FIFO persistante en mémoire de session (ENF-12, EF-96, EF-98).
class FileLocale extends ChangeNotifier {
  final List<ElementFile> elements = [];

  int get aEnvoyer => elements.where((e) => e.statut == StatutSync.enAttente).length;

  String? get conflit {
    for (final e in elements) {
      if (e.statut == StatutSync.enConflit) {
        return e.etatServeur;
      }
    }
    return null;
  }

  void empiler({
    required String id,
    required String type,
    required Map<String, dynamic> corps,
    required String resume,
  }) {
    elements.add(ElementFile(id: id, type: type, corps: corps, resume: resume));
    notifyListeners();
  }

  void marquerEnvoye(String id) {
    for (final e in elements) {
      if (e.id == id) {
        e.statut = StatutSync.envoye;
      }
    }
    notifyListeners();
  }

  void marquerConflit(String id, String etatServeur) {
    for (final e in elements) {
      if (e.id == id) {
        e.statut = StatutSync.enConflit;
        e.etatServeur = etatServeur;
      }
    }
    notifyListeners();
  }

  /// ENF-12 : on ne retire rien avant confirmation (ENVOYE). Les EN_ATTENTE restent.
  void purgerConfirmes() {
    elements.removeWhere((e) => e.statut == StatutSync.envoye);
    notifyListeners();
  }
}

class ElementFile {
  ElementFile({
    required this.id,
    required this.type,
    required this.corps,
    required this.resume,
    this.statut = StatutSync.enAttente,
  });

  final String id;
  final String type;
  final Map<String, dynamic> corps;
  final String resume;
  StatutSync statut;
  String? etatServeur;
}
