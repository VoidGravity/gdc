-- Create clients table
CREATE TABLE IF NOT EXISTS clients (
                                       id SERIAL PRIMARY KEY,
                                       nom VARCHAR(100) NOT NULL,
    adresse TEXT,
    telephone VARCHAR(20),
    est_professionnel BOOLEAN
    );

-- Create projects table
CREATE TABLE IF NOT EXISTS projects (
                                        id SERIAL PRIMARY KEY,
                                        nom_projet VARCHAR(100) NOT NULL,
    marge_beneficiaire DECIMAL(5,2),
    cout_total DECIMAL(10,2),
    etat_projet VARCHAR(20),
    client_id INTEGER REFERENCES clients(id)
    );

-- Create components table
CREATE TABLE IF NOT EXISTS components (
                                          id SERIAL PRIMARY KEY,
                                          nom VARCHAR(100) NOT NULL,
    type_composant VARCHAR(20) NOT NULL,
    taux_tva DECIMAL(5,2)
    );

-- Create materials table
CREATE TABLE IF NOT EXISTS materials (
                                         component_id INTEGER PRIMARY KEY REFERENCES components(id),
    cout_unitaire DECIMAL(10,2),
    quantite DECIMAL(10,2),
    cout_transport DECIMAL(10,2),
    coefficient_qualite DECIMAL(5,2)
    );

-- Create labor table
CREATE TABLE IF NOT EXISTS labor (
                                     component_id INTEGER PRIMARY KEY REFERENCES components(id),
    taux_horaire DECIMAL(10,2),
    heures_travail DECIMAL(10,2),
    productivite_ouvrier DECIMAL(5,2)
    );

-- Create quotes table
CREATE TABLE IF NOT EXISTS quotes (
                                      id SERIAL PRIMARY KEY,
                                      montant_estime DECIMAL(10,2),
    date_emission DATE,
    date_validite DATE,
    accepte BOOLEAN,
    project_id INTEGER REFERENCES projects(id)
    );

-- Create project_components junction table
CREATE TABLE IF NOT EXISTS project_components (
                                                  project_id INTEGER REFERENCES projects(id),
    component_id INTEGER REFERENCES components(id),
    PRIMARY KEY (project_id, component_id)
    );