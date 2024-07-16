-- Script de création de la base de données ENCHERES
--   type :      SQL Server 2012
--

USE ENI_Encheres;


DROP TABLE ENCHERES
DROP TABLE RETRAITS
DROP TABLE ARTICLES_VENDUS
DROP TABLE TOKEN
DROP TABLE UTILISATEURS
DROP TABLE CATEGORIES
DROP TABLE ROLES



CREATE TABLE CATEGORIES (
    no_categorie   INTEGER IDENTITY(1,1) NOT NULL,
    libelle        VARCHAR(30) NOT NULL
)

ALTER TABLE CATEGORIES ADD constraint categorie_pk PRIMARY KEY (no_categorie)

CREATE TABLE ENCHERES (
    no_utilisateur   INTEGER NOT NULL,
    no_article       INTEGER NOT NULL,
    date_enchere     datetime2 NOT NULL,
	montant_enchere  INTEGER NOT NULL

)

ALTER TABLE ENCHERES ADD constraint enchere_pk PRIMARY KEY (no_utilisateur, no_article, date_enchere)

CREATE TABLE RETRAITS (
	no_article       INTEGER NOT NULL,
    rue              VARCHAR(30) NOT NULL,
    code_postal      VARCHAR(15) NOT NULL,
    ville            VARCHAR(30) NOT NULL
)

ALTER TABLE RETRAITS ADD constraint retrait_pk PRIMARY KEY  (no_article)

CREATE TABLE UTILISATEURS (
    no_utilisateur   INTEGER IDENTITY(0,1) NOT NULL,
    pseudo           VARCHAR(30) NOT NULL,
    nom              VARCHAR(30) NOT NULL,
    prenom           VARCHAR(30) NOT NULL,
    email            VARCHAR(100) NOT NULL,
    telephone        VARCHAR(15),
    rue              VARCHAR(30) NOT NULL,
    code_postal      CHAR(5) NOT NULL,
    ville            VARCHAR(30) NOT NULL,
    mot_de_passe     VARCHAR(255) NOT NULL,
    credit           INTEGER NOT NULL,
    administrateur   bit NOT NULL,
	etat_utilisateur bit NOT NULL DEFAULT 1
)

ALTER TABLE UTILISATEURS ADD constraint utilisateur_pk PRIMARY KEY (no_utilisateur)


CREATE TABLE ARTICLES_VENDUS (
    no_article                    INTEGER IDENTITY(1,1) NOT NULL,
    nom_article                   VARCHAR(30) NOT NULL,
    description                   VARCHAR(300) NOT NULL,
	date_debut_encheres           datetime2 NOT NULL,
    date_fin_encheres             datetime2 NOT NULL,
    prix_initial                  INTEGER,
    prix_vente                    INTEGER,
    no_utilisateur                INTEGER NOT NULL,
    no_categorie                  INTEGER NOT NULL,
	no_acheteur					  INTEGER DEFAULT 0,
	etat_vente					  INTEGER DEFAULT 1,
	imageUUID					  VARCHAR(200) DEFAULT 'placeholderImage.jpg'
)

CREATE TABLE ROLES(
	role VARCHAR(50) NOT NULL, 
	is_admin bit NOT NULL ,
PRIMARY KEY (role,is_admin));

INSERT INTO ROLES (role,is_admin) VALUES ('ROLE_MEMBRE',0);
INSERT INTO ROLES (role,is_admin) VALUES ('ROLE_MEMBRE',1);
INSERT INTO ROLES (role,is_admin) VALUES ('ROLE_ADMIN',1);

CREATE TABLE TOKEN (
    id INT IDENTITY(1,1) PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    user_id INT NOT NULL,
    expiry_date DATETIME, 
    FOREIGN KEY (user_id) REFERENCES UTILISATEURS (no_utilisateur)
);


ALTER TABLE ARTICLES_VENDUS ADD constraint articles_vendus_pk PRIMARY KEY (no_article)

ALTER TABLE ARTICLES_VENDUS
    ADD CONSTRAINT encheres_utilisateur_fk FOREIGN KEY ( no_utilisateur ) REFERENCES UTILISATEURS ( no_utilisateur )
ON DELETE NO ACTION 
    ON UPDATE no action 

