# Gestionnaire de Flotte Automobile

## Groupe
PHILIPPE Mathis ALVES Alexandre

## Domaine métier
Application de gestion d'une flotte automobile : véhicules légers, lourds et spéciaux,
chauffeurs et missions de transport. Permet le CRUD complet sur chaque entité, l'affectation
de véhicules et chauffeurs aux missions, le suivi des maintenances et des statistiques
dynamiques calculées en temps réel.

## Interface choisie : Swing (Option A)
Nous avons choisi Swing pour sa simplicité de déploiement (pas de serveur Tomcat requis),
la richesse de ses composants (`JTable` avec tri, `JDialog`, `JTabbedPane`) et sa compatibilité
avec Java standard.

## Compilation et exécution

### Prérequis
- Java 17 ou supérieur
- Aucune dépendance externe

### Compiler (depuis le dossier `flotte/`)
```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
```

### Lancer
```bash
java -cp out Main
```
Les fichiers CSV dans `resources/` sont chargés automatiquement au démarrage.
Si aucun fichier n'existe, l'application démarre vide (utiliser Données → Charger démo).

## Fonctionnalités implémentées
- [x] CRUD complet : Véhicules, Chauffeurs, Missions
- [x] Filtrage multicritères (type, disponibilité, marque, permis, statut…)
- [x] Tri dynamique sur toutes les colonnes (TableRowSorter + Comparator)
- [x] Affectation véhicule + chauffeur à une mission avec vérifications métier
- [x] Clôture de mission avec mise à jour du kilométrage et libération des ressources
- [x] 8 statistiques dynamiques calculées par Streams (km, coûts, répartition…)
- [x] Persistance CSV avec chargement au démarrage et sauvegarde à la fermeture
- [x] 4 exceptions métier custom
- [x] Sauvegarde proposée automatiquement à la fermeture

## Architecture OO — checklist cahier des charges
| Critère | Fichier(s) |
|---|---|
| Classe abstraite `Vehicule` | `model/abstracts/Vehicule.java` |
| Classe abstraite `Mission` | `model/abstracts/Mission.java` |
| Interface `Assignable` | `model/interfaces/Assignable.java` |
| Interface `Maintenable` | `model/interfaces/Maintenable.java` |
| Interface `Trackable` | `model/interfaces/Trackable.java` |
| Interface `Facturable` | `model/interfaces/Facturable.java` |
| Generic borné `Registre<T extends Entite>` | `util/Registre.java` |
| Streams + Lambdas | `controller/FlotteController.java` |
| 4 exceptions custom | `model/exceptions/` |
| Persistance CSV | `util/CsvUtil.java` + `resources/` |
| 4 vues Swing | `view/` |

## Répartition des tâches
| Membre | Tâches |
|---|---|
| Alexandre | Interfaces, classes abstraites, enums, Classes concrètes |
| Mathis | Registre, FlotteController, Vues Swing, CsvUtil, README|
