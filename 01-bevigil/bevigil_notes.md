# Notes d'analyse BeVigil - DIVA

## Ce qui est certain
- Security Rating : 8.2/10 (Good) — score CVSS-based
- SQL Query non paramétrée détectée (97.1% de confiance)
- Secret/clé API potentiellement exposée (2.9% de confiance)
- Package : jakhar.aseem.diva (v1.0)

## Ce qui est hypothèse
- La clé API détectée pourrait être un faux positif (app pédagogique)
- Le score 8.2 ne reflète pas la réalité : DIVA est intentionnellement vulnérable
- BeVigil n'a pas détecté les ~11 autres vulnérabilités intentionnelles de DIVA

## Points d'intérêt
- Faux négatifs massifs : analyse statique partielle de BeVigil
- SQL Injection confirmée → à croiser avec Yaazhini
- Secret hardcodé → à localiser précisément avec Yaazhini

## Domaines et sous-domaines
- Aucun domaine externe détecté (app pédagogique locale)
- Pas de communication réseau vers serveurs tiers

## Endpoints et APIs
- Aucun endpoint réseau externe détecté
- API interne : content://jakhar.aseem.diva.provider.notesprovider (ContentProvider local)

## URLs HTTP/HTTPS
- Aucune URL réseau externe détectée
- URLs internes potentielles : à confirmer avec Yaazhini

## Emails et identifiants
- Développeur : aseem[at]payatu[dot]com (metadata GitHub)
- Identifiants hardcodés suspectés (finding LOW - Possible Secret)

## Technologies détectées
- Plateforme  : Android
- Version min : API 15 (Android 4.0.3)
- Version cible: API 23 (Android 6.0)
- Langage     : Java
- JNI/C natif : oui (divajni.c)
- Base de données : SQLite (non paramétrée → vulnérable SQLi)

## Résumé des findings BeVigil
| Sévérité | Finding                        | Confiance | OWASP    |
|----------|-------------------------------|-----------|----------|
| MED      | Non-parameterized SQL Query   | 97.1%     | M7       |
| LOW      | Possible Secret Detected      | 2.9%      | M9       |

## Faux négatifs identifiés (non détectés par BeVigil)
- M2 : Stockage données en clair (SharedPreferences, SQLite, fichier externe)
- M1 : Activités exportées sans authentification
- M2 : Logging de credentials en clair (LogCat)
- M4 : Injection via WebView
- M7 : Input validation insuffisante