ALTER TABLE ENCHERES
    ADD CONSTRAINT encheres_articles_vendus_fk FOREIGN KEY ( no_article )
        REFERENCES ARTICLES_VENDUS ( no_article )
ON DELETE NO ACTION 
    ON UPDATE no action 

ALTER TABLE RETRAITS
    ADD CONSTRAINT retraits_articles_vendus_fk FOREIGN KEY ( no_article )
        REFERENCES ARTICLES_VENDUS ( no_article )
ON DELETE NO ACTION 
    ON UPDATE no action 

ALTER TABLE ARTICLES_VENDUS
    ADD CONSTRAINT articles_vendus_categories_fk FOREIGN KEY ( no_categorie )
        REFERENCES categories ( no_categorie )
ON DELETE NO ACTION 
    ON UPDATE no action 

ALTER TABLE ARTICLES_VENDUS
    ADD CONSTRAINT ventes_utilisateur_fk FOREIGN KEY ( no_utilisateur )
        REFERENCES utilisateurs ( no_utilisateur )
ON DELETE NO ACTION 
    ON UPDATE no action 


-- INSERTION DE DONNEES POUR LE DEV

--UTILISATEURS
   
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, etat_utilisateur)
VALUES ('<utilisateur supprimé>','<utilisateur supprimé>','<utilisateur supprimé>','<utilisateur supprimé>','<supprimé>','<utilisateur supprimé>','00000','<utilisateur supprimé>','aucun mot de passe',0,0,0); 

INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('admin','ad','min','admin@test.com','0601020304','5 rue des administrateurs','44000','Nantes','{bcrypt}$2y$10$ZxhAAV3G6z69EqXiXxhC5u2mfYFaHZ7s7xTN7cfP90uJf52XiLmie',10000,1);
--mot de passe = 'admin'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('gilbertlebg','bg','gilbert','gilbertbg@test.com','0606060606','10 rue des membres normaux','75001','Paris','{bcrypt}$2y$10$izIV72abEewZ54SaIRNKlejc7Vhz2L/bG24z420UXoyXIYmmCPMgS',1200,0);
--mot de passe = '1234'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('toto','Di','Amar','amardi@test.com','0678910111','10 rue du lundi','44320','Saint Père en Retz','{bcrypt}$2a$10$CpL8gIexU56OfkrU34oTjuZpn7ET4gKjY06rUgeqm1a4y3Sp4mNxq',900,0);
--mot de passe = '1234'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('clem','Omique','Clémentine','clem@test.com','0654321099','5 allée du contraire','97460','Saint Paul','{bcrypt}$2a$10$CpL8gIexU56OfkrU34oTjuZpn7ET4gKjY06rUgeqm1a4y3Sp4mNxq',1100,0);
--mot de passe = '1234'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('manda','Maire','Amanda','amanda@test.com','0789101112','5 passage des Amandiers','03800','Gannat','{bcrypt}$2a$10$CpL8gIexU56OfkrU34oTjuZpn7ET4gKjY06rUgeqm1a4y3Sp4mNxq',8000,0);
--mot de passe = '1234'

INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('StaloneS','Stalone','Sylvester','stalone@test.com','0612345678','1 avenue des cogneurs','75008','Paris','{bcrypt}$2y$10$/D8Y6JsCVTA5piBLkl1gT.RnDNnH1yMRFaoRWEWz7927/GmeHBtA.',950,0);
--mot de passe = '1234'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('Schwarzi','Schwarzenegger','Arnold','arnoldS@test.com','0623456789','88 rue de la bagarre','75015','Paris','{bcrypt}$2y$10$.i48uLipRT1lFNsyHmzV5eNePjsqYVwn6h2ZhCgpeusreIWe7ZsYu',1000,0);
--mot de passe = '1234'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('Bman','Wayne','Bruce','bruce@test.com','0634567890','1007 Mountain Drive','93419','Gotham','{bcrypt}$2y$10$9R1e8ovcM/YIVsoB3MdEi.Bb9vl9gQhFnv.nOnmkTX83kSxj0UlVa',1200,0);
--mot de passe = '1234'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('AlbertoE','Einstein','Albert','albert.einstein@test.com','0645678901','729 avenue du Temps','03001','Berne','{bcrypt}$2y$10$n3VRBmfCsTibyz0oijzCT.EtSDMpHzneVlhzF3rzba4Ds85XVeTNm',1100,0);
--mot de passe = '1234'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('MarieC','Curie','Marie','curieM@test.com','0656789012','4 rue de la Chimie','64100','Bayonne','{bcrypt}$2y$10$UEJOik7hnr175h6T/xTI0OGwodVvdTyQTl2z.8jc.A5XQnZdjJEDa',1050,0);
--mot de passe = '1234'
INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur)
VALUES ('laraC','Croft','Lara','lara@test.com','0667890123','1 Unknown Street','75009','London','{bcrypt}$2y$10$gQe6Gk8ps6HSpr5hmGNnXuZCybOtiSaro0raS5EQkw5hH/o1rXnfS',1150,0);
--mot de passe = '1234'



