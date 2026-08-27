-- ISS-065 : jeu de démonstration à identifiants stables. Profil `demo` uniquement.
-- Aucun volume d'eau (H-2). Personnes fictives uniquement.

INSERT INTO localite (id, code, nom, niveau, parent_id) VALUES
    ('11111111-1111-1111-1111-111111111111', 'CM-C', 'Centre', 'REGION', NULL),
    ('22222222-2222-2222-2222-222222222222', 'YDE', 'Yaoundé', 'COMMUNE', '11111111-1111-1111-1111-111111111111'),
    ('33333333-3333-3333-3333-333333333333', 'YDE-NKB', 'Nkolbisson', 'QUARTIER', '22222222-2222-2222-2222-222222222222')
ON CONFLICT (id) DO NOTHING;

INSERT INTO comite (id, nom, localite_id, actif) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Comité A — Nkolbisson', '11111111-1111-1111-1111-111111111111', TRUE),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Comité B — Soa', '11111111-1111-1111-1111-111111111111', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO point_eau (
    id, code, nom_usage, type, latitude, longitude, localite_id, comite_id,
    date_mise_en_service, population_desservie, intervalle_maintenance_jours,
    etat, actif, version, cree_le, modifie_le
) VALUES
    ('d0000001-0000-4000-8000-000000000001', 'YDE-D01', 'Forage école Nkolbisson', 'FORAGE_MANUEL',
     3.872000, 11.522000, '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     DATE '2019-03-12', 420, 180, 'OPERATIONNEL', TRUE, 0, TIMESTAMPTZ '2025-08-01 08:00:00+00', NOW()),
    ('d0000001-0000-4000-8000-000000000002', 'YDE-D02', 'Borne marché', 'BORNE_FONTAINE',
     3.875000, 11.518000, '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     DATE '2020-11-02', 280, 120, 'SOUS_SURVEILLANCE', TRUE, 0, TIMESTAMPTZ '2025-08-01 08:00:00+00', NOW()),
    ('d0000001-0000-4000-8000-000000000003', 'YDE-D03', 'Forage comité A', 'FORAGE_MOTORISE',
     3.869000, 11.526000, '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     DATE '2018-06-20', 510, 150, 'RISQUE_ELEVE', TRUE, 0, TIMESTAMPTZ '2025-08-01 08:00:00+00', NOW()),
    ('d0000001-0000-4000-8000-000000000004', 'YDE-D04', 'Mini-réseau Soa', 'MINI_RESEAU',
     3.980000, 11.590000, '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     DATE '2021-01-15', 640, 200, 'EN_PANNE', TRUE, 0, TIMESTAMPTZ '2025-08-01 08:00:00+00', NOW()),
    ('d0000001-0000-4000-8000-000000000005', 'YDE-D05', 'Forage église', 'FORAGE_MANUEL',
     3.871000, 11.530000, '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     DATE '2017-09-01', 190, 180, 'EN_REPARATION', TRUE, 0, TIMESTAMPTZ '2025-08-01 08:00:00+00', NOW()),
    ('d0000001-0000-4000-8000-000000000006', 'YDE-D06', 'Ancien puits (hors service)', 'FORAGE_MANUEL',
     3.866000, 11.515000, '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     DATE '2012-04-08', 0, NULL, 'HORS_SERVICE', TRUE, 0, TIMESTAMPTZ '2025-08-01 08:00:00+00', NOW()),
    ('d0000001-0000-4000-8000-000000000007', 'YDE-D07', 'Borne collège', 'BORNE_FONTAINE',
     3.878000, 11.521000, '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     DATE '2022-02-10', 330, 90, 'OPERATIONNEL', TRUE, 0, TIMESTAMPTZ '2025-08-01 08:00:00+00', NOW()),
    ('d0000001-0000-4000-8000-000000000008', 'YDE-D08', 'Forage quartier haut', 'FORAGE_MANUEL',
     3.882000, 11.528000, '33333333-3333-3333-3333-333333333333', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     DATE '2016-12-05', 250, 180, 'SOUS_SURVEILLANCE', TRUE, 0, TIMESTAMPTZ '2025-08-01 08:00:00+00', NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO historique_etat (id, point_eau_id, etat_precedent, etat_nouveau, motif, survenu_le)
SELECT CAST(md5('hist-' || p.id::text || gs::text) AS uuid),
       p.id,
       'OPERATIONNEL',
       'EN_PANNE',
       'Panne saison sèche (démo, aucun volume)',
       (gs::date + TIME '10:00') AT TIME ZONE 'UTC'
FROM (VALUES
    ('d0000001-0000-4000-8000-000000000003'::uuid),
    ('d0000001-0000-4000-8000-000000000004'::uuid)
) AS p(id)
CROSS JOIN generate_series(DATE '2025-09-15', DATE '2026-07-15', INTERVAL '2 months') AS gs
ON CONFLICT (id) DO NOTHING;

INSERT INTO indice_sante (
    id, point_eau_id, date_calcul, score, bande, confiance,
    charge_cumulee_jours, intervalle_effectif_jours,
    indicateur_m, indicateur_p, indicateur_s, indicateur_t,
    facteurs, version_parametrage
)
SELECT CAST(md5('idx-' || pe.id::text || gs::text) AS uuid),
       pe.id,
       gs::date,
       72.5,
       'SOUS_SURVEILLANCE',
       'MOYENNE',
       40.0,
       180,
       0.22, 0.10, 1.10, 0.05,
       '[{"code":"M","libelle":"Echeance entretien"},{"code":"P","libelle":"Historique pannes"}]',
       'demo-1'
FROM point_eau pe
CROSS JOIN generate_series(DATE '2025-09-01', DATE '2026-08-01', INTERVAL '1 month') AS gs
WHERE pe.code LIKE 'YDE-D%'
ON CONFLICT (point_eau_id, date_calcul) DO NOTHING;

INSERT INTO alerte (
    id, point_eau_id, type_regle, niveau, horizon_jours, emise_le,
    explication, recommandation, facteurs, statut, version_parametrage
) VALUES (
    'a0000001-0000-4000-8000-000000000001',
    'd0000001-0000-4000-8000-000000000003',
    'R1_ECHEANCE_MAINTENANCE',
    'ELEVE',
    14,
    TIMESTAMPTZ '2026-08-10 06:00:00+00',
    'Echeance d entretien atteinte pour la population desservie, saison seche. Risque de panne sous ~2 semaines.',
    'Planifier une visite preventive du comite.',
    '[{"nom":"jours_pondere","valeur":165},{"nom":"population","valeur":510},{"nom":"horizon","valeur":14}]',
    'ACTIVE',
    'demo-1'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO signalement (
    id, reference, uuid_client, point_eau_id, categorie, gravite, commentaire,
    declarant_telephone_hache, declarant_telephone_suffixe, canal, statut,
    nb_corroborations, priorite, priorite_figee, declare_le
) VALUES (
    's0000001-0000-4000-8000-000000000001',
    'SIG-90001',
    'c0000001-0000-4000-8000-000000000001',
    'd0000001-0000-4000-8000-000000000004',
    'PANNE_TOTALE',
    'HAUTE',
    'Plus d eau au robinet du mini-reseau (demo).',
    '0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef',
    '1122',
    'WEB',
    'QUALIFIE',
    2,
    90,
    FALSE,
    TIMESTAMPTZ '2026-08-20 07:15:00+00'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO intervention (
    id, reference, point_eau_id, type, origine, statut,
    diagnostic, cause_racine, actions,
    ouverte_le, affectee_le, demarree_le, realisee_le, cloturee_le,
    temps_retablissement_minutes, version
) VALUES (
    'i0000001-0000-4000-8000-000000000001',
    'INT-90001',
    'd0000001-0000-4000-8000-000000000007',
    'CORRECTIVE',
    'SIGNALEMENT',
    'CLOTUREE',
    'Joint pompe use.',
    'Usure calendaire, pas un volume mesure.',
    'Remplacement du joint et essai de debit a la pompe (observation).',
    TIMESTAMPTZ '2026-07-01 08:00:00+00',
    TIMESTAMPTZ '2026-07-01 09:00:00+00',
    TIMESTAMPTZ '2026-07-01 10:00:00+00',
    TIMESTAMPTZ '2026-07-02 11:00:00+00',
    TIMESTAMPTZ '2026-07-02 16:00:00+00',
    1920,
    0
)
ON CONFLICT (id) DO NOTHING;
