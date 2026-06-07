# Notes d'analyse Yaazhini — DIVA (jakhar.aseem.diva)
**Date d'analyse** : 7-JUN-2026  
**Outil** : Yaazhini APK Scanner v1.1.0  
**APK** : DivaApplication.apk (v1.0 — SDK min 15, target 23)

---

## Éléments identifiés

### Élément 1 : Communication non sécurisée (Credentials API)
- **Niveau** : 🔴 HIGH
- **Localisation** : Vulnerabilities > Insecure communication > APICreds2Activity.java (ligne 1)
- **Description** : L'activité APICreds2Activity transmet des credentials d'API en clair sans chiffrement TLS/SSL.
- **Impact potentiel** : Interception des credentials par attaque MitM (Man-in-the-Middle), compromission de compte ou d'API distante.
- **Remédiation suggérée** : Utiliser HTTPS exclusivement, implémenter le SSL pinning, ne jamais inclure de credentials dans les requêtes HTTP en clair.

---

### Élément 2 : Mode débogage activé en production
- **Niveau** : 🟠 MEDIUM
- **Localisation** : Vulnerabilities > Android debuggable enabled > AndroidManifest.xml (ligne 1)
- **Description** : Le flag `android:debuggable="true"` est présent dans le manifeste, permettant le débogage ADB de l'application en production.
- **Impact potentiel** : Un attaquant ayant accès physique ou ADB peut extraire des données, injecter du code ou contourner les contrôles de sécurité via JDWP.
- **Remédiation suggérée** : Définir `android:debuggable="false"` dans les builds de release. Utiliser les build variants Gradle (debug/release).

---

### Élément 3 : Backup Android non restreint
- **Niveau** : 🟠 MEDIUM
- **Localisation** : Vulnerabilities > Android backup vulnerability > AndroidManifest.xml (ligne 1)
- **Description** : Le flag `android:allowBackup="true"` autorise la sauvegarde complète des données de l'application via ADB sans root.
- **Impact potentiel** : Extraction de données sensibles (SharedPreferences, bases SQLite, tokens) via `adb backup` sans déverrouillage de l'appareil.
- **Remédiation suggérée** : Définir `android:allowBackup="false"` ou configurer des règles `android:fullBackupContent` pour exclure les données sensibles.

---

### Élément 4 : Export de ContentProvider non protégé
- **Niveau** : 🟠 MEDIUM
- **Localisation** : Vulnerabilities > Improper export of providers > AndroidManifest.xml
- **Description** : Un ou plusieurs ContentProviders sont exportés (`android:exported="true"`) sans permission de protection, les rendant accessibles à toute application installée sur le même appareil.
- **Impact potentiel** : Accès non autorisé aux données de l'application depuis une app tierce malveillante (lecture/écriture de la base de données).
- **Remédiation suggérée** : Ajouter `android:exported="false"` si le provider est usage interne, ou définir une `android:permission` de protection de niveau `signature`.

---

### Élément 5 : JavaScript activé dans WebView
- **Niveau** : 🟡 LOW
- **Localisation** : Vulnerabilities > Javascript enabled in WebView > InputValidation2URISchemeActivity.java (ligne 1)
- **Description** : L'appel `setJavaScriptEnabled(true)` est présent dans une WebView sans restriction de contenu chargé.
- **Impact potentiel** : Vulnérabilité XSS ou injection JavaScript si l'URL chargée est contrôlable par un attaquant (URI scheme non validé).
- **Remédiation suggérée** : Désactiver JavaScript si non nécessaire, valider strictement les URI schemes acceptés, implémenter une Content Security Policy.

---

### Élément 6 : Stockage externe non sécurisé
- **Niveau** : ⚠️ WARNING
- **Localisation** : Vulnerabilities > Android external storage (×9) > ContextCompat.java(4), InsecureDataStorage4Activity.java(1), FileProvider.java(1)...
- **Description** : L'application utilise le stockage externe (carte SD / répertoire public) pour stocker des données via `getExternalStorage*` dans 9 occurrences.
- **Impact potentiel** : Toute application disposant de la permission `READ_EXTERNAL_STORAGE` peut lire ces fichiers. Données utilisateur exposées sans chiffrement.
- **Remédiation suggérée** : Utiliser le stockage interne (`getFilesDir()`) pour les données sensibles, chiffrer les fichiers si le stockage externe est nécessaire.

---

### Élément 7 : Absence de protection copier-coller sur les champs sensibles
- **Niveau** : ℹ️ INFORMATION
- **Localisation** : Vulnerabilities > Missing copy and paste protection from EditText fields (×34) > activity_access_control3.xml, activity_apicreds2.xml, activity_hardcode.xml...
- **Description** : 34 champs EditText dans les layouts XML ne définissent pas `android:textIsSelectable="false"` ni `inputType="textPassword"`, exposant leur contenu au presse-papiers système.
- **Impact potentiel** : Une application malveillante en arrière-plan peut lire le presse-papiers et capturer des données saisies (mots de passe, codes, tokens).
- **Remédiation suggérée** : Utiliser `inputType="textPassword"` pour les champs sensibles, implémenter un listener pour vider le clipboard après utilisation.

---

## Synthèse

| Niveau | Nombre | Vulnérabilités principales |
|--------|--------|---------------------------|
| 🔴 High | 1 | Communication en clair |
| 🟠 Medium | 3 | Debug, Backup, Provider exposé |
| 🟡 Low | 1 | WebView JavaScript |
| ⚠️ Warning | 9 | Stockage externe |
| ℹ️ Info | 51 | Copy/paste, divers |
| **Total** | **65** | |

> ⚠️ Aucun secret réel n'a été exposé dans ce document. Les valeurs sensibles éventuelles sont masquées selon les bonnes pratiques.
