Documentation Projet BD Groupe 5
============================


# Exécution

- Lors du développement du projet, la version 17.0.1 de la JDK a été utilisée. Ceci est la dernière version présente 
sur le site d'Oracle ([lien de téléchargement](https://www.oracle.com/java/technologies/downloads/)).
- Afin de pouvoir exécuter le projet, la version minimum requise est Java 14.
- Afin de lancer un des programmes, il faut utiliser le Makefile : ouvrir un terminal puis saisir la commande `make <nomProgramme>`.
- Nous avons développer deux programmes Java disctints. Le premier, `Interface`, permet d'utiliser l'application Gange d'un point de vue utilisateur. Faire `make interface` pour l'exécuter. Le second, `DatabaseManagement`, permet de gérer la base de données d'un point de vue administrateur. il permet notamment de créer, vider et supprimer les tables ainsi que d'insérer un jeu de test dans la base de données. Faire `make manageDB` pour l'exécuter.
