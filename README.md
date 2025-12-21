# Projet PAA : Réseau de Distribution d'Electricité

**Auteurs:** EULLAFFROY Nathan & RASOARISOA Bayrone

## Description

Programme de gestion et d'optimisation de réseaux électriques permettant de distribuer efficacement l'énergie entre générateurs et consommateurs tout en minimisant les coûts (dispersion et surcharges).

## Classe principale

**Classe à exécuter:** `up.mi.paa.app.Main`

## Utilisation

### Mode 1: Construction manuelle (Partie 1)

```bash
java up.mi.paa.app.Main
```

Ce mode permet de construire interactivement un réseau en ajoutant manuellement générateurs, maisons et connexions.

### Mode 2: Chargement depuis fichier (Partie 2)

```bash
java up.mi.paa.app.Main fichier.txt [lambda]
```

**Paramètres:**
- `fichier.txt` : chemin vers le fichier de description du réseau
- `lambda` : (optionnel) coefficient de pénalisation des surcharges (défaut: 10)

**Exemple:**
```bash
java up.mi.paa.app.Main exemple_reseau.txt 10
```

## Format du fichier réseau

Le fichier doit respecter l'ordre suivant : générateurs, maisons, puis connexions.

```
generateur(nom,capacite).
maison(nom,TYPE).
connexion(element1,element2).
```

**Types de maisons:**
- `BASSE` : 10 kW
- `NORMAL` : 20 kW
- `FORTE` : 40 kW

**Exemple complet:**
```
generateur(gen1,60).
generateur(gen2,45).
maison(maison1,NORMAL).
maison(maison2,BASSE).
connexion(gen1,maison1).
connexion(gen2,maison2).
```

## Fonctionnalités implémentées

### ✅ Partie 1 (toutes implémentées)

- ✅ Création manuelle de générateurs
- ✅ Création manuelle de maisons (3 types)
- ✅ Ajout/suppression de connexions
- ✅ Validation du réseau
- ✅ Calcul du coût (Disp + lambda x Surcharge)
- ✅ Modification de connexions
- ✅ Affichage détaillé du réseau

### ✅ Partie 2 (toutes implémentées)
- ✅ Lecture de fichier réseau avec validation complète
- ✅ Gestion des erreurs avec numéro de ligne
- ✅ Résolution auto avec 2 algo :
    - **Algo naïf**: échanges aléatoires simples
    - **Algo glouton amélioré**: optimisation intelligente (MEILLEUR)
- ✅ Sauvegarde de solutions
- ✅ Gestion complète des exceptions
- ✅ Validation des entrées utilisateur

### ✅ Amélioration suite aux retours du premier bilan

1. ✅ **Problème modification connexion**: Méthode `modifierConnexion()` avec vérification complète
2. ✅ **Package algo**: Création du package `up.mi.paa.algo` avec interface et implémentations
3. ✅ **Commentaires**: Documentation Javadoc complète pour toutes les classes
4. ✅ **Organisation HashMap**: Encapsulation avec getters non modifiables, méthodes utilitaires
5. ✅ **Méthodes Surcharge et Disp**: Séparation en `calculerDisp()` et `calculerSurcharge()`
6. ✅ **Voir Menu**: Classe `Menu` dédiée dans package `up.mi.paa.cli`

## Architecture
```
src/
└── up/mi/paa/
    ├── app/
    │   ├── Main.java              # Point d'entrée
    │   └── AppFX.java             # Point d'entrée
    ├── cli/
    │   └── Menu.java              # Interface utilisateur
    ├── model/
    │   ├── Network.java           # Réseau électrique
    │   ├── Generator.java         # Générateur
    │   ├── House.java             # Maison
    │   └── HouseType.java         # Types de maisons
    ├── algo/
    │   ├── SolverAlgorithm.java   # Interface algorithmes
    │   ├── NaiveSolver.java       # Algorithme naïf
    │   └── GreedySolver.java      # Algorithme glouton (MEILLEUR)
    ├── io/
    │   └── NetworkFileHandler.java # Lecture/écriture fichiers
    ├── gui/
    │   └── NetworkFXApp.java              
    └── exception/
        └── NetworkParseException.java # Exceptions parsing
```

