# POC – Récupération des réunions Teams via Microsoft Graph (Spring Boot)

Ce projet est un **Proof of Concept (POC)** permettant :

- L’authentification via Microsoft (OAuth2 – flow délégué)
- L’appel à l’API Microsoft Graph
- La récupération des réunions Teams de l’utilisateur connecté
- La tentative de récupération des transcriptions (si autorisation admin accordée)

⚠️ La récupération des transcriptions nécessite la permission :
`OnlineMeetingTranscript.Read.All` avec consentement administrateur.

---

## 🚀 Fonctionnalités

- Connexion Microsoft (Entra ID)
- Récupération du profil utilisateur (`/poc/me`)
- Récupération des réunions Teams (`/poc/meetings`)
- Gestion propre du cas 403 si la transcription n’est pas autorisée

---

## 🧱 Stack technique

- Java 17+
- Spring Boot 3+
- Spring Security OAuth2 Client
- Microsoft Graph API

---

## 🔐 Configuration Microsoft Entra (Azure)

### 1️⃣ Créer une App Registration

Aller sur :

https://portal.azure.com  
→ Microsoft Entra ID  
→ Inscriptions d’applications  
→ Nouvelle inscription

Configuration :

- Nom : `Teams Transcript POC`
- Type de compte : Locataire unique


---

### 2️⃣ Créer un secret client

Application → Certificats et secrets → Nouveau secret client

⚠️ Copier immédiatement la **Value** (elle ne sera plus visible après).

---

### 3️⃣ Ajouter les permissions Microsoft Graph

API permissions → Ajouter une permission  
→ Microsoft Graph → Permissions déléguées

Ajouter :

- `User.Read`
- `OnlineMeetings.Read`
- `OnlineMeetingTranscript.Read.All`

⚠️ Selon la politique du tenant, un consentement administrateur peut être requis.

---

## 🔑 Variables d’environnement

Le projet nécessite les variables suivantes :

| Variable | Description |
|----------|------------|
| `AZURE_CLIENT_ID` | ID d’application (client) |
| `AZURE_CLIENT_SECRET` | Secret client |
| `AZURE_TENANT_ID` | ID du locataire (tenant) |

---

## 💻 Configuration des variables

### 🪟 Windows (PowerShell)

```powershell
setx AZURE_CLIENT_ID "votre-client-id"
setx AZURE_CLIENT_SECRET "votre-client-secret"
setx AZURE_TENANT_ID "votre-tenant-id"
``` 

---

## Tester l’API 

L’application démarre sur :
http://localhost:8080

Vérifier l’authentification :
http://localhost:8080/poc/me 

⚠️ Attention : Pas autorisé chez epitech

Récupérer les réunions Teams : 
http://localhost:8080/poc/meetings 

Tester la récupération des transcriptions :
http://localhost:8080/poc/meetings/{meetingId}/transcripts