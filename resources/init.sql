-- Créations des tables
CREATE SEQUENCE id_compte;
CREATE SEQUENCE id_produit;
CREATE TABLE Categorie(nomCategorie varchar(30) NOT NULL PRIMARY KEY);
CREATE TABLE Compte(idCompte integer NOT NULL PRIMARY KEY);
CREATE TABLE Produit(idProd integer NOT NULL PRIMARY KEY,nomProd varchar(100) NOT NULL,prixCProd float CONSTRAINT PrixCPositif NOT NULL CHECK(prixCProd > 0),descProd varchar(200) NOT NULL,urlProd varchar(100) NOT NULL,nomCategorie varchar(30) NOT NULL CONSTRAINT FKCategorieProd REFERENCES Categorie(nomCategorie));
CREATE TABLE Utilisateur(mailUtil varchar(30) NOT NULL PRIMARY KEY,mdpUtil varchar(30) NOT NULL,nomUtil varchar(30) NOT NULL,prenomUtil varchar(30) NOT NULL,adrUtil varchar(100) NOT NULL,idCompte integer NOT NULL CONSTRAINT FKCompteUtil REFERENCES Compte(idCompte));
CREATE TABLE Offre(idProd integer CONSTRAINT FKProdOffre NOT NULL REFERENCES Produit (idProd), dateOffre date NOT NULL,prixOffre float CONSTRAINT PrixPositif NOT NULL CHECK(prixOffre > 0), idCompte integer NOT NULL CONSTRAINT FKCompteOffre REFERENCES COMPTE(idCompte), CONSTRAINT PKOffre PRIMARY KEY (idProd, dateOffre));
CREATE TABLE CaracProduit(idProd integer NOT NULL CONSTRAINT FKidProCarac REFERENCES Produit(idProd),caracProd varchar(30) NOT NULL,valeurProd varchar(100) NOT NULL,CONSTRAINT PKCaracProduit PRIMARY KEY (idProd, caracProd));
CREATE TABLE APourMere(filleCategorie varchar(30) CONSTRAINT FKfille NOT NULL REFERENCES Categorie(nomCategorie),mereCategorie varchar(30) CONSTRAINT FKmere NOT NULL REFERENCES Categorie(nomCategorie), PRIMARY KEY(filleCategorie));
CREATE TABLE OffreGagnante(dateOffre date NOT NULL, idProd integer NOT NULL, PRIMARY KEY (dateOffre, idProd), FOREIGN KEY (dateOffre, idProd) REFERENCES Offre(dateOffre, idProd));

-- Commit
COMMIT;