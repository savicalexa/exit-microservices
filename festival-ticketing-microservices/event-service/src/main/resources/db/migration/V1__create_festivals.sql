CREATE TABLE festivals (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    naziv NVARCHAR(200) NOT NULL,
    lokacija NVARCHAR(200) NOT NULL,
    maksimalni_kapacitet INT NOT NULL,
    version BIGINT NOT NULL CONSTRAINT df_festivals_version DEFAULT 0,
    CONSTRAINT ck_festivals_capacity CHECK (maksimalni_kapacitet > 0)
);

CREATE UNIQUE INDEX ux_festivals_name_location ON festivals(naziv, lokacija);
