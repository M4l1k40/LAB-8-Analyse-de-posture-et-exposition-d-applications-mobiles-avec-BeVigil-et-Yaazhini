# LAB 8 — Analyse de posture et exposition d'applications mobiles
## BeVigil + Yaazhini · Sécurité Mobile Android
  
> **Date d'analyse** : 07 Juin 2026  
> **Statut** : ✅ Complété

---

## Table des matières

1. [Contexte et objectifs](#1-contexte-et-objectifs)
2. [Pré-requis et environnement](#2-pré-requis-et-environnement)
3. [Cible d'analyse — DIVA](#3-cible-danalyse--diva)
4. [Outils utilisés](#4-outils-utilisés)
5. [Structure du projet](#5-structure-du-projet)
6. [Démarche d'analyse pas à pas](#6-démarche-danalyse-pas-à-pas)
7. [Résultats — Tableau consolidé des findings](#7-résultats--tableau-consolidé-des-findings)
8. [Mapping OWASP MASVS](#8-mapping-owasp-masvs)
9. [Faux positifs identifiés](#9-faux-positifs-identifiés)
10. [Recommandations prioritaires](#10-recommandations-prioritaires)
11. [Comparaison BeVigil vs Yaazhini](#11-comparaison-bevigil-vs-yaazhini)
12. [Points clés d'apprentissage](#12-points-clés-dapprentissage)
13. [Références](#13-références)

---

## 1. Contexte et objectifs

Ce laboratoire porte sur l'**analyse de la posture de sécurité d'une application mobile Android** à l'aide de deux outils complémentaires d'analyse statique : **BeVigil** (plateforme d'analyse externe en ligne) et **Yaazhini** (scanner APK local).

L'application cible est **DIVA** (*Damn Insecure and Vulnerable App*), une application Android intentionnellement vulnérable conçue à des fins pédagogiques.

### Objectifs pédagogiques

- Maîtriser l'utilisation de BeVigil pour l'analyse externe de réputation et d'exposition d'un APK
- Maîtriser l'utilisation de Yaazhini pour l'analyse statique approfondie (code, manifeste, vulnérabilités)
- Identifier, documenter et classer les vulnérabilités découvertes selon leur sévérité
- Mapper les findings sur le référentiel **OWASP MASVS v2**
- Distinguer les vrais positifs des faux positifs
- Produire un rapport d'analyse structuré et exploitable

### Périmètre et limites éthiques

> ⚠️ **Ce lab est strictement pédagogique.** L'APK analysé (DIVA) est une application de formation fournie par l'enseignant. Aucune exploitation des vulnérabilités n'a été réalisée et aucune application réelle n'a été ciblée.

| Contrainte | Détail |
|---|---|
| Pas d'exploitation | Analyse statique uniquement — pas d'exécution d'attaques |
| Pas de tests intrusifs | Aucun test dynamique ou réseau sur cibles non autorisées |
| Application pédagogique | DIVA est open-source et conçue pour être analysée |
| Période d'analyse | 23 mai 2026 → 07 juin 2026 |

---

## 2. Pré-requis et environnement

### Environnement technique

| Composant | Détail |
|---|---|
| Système d'exploitation | Windows 10/11 |
| Yaazhini | Version 1.1.0 (application desktop Windows) |
| BeVigil | Plateforme web — [bevigil.com](https://bevigil.com) |
| APK analysé | `DivaApplication.apk` (1.4 MB) |

### Installation de Yaazhini

1. Télécharger Yaazhini depuis le site officiel
2. Lancer `Yaazhini.exe`
3. Charger l'APK via **File > Open APK** ou glisser-déposer le fichier `.apk`
4. Attendre la décompilation et l'analyse automatique

### Utilisation de BeVigil

1. Se rendre sur [bevigil.com](https://bevigil.com)
2. Créer un compte ou se connecter
3. Uploader l'APK ou rechercher le package `jakhar.aseem.diva`
4. Consulter le rapport d'analyse généré

---

## 3. Cible d'analyse — DIVA

**DIVA** (*Damn Insecure and Vulnerable App*) est une application Android open-source développée par **Aseem Jakhar** (Payatu Security) pour l'apprentissage de la sécurité mobile. Elle contient volontairement de nombreuses vulnérabilités couvrant les principales catégories de l'OWASP Mobile Top 10.

### Informations de l'application

| Champ | Valeur |
|---|---|
| **Nom** | DIVA_ok |
| **Package Android** | `jakhar.aseem.diva` |
| **Version** | 1.0 |
| **Android Min SDK** | 15 (Android 4.0.3 Ice Cream Sandwich) |
| **Android Target SDK** | 23 (Android 6.0 Marshmallow) |
| **Taille** | 1.4 MB (1 502 294 octets) |
| **Hash MD5** | `82AB8B2193B3CFB1C737E3A786BE363A` |
| **Langage** | Java |
| **JNI/Code natif** | Oui (`libdivajni.so`) |
| **Date de scan** | 7 juin 2026 — 04:06 AM |

### Activités principales identifiées dans le code source

| Activité | Vulnérabilité intentionnelle |
|---|---|
| `APICreds2Activity.java` | Credentials API en clair / communication insécurisée |
| `InsecureDataStorage1-4Activity.java` | Stockage de données non sécurisé (SQLite, SharedPrefs, fichiers) |
| `InputValidation2URISchemeActivity.java` | Validation d'entrée insuffisante / WebView XSS |
| `AccessControl1-3Activity.java` | Contrôle d'accès insuffisant |
| `HardcodeActivity.java` / `Hardcode2Activity.java` | Credentials hardcodés dans le code |
| `SQLInjectionActivity.java` | Injection SQL |
| `LogActivity.java` | Journalisation de données sensibles |
| `NotesProvider.java` | ContentProvider exporté non protégé |

---

## 4. Outils utilisés

### 4.1 BeVigil (analyse externe)

**BeVigil** est une plateforme OSINT spécialisée dans l'analyse de sécurité des applications mobiles (Android & iOS). Elle permet d'obtenir rapidement une vue externe de l'exposition d'une application sans nécessiter de décompilation manuelle.

**Capacités utilisées dans ce lab :**
- Score de sécurité global (CVSS-based)
- Détection de secrets et credentials exposés
- Analyse des URLs et endpoints réseaux
- Détection des requêtes SQL non paramétrées
- Analyse des assets et ressources embarquées

**Résultats BeVigil pour DIVA :**

| Métrique | Résultat |
|---|---|
| Security Rating | 8.2/10 (Good) — ⚠️ Trompeur pour une app pédagogique |
| SQL Query non paramétrée | Détectée (97.1% de confiance) |
| Secret potentiellement exposé | Détecté (2.9% de confiance) |
| Domaines externes | Aucun |
| Endpoints réseau | `content://jakhar.aseem.diva.provider.notesprovider` (local) |

> **Observation importante** : Le score 8.2/10 de BeVigil est **trompeur**. DIVA contient intentionnellement ~11 vulnérabilités critiques que BeVigil n'a pas détectées. Cela illustre les **limites de l'analyse statique automatisée externe** et la nécessité d'une analyse approfondie locale.

### 4.2 Yaazhini APK Scanner (analyse statique locale)

**Yaazhini** est un outil d'analyse statique desktop (Windows) qui décompile l'APK et effectue une inspection complète du code Java, du manifeste Android, des ressources et des bibliothèques natives.

**Fonctionnalités utilisées :**

| Onglet Yaazhini | Ce qui a été analysé |
|---|---|
| **Home** | App Summary (package, SDK, taille) |
| **Vulnerabilities** | Arbre complet des vulnérabilités classées par sévérité |
| **View Code** | Code Java décompilé (jadx intégré) |
| **Linked URLs** | URLs hardcodées dans le bytecode |
| **Libraries** | Bibliothèques tierces incluses |
| **Permissions** | Permissions Android déclarées |
| **Activities** | Activités exportées |
| **Receivers / Services** | Composants exposés |

---

## 5. Structure du projet

```
lab-mobile-security/
├── 00-scope/
│   ├── DivaApplication.apk          # APK cible (MD5: 82AB8B2193B3CFB1C737E3A786BE363A)
│   ├── scope.md                     # Périmètre d'analyse et limites éthiques
│   └── targets.txt                  # Identifiants de la cible
│
├── 01-bevigil/
│   ├── bevigil_notes.md             # Notes structurées de l'analyse BeVigil
│   ├── 01-bevigilbevigil_diva_report.txt  # Rapport brut BeVigil
│   └── captures/                   # Screenshots BeVigil (5 captures)
│
├── 02-yaazhini/
│   ├── yaazhini_notes.md            # Notes structurées de l'analyse Yaazhini (8 findings)
│   ├── resources/                  # Ressources extraites de l'APK
│   │   ├── AndroidManifest.xml     # Manifeste Android décompilé
│   │   ├── classes.dex             # Bytecode Dalvik
│   │   ├── lib/                    # Bibliothèques natives (.so)
│   │   └── res/                    # Ressources XML/images
│   └── sources/                    # Code source Java décompilé
│       └── jakhar/aseem/diva/      # Package principal DIVA
│
├── 03-triage/
│   ├── triage.csv                  # Tableau consolidé des 12 findings
│   └── owasp_mapping.md            # Mapping OWASP MASVS v2 (12 références)
│
├── 04-report/
│   └── rapport_final.md            # Rapport d'analyse complet
│
├── analyse_info.txt                # Métadonnées de l'analyse
└── commands.log                    # Journal des commandes exécutées
```

---

## 6. Démarche d'analyse pas à pas

### Étape 1 — Définition du périmètre (`00-scope/`)

Avant toute analyse, définir clairement :
- La cible autorisée (APK pédagogique fourni par l'enseignant)
- Les limites éthiques (pas d'exploitation, pas de tests intrusifs)
- La période d'analyse

### Étape 2 — Analyse externe avec BeVigil (`01-bevigil/`)

1. Uploader `DivaApplication.apk` sur bevigil.com
2. Consulter le Security Score et les findings automatiques
3. Documenter les résultats dans `bevigil_notes.md`
4. **Identifier les faux négatifs** (ce que BeVigil n'a pas détecté)
5. Capturer les écrans pertinents

### Étape 3 — Analyse statique avec Yaazhini (`02-yaazhini/`)

1. Ouvrir `DivaApplication.apk` dans Yaazhini
2. Consulter le **Home** → noter les métadonnées de l'APK
3. Explorer l'onglet **Vulnerabilities** → parcourir l'arbre complet (High → Medium → Low → Warning → Info)
4. Pour chaque finding : noter la localisation précise (fichier Java ou XML)
5. Vérifier le code source via **View Code** pour confirmer les vulnérabilités
6. Documenter dans `yaazhini_notes.md`

**Captures réalisées (images fournies dans ce lab) :**

| Image | Contenu |
|---|---|
| Image 1 | App Summary — métadonnées de l'APK |
| Image 2 | Vulnerabilities : High (1) + Medium (3) |
| Image 3 | Vulnerabilities : Low (1) + Warning (9) |
| Image 4 | Information (51) — Missing copy/paste protection |

### Étape 4 — Triage et consolidation (`03-triage/`)

1. Croiser les findings BeVigil et Yaazhini
2. Éliminer les doublons, identifier les confirmations croisées
3. Remplir le `triage.csv` (12 findings retenus)
4. Mapper chaque finding sur **OWASP MASVS v2** dans `owasp_mapping.md`

### Étape 5 — Rédaction du rapport (`04-report/`)

1. Résumé exécutif
2. Top 5 des constats critiques avec preuves
3. Identification des faux positifs
4. Recommandations de remédiation priorisées

---

## 7. Résultats — Tableau consolidé des findings

**Total : 12 findings · Niveau de risque global : 🔴 ÉLEVÉ**

| ID | Source | Finding | Sévérité | OWASP MASVS | Statut |
|---|---|---|---|---|---|
| FIND-001 | BeVigil + Yaazhini | Communication réseau en clair | 🔴 **HIGH** | MASVS-NETWORK-1 | ✅ Confirmé |
| FIND-002 | Yaazhini | Mode debug activé (`android:debuggable=true`) | 🟠 MEDIUM | MASVS-RESILIENCE-2 | ✅ Confirmé |
| FIND-003 | BeVigil + Yaazhini | Backup Android non restreint (`allowBackup=true`) | 🟠 MEDIUM | MASVS-STORAGE-4 | ✅ Confirmé |
| FIND-004 | Yaazhini | ContentProvider exporté sans permission | 🟠 MEDIUM | MASVS-PLATFORM-1 | ✅ Confirmé |
| FIND-005 | Yaazhini | JavaScript activé dans WebView | 🟡 LOW | MASVS-PLATFORM-2 | ✅ Confirmé |
| FIND-006 | Yaazhini | Stockage de données sur mémoire externe (×9) | 🟠 MEDIUM | MASVS-STORAGE-2 | ✅ Confirmé |
| FIND-007 | BeVigil + Yaazhini | URL HTTP hardcodée (`http://payatu.com`) | 🟠 MEDIUM | MASVS-NETWORK-1 | ✅ Confirmé |
| FIND-008 | Yaazhini | Absence protection copier-coller (34 champs) | 🟡 LOW | MASVS-PLATFORM-3 | ✅ Confirmé |
| FIND-009 | BeVigil | Credentials API dans les assets | 🔴 **HIGH** | MASVS-STORAGE-1 | ⚠️ À confirmer |
| FIND-010 | BeVigil + Yaazhini | Absence de certificate pinning | 🟠 MEDIUM | MASVS-NETWORK-2 | ✅ Confirmé |
| FIND-011 | BeVigil | SharedPreferences non chiffrées | 🟠 MEDIUM | MASVS-STORAGE-1 | ⚠️ À confirmer |
| FIND-012 | BeVigil + Yaazhini | Code source non obfusqué | 🟡 LOW | MASVS-RESILIENCE-3 | ✅ Confirmé |

### Répartition par sévérité (Yaazhini)

| Sévérité | Nombre | Détails |
|---|---|---|
| 🔴 **High** | 1 | Insecure communication — `APICreds2Activity.java` |
| 🟠 **Medium** | 3 | Debug activé, Backup non restreint, Improper export providers |
| 🟡 **Low** | 1 | JavaScript dans WebView — `InputValidation2URISchemeActivity.java` |
| ⚠️ **Warning** | 9 | Stockage externe (ContextCompat, InsecureDataStorage4Activity...) |
| ℹ️ **Information** | 51 | Missing copy/paste protection (34 champs EditText) |
| **Total** | **65** | |

---

## 8. Mapping OWASP MASVS

Les findings ont été mappés sur le standard **OWASP Mobile Application Security Verification Standard (MASVS v2)** :

| Catégorie MASVS | Findings associés | Nombre |
|---|---|---|
| **MASVS-NETWORK** | FIND-001, FIND-007, FIND-010 | 3 |
| **MASVS-STORAGE** | FIND-003, FIND-006, FIND-009, FIND-011 | 4 |
| **MASVS-PLATFORM** | FIND-004, FIND-005, FIND-008 | 3 |
| **MASVS-RESILIENCE** | FIND-002, FIND-012 | 2 |

### Détail des mappings clés

**FIND-001 — MASVS-NETWORK-1**  
*"Les données sont chiffrées sur le réseau en utilisant TLS."*  
`APICreds2Activity` transmet des credentials via HTTP sans chiffrement → violation directe.

**FIND-002 — MASVS-RESILIENCE-2**  
*"Le mode debug est désactivé dans les builds de release."*  
`android:debuggable=true` dans le manifeste → attachement JDWP possible.

**FIND-003 — MASVS-STORAGE-4**  
*"Aucune donnée sensible ne doit être incluse dans les sauvegardes automatiques."*  
`android:allowBackup=true` → extraction via `adb backup` sans root.

**FIND-004 — MASVS-PLATFORM-1**  
*"L'application utilise les mécanismes IPC de manière sécurisée."*  
ContentProvider exporté sans permission → accès depuis une app tierce malveillante.

**FIND-005 — MASVS-PLATFORM-2**  
*"JavaScript est désactivé dans les WebViews sauf si explicitement nécessaire."*  
`setJavaScriptEnabled(true)` sans validation de l'URI → risque XSS.

---

## 9. Faux positifs identifiés

### FP-001 — `http://schemas.android.com` (Linked URLs)

**Signalé par** : Yaazhini > Linked URLs  
**Description** : L'URL `http://schemas.android.com` est détectée comme une URL HTTP hardcodée.  
**Pourquoi c'est un faux positif** : Il s'agit d'un **namespace XML Android standard** (`xmlns:android="http://schemas.android.com/apk/res/android"`) présent dans tous les fichiers de ressources et manifestes Android. Cette URL n'est jamais appelée à l'exécution et ne constitue aucun risque réseau.

### FP-002 — Protection copier-coller (FIND-008 partiel)

**Signalé par** : Yaazhini > Information > 34 fichiers XML  
**Description** : 34 champs EditText signalés sans protection copier-coller.  
**Nuance** : Parmi ces 34 champs, certains sont des champs de saisie **non sensibles** (texte générique, recherche) pour lesquels la restriction du copier-coller serait contre-productive. Seuls les champs de saisie de credentials et tokens constituent des vulnérabilités réelles. Il s'agit donc d'un **faux positif partiel**.

---

## 10. Recommandations prioritaires

### 🔴 CRITIQUE — Sécuriser toutes les communications réseau

**Findings adressés** : FIND-001, FIND-007, FIND-010

- Migrer l'ensemble des appels réseau vers **HTTPS (TLS 1.2 minimum)**
- Implémenter le **certificate pinning** via `OkHttp CertificatePinner`
- Configurer un `NetworkSecurityConfig` Android : `android:cleartextTrafficPermitted="false"`
- Ne jamais transmettre de credentials dans des requêtes HTTP en clair

```xml
<!-- res/xml/network_security_config.xml -->
<network-security-config>
    <base-config cleartextTrafficPermitted="false" />
</network-security-config>
```

### 🔴 CRITIQUE — Supprimer les secrets hardcodés

**Findings adressés** : FIND-009, FIND-011

- Auditer le code source complet pour tous les credentials, clés API et tokens hardcodés
- Migrer vers **Android Keystore** pour le stockage local des secrets
- Utiliser **EncryptedSharedPreferences** (Jetpack Security) pour les préférences sensibles
- **Révoquer immédiatement** tous les credentials exposés

### 🟠 URGENT — Corriger les configurations du manifeste

**Findings adressés** : FIND-002, FIND-003, FIND-004

```xml
<!-- AndroidManifest.xml — build de release -->
<application
    android:debuggable="false"        <!-- FIND-002 -->
    android:allowBackup="false"       <!-- FIND-003 -->
    ...>

<!-- Pour le ContentProvider — FIND-004 -->
<provider
    android:exported="false"
    .../>
```

### 🟡 IMPORTANT — Corriger les vulnérabilités WebView et UI

**Findings adressés** : FIND-005, FIND-008, FIND-012

- Désactiver JavaScript dans les WebViews si non nécessaire
- Valider strictement les URI schemes acceptés
- Utiliser `inputType="textPassword"` pour tous les champs sensibles
- Activer **ProGuard/R8** dans les builds de release pour l'obfuscation

---

## 11. Comparaison BeVigil vs Yaazhini

| Critère | BeVigil | Yaazhini |
|---|---|---|
| **Type d'analyse** | Externe (cloud) | Locale (desktop) |
| **Profondeur** | Superficielle (OSINT) | Approfondie (décompilation) |
| **Facilité d'utilisation** | ✅ Très facile (web) | ✅ Facile (desktop) |
| **Détection vulnérabilités** | Partielle (faux négatifs) | Complète (arbre détaillé) |
| **Accès au code source** | ❌ Non | ✅ Oui (jadx intégré) |
| **Score de risque global** | ✅ Oui (CVSS-based) | ❌ Non |
| **Analyse réseau/OSINT** | ✅ Domaines, endpoints | ❌ Limité |
| **Faux positifs notables** | Peu | Namespace XML, champs non sensibles |
| **Findings pour DIVA** | 2 findings | 65 items (dont 10 findings critiques) |

**Conclusion** : BeVigil et Yaazhini sont **complémentaires**. BeVigil fournit une vue externe rapide (réputation, exposition réseau, OSINT), tandis que Yaazhini offre une analyse statique locale détaillée indispensable pour identifier toutes les vulnérabilités du code. L'utilisation des deux outils permet de couvrir un spectre d'analyse plus large.

---

## 12. Points clés d'apprentissage

1. **Les outils automatisés ont des limites** : BeVigil a donné un score de 8.2/10 à DIVA malgré ses 11+ vulnérabilités intentionnelles. Toujours compléter une analyse automatisée par une inspection manuelle.

2. **Le manifeste Android est une surface d'attaque critique** : Trois vulnérabilités Medium proviennent directement de mauvaises configurations dans `AndroidManifest.xml` (`debuggable`, `allowBackup`, `exported`).

3. **L'analyse statique ne remplace pas l'analyse dynamique** : Certains findings (FIND-009, FIND-011) ont été marqués "À confirmer" car ils nécessitent une vérification en exécution réelle pour être validés.

4. **OWASP MASVS est le référentiel de référence** : Tous les findings peuvent être rattachés à une exigence MASVS, ce qui facilite la communication avec les équipes de développement et la priorisation des corrections.

5. **Distinction vrais positifs / faux positifs** : La maîtrise technique (ex : comprendre que `http://schemas.android.com` est un namespace XML) est indispensable pour éviter de polluer un rapport avec des faux positifs.

---

## 13. Références

| Ressource | URL |
|---|---|
| OWASP MASVS v2 | https://mas.owasp.org/MASVS/ |
| OWASP MASTG | https://mas.owasp.org/MASTG/ |
| OWASP Mobile Top 10 | https://owasp.org/www-project-mobile-top-10/ |
| DIVA (dépôt GitHub) | https://github.com/payatu/diva-android |
| BeVigil | https://bevigil.com |
| Yaazhini | https://www.vegabird.com/yaazhini/ |
| Android Keystore | https://developer.android.com/privacy-and-security/keystore |
| Android Network Security Config | https://developer.android.com/privacy-and-security/security-config |

