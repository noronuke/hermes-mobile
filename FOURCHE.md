# Fourche noronuke/hermes-mobile

Reprise de [Hy4ri/hermes-mobile](https://github.com/Hy4ri/hermes-mobile) (Apache 2.0), version amont dans `VERSION_AMONT`.

Ce qui change par rapport à l'amont :

- identifiant `com.noronuke.hermes`, nom « Hermes », signature propre (clé hors dépôt sur le NUC, `~/.hermes/cles/hermes-noronuke.keystore`, en secret GitHub `KEYSTORE_B64` ; mot de passe au coffre Hermes sous `HERMES_MOBILE_KEYSTORE_PASSWORD`, en secret GitHub `KEYSTORE_PASSWORD`) ;
- traduction française complète (`app/src/main/res/values-fr/`), produite par `outils/traduire_strings.py` puis relue ;
- menu réduit à Conversation, Historique, Projets, Modèles, Réglages (`Allegement.kt`) ; écrans de configuration, secrets, journaux, fichiers, passerelle et fournisseurs refusés depuis tout point d'entrée ;
- présentation dans l'esprit de l'appli Claude : palette « Argile » à la place du thème par défaut, bulle de l'utilisateur neutre, réponses en police à empattements ;
- une seule fabrique (`.github/workflows/compilation.yml`) au lieu de celles de l'amont.

Remettre à niveau sur l'amont : `git fetch amont && git merge amont/main`, régler les conflits (en général `strings.xml`, `Navigation.kt`), relancer `outils/traduire_strings.py` pour les chaînes nouvelles (il ne retraduit pas ce qui l'est déjà), mettre à jour `VERSION_AMONT`.
