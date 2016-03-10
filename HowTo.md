## Ajouter notre plugin maven de test par mutation à votre projet

Pour ajouter notre plugin maven à votre propre projet maven, il faut ajouter dans votre `pom.xml` :

```
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <testFailureIgnore>true</testFailureIgnore>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <failOnError>false</failOnError>
                </configuration>
            </plugin>
            <plugin>
                <groupId>fr.inria.gforge.spoon</groupId>
                <artifactId>spoon-maven-plugin</artifactId>
                <version>2.2</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <processors/>
                </configuration>
                <!-- To be sure that you use the latest version of Spoon, specify it as dependency. -->
                <dependencies>
                    <dependency>
                        <groupId>fr.inria.gforge.spoon</groupId>
                        <artifactId>spoon-core</artifactId>
                        <version>5.0.2</version>
                    </dependency>
                    <dependency>
                        <groupId>fr.unice.polytech.devops</groupId>
                        <artifactId>spoon-test-maven-plugin</artifactId>
                        <version>1.0-SNAPSHOT</version>
                    </dependency>
                </dependencies>
            </plugin>
            <plugin>
                <groupId>fr.unice.polytech.devops</groupId>
                <artifactId>spoon-test-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>rapport</goal>
                        </goals>
                        <phase>test</phase><!--test-->
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```
Ce que cela fait c'est dire à maven que notre plugin participe à la phase de la génération du code (pour générer les mutants) et à la phase des tests (pour les tester)
Il faut aussi que vous ajoutiez au même niveau que votre pom.xml un fichier `myProcessor.xml` qui contiendra la liste des mutations que vous voullez appliquer sur votre projet.
Vous avez la possibilité de spécifier un package et/ou une méthode pour appliquer les mutation en ajoutant des attributs au éléments processor 

Voici un exemple de se fichier :
```
<?xml version="1.0" encoding="UTF-8"?>
<myprocessors>
    <processors>
        <!--Ajout d'un processor-->
        <processor methode="methode1">fr.unice.polytech.devops.transformation.IfProcessor</processor>
        <processor package="packageA">fr.unice.polytech.devops.transformation.BinaryOperatorMutator</processor>

    </processors>
    <processors>
        <!--Ajout de plusieurs processeurs-->
        <processor>fr.unice.polytech.devops.transformation.MortNeMutator</processor>
        <processor>fr.unice.polytech.devops.transformation.IfProcessor</processor>
    </processors>
</myprocessors>
```

### Nos differentes mutations

Supprimer une condition

`if(c){....} ==> if(true){....}`

cette mutation s'applique à condition que le test n'est pas pour s'assurer qu'une variable n'est pas null, car dans ce cas mettre `true` au lieu de la vérification conduiras très probablement à `NullPointerException` ==> peut d’intérêts pour l'utilisateur

Modifier une boucle

`for( int i = 0 ; i < a ; i++ ==>  for( int i = 0 ; i <=a ; i++`  

Dépasser les bords d'une liste ou d'un tableau est une source très courante d'erreurs, cette mutation permet alors de produire ce cas. elle permet aussi d'introduire une erreur dans le calcul que fait la boucle

Operation binaire : 

`for(a+b ==> a-b`  


 



### Rapport HTML généré

Le rapport HTML généré se retrouve après le lancement d'un `mvn package` sur votre projet dans le dossier `target/mutation-report/htmlReport.html` de votre projet. Il contient plusieurs informations utiles de tout les mutants crée, les tests en rouge sont les tests qui sont passés, et les test en vert sont ceux qui ont échoués. Ensuite nous avons un diagramme circulaire qui montre le pourcentage de mutant vivant, tué et mort-né.

