# Description de la chaîne de build

## Place dans le cycle de vie Maven
Dans le cycle de vie Maven, notre Plugin se situe après les étapes de compilation (`compil`) et de tests (`test`). Nous avons considéré que la phase `test` de Maven était prioritaire par rapport à notre Plugin, car celui-ci ayant pour utilité de tester un programme, si les tests de sur le programme d'origine ne passent pas, cela n'aurait aucun sens de lancer les tests de mutation. 
Plus précisément, notre plugin sera un « goal » de la phase `test `et s’exécutera après l’exécution des goals `built-in`  de cette phase. 
Une autre option est d’invoquer directement notre plugin via la ligne de commande, cela lancera les phases qui précèdent la phase `test` et finalement celle-ci elle même.


## Mutations du programme
Une fois le programme d'origine testé, la mutation s'amorce, et les étapes suivantes sont répétées autant de fois que désiré, avec des combinaisons différentes (mutations et sélecteurs).

### Mutations et sélecteurs
Chaque test de mutation permet d'effectuer une ou plusieurs mutations sur un certain nombre d'éléments choisis grâce aux sélecteurs, le tout en exploitant la bibliothèque `Spoon`, développée par l'INRIA, permettant la modification de fichiers source. Une fois les nouvelles sources créées et compilées, nous obtenons le programme dit "mutant".

### Tester la mutation du programme
Tous les tests créés pour le programme d'origine sont effectés sur le programme mutant, avec pour but de le tuer. Les tests sont executés avec le framework de test unitaire JUnit, qui génère un rapport par test au format XML.

## Afficher le résultat des tests
Une fois toutes les mutations du programme effectuées et testées, les rapports générés précédemment par JUnit sont lus et analysés afin d'en ressortir des informations pertinantes et utiles pour des statistiques, comme le nombre de tests échoués (programmes mutants non tués) afin de générer un document HTML.
