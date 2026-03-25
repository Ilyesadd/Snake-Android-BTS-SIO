# Snake Android — BTS SIO

Jeu Snake développé en Java avec Android Studio dans le cadre du BTS SIO.

## Aperçu

- Grille 20×20 qui s'adapte à tous les écrans
- Score en temps réel + meilleur score sauvegardé
- Contrôles : swipe tactile ou boutons directionnels (D-Pad)
- Thème sombre

## Technologies utilisées

| Technologie | Usage |
|---|---|
| Java | Langage principal |
| Android Studio | IDE de développement |
| Canvas 2D | Dessin du jeu |
| Handler + Runnable | Boucle de jeu |
| SharedPreferences | Sauvegarde du meilleur score |
| ArrayList | Corps du serpent |

## Structure du projet

```
app/src/main/
├── AndroidManifest.xml
├── java/com/btssio/snake/
│   ├── MainActivity.java       → UI, boutons, scores
│   └── SnakeView.java          → logique du jeu, dessin
└── res/
    ├── layout/activity_main.xml
    └── values/themes.xml
```

## Installation

1. Cloner le repo 

2. Ouvrir le projet dans **Android Studio**

3. Lancer sur un émulateur ou un téléphone réel avec **Run ▶**

> Min SDK : API 24 (Android 7.0)

## Règles du jeu

- Le serpent se déplace en continu
- Mange les pommes 🔴 pour grandir et marquer des points
- Évite les murs et ton propre corps
- Game Over si collision !

## Concepts Java / Android abordés

- Héritage (`extends View`) et `@Override`
- `ArrayList` pour gérer le corps du serpent
- `Handler` + `Runnable` (équivalent de `setInterval`)
- Interface Java (pattern callback)
- `Canvas` / `Paint` pour le dessin 2D
- `SharedPreferences` pour la persistance des données
- Cycle de vie d'une `Activity` (`onCreate`, `onPause`)
- Gestion des événements tactiles (`onTouchEvent`)

## Auteurs

**Elyas, Mohammed, Salah** — BTS SIO
