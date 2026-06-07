# Rapport d'analyse de sécurité mobile

## A. Informations générales

| Champ | Valeur |
|-------|--------|
| **Date** | 07-JUN-2026 |
| **Analyste** | Étudiant GS4 — ENSA Marrakech |
| **Cible** | DIVA — Damn Insecure and Vulnerable App |
| **Package Android** | jakhar.aseem.diva |
| **Version APK** | 1.0 (SDK min 15 / target 23) |
| **Hash MD5** | 82AB8B2193B3CFB1C737E3A786BE363A |
| **Taille** | 1.4 MB (1 502 294 octets) |
| **Outils utilisés** | BeVigil (analyse externe) — Yaazhini v1.1.0 (analyse statique interne) |

---

## B. Résumé exécutif

L'analyse statique de l'application DIVA a permis d'identifier **12 constats de sécurité** répartis en 2 niveaux High, 6 Medium, 2 Low et 2 niveaux Warning/Info. Le niveau de risque global est évalué à **ÉLEVÉ**. Les problèmes les plus critiques concernent la transmission de credentials en clair sur le réseau et la présence de secrets hardcodés dans le code source, deux vulnérabilités exploitables sans privilège particulier. À cela s'ajoutent de multiples erreurs de configuration dans l'AndroidManifest (debug activé, backup autorisé, composants exportés) qui élargissent significativement la surface d'attaque. L'application ne met en œuvre aucun mécanisme de durcissement (obfuscation, certificate pinning, protection root) et doit être considérée comme non conforme aux exigences OWASP MASVS avant toute mise en production.

---

## C. Top 5 constats

### 1. Communication réseau en clair — FIND-001
- **Sévérité** : 🔴 HIGH
- **Preuve** : Yaazhini > Vulnerabilities > Insecure communication > APICreds2Activity.java + BeVigil > Network analysis
- **Impact** : Des credentials d'API sont transmis en HTTP sans chiffrement. Sur un réseau Wi-Fi public ou un réseau compromis, un attaquant peut intercepter ces données en temps réel via une attaque Man-in-the-Middle sans outil sophistiqué (Wireshark, Burp Suite suffisent).
- **Remédiation** : Migrer toutes les communications vers HTTPS (TLS 1.2 minimum) — implémenter le certificate pinning via OkHttp CertificatePinner — ne jamais transmettre de credentials dans des requêtes HTTP en clair.
- **Référence OWASP** : MASVS-NETWORK-1

---

### 2. Credentials API hardcodés dans le code source — FIND-009
- **Sévérité** : 🔴 HIGH
- **Preuve** : BeVigil > Assets analysis > bevigil_export.json — identifiés dans les ressources de l'APK
- **Impact** : Des clés ou credentials d'API codés en dur sont extractibles par simple décompilation de l'APK (outil : jadx, apktool). Si ces credentials sont actifs, un attaquant peut accéder directement aux services backend associés sans authentification supplémentaire.
- **Remédiation** : Ne jamais stocker de credentials dans le code source ou les assets — utiliser un gestionnaire de secrets (Android Keystore, HashiCorp Vault) — révoquer immédiatement les credentials exposés — mettre en place une rotation automatique des clés.
- **Référence OWASP** : MASVS-STORAGE-1

---

### 3. Backup Android non restreint — FIND-003
- **Sévérité** : 🟠 MEDIUM
- **Preuve** : Yaazhini > Vulnerabilities > AndroidManifest.xml — android:allowBackup="true" (confirmé BeVigil)
- **Impact** : La commande `adb backup -noapk jakhar.aseem.diva` permet d'extraire l'intégralité du répertoire de données de l'application (SharedPreferences, bases SQLite, tokens) sans root et sans déverrouillage de l'écran sur les versions Android vulnérables.
- **Remédiation** : Définir `android:allowBackup="false"` dans le manifeste — ou configurer `android:fullBackupContent` avec des règles d'exclusion explicites pour les données sensibles.
- **Référence OWASP** : MASVS-STORAGE-4

---

### 4. ContentProvider exporté sans permission de protection — FIND-004
- **Sévérité** : 🟠 MEDIUM
- **Preuve** : Yaazhini > Vulnerabilities > Improper export of providers > AndroidManifest.xml
- **Impact** : Un ContentProvider accessible publiquement (android:exported="true" sans permission) peut être interrogé par toute application installée sur le même appareil. Une application malveillante peut lire ou modifier les données de DIVA sans aucune permission déclarée.
- **Remédiation** : Définir `android:exported="false"` si le provider est à usage interne uniquement — sinon, appliquer une permission de niveau `signature` pour restreindre l'accès aux applications du même développeur.
- **Référence OWASP** : MASVS-PLATFORM-1

