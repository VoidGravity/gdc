CREATE TABLE clients (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) UNIQUE NOT NULL,
                         adresse VARCHAR(255) NOT NULL,
                         telephone VARCHAR(20) NOT NULL,
                         est_professionnel BOOLEAN NOT NULL
);

CREATE TABLE projets (
                         id SERIAL PRIMARY KEY,
                         nom_projet VARCHAR(100) UNIQUE NOT NULL,
                         marge_beneficiaire DECIMAL(5,2) NOT NULL,
                         cout_total DECIMAL(10,2) NOT NULL,
                         etat_projet VARCHAR(20) NOT NULL,
                         client_id INTEGER REFERENCES clients(id)
);

CREATE TABLE composants (
                            id SERIAL PRIMARY KEY,
                            projet_id INTEGER REFERENCES projets(id),
                            nom VARCHAR(100) NOT NULL,
                            cout_unitaire DECIMAL(10,2) NOT NULL,
                            quantite DECIMAL(10,2) NOT NULL,
                            type_composant VARCHAR(20) NOT NULL,
                            taux_tva DECIMAL(5,2) NOT NULL,
                            cout_transport DECIMAL(10,2),
                            coefficient_qualite DECIMAL(5,2),
                            taux_horaire DECIMAL(10,2),
                            heures_travail DECIMAL(10,2),
                            productivite_ouvrier DECIMAL(5,2)
);

CREATE TABLE devis (
                       id SERIAL PRIMARY KEY,
                       projet_id INTEGER UNIQUE REFERENCES projets(id),
                       montant_estime DECIMAL(10,2) NOT NULL,
                       date_emission DATE NOT NULL,
                       date_validite DATE NOT NULL,
                       accepte BOOLEAN NOT NULL
);