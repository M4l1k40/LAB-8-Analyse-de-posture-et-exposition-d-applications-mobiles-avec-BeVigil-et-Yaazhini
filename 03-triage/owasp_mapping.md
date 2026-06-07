# Mapping OWASP MASVS — DIVA Application
**Standard** : OWASP Mobile Application Security Verification Standard (MASVS v2)  
**Guide** : OWASP Mobile Application Security Testing Guide (MASTG)  
**Date** : 07-JUN-2026

---

## FIND-001 : Communication réseau en clair
- **Catégorie OWASP** : MASVS-NETWORK
- **Référence spécifique** : MASVS-NETWORK-1
- **Exigence** : Les données sont chiffrées sur le réseau en utilisant TLS.
- **Justification** : APICreds2Activity transmet des credentials via HTTP sans chiffrement, exposant les données en transit à toute interception sur le réseau local.

---

## FIND-002 : Mode débogage activé en production
- **Catégorie OWASP** : MASVS-RESILIENCE
- **Référence spécifique** : MASVS-RESILIENCE-2
- **Exigence** : Le mode debug est désactivé dans les builds de release.
- **Justification** : android:debuggable=true permet l'attachement d'un débogueur JDWP et l'exécution de code arbitraire dans le contexte de l'application, violant directement les exigences anti-tamper de MASVS-RESILIENCE.

---

## FIND-003 : Backup Android non restreint
- **Catégorie OWASP** : MASVS-STORAGE
- **Référence spécifique** : MASVS-STORAGE-4
- **Exigence** : Aucune donnée sensible ne doit être incluse dans les sauvegardes automatiques.
- **Justification** : android:allowBackup=true autorise l'extraction complète du répertoire de données de l'application via adb backup, contournant les mécanismes de protection du stockage.

---

## FIND-004 : ContentProvider exporté sans permission
- **Catégorie OWASP** : MASVS-PLATFORM
- **Référence spécifique** : MASVS-PLATFORM-1
- **Exigence** : L'application utilise les mécanismes IPC de manière sécurisée.
- **Justification** : Un ContentProvider exporté sans permission constitue un canal IPC non protégé permettant à toute application tierce d'accéder aux données internes, violant le principe de moindre privilège.

---

## FIND-005 : JavaScript activé dans WebView
- **Catégorie OWASP** : MASVS-PLATFORM
- **Référence spécifique** : MASVS-PLATFORM-2
- **Exigence** : JavaScript est désactivé dans les WebViews sauf si explicitement nécessaire.
- **Justification** : setJavaScriptEnabled(true) dans InputValidation2URISchemeActivity sans validation de l'URI chargé ouvre la porte aux attaques XSS et à l'injection de code JavaScript malveillant.

---

## FIND-006 : Stockage de données sur mémoire externe
- **Catégorie OWASP** : MASVS-STORAGE
- **Référence spécifique** : MASVS-STORAGE-2
- **Exigence** : Aucune donnée sensible n'est stockée en dehors du bac à sable de l'application.
- **Justification** : L'utilisation de getExternalStorage* dans 9 occurrences place des données applicatives dans un espace accessible à toute application disposant de READ_EXTERNAL_STORAGE, sans chiffrement.

---

## FIND-007 : URL hardcodée en HTTP
- **Catégorie OWASP** : MASVS-NETWORK
- **Référence spécifique** : MASVS-NETWORK-1
- **Exigence** : Toutes les communications réseau utilisent TLS avec une configuration sécurisée.
- **Justification** : http://payatu.com hardcodé dans le bytecode utilise HTTP non chiffré et expose la topologie réseau de l'application à la rétro-ingénierie, cumulant deux violations MASVS-NETWORK-1.

---

## FIND-008 : Absence de protection copier-coller
- **Catégorie OWASP** : MASVS-PLATFORM
- **Référence spécifique** : MASVS-PLATFORM-3
- **Exigence** : Les mécanismes de partage de données inter-applications sont correctement contrôlés.
- **Justification** : L'absence d'inputType=textPassword sur 34 champs EditText permet la capture du contenu via le presse-papiers système par toute application en arrière-plan.

---

## FIND-009 : Credentials API dans les assets
- **Catégorie OWASP** : MASVS-STORAGE
- **Référence spécifique** : MASVS-STORAGE-1
- **Exigence** : Aucune donnée sensible n'est stockée dans le code source ou les ressources de l'application.
- **Justification** : Des credentials codés en dur dans les assets sont extractibles par simple décompilation de l'APK sans nécessiter de privilèges particuliers, constituant une exposition directe de secrets.

---

## FIND-010 : Absence de certificate pinning
- **Catégorie OWASP** : MASVS-NETWORK
- **Référence spécifique** : MASVS-NETWORK-2
- **Exigence** : L'application valide le certificat TLS et la chaîne de confiance côté serveur.
- **Justification** : L'absence de certificate pinning permet l'interception HTTPS via un proxy (Burp Suite) en substituant un certificat auto-signé, ce qui a été démontré dans les TPs précédents.

---

## FIND-011 : SharedPreferences non chiffrées
- **Catégorie OWASP** : MASVS-STORAGE
- **Référence spécifique** : MASVS-STORAGE-1
- **Exigence** : Les données sensibles sont stockées de manière chiffrée.
- **Justification** : Les SharedPreferences stockées en clair dans un fichier XML sont lisibles sur tout appareil rooté ou via backup ADB, violant l'exigence de protection des données au repos.

---

## FIND-012 : Code source non obfusqué
- **Catégorie OWASP** : MASVS-RESILIENCE
- **Référence spécifique** : MASVS-RESILIENCE-3
- **Exigence** : L'application applique une obfuscation du code pour compliquer la rétro-ingénierie.
- **Justification** : Le code Java décompilé via jadx (intégré à Yaazhini) est lisible et structuré, permettant d'identifier directement la logique métier, les algorithmes et les endpoints sans aucune difficulté.

---

## Tableau récapitulatif

| ID | Élément | Catégorie MASVS | Référence |
|----|---------|-----------------|-----------|
| FIND-001 | Communication en clair | MASVS-NETWORK | MASVS-NETWORK-1 |
| FIND-002 | Debug activé | MASVS-RESILIENCE | MASVS-RESILIENCE-2 |
| FIND-003 | Backup non restreint | MASVS-STORAGE | MASVS-STORAGE-4 |
| FIND-004 | ContentProvider exposé | MASVS-PLATFORM | MASVS-PLATFORM-1 |
| FIND-005 | JavaScript WebView | MASVS-PLATFORM | MASVS-PLATFORM-2 |
| FIND-006 | Stockage externe | MASVS-STORAGE | MASVS-STORAGE-2 |
| FIND-007 | URL HTTP hardcodée | MASVS-NETWORK | MASVS-NETWORK-1 |
| FIND-008 | Copier-coller | MASVS-PLATFORM | MASVS-PLATFORM-3 |
| FIND-009 | Credentials dans assets | MASVS-STORAGE | MASVS-STORAGE-1 |
| FIND-010 | Pas de cert pinning | MASVS-NETWORK | MASVS-NETWORK-2 |
| FIND-011 | SharedPreferences clair | MASVS-STORAGE | MASVS-STORAGE-1 |
| FIND-012 | Code non obfusqué | MASVS-RESILIENCE | MASVS-RESILIENCE-3 |
