Projet P9 – Évaluation du risque de diabète (microservices)

Application composée de 4 microservices + Gateway + UI Thymeleaf :

frontendservice : interface Web (Spring MVC + Thymeleaf), login par formulaire (Spring Security).

patientservice : CRUD patients (Spring Data JPA, base relationnelle).

notesservice : notes médicales (MongoDB).

riskservice : calcule le niveau de risque à partir des patients + notes.

gateway : point d’entrée unique, route /api/** vers les services.

Fonctionnement (vue d’ensemble)

L’utilisateur ouvre /patients/{id} dans le frontend.

Le frontend appelle le Gateway :

/api/patients/{id} → patientservice

/api/patients/{id}/notes → notesservice

/api/risk/{id} → riskservice

riskservice orchestre : récupère patient + notes, passe le texte des notes au RiskCalculator (regex pré-compilées, comptage de déclencheurs distincts, calcul de l’âge → règles métier None | Borderline | In Danger | Early onset).

Le frontend affiche patient, notes et risque.

Sécurité (frontend)

Form login :

GET /login (vue Thymeleaf), POST /login (traité par Spring Security)

Après succès : redirection vers /patients

Utilisateurs en mémoire (démo) : user/password, admin/admin.

Démarrage rapide

Lancer les bases : MongoDB (+ une base relationnelle pour patientservice).

Démarrer : patientservice → notesservice → riskservice → gateway → frontendservice.

Ouvrir http://localhost:8085/login puis se connecter.