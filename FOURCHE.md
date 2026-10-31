# Fourche noronuke/hermes-mobile

Reprise de [Hy4ri/hermes-mobile](https://github.com/Hy4ri/hermes-mobile) (Apache 2.0), version amont dans `VERSION_AMONT`.

Ce qui change par rapport à l'amont :

- identifiant `com.noronuke.hermes`, nom « Hermes », signature propre (clé hors dépôt sur le NUC, `~/.hermes/cles/hermes-noronuke.keystore`, en secret GitHub `KEYSTORE_B64` ; mot de passe au coffre Hermes sous `HERMES_MOBILE_KEYSTORE_PASSWORD`, en secret GitHub `KEYSTORE_PASSWORD`) ;
- traduction française complète (`app/src/main/res/values-fr/`), produite par `outils/traduire_strings.py` puis relue ;
- menu réduit à Conversation, Historique, Projets, Modèles, Réglages (`Allegement.kt`) ; écrans de configuration, secrets, journaux, fichiers, passerelle et fournisseurs refusés depuis tout point d'entrée ;
- présentation dans l'esprit de l'appli Claude : palette « Argile » à la place du thème par défaut, bulle de l'utilisateur neutre, réponses en police à empattements ;
- vérification de mise à jour interne coupée (elle interrogeait l'amont, dont l'APK n'est pas installable par-dessus le nôtre) : les mises à jour passent par Obtainium ;
- une seule fabrique (`.github/workflows/compilation.yml`) au lieu de celles de l'amont.

Remise à niveau automatique : `~/.hermes/scripts/hermes-mobile-amont.sh` (cron hebdomadaire), qui s'arrête et prévient en cas de conflit. À la main : `git fetch amont && git merge amont/main`, régler les conflits (en général `strings.xml`, `Navigation.kt`), relancer `outils/traduire_strings.py` pour les chaînes nouvelles (il ne retraduit pas ce qui l'est déjà), mettre à jour `VERSION_AMONT`.

## Présentation « Maquis » (25/10/2026)

Refonte visuelle demandée par Baptiste (« boutons arrondis dans des carrés »), d'après la charte commune des applis (`Projet_Hermes/06 — Application/_IA/Charte des applis (26-10).md`). Aucune fonction ne change ; seuls thème, couleurs, formes, espacements et libellés affichés.

- **Palette** `theme/presets/DefaultScheme.kt` : crème `#F2EFE6`, papier `#FAF8F2`, filet `#DFDCCF`, encre `#1F2A24`, accent bordeaux `#7D2E3A` (sombre : nuit `#151B17`, texte crème, bordeaux éclairci `#E7A1AB`). Contrastes calculés : encre/fond 12,9:1 (sombre 14,3:1), accent/fond 7,9:1 (8,4:1), toutes les paires des tests ≥ 4,5:1. Pas de voile teinté sur les surfaces surélevées (`Theme.kt`).
- **Formes** `theme/Shapes.kt`, objet `Formes` : Fin 6, Contrôle 12, Carte 16, Bulle 20, Feuille 28, Puce ronde ; les rayons écrits en dur sont ramenés à ces familles. Règle : jamais un élément arrondi posé dans un carré.
- **Écrans** : cartes de conversation papier au filet fin (plus de cadre épais coloré), pastilles de 10 dp (bleu, vert, gris visible au repos), étiquette d'état en puce discrète ; barre de saisie flottante de 28 dp sur papier ; bulle envoyée en bordeaux, coin de départ en bas à droite ; raisonnements et outils en blocs papier au filet ; recherche en pilule de 48 dp ; tiroir à pilule de sélection décollée du bord, icônes au trait, en-tête au logo d'Hermes (silhouette de l'icône, à l'encre, posée sur le fond du tiroir ; le couvercle de piano coloré a été retiré le 26/10) ; conversation neuve illustrée par le pianiste du site (un WebP de 32 Ko, `res/drawable-nodpi/fourche_illu_*`) ; libellés anglais restants traduits (`fourche_*` dans les deux `strings.xml`).
- **Fabrique d'essai** `.github/workflows/essai.yml` : toute poussée sur une branche `essai/**` compile et passe toute la suite de tests en `continue-on-error`, avec le décompte dans le résumé. La suite complète a 35 échecs **hérités** de la fourche (vérification de mise à jour coupée, verrou de profil, réglages cosmétiques retirés), identiques sur `main` avant la refonte (branche `essai/temoin`) ; la fabrique principale ne lance que les tests des thèmes.
- **Captures avant/après** : hors dépôt (le dépôt est public et les titres de conversation sont privés), dans `Projet_Hermes/06 — Application/_IA/coup-de-frais-2610/hermes/`.
- Reste un écart assumé avec la charte : la prose des réponses garde sa police à empattements (le « style Claude » que Baptiste aime) ; à repasser en police système s'il le préfère.

## Connexion : garde « réseau local » d'Android 17 (26/10/2026)

Tailscale donne au NUC une adresse 100.64/10, qu'Android 17 range dans le « réseau local » : une appli
qui vise l'API 37 doit avoir la permission `ACCESS_LOCAL_NETWORK`, sinon le système bloque la connexion
sans rien dire. La garde de démarrage (`MainActivity.SavedSessionLanGate`) décidait de vérifier cette
permission en résolvant le nom du serveur ; si la résolution échouait au lancement, elle concluait
« pas de réseau local » et laissait passer. Depuis le 26/10, un nom MagicDNS (`*.ts.net`) est reconnu
comme local sur le nom seul (`LocalNetwork.isTailscaleMagicDnsName`) : la permission est toujours
vérifiée, et demandée si elle manque, au lieu d'un « Non connecté » muet.

### Captures du tiroir (26/10/2026)

Rendus hors appareil (Robolectric, branches `essai/captures-tiroir-avant` et `-apres`), libellés en anglais (langue du rendu), données fictives.

| Avant (couvercle coloré) | Après (logo d'Hermes) |
|---|---|
| ![](fourche/captures/2610/tiroir-avant-clair.jpg) | ![](fourche/captures/2610/tiroir-apres-clair.jpg) |
| ![](fourche/captures/2610/tiroir-avant-sombre.jpg) | ![](fourche/captures/2610/tiroir-apres-sombre.jpg) |

Logo : silhouette de l'avant-plan de l'icône (`res/drawable-nodpi/fourche_logo.webp`, 9 Ko), teinte `onSurface` : encre `#1F2A24` sur papier en clair (≈ 13:1), crème `#ECE8DC` sur nuit en sombre (≈ 14:1). Aucun cadre ni disque autour.

## Dictée et provenance des conversations (30/10/2026)

Demandé par Baptiste d'après le comportement du client Desktop : dicter, voir le texte s'écrire dans le
champ, le compléter, re-dicter ; et, à côté, pouvoir lâcher un vocal qui part tout de suite sans attendre
la transcription (le geste de Float). Son téléphone n'a pas de reconnaissance vocale Google : tout passe
par la transcription du serveur (`POST /api/audio/transcribe`), comme avant.

- **Tap sur le micro = dictée** (`startDictation()` dans `ChatMediaLaunchers`) : enregistrement mains libres (le bouton d'action devient l'arrêt), le texte transcrit s'écrit à la suite du champ au lieu de partir, et l'écran le dit une fois par dictée. **Appui long = vocal direct**, inchangé.
- **File d'attente** (`ChatViewModel.sendVoiceNote(file, insertIntoComposer)`) : un vocal enregistré pendant qu'une transcription tourne n'est plus jeté, il attend son tour et se transcrit dans l'ordre d'enregistrement. La session, la connexion et la portée de profil sont retenues à l'enregistrement : si l'une a bougé, le texte revient au champ plutôt que de partir dans la mauvaise conversation.
- **Provenance** : chaîne nouvelle `voiceTranscriptForComposer` (`ComposerInsert`, identifiant croissant pour que deux dictées identiques ne se perdent pas) et `consumeVoiceTranscript()` ; `ChatScreen` l'écrit avec `appendSpeechComposerDraft`.
- **Liste des conversations** : `Allegement.pucesSourceConversations` rallume la rangée de puces « provenance » sans les autres filtres coupés de la fourche, et chaque carte porte l'icône et le libellé de sa source (`sourceIcon()` / `sourceLabel()` dans `SessionCard.kt`).
- **Tests** : `ChatViewModelTest` — la file remplace l'ancien « le second vocal est jeté », plus un cas dictée qui vérifie que rien n'est envoyé. Les imports Google (`RecognizerIntent`, `SpeechInputHelper`) ne sont plus sur le chemin du micro.

## Conversations par provenance, vocaux sans blocage (31/10/2026)

Demandé par Baptiste le 30/10 (d'après le panneau du Desktop : ÉPINGLÉS, SESSIONS, SIGNAL, WEBHOOK, CRON JOBS).

- **Liste des conversations** : la liste principale devient la section « Desktop » (en-tête repliable, ouverte d'office, tout ce qui n'a pas sa propre section : Desktop, terminal, Telegram…). Dessous, des sections repliées par provenance, avec leur nombre : Signal, Webhooks, Tâches planifiées (les crons restent regroupés par tâche), Tâches ponctuelles (`oneshot`), Agents (`a2a`, `subagent`, `kanban`), Séances de test (`banc`, le banc d'amélioration d'Hermes). Une section vide n'apparaît pas. Elle ne charge ses conversations qu'à l'ouverture (50 par 50), via le paramètre serveur `sources`. Les puces de provenance (où figuraient « banc » et « oneshot ») sont retirées (`Allegement.pucesSourceConversations = false`).
- Code : `SOURCE_GROUPS` et `EXCLUS_LISTE_PRINCIPALE` (SessionsViewModel.kt), `SourceGroupHeader.kt`, `loadGroupPage()` ; les lignes des sections vivent dans la même liste `sessions` que la principale (épingler, renommer, supprimer marchent partout) mais sont écartées de `displaySessions`. La recherche, elle, couvre toujours tout (hors crons).
- **Vocaux** : le texte d'un vocal ne peut pas partir avant d'exister (la transcription se fait sur le serveur, `POST /api/audio/transcribe`) ; ce qui change : la transcription ne bloque plus rien (le micro redevient un micro pendant qu'elle tourne), un bandeau « Transcription en cours (n) » avec barre de progression s'affiche au-dessus de la barre de saisie, et un message confirme « Vocal en route » au lâcher. Les vocaux partent dans l'ordre dès que leur texte revient. Envoyer l'audio lui-même avant sa transcription demanderait un changement côté serveur (message à pièce audio puis texte rattaché) : non fait.
