-- Transactions à travers un cas d'utilisation --

-- Connexion de l'utilisateur ADMIN
SELECT COUNT(*) FROM UTILISATEUR
WHERE MAILUTIL = 'root' AND MDPUTIL = 'root';

COMMIT;


-- Récupération de l'identifiant du compte
SELECT IDCOMPTE FROM UTILISATEUR
WHERE MAILUTIL = 'root';

COMMIT;


-- Récupération des catégories mères du catalogue
SELECT NOMCATEGORIE FROM CATEGORIE
WHERE NOMCATEGORIE NOT IN (SELECT FILLECATEGORIE FROM APOURMERE);

COMMIT;


-- Récupération des catégories recommandées (les catégories personnalisées puis générales, et pas d'autres)
-- Première sélection sur les recommandations personnalisées
SELECT p.nomCategorie AS nomCategorie, count(o.dateOffre) AS nb, 0 AS union_order
FROM Offre o, Produit p
WHERE o.idProd = p.idProd
AND o.idCompte = 1
AND NOT EXISTS (SELECT *
                FROM OffreGagnante og
                WHERE o.dateOffre = og.dateOffre
                AND o.idProd = og.idProd)
GROUP BY p.nomCategorie
UNION
-- Deuxième sélection sur les recommandations générales, en enlevant les catégories qui apparaissent dans la première requête
SELECT p.nomCategorie AS nomCategorie, count(o.dateOffre)/count(DISTINCT o.idProd) AS nb, 1 AS union_order
FROM Offre o, Produit p
WHERE o.idProd = p.idProd
AND p.nomCategorie NOT IN (SELECT p.nomCategorie
                           FROM Offre o, Produit p
                           WHERE o.idProd = p.idProd
                           AND o.idCompte = 1
                           AND NOT EXISTS (SELECT *
                                           FROM OffreGagnante og
                                           WHERE o.dateOffre = og.dateOffre
                                           AND o.idProd = og.idProd))
GROUP BY p.nomCategorie
ORDER BY union_order, nb DESC, nomCategorie;

COMMIT;


-- Récupération des catégories filles de la catégorie 'Smartphones' ainsi que de ses produits
SELECT FILLECATEGORIE FROM APOURMERE
WHERE APOURMERE.MERECATEGORIE = 'Smartphones';

SELECT p.IDPROD, p.NOMPROD, COUNT(o.IDPROD) as NBOFFRE
FROM PRODUIT p, OFFRE o
WHERE p.NOMCATEGORIE = 'Smartphones'
AND o.IDPROD = p.IDPROD
AND p.IDPROD NOT IN (SELECT IDPROD FROM OFFREGAGNANTE)
GROUP BY p.IDPROD, p.NOMPROD
UNION
SELECT p.IDPROD, p.NOMPROD, 0 as NBOFFRE
FROM PRODUIT p
WHERE p.NOMCATEGORIE = 'Smartphones'
AND p.IDPROD NOT IN (SELECT OFFRE.IDPROD FROM OFFRE)
ORDER BY NBOFFRE DESC, NOMPROD;

COMMIT;


-- Visualiser la fiche complète d'un produit
SELECT NOMPROD, PRIXCPROD, DESCPROD, URLPROD, NOMCATEGORIE
FROM PRODUIT WHERE IDPROD = 1;

SELECT CARACPROD, VALEURPROD FROM CARACPRODUIT WHERE IDPROD = 1;

COMMIT;


-- Insérer une offre (les contraintes sont vérifiées dans le code)
-- Dans l'application Gange, CURRENT_DATE est sauvegardé pour avoir la même valeur lors des deux insertions
INSERT INTO OFFRE VALUES (1, CURRENT_DATE, 900, 1);
-- Connaître le prix courant du produit au sein de la transaction
SELECT PRIXCPROD FROM PRODUIT WHERE IDPROD = 1;
-- Si le prix est supérieur ou égal au prix de l'offre, ROLLBACK
ROLLBACK;
-- Sinon, la transaction se poursuit
UPDATE PRODUIT SET PrixCProd = 900 WHERE IDPROD = 1;
-- Connaître le nombre d'offres faites sur un produit
SELECT COUNT(IDPROD) as NbOffres FROM OFFRE WHERE IDPROD = 1;
-- Si le nombre d'offres faites est égal au nombre d'offres maximum autorisées, l'offre devient gagnante
-- Un COMMIT est fait afin de réaliser l'insertion dans la table OFFRE, OFFREGAGNANTE étant dépendante de celle-ci
COMMIT;
INSERT INTO OFFREGAGNANTE VALUES (CURRENT_DATE, 1);
-- Si le nombre d'offres faites est supérieur au nombre d'offres maximum autorisées, ROLLBACK
ROLLBACK;
-- Sinon, COMMIT
COMMIT;


-- Appliquer le droit à l'oubli sur l'utilisateur root
DELETE FROM UTILISATEUR WHERE MAILUTIL= 'root';

COMMIT;