--CATEGORIES

INSERT INTO CATEGORIES (libelle) VALUES ('Mobilier');
INSERT INTO CATEGORIES (libelle) VALUES ('Informatique');
INSERT INTO CATEGORIES (libelle) VALUES ('Électronique');
INSERT INTO CATEGORIES (libelle) VALUES ('Véhicules');
INSERT INTO CATEGORIES (libelle) VALUES ('Mode');
INSERT INTO CATEGORIES (libelle) VALUES ('Loisirs');
INSERT INTO CATEGORIES (libelle) VALUES ('Autres');

--ARTICLES VENDUS

-- Articles avant la vente (état 1)
--No acheteur à 0 normal car pas encore d'acheteurs
INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Vélo de route', 'Vélo de route en bon état pour amateur de cyclisme', '2024-07-20 00:00:00', '2024-07-30 00:00:00', 150, 150, 1, 4, 0, 1,'961b7f309e848e957a6875d0b9e6230a85beb0aa.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (1, '5 rue des administrateurs','44000','Nantes')

INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Télévision 4K', 'Télévision 4K de 55 pouces, parfaite pour le salon', '2024-07-18 00:00:00', '2024-07-28 00:00:00', 400, 400, 2, 3, 0, 1, 'd62106292615845e0e8253210f4729901737666a.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (2, '10 rue des membres normaux','75001','Paris')

INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Guitare acoustique', 'Guitare acoustique en parfait état', '2024-07-22 00:00:00', '2024-08-01 00:00:00', 100, 100, 3, 6, 0, 1, 'cfe4d8e1c0d656574314e0ae394ac4101a3c5939.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (3, '10 rue du lundi','44320','Saint Père en Retz')

-- Articles en cours de vente (état 2)

INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Smartphone dernier cri', 'Smartphone haut de gamme récent', '2024-07-05 20:00:00', '2024-07-15 12:00:00', 700, 700, 4, 2, 0, 2, '35a10bc0339b57c90713288dae910342bf2e2606.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (4, '5 allée du contraire','97460','Saint Paul')


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente)
VALUES ('Casque audio', 'Casque audio sans fil avec réduction de bruit', '2024-07-03 21:00:00', '2024-07-13 12:00:00', 150, 190, 5, 3, 8, 2);
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (5, '5 passage des Amandiers','03800','Gannat')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) 
VALUES	(2, 5, '2024-07-09 12:00:00.320', 155),
		(6, 5, '2024-07-09 12:15:22.150', 165),
		(8, 5, '2024-07-10 01:02:54.063', 190);

INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente)
VALUES ('Montre connectée', 'Montre connectée avec multiples fonctionnalités', '2024-07-07 22:30:00', '2024-07-17 12:00:00', 250, 250, 6, 3, 0, 2);
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (6, '1 avenue des cogneurs','75008','Paris')


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Appareil photo', 'Appareil photo numérique reflex', '2024-06-28 19:45:00', '2024-07-12 12:00:00', 500, 700, 3, 3, 6, 2, '99aa468e7d6654def37b895dccbf7b4601a06ee0.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (7, '10 rue du lundi','44320','Saint Père en Retz')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) 
VALUES	(2, 7, '2024-07-09 12:00:00.320', 510),
		(6, 7, '2024-07-09 12:15:22.150', 530),
		(2, 7, '2024-07-10 01:02:54.063', 550),
		(5, 7, '2024-07-10 09:20:40.685', 600),
		(2, 7, '2024-07-10 20:00:00.111', 650),
		(6, 7, '2024-07-11 12:05:32.222', 700)


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur ,no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Tableau un peu kitch', 'Magnifique tableau un peu kitch parfait pour un intérieur rétro','2024-07-01 12:00:00','2024-07-31 12:00:00', 30, 80, 3, 1, 4, 2, 'c3ba4fb33d0a233c909ef574511f2219938092aa.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (8, '10 rue du lundi','44320','Saint Père en Retz')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) 
VALUES	(7, 8, '2024-07-02 10:00:00', 35),
		(4, 8, '2024-07-05 12:30:00', 40),
		(3, 8, '2024-07-09 15:00:00', 50),
		(6, 8, '2024-07-10 08:45:00', 60),
		(7, 8, '2024-07-10 10:10:00', 70),
		(4, 8, '2024-07-10 11:00:00', 80);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Canapé', 'Canapé en tissu à fleurs','2024-07-05 12:00:00','2024-07-12 00:00:00.000', 50, 90, 4, 1, 3, 2, 'd4564b2033293992f83a6e14aeb18baad5320e88.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (9, '5 allée du contraire','97460','Saint Paul')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) 
VALUES	(2, 9, '2024-07-06 14:00:00', 55),
		(3, 9, '2024-07-07 11:15:00', 60),
		(10, 9, '2024-07-08 13:25:00', 65),
		(5, 9, '2024-07-09 16:50:00', 70),
		(10, 9, '2024-07-10 09:40:00', 75),
		(3, 9, '2024-07-10 11:00:00', 90);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Console de jeux', 'Console de jeux avec 2 manettes', '2024-07-01 15:50:00', '2024-07-11 12:00:00', 250, 250, 6, 6, 0, 2, '473008c1f1f2dc5e439b8c8052dcc0476eee5e6b.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (10, '1 avenue des cogneurs','75008','Paris')


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Chaussures de sport', 'Chaussures de sport en bon état', '2024-06-30 17:00:00', '2024-07-13 10:00:00', 80, 80, 7, 5, 0, 2, 'afdedf3f97e6df69854e64813839a41ea160930b.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (11, '88 rue de la bagarre','75015','Paris')


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Lampe de chevet', 'Lampe de chevet design', '2024-07-01 15:50:00', '2024-07-11 12:00:00', 40, 70, 1, 1, 2, 2, '049ef4203cd3b562184ac562cd72e970cf3086da.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (12, '5 rue des administrateurs','44000','Nantes')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) 
VALUES	(2, 12, '2024-07-02 10:00:00', 50),
		(5, 12, '2024-07-03 11:30:00', 60),
		(3, 12, '2024-07-04 09:45:00', 65),
		(2, 12, '2024-07-10 10:15:00', 70);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Chaise ergonomique', 'Chaise ergonomique pour bureau', '2024-07-05 20:00:00', '2024-07-15 12:00:00', 120, 200, 2, 1, 3, 2, 'c2df78c1f1f2dc5e439b8c8052dcc0476eee5e6b.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (13, '10 rue des membres normaux','75001','Paris')
-- Enchères pour "Chaise ergonomique"
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) 
VALUES	(1, 13, '2024-07-06 14:00:00', 150),
		(6, 13, '2024-07-07 16:20:00', 170),
		(7, 13, '2024-07-08 18:40:00', 200);


-- Articles vente terminée (état 3)
INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Table de jardin', 'Table de jardin en bois pour extérieur', '2024-06-15 00:00:00', '2024-07-10 00:00:00', 100, 200, 7, 1, 9, 3, '55827968c249d7b5299e104c6dafe1595d8418e5.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (14, '88 rue de la bagarre','75015','Paris')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (5, 14, '2024-06-20 10:00:00', 120);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (8, 14, '2024-06-25 14:00:00', 150);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (5, 14, '2024-07-01 16:00:00', 180);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (9, 14, '2024-07-09 11:30:00', 200);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente)
VALUES ('Bureau moderne', 'Bureau moderne en verre', '2024-06-20 00:00:00', '2024-07-05 00:00:00', 150, 300, 8, 1, 7, 3);
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (15, '1007 Mountain Drive','93419','Gotham')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (7, 15, '2024-06-22 10:00:00', 180);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (9, 15, '2024-06-27 12:00:00', 220);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (7, 15, '2024-07-03 17:00:00', 300);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente)
VALUES ('Sac à dos', 'Sac à dos de randonnée', '2024-06-25 00:00:00', '2024-07-10 00:00:00', 50, 120, 9, 5, 4, 3);
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (16, '729 avenue du Temps','03001','Berne')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (4, 16, '2024-06-28 09:00:00', 80);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (5, 16, '2024-07-01 11:00:00', 100);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (4, 16, '2024-07-08 15:00:00', 120);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Imprimante 3D', 'Imprimante 3D de bureau', '2024-06-20 00:00:00', '2024-07-05 00:00:00', 300, 300, 4, 2, 0, 3, '678af736cc0a6513bad4c73f84d16cb2ce18cd65.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (17, '5 allée du contraire','97460','Saint Paul')


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente)
VALUES ('Lit double', 'Lit double avec matelas confortable', '2024-06-25 00:00:00', '2024-07-05 00:00:00', 200, 200, 8, 1, 3, 3);
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (18, '1007 Mountain Drive','93419','Gotham')


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente)
VALUES ('Machine à café', 'Machine à café avec capsules', '2024-06-10 00:00:00', '2024-06-20 00:00:00', 50, 130, 10, 3, 6, 3);
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (19, '4 rue de la Chimie','64100','Bayonne')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (6, 19, '2024-06-18 15:00:00', 130);


-- Articles vente conclue et retirée par l'acheteur (état 4)
INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Clavier mécanique', 'Clavier mécanique pour gamer', '2024-06-10 00:00:00', '2024-06-20 00:00:00', 80, 150, 9, 2, 2, 4, 'c4769cb8c6dde888f3b7ebaec62c1458c7a23d02.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (20, '4 rue de la Chimie','64100','Bayonne')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (2, 20, '2024-06-15 10:00:00', 100);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (3, 20, '2024-06-18 12:00:00', 120);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (2, 20, '2024-06-19 16:00:00', 150);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente)
VALUES ('Drone', 'Drone avec caméra HD', '2024-06-18 00:00:00', '2024-06-28 00:00:00', 200, 450, 8, 3, 4, 4);
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (21, '5 passage des Amandiers','03800','Gannat')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (5, 21, '2024-06-20 09:00:00', 300);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (4, 21, '2024-06-27 18:00:00', 450);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Tondeuse à gazon', 'Tondeuse à gazon électrique', '2024-06-15 00:00:00', '2024-06-25 00:00:00', 100, 180, 11, 7, 10, 4, 'e84d81db3576b650500d5cecc7f72ba156abdfb6.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (22, '729 avenue du Temps','03001','Berne')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (10, 22, '2024-06-16 11:00:00', 120);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (11, 22, '2024-06-21 15:00:00', 130);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (4, 22, '2024-06-21 16:00:00', 150);
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (10, 22, '2024-06-24 09:00:00', 180);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('PC Gamer pour travailler', 'Ordinateur puissant pour faire du excel ...','2024-05-20 00:00:00.000','2024-05-25 00:00:00.000', 200, 320, 8, 2, 11, 4, 'e00abad70c2b6a5c60d7a14b24718754479f08d1.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (23, '5 rue des administrateurs','44000','Nantes')
INSERT INTO ENCHERES (no_utilisateur, no_article, date_enchere, montant_enchere) VALUES (11, 23, '2024-05-24 16:00:00', 320);


INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, no_acheteur, etat_vente, imageUUID)
VALUES ('Renault Zoé', 'Voiture électrique d''occas car ne roule plus','2024-05-30 00:00:00.000','2024-06-12 00:00:00.000', 3000, 3000, 2, 4, 0, 4, '6a54632f97e997be54a43e872aa7d59a0bba68f8.jpg');
INSERT INTO RETRAITS (no_article, rue, code_postal, ville)
VALUES (24, '10 rue des membres normaux','75001','Paris')