---

### 5. Absence de certificate pinning — FIND-010
- **Sévérité** : 🟠 MEDIUM
- **Preuve** : Yaazhini > Proxy Settings accessible sans restriction — BeVigil > Network configuration
- **Impact** : Sans certificate pinning, l'application accepte tout certificat TLS signé par une autorité de certification de confiance du système. Un proxy d'interception (Burp Suite avec certificat installé) intercepte le trafic HTTPS sans aucune alerte côté application. Cette vulnérabilité a été démontrée en pratique lors du TP SSL pinning bypass.
- **Remédiation** : Implémenter le certificate pinning via `OkHttp CertificatePinner` ou via `android:networkSecurityConfig` avec une directive `<pin-set>` — maintenir une stratégie de rotation des pins pour éviter le blocage lors des renouvellements de certificats.
- **Référence OWASP** : MASVS-NETWORK-2

---

## D. Faux positifs notables

### FP-001 : http://schemas.android.com (Linked URLs)
- **Source** : Yaazhini > Linked URLs
- **Signalement** : L'URL `http://schemas.android.com` est détectée comme URL hardcodée en HTTP.
- **Justification du faux positif** : Cette URL est un **namespace XML Android standard** utilisé dans les fichiers de ressources et le manifeste (`xmlns:android="http://schemas.android.com/apk/res/android"`). Elle ne correspond à aucune communication réseau réelle et est présente dans toutes les applications Android. Ce n'est pas une URL appelée à l'exécution.

### FP-002 : Absence de protection copier-coller sur champs non sensibles (FIND-008 partiel)
- **Source** : Yaazhini > Info > 34 fichiers XML
- **Signalement** : 34 champs EditText signalés sans protection copier-coller.
- **Justification du faux positif partiel** : Parmi les 34 champs identifiés, certains sont des champs de saisie non sensibles (champs de recherche, champs de texte générique) pour lesquels la restriction du copier-coller serait contra-productive. Seuls les champs de saisie de credentials et tokens constituent de véritables vulnérabilités.

---

## E. Recommandations prioritaires

**1. Sécuriser toutes les communications réseau (CRITIQUE)**  
Migrer l'ensemble des appels réseau vers HTTPS avec TLS 1.2 minimum, implémenter le certificate pinning via OkHttp CertificatePinner, et configurer un `NetworkSecurityConfig` Android pour interdire le trafic en clair (`cleartextTrafficPermitted="false"`). Cette action adresse simultanément FIND-001, FIND-007 et FIND-010.

**2. Corriger les configurations dangereuses du manifeste (URGENT)**  
Définir `android:debuggable="false"`, `android:allowBackup="false"` et `android:exported="false"` sur tous les composants non destinés à être publics dans l'AndroidManifest.xml. Effectuer cette correction avant tout déploiement en production. Cette action adresse FIND-002, FIND-003 et FIND-004.

**3. Supprimer tous les secrets hardcodés et mettre en place une gestion sécurisée des clés (CRITIQUE)**  
Auditer l'intégralité du code source pour identifier et supprimer tous les credentials, clés API et tokens hardcodés. Migrer vers Android Keystore pour le stockage local des secrets, utiliser un service de gestion de secrets côté serveur (Vault, AWS Secrets Manager), et révoquer immédiatement tous les credentials exposés. Cette action adresse FIND-009 et FIND-011.

---

## F. Annexes

| Fichier | Description |
|---------|-------------|
| `00-scope/DivaApplication.apk` | APK analysé (MD5: 82AB8B2193B3CFB1C737E3A786BE363A) |
| `01-bevigil/bevigil_notes.md` | Notes d'analyse BeVigil (analyse externe) |
| `02-yaazhini/yaazhini_notes.md` | Notes d'analyse Yaazhini — 8 éléments documentés |
| `03-triage/triage.csv` | Tableau de triage consolidé — 12 constats |
| `03-triage/owasp_mapping.md` | Mapping OWASP MASVS — 12 références |
| `analyse_info.txt` | Métadonnées de l'analyse |
| `commands.log` | Journal des commandes exécutées |
