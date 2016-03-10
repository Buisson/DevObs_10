## Comment utiliser notre plugin

### Modifier le fichier `pom.xml` de votre projet `Maven`
Si votre projet n'utilise pas de plugin, ajoutez la portion suivante au même niveau que les `dependencies` :

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

#### Exemple de fichier `pom.xml` d'un projet utilisant notre plugin
Vous obtiendriez par exemple le fichier complet suivant : 
```
<?xml version="1.0" encoding="UTF-8" standalone="no"?><project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>fr.unice.polytech.devops</groupId>
    <artifactId>Programme-cible</artifactId>
    <version>1.0-SNAPSHOT</version>

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

    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
</project>
```

#### Explication
Notre plugin tire parti des fonctionnalités d'une bibliothèque nommée Spoon qui permet la création de projets mutants. Lors de la phase `generate-sources` de `Maven`, les mutations sont effectuées sur le projet cible (votre projet).
Grâce à ce balisage, notre plugin est défini comme partie entière de la  `chaîne de build` de Maven, et sera déclenché après la phase `test` (afin de tester chacun des projets mutants créés).

### Créer un fichier de configuration à la racine de votre projet


Il faut aussi que vous ajoutiez au même niveau que votre pom.xml un fichier `myProcessor.xml` qui contiendra la liste des mutations que vous voulez appliquer sur votre projet.
Vous avez la possibilité de spécifier un package et/ou une méthode pour appliquer les mutations en ajoutant des attributs aux éléments `<processor>` .

Voici un exemple de ce fichier :
```
<?xml version="1.0" encoding="UTF-8"?>
<myprocessors>
    <processors>
        <!--Ajout d'un processeur-->
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

### Nos différentes mutations

#### Supprimer une condition

`if(c){....} ==> if(true){....}`

Cette mutation s'applique à condition que le test ne compare pas une variable est null. Dans ce cas mettre `true` au lieu de la vérification conduira très probablement à un `NullPointerException` ==> peu d’intérêt pour l'utilisateur.

#### Modifier une boucle

`for( int i = 0 ; i < a ; i++ ==>  for( int i = 0 ; i <=a ; i++`  

Dépasser les bornes d'une liste ou d'un tableau est une source d'erreurs très courante, cette mutation permet alors de provoquer ce cas de figure. Elle permet aussi d'introduire une erreur dans le calcul que fait la boucle.

#### Opération binaire : 

`for(a+b ==> a-b`  


 



### Rapport HTML généré

Le rapport HTML généré se retrouve après le lancement d'un `mvn package` sur votre projet dans le dossier `target/mutation-report/htmlReport.html` de votre projet. Il contient plusieurs informations utiles de tout les mutants créés, les tests en rouge sont les tests qui sont validés, et les tests en vert sont ceux qui ont échoué. Ensuite nous avons un diagramme circulaire qui montre le pourcentage de mutants vivants, tués et mort-nés.

