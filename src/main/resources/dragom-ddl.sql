DROP TABLE IF EXISTS PROPERTY;
DROP TABLE IF EXISTS PLUGIN;
DROP TABLE IF EXISTS NODE;
DROP TABLE IF EXISTS NEXT_ID;

CREATE TABLE NEXT_ID (
  ENTITY CHAR(16) NOT NULL,
  NEXT_ID INTEGER NOT NULL,
  PRIMARY KEY (ENTITY)
);

CREATE TABLE NODE (
  ID                 INTEGER      NOT NULL,
  ID_NODE_PARENT     INTEGER              ,
  NAME               VARCHAR(255)         ,
  TYPE               CHAR(1)      NOT NULL,
  TIMESTAMP_LAST_MOD TIMESTAMP    NOT NULL,

  PRIMARY KEY (ID),
  FOREIGN KEY (ID_NODE_PARENT) REFERENCES NODE (ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
  UNIQUE (ID_NODE_PARENT, NAME)
);

CREATE TABLE PROPERTY (
  ID_NODE            INTEGER            NOT NULL,
  NAME               VARCHAR(255)       NOT NULL,
  VALUE              VARCHAR(4000)              ,
  IND_ONLY_THIS_NODE BOOLEAN            NOT NULL,

  FOREIGN KEY (ID_NODE) REFERENCES NODE (ID) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE PLUGIN (
  ID_NODE            INTEGER      NOT NULL,
  INTERFACE          VARCHAR(255)         ,
  PLUGIN_ID          VARCHAR(255)         ,
  CLASS              VARCHAR(255) NOT NULL,
  IND_ONLY_THIS_NODE BOOLEAN      NOT NULL,

  FOREIGN KEY (ID_NODE) REFERENCES NODE (ID) ON DELETE CASCADE ON UPDATE RESTRICT
);