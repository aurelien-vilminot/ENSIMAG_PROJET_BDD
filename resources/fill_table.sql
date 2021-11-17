INSERT INTO
    Compte (idCompte)
VALUES
    (default),
    (default);

INSERT INTO
    Produit (
        nomProd,
        prixCProd,
        descProd,
        urlProd,
        nomCategorie
    )
VALUES
    (
        'Chaussures de sport NEXT',
        '49.99',
        'Des chaussures qui conviennent à toute activité sportive',
        '/chaussures_sport_next',
        'Chaussures de sport'
    );

INSERT INTO
    Produit (
        nomProd,
        prixCProd,
        descProd,
        urlProd,
        nomCategorie
    )
VALUES
    (
        'Chaussures de ville GEN',
        '69.99',
        'Les dernières chaussures GEN',
        '/chaussures_ville_gen',
        'Chaussures de ville'
    ),
;

INSERT INTO
    Utilisateur (
        mailUtil,
        mdpUtil,
        nomUtil,
        prenomUtil,
        adrUtil,
        idCompte
    )
VALUES
    (
        'bertrand.durand@gmail.com',
        'pass123',
        'Durand',
        'Bertrand',
        '9 rue du loup Paris 75015',
        1
    );

INSERT INTO
    Utilisateur (
        mailUtil,
        mdpUtil,
        nomUtil,
        prenomUtil,
        adrUtil,
        idCompte
    )
VALUES
    (
        'jacques.bernard@gmail.com',
        'cestmonmotdepasse',
        'Bernard',
        'Jacques',
        '14 chemin neuf Grenoble 38000',
        2
    );

INSERT INTO
    Categorie (nomCategorie)
VALUES
    ('Chaussures');

INSERT INTO
    Categorie (nomCategorie)
VALUES
    ('Chaussures de sport');

INSERT INTO
    Categorie (nomCategorie)
VALUES
    ('Chaussures de ville');

INSERT INTO
    Offre (idProd, dateOffre, prixOffre, idCompte)
VALUES
    (
        1,
        TO_DATE('2021/11/17 10:27:44', 'yyyy/mm/dd hh24:mi:ss'),
        '60',
        1
    );

INSERT INTO
    CARACPRODUIT (idProd, caracProd, valeurProd)
VALUES
    (1, pointure, '44'),
    (1, couleur, 'Rouge et Blanche'),
    (2, pointure, '45'),
    (2, couleur, 'Bleue');

INSERT INTO
    APOURMERE (filleCategorie, mereCategorie)
VALUES
    ('Chaussures de sport', 'Chaussures'),
    ('Chaussures de ville', 'Chaussures');

-- INSERT INTO
--     OFFREGAGNANTE(dateOffre, idProd)
-- VALUES
--     ();