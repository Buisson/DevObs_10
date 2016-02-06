# Description de la chaîne de build

## Place dans le cycle de vie Maven
Dans le cycle de vie Maven, notre Plugin se situe après les étapes de compilation (`compil`) et de tests (`test`). Nous avons considéré que la phase `test` de Maven était prioritaire par rapport à notre Plugin, car celui-ci ayant pour utilité de tester un programme, si les tests de sur le programme d'origine ne passent pas, cela n'aurait aucun sens de lancer les tests de mutation. 

Plus précisément, notre plugin sera un `goal `de la phase `test `et s’exécutera après l’exécution des goals `built-in` de cette phase. 
Une autre option est d’invoquer directement notre plugin via la ligne de commande, cela lancera les phases qui précèdent la phase `test` et finalement celle-ci elle même.
L'ordre de l'éxccution 

L'ordre de l'exécution n’est pas problématique car il est configurable depuis le fichier `pom.xml ` on peut alors « insérer » notre plugin dans n’importe quelle phase et au même temps proposer une exécution « hors-phase » c.-à-d. qui ne fait pas partie du build lifecycle  


## Mutations du programme
Une fois le programme d'origine testé, la mutation s'amorce, et les étapes suivantes sont répétées autant de fois que désiré, avec des combinaisons différentes (mutations et sélecteurs).

### Mutations et sélecteurs
Chaque test de mutation permet d'effectuer une ou plusieurs mutations sur un certain nombre d'éléments choisis grâce aux sélecteurs, le tout en exploitant la bibliothèque `Spoon`, développée par l'INRIA, permettant la modification de fichiers source. Une fois les nouvelles sources créées et compilées, nous obtenons le programme dit "mutant".

### Tester la mutation du programme
Tous les tests créés pour le programme d'origine sont effectés sur le programme mutant, avec pour but de le tuer. Les tests sont executés avec le framework de test unitaire JUnit, qui génère un rapport par test au format XML.

## Afficher le résultat des tests
Une fois toutes les mutations du programme effectuées et testées, les rapports générés précédemment par JUnit sont lus et analysés afin d'en ressortir des informations pertinantes et utiles pour des statistiques, comme le nombre de tests échoués (programmes mutants non tués) afin de générer un document HTML.
## Quelles mutations ?
modifier un opérateur arithmétique par un autre
exemple : int i = 5 + 3; ----> int i = 5 * 3;

modifier un opérateur booléen par un autre
exemple : if(true || false) ---> if(true && false)

modifier une expression booléen par son inverse
exemple : if(true) ------> if(!true)

modifier un nom de variable
exemple : int i; ----> int j;

## Ou les appliquer ?
On les applique dans toutes les classes du projet à tester. (Toutes les classes testé).
## Comment les appliquer ?
En utilisant Spoon.