## Calcul du coût

Le coût d'un réseau S = {M, G, C} est calculé ainsi :

**Cout(S) = Disp(S) + lambda x Surcharge(S)**

Où:

- **Disp(S)** = Σ |u_g - ū| : somme des écarts à la moyenne des taux d'utilisation
- **Surcharge(S)** = Σ max(0, (L_g - C_g)/C_g) : pénalisation des surcharges
- **lambda** : coefficient de sévérité (typiquement 10)
- **u_g** = L_g / C_g : taux d'utilisation du générateur g
- **L_g** : charge actuelle du générateur g
- **C_g** : capacité maximale du générateur g

## Algorithmes d'optimisation

### Algorithme Naïf
- Essaie k fois de changer aléatoirement une connexion
- Garde le changement si le coût diminue
- Simple mais peut être lent

### Algorithme Glouton Amélioré (RECOMMANDE)
- Identifie les générateurs sur/sous-utilisés
- Déplace intelligement les maisons pour équilibrer
- Réduit activement les surcharges
- Combine plusieurs stratégies
- **Nettement plus efficace que l'algorithme naïf**

## Compilation

```bash
# Depuis la racine du projet
javac -d bin -sourcepath src src/up/mi/paa/app/Main.java
```

## Exécution

### 1. Mode Standard

```bash
# Mode manuel
java -cp bin up.mi.paa.app.Main

# Mode fichier
java -cp bin up.mi.paa.app.Main exemple_reseau.txt 10
```

### 2. Mode Interface Graphique

Nécessite le SDK JavaFX dans le dossier lib/javafx-sdk

```bash
# Mac / Linux
java --module-path lib/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin up.mi.paa.app.AppFX

# Windows
java --module-path lib/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin up.mi.paa.app.AppFX
```

## Exemples de résultats

Pour le réseau exemple fourni:
- **Coût initial**: ~0.5-1.5 (selon connexions initiales)
- **Après optimisation (naïf, 1000 iter)**: ~0.2-0.5
- **Après optimisation (glouton, 1000 iter)**: ~0.1-0.3

L'algorithme glouton converge généralement en moins de 200 itérations.

## Tests

Fichiers de test fournis:
- `exemple_reseau.txt` : réseau de base du sujet
- Créez vos propres fichiers pour tester différents scénarios

## Tests Unitaires

Une suite complète de tests unitaires (JUnit 5) a été implémentée pour garantir la robustesse de l'application.
Les fichiers de tests se trouvent dans `test/up/mi/paa/test/`.

### Couverture des tests
* **Modèle (`NetworkTest`)** : Vérifie la cohérence du réseau, l'unicité des connexions et la précision des calculs de coûts (Disp et Surcharge).
* **Parsing (`FileHandlerTest`)** : Teste la robustesse du chargement de fichier (gestion des erreurs de syntaxe, ordre incorrect, fichiers inexistants).
* **Algorithme (`AlgoTest`)** : Vérifie que l'optimisation ne dégrade jamais la solution (non-régression) et respecte les contraintes du réseau.

### Lancer les tests
Les tests peuvent être exécutés via la console JUnit fournie dans le dossier `lib/`.

**Commande d'exécution :**
```bash
java -jar lib/junit-platform-console-standalone-1.9.0.jar -cp bin --scan-classpath
```

## Problèmes connus

Aucun problème connu. Toutes les fonctionnalités demandées sont implémentées et testées.

## Notes du développement

### Choix de conception

1. **Collections non modifiables**: Les getters retournent des copies pour protéger l'intégrité
2. **Séparation des responsabilités**: Chaque classe a un rôle bien défini
3. **Exceptions**: Utilisation d'exceptions personnalisées pour une meilleure gestion d'erreurs

### Performance

- Algo naïf: 0(k) où k = nombre d'itérations
- Algo glouton: 0(k x n x m) mais converge rapidement en pratique
    - n = nombre de maisons
    - m = nombre de génératuers

## Améliorations futures possibles

- Algo plus sophistiqués
- Parallélisation pour grands réseaux
- Visualisation graphie du réseau
- Export vers différents formats (JSON, XML)
