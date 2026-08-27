import 'package:aquasensus_mobile/sync/file_locale.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('FIFO et pas de purge avant ENVOYE (ENF-12)', () {
    final file = FileLocale();
    file.empiler(id: 'a', type: 'SIGNALEMENT', corps: {}, resume: 'un');
    file.empiler(id: 'b', type: 'TRANSITION', corps: {}, resume: 'deux');
    expect(file.elements.map((e) => e.id), ['a', 'b']);
    expect(file.aEnvoyer, 2);
    file.purgerConfirmes();
    expect(file.aEnvoyer, 2);
    file.marquerEnvoye('a');
    file.purgerConfirmes();
    expect(file.elements.single.id, 'b');
  });

  test('conflit conserve la copie locale', () {
    final file = FileLocale();
    file.empiler(id: 'c', type: 'TRANSITION', corps: {'cible': 'EN_COURS'}, resume: 't');
    file.marquerConflit('c', 'État serveur : REALISEE');
    expect(file.elements.single.statut, StatutSync.enConflit);
    expect(file.elements.single.corps['cible'], 'EN_COURS');
    expect(file.conflit, contains('serveur'));
  });
}
