## Notre Architecture

Tout d'abord voici l'arborescence de notre plugin Maven :

```
SpoonTester
│
└───src/main/java/fr/unice/polytech/devops
|   │
|   |
|   ├───selectors/          #Contient les selecteurs pour nos mutations
|   |
|   ├───transformation/     #contient nos processeurs Spoon qui appliquent differentes mutations
|   |
|   ├───utils/              #contient differentes fonctions utilitaires (parseurs XML, etc...)
|   |
|   └───AppMojo.java        #Le mojo qui est appelé après la phase de test de Maven
| 
└───pom.xml
```

###Package selecteur
Dans le package selectors nous avons toute les classes Java qui permettent de gérer ou seront appliquer nos mutation. Ils posèdent tous une méthode `public boolean decide(Ct Element)` qui permet de savoir si on applique une mutation ou non selon différents critère qui sont définit pour un selecteur donné.

###Package transformation
Dans le package transformation nous avons toute les classes Java qui permettent d'appliquer les mutations sur les sources du projet qui appelle notre plugin. Toutes ces classes héritent de `AbstractProcessor<CtElement>` qui est une interface de Spoon et qui nous demande d'implémenter deux méthodes qui sont `boolean isToBeProcessed(CtElement candidate)` , qui permet d'utiliser nos selecteurs, et `void process(CtElement candidate)`, qui permet de modifier le source.

###AppMojo.java

Cette class est l'entrée vers notre plugin, elle associée au goal `rapport`, sa méthode `execute ` se lance alors pendant la phase `generate`, et elle lance les différent mécanisme dont notre plugin a besoin à savoir

  - Lecture du fichier de configurtion contenant les mutation à appliquer
  - Création du fichier temporaire qui va être modéfier à chaque itération
  - Lancer `maven package`, cela auras comme effet de lancer maven récursivement le test d’arrêt étant si oui ou non le fichier  emporaire est vide 
  - Récuperer les résultats des tests après chaque itération
  - Génerer le fichier HTML


## Forces

//TODO

## Faiblesses

//TODO
