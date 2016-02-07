### Mutations et sélecteurs
Chaque test de mutation permet d'effectuer une ou plusieurs mutations sur un certain nombre d'éléments choisis grâce aux sélecteurs, le tout en exploitant la bibliothèque `Spoon`, développée par l'INRIA, permettant la modification de fichiers source. Une fois les nouvelles sources créées et compilées, nous obtenons le programme dit "mutant".

### Tester la mutation du programme
Tous les tests créés pour le programme d'origine sont effectés sur le programme mutant, avec pour but de le tuer. Les tests sont executés avec le framework de test unitaire JUnit, qui génère un rapport par test au format XML.


# Description de la chaîne de build
Dans le cycle de vie Maven, notre Plugin intervient dans plusieurs phases du build lifecycle. 

## La phase `generate-sources`
Dans cette phase notre Plugin va générer les programmes mutants et les mettre dans le dossier target/genereted-sources, donc notre Mojo se lance et fait appel aux différents mécanismes qui créent les programmes mutants (en utilisant `Spoon`). Pour ce faire il faut spécifier à Maven que notre Plugin participe à la génération du code. On peut alors utiliser `Build Helper`, un outil permettant de configurer le build lifecycle.
## La phase `compile` 
Par défaut, Maven compile uniquement les sources dans `src/main/java`. Une fois encore, on peut utiliser `Build Helper` afin d'ajouter les dossiers supplémentaires que nous avons généré dans la phase `generate-sources`, cela se fait simplement en manipulant le pom.xml. Les programmaes mutants sont alors aussi compilés.
## La phase `test` 
Ici, il s’agit encore une fois de prendre en considération les mutations générées pour les tester. `Surefire` permet de configurer les tests  en spécifiant les dossiers contenant les fichiers sources à tester et le nombre de threads pour permettre le parallélisme.
Cela parait très intéressant car on peut tester les divers programmes mutants en parallèle. Chaque test lancé génère un fichier XML contenant les informations relative à ce test (fail, success).    


## Afficher le résultat des tests
Une fois toutes les mutations du programme effectuées et testées, les rapports générés précédemment par JUnit sont lus et analysés afin d'en ressortir des informations pertinantes et utiles pour des statistiques, comme le nombre de tests échoués (programmes mutants non tués) afin de générer un document HTML.
##Contenu du document HTML
Le document HTML généré par notre framework affichera comme informations :
* Le pourcentage de mutants tués.
* Le nombre de classes testées.
* Description des mutants ayant survécu (ligne et nom de la classe).

Ce document HTML utilisera Highchart pour génerer des graphiques permettant l'affichage des informations pertinantes que nous avons récupéré auparavant.

## Quelles mutations ?
On évite les mutations comme suppression de la déclaration d'une variable car elle produira une erreur de compilation et par conséquence n'aura aucune importance.

#### Modifier une valeur 

`int fct(){return 0;} ==> int fct(){return 42;}`

#### Modifier un opérateur arithmétique par un autre, exemple :   

`int i = 5 + 3;   ==> int i = 5 * 3`

#### Modifier un opérateur booléen par un autre, exemple:  

`if(condition1 || condition2) ==> if(condition1 && condition2)`

#### Modifier une expression booléen par son inverse, exemple:  

`if(condition)          ==> if(!condition)`

#### Modifier une incrémentation par une décrémentation, exemple              

`i++   ==> i--`

#### Modifications sur les opérateurs de comparais (plusieurs combinaisons possible) 

`a >= b ==> a >b `

#### Supprimer une condition : 

`if(c){....} ==> if(true){....}`

#### Supprimer l’appel à un constructeur  

`A a = new A()   ==>    A a = null;` 






## Ou les appliquer ?
On les applique dans toutes les classes du projet à tester. (Toutes les classes testé).
C'est à dire :
* Dans les méthodes.
* Dans les constructeurs.
* Dans les blocs.
* Dans les attributs.
* Dans les variables.

## Comment les appliquer ?
En utilisant Spoon.
