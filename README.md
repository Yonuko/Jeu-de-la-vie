# Jeu de la vie

---

## Présentation
Le jeu de la vie est un algorithme très simple et très connu, qui consiste à observer l'évolution de cellules dans un univers au règle assez simple.

Les règles sont les suivantes:

- Une cellule peut être vivante ou morte
- Une cellule morte entouré d’exactement 3 cellules vivantes, devient vivante
- Une cellule vivante entouré de 2 ou 3 cellules vivantes le reste, sinon elle meurt.

De ces règles assez simple, des comportement assez intéressant peuvent émaner.

## Developement

Ce projet a été réaliser en Java 8, il permet deux utilisation différentes, une utilisation graphique et une utilisation console.

Dans les deux cas, l'utilisateur pourra choisir de générer une grille aléatoirement ou non, et de placer ses cellules mortes ou vivante.

Une fois les cellules placées, le programme possède plusieurs fonctionnalités:
- Passer à la génération suivante
- Revenir à la génération précédente
- Passer aux générations suivantes jusqu'à arrêt de l'utilisateur

## Utilisation

### Interface console

Pour lancer l'application en mode console, il vous faudra lancer un invité de commande dans le répertoire où se trouve le fichier Jeu de la vie.jar
Et tapper la commande suivante
```shell
java -jar '.\Jeu de la vie.jar'
```

Une boite de dialogue va s'ouvrir, vous demandant quelle interface vous voulez utiliser, il vous suffit de cliquer sur "Interface Console".

### Interface Graphique

En ce qui concerne l'interface graphique, il suffit double cliquer sur le fichier '.\Jeu de la vie.jar', et de choisir "Interface Graphique". 