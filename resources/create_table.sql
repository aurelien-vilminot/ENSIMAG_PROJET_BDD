CREATE TABLE CATEGORIE(nomCategorie varchar(30) NOT NULL PRIMARY KEY);
CREATE TABLE COMPTE(
    idCompte NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY
);
CREATE TABLE PRODUIT(
    idProd NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    nomProd varchar(30) NOT NULL,
    prixCProd float CONSTRAINT PrixCPositif NOT NULL CHECK(prixCProd > 0),
    descProd varchar(100) NOT NULL,
    urlProd varchar(100) NOT NULL,
    nomCategorie varchar(30) NOT NULL CONSTRAINT FKCategorieProd REFERENCES CATEGORIE(nomCategorie)
);
CREATE TABLE UTILISATEUR(
    mailUtil varchar(30) NOT NULL PRIMARY KEY,
    mdpUtil varchar(30) NOT NULL,
    nomUtil varchar(30) NOT NULL,
    prenomUtil varchar(30) NOT NULL,
    adrUtil varchar(30) NOT NULL,
    idCompte integer NOT NULL CONSTRAINT FKCompteUtil REFERENCES COMPTE(idCompte)
);
CREATE TABLE OFFRE(
    idProd integer CONSTRAINT FKProdOffre NOT NULL REFERENCES PRODUIT (idProd),
    dateOffre date NOT NULL,
    prixOffre float CONSTRAINT PrixPositif NOT NULL CHECK(prixOffre > 0),
    CONSTRAINT PKOffre PRIMARY KEY (idProd, dateOffre)
);
CREATE TABLE CARACPRODUIT(
    idProd integer NOT NULL CONSTRAINT FKidProCarac REFERENCES PRODUIT(idProd),
    caracProd varchar(30) NOT NULL,
    valeurProd varchar(30) NOT NULL,
    CONSTRAINT PKCaracProduit PRIMARY KEY (idProd, caracProd)
);
CREATE TABLE APOURMERE(
    filleCategorie varchar(30) CONSTRAINT FKfille NOT NULL REFERENCES CATEGORIE(nomCategorie),
    mereCategorie varchar(30) CONSTRAINT FKmere NOT NULL REFERENCES CATEGORIE(nomCategorie)
);
CREATE TABLE OFFREGAGNANTE(
    dateOffre date NOT NULL,
    idProd integer NOT NULL CONSTRAINT FKProdGagnant REFERENCES PRODUIT(idProd),
    PRIMARY KEY (dateOffre, idProd);