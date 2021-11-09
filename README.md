### __Propriétés élémentaires :__

idProd, intituléProd, prixCProd, descProd, urlProd

caracProd, valeurProd

idUtil, mailUtil, mdpUtil, nomUtil, prenomUtil, adrUtil

idCategorie, nomCategorie

idOffre, dateOffre, heureOffre, prixOffre


### __Dépendances fonctionnelles :__ 

> Les  produits  ont  un  identifiant  unique  et  sont  décrits  par  un  intitulé,  un  prix  courant,  un  petit  texte  de description, l’URL d’une photo du produit,  et  un  ensemble  de  couples  caractéristique/valeur  qui  lui  sont spécifiques

idProd -> intituléProd, PrixCProd, DescProd, UrlProd

> Les clients sont identifiés par leur adresse email, un mot de
passe, leur nom, leur prénom et leur adresse postale 

idUtil -> mailUtil, mdpUtil, nomUtil, prenomUtil, adrUtil


> Une catégorie a un nom

idCategorie -> nomCategorie

> Les offres sont identifiées par l’identifiant de produit et la date et l’heure de l’offre, et on précisera le prix proposé et le compte utilisateur concerné
Une offre est faite par un utilisateur pour un produit

idOffre -> dateOffre, heureOffre, prixOffre, idProd, idUtil



### __Contraintes de valeurs :__

PrixCProdut > 0

prixOffre > 0



### __Contraintes de multiplicité :__

> un produit peut avoir des caractéristiques

idProd -|->> caracProd, valeurProd 

> une catégorie possède une ou plusieurs sous-catégorie.s


idCategorie -|->> idCategorie 

> Si un produit est acheté, alors il est associé a son offre gagnante

idProd -|-> idOffre




### **Contraintes contextuelles :**

__Pour une offre donné :__
> “Une nouvelle offre doit avoir un prix (prixOffre) strictement supérieur au prix courant du produit (prixCProd)”

> “Si une offre est ajoutée, le prix courant (prixCProd) du produit devient celui de l’offre (prixOffre)”


### __les liens importants__

 - [google docs](https://docs.google.com/document/d/1b-AAK8kgm9GcqEW04x_09UjPTDIkSe-M58zEgZglvnQ/edit)
 - [UML colaboratif](https://drive.google.com/file/d/1lO0r5_xNKQ7HWTG9Te2wFYVs2R2wrCxR/view?usp=sharing)