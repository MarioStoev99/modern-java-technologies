DROP TABLE IF EXISTS parcel;
CREATE TABLE parcel (
                          origin_id UUID NOT NULL PRIMARY KEY,
                          recipient VARCHAR(255) NULL,
                          address VARCHAR(255) NULL,
                          sender VARCHAR(255) NOT NULL,
                          weight int NOT NULL
);