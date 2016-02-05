# Description de la chaîne de build

## Place dans le cycle de vie Maven
Dans le cycle de vie Maven, notre Plugin se situe après les étapes de compilation (`compil`) et de tests (`test`). Nous avons considéré que la phase `test` de Maven était prioritaire par rapport à notre Plugin, car celui-ci ayant pour utilité de tester un programme, si les tests de sur le programme de départ ne passent pas, cela n'aurait aucun sens de lancer les tests de mutation. 

## Mutations du programme
Une fois le programme de départ testé, la mutation s'amorce, et les étapes suivantes sont répétées autant de fois que désiré.

### Mutations et sélecteurs
Chaque test de mutation permet d'effectuer une ou plusieurs mutations sur un certain nombre d'éléments choisis grâce aux sélecteurs, le tout en exploitant la bibliothèque `Spoon`, développée par l'INRIA, permettant la modification de fichiers source. 

### Tester la mutation du programme


## Afficher le résultat des tests
