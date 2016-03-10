## Notre Architecture

Tout d'abord voici l'arborescence de notre plugin Maven :

```
SpoonTester
│
└───src/main/java/fr/unice/polytech/devops
|   │
|   |
|   ├───selectors/          # Contient les selecteurs pour nos mutations
|   |
|   ├───transformation/     # contient nos processeurs Spoon qui appliquent differentes mutations
|   |
|   ├───utils/              # contient differentes fonctions utilitaires (parseurs XML, etc...)
|   |
|   └───AppMojo.java        # Le mojo qui est appelé après la phase de test de Maven
| 
└───pom.xml
```

### Package selectors
Dans le package `selectors` se trouvent les classes permettant de gérer où seront appliquées nos mutations sur le code du projet. Elles posèdent toutes une méthode `public boolean decide(Ct Element)` permettant de savoir s'il faut appliquer ou non une mutation, selon différents critères définis pour un selecteur donné.

### Package transformation
Dans le package `transformation` se trouvent les classes permettant d'appliquer les mutations sur les sources du projet qui appelle notre plugin. Toutes ces classes héritent de la classe `AbstractProcessor<CtElement>`, une classe abstraite de la bibliothèque `Spoon` et qui nous demande d'implémenter deux méthodes qui sont `boolean isToBeProcessed(CtElement candidate)` , permettant d'utiliser nos selecteurs, et `void process(CtElement candidate)`, permettant de modifier le source.

### AppMojo.java

Cette classe est l'entrée vers notre plugin, elle associée au goal `rapport`, sa méthode `execute ` se lance alors pendant la phase `generate`, et elle lance les différent mécanisme dont notre plugin a besoin à savoir : 

  - Lecture du fichier de configuration contenant les mutation à appliquer ;
  - Création du fichier temporaire qui va être modifié à chaque itération ;
  - Lancer `maven package` : cela aura comme effet de lancer maven récursivement, le test d’arrêt étant le nombre d'éléments présents dans le fichier temporaire ; 
  - Récuperer les résultats des tests après chaque itération ;
  - Génerer le fichier HTML.

Le choix d'appeler maven récursivement 

## Forces

### Devs
  - Facilité d'extension de mutations : Il est très simple de créer un nouveau `Processor` (une mutation) dans le projet en étendant la classe `AbstractProcessor<CtElement>` (voir les classes du package `transformation`), et implémenter les méthodes `process` et `isToBeProcessed`.
  - Facilité d'ajout de sélecteurs de mutation : Une sélecteur n'a besoin d'implémenter que la méthode `decide`.
  
### Ops
  - Plugin Maven : Entièrement intégré à la chaîne de build, notre plugin intervient au bon moment pour générer les sources des mutants, et les tester. Pour l'utiliser, il suffit de modifier le `pom` du projet cible et de créer un fichier xml de configuration très léger, dans lequel seront spécifiés les regroupes de mutations à effectuer (UN regroupement de mutations = UN mutant).
  - Simplicité dans la configuration du plugin : Une balise `<processors></processors>` pour chaque mutant à créer, dans laquelle mettre des balises `<processor></processor>` pour chaque type de mutation à appliquer au mutant dans un fichier `myProcessor.xml`.

## Faiblesses
  - Appel recursif à Maven dans notre plugin (durée d'execution plus élevé et danger au niveau mémoire).
  - Peu de mutations implémenté.
  - Selecteurs qui sont peu architecturé.
