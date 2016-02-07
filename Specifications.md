## Mutations et sélecteurs
Chaque test de mutation permet d'effectuer une ou plusieurs mutations sur un certain nombre d'éléments choisis grâce aux sélecteurs, le tout en exploitant la bibliothèque `Spoon`, développée par l'INRIA, permettant la modification de fichiers source. Une fois les nouvelles sources créées et compilées, nous obtenons le programme dit "mutant".

## Tester la mutation du programme
Tous les tests créés pour le programme d'origine sont effectués sur le programme mutant, avec pour but de le tuer. Les tests sont exécutés avec le framework de test unitaire JUnit, qui génère un rapport par test au format XML.


# Description de la chaîne de build
Dans le cycle de vie Maven, notre Plugin intervient dans plusieurs phases du build lifecycle. 

## La phase `generate-sources`
Dans cette phase notre Plugin va générer les programmes mutants et les mettre dans le dossier target/genereted-sources, donc notre Mojo se lance et fait appel aux différents mécanismes qui créent les programmes mutants (en utilisant `Spoon`). Pour ce faire, il faut spécifier à Maven que notre Plugin participe à la génération du code. On peut alors utiliser `Build Helper`, un outil permettant de configurer le build lifecycle.
## La phase `compile` 
Par défaut, Maven compile uniquement les sources dans `src/main/java`. Une fois encore, on peut utiliser `Build Helper` afin d'ajouter les dossiers supplémentaires que nous avons générés dans la phase `generate-sources`, cela se fait simplement en manipulant le pom.xml. Les programmes mutants sont alors aussi compilés.
## La phase `test` 
Ici, il s’agit encore une fois de prendre en considération les mutations générées pour les tester. `Surefire` permet de configurer les tests  en spécifiant les dossiers contenant les fichiers sources à tester et le nombre de threads pour permettre le parallélisme.
Cela parait très intéressant car on peut tester les divers programmes mutants en parallèle. Chaque test lancé génère un fichier XML contenant les informations relatives à ce test (fail, success).    


## Afficher le résultat des tests
Une fois toutes les mutations du programme effectuées et testées, les rapports générés précédemment par JUnit sont lus et analysés afin d'en ressortir des informations pertinentes et utiles pour des statistiques, comme le nombre de tests échoués (programmes mutants non tués) afin de générer un document HTML.
##Contenu du document HTML
Le document HTML généré par notre framework affichera comme informations :
* Le pourcentage de mutants tués.
* Le nombre de classes testées.
* Description des mutants ayant survécus (ligne et nom de la classe).

Ce document HTML utilisera Highchart pour générer des graphiques permettant l'affichage des informations pertinentes que nous avons récupérées auparavant.

## Quelles mutations ?
On évite les mutations comme la suppression de la déclaration d'une variable car elle produira une erreur de compilation et par conséquence n'aura aucune importance.

#### Modifier une valeur 

`int fct(){return 0;} ==> int fct(){return 42;}`

#### Modifier un opérateur arithmétique par un autre 

`int i = 5 + 3;   ==> int i = 5 * 3`

#### Modifier un opérateur booléen par un autre

`if(condition1 || condition2) ==> if(condition1 && condition2)`

#### Modifier une expression booléen par son inverse 

`if(condition)          ==> if(!condition)`

#### Modifier une incrémentation par une décrémentation             

`i++   ==> i--`

#### Modifications sur les opérateurs de comparaisons (plusieurs combinaisons possibles) 

`a >= b ==> a >b `

#### Supprimer une condition

`if(c){....} ==> if(true){....}`

#### Supprimer l’appel à un constructeur  

`A a = new A()   ==>    A a = null;` 

Notre framework pourra facilement intégrer d'autres types de mutations au bon vouloir de l'utilisateur.




## Où les appliquer ?
On les applique dans toutes les classes du projet à tester. (Toutes les classes testées).
C'est-à-dire :
* Dans les méthodes.
* Dans les constructeurs.
* Dans les blocs.
* Dans les attributs.
* Dans les variables.

L'intérêt étant de créer des mutants ayant des chances potentielles de survivre à la phase de test, il faut que le nombre de modifications par classe ne dépasse pas une certaine limite (qui peut être configurable). Dans le cas contraire, si on effectue tous les changements possibles dans une classe la probabilité que le mutant ne passe pas les tests devient beaucoup plus importante. 

Cela  permet aussi à l’utilisateur de mieux interpréter les résultats (dans le cas où les tests passent), car si on effectue beaucoup de changements, il est difficile d’identifier exactement le problème.


## Comment les appliquer ?
En utilisant les différents outils mis  à notre disposition par `spoon`  :

Un `Processor`  nous permet de « extraire » toute sorte d’information sur un type donnée (type au sens de l’AST) en utilisant les  `Filter` et les`Path`.

Pour les Filter, spoon nous donne un API qui nous permet de faire des requetés et « interroger » l’AST, par exemple le filtre `OverridingMethodFilter` renvoie toute les méthodes qui surchargent la méthode passée en paramètre. 

Après avoir récolté toutes les informations sur une classe, on peut en créer une autre identique on appliquant les modifications souhaitées, pour ce faire on peut utiliser les `Factory` , par exemple pour créer une classe: 

`CtClass newClass = factory.Core().createClass();`   




