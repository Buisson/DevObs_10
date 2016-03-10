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

Il faut aussi que vous ajoutiez au même niveau que votre pom.xml un fichier `myProcessor.xml` qui contiendra la liste des mutations que vous voullez appliquer sur votre projet.

Voici un exemple de se fichier :
```
<?xml version="1.0" encoding="UTF-8"?>
<myprocessors>
    <processors>
        <!--Ajout d'un processor-->
        <processor>fr.unice.polytech.devops.transformation.IfProcessor</processor>
    </processors>
    <processors>
        <!--Ajout de plusieurs processeurs-->
        <processor>fr.unice.polytech.devops.transformation.MortNeMutator</processor>
        <processor>fr.unice.polytech.devops.transformation.IfProcessor</processor>
    </processors>
</myprocessors>
```
