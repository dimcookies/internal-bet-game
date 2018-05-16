--------------------------------------------------------------------------------
-- tables/TEXT_MESSAGE
--------------------------------------------------------------------------------
CREATE TABLE GAME
(
	ID					INTEGER NOT NULL, --SERIAL 			NOT NULL,
	HOME_ID             INTEGER NOT NULL,
	HOME_NAME			VARCHAR(100)	NOT NULL,
	AWAY_ID             INTEGER NOT NULL,
	AWAY_NAME			VARCHAR(100)	NOT NULL,
	GAME_DATE           TIMESTAMP WITH TIME ZONE NOT NULL,
	STATUS              VARCHAR(100)	NOT NULL,
	MATCH_DAY           INTEGER NOT NULL,
    GOALS_HOME          INTEGER NOT NULL,
    GOALS_AWAY          INTEGER NOT NULL,
	CONSTRAINT GAME_PK PRIMARY KEY ( ID )
);


CREATE TABLE ODD
(
	ID					SERIAL 			NOT NULL,
	GAME_ID             INTEGER NOT NULL,
	ODDS_HOME             REAL NOT NULL,
	ODDS_AWAY             REAL NOT NULL,
	ODDS_TIE              REAL NOT NULL,
	ODDS_OVER             REAL NOT NULL,
	ODDS_UNDER            REAL NOT NULL,
	CONSTRAINT ODD_PK PRIMARY KEY ( ID )
);

ALTER TABLE ODD ADD CONSTRAINT ODD_U01 UNIQUE ( GAME_ID );
ALTER TABLE ODD ADD CONSTRAINT ODD_FK01 FOREIGN KEY ( GAME_ID ) REFERENCES GAME ( ID );


CREATE TABLE ALLOWED_USERS
(
	ID					SERIAL 			NOT NULL,
	NAME              VARCHAR(100)	NOT NULL,
	EMAIL              VARCHAR(100)	NOT NULL,
	PASSWORD              VARCHAR(100)	NOT NULL,
	ROLE              VARCHAR(100)	NOT NULL,
	CONSTRAINT ALLOWED_USERS_PK PRIMARY KEY ( ID )
);
ALTER TABLE ALLOWED_USERS ADD CONSTRAINT ALLOWED_USERS_U01 UNIQUE ( NAME );
ALTER TABLE ALLOWED_USERS ADD CONSTRAINT ALLOWED_USERS_U02 UNIQUE ( EMAIL );


CREATE TABLE BET
(
	ID					SERIAL 			NOT NULL,
	GAME_ID             INTEGER NOT NULL,
	USER_ID             INTEGER NOT NULL,
	RESULT_BET          VARCHAR(100)	NOT NULL,
	RESULT_POINTS       INTEGER	NOT NULL DEFAULT 0,
	OVER_BET            VARCHAR(100),
	OVER_POINTS         INTEGER	NOT NULL DEFAULT 0,
	BET_DATE           TIMESTAMP WITH TIME ZONE NOT NULL,
	CONSTRAINT BET_PK PRIMARY KEY ( ID )
);
ALTER TABLE BET ADD CONSTRAINT BET_FK01 FOREIGN KEY ( GAME_ID ) REFERENCES GAME ( ID );
ALTER TABLE BET ADD CONSTRAINT BET_FK02 FOREIGN KEY ( USER_ID ) REFERENCES ALLOWED_USERS ( ID );
ALTER TABLE BET ADD CONSTRAINT BET_U01 UNIQUE ( GAME_ID, USER_ID );



CREATE TABLE ENCRYPTED_BET
(
	ID					SERIAL 			NOT NULL,
	GAME_ID             INTEGER NOT NULL,
	USER_ID             INTEGER NOT NULL,
	RESULT_BET          VARCHAR(100)	NOT NULL,
	OVER_BET            VARCHAR(100),
	BET_DATE           TIMESTAMP WITH TIME ZONE NOT NULL,
	CONSTRAINT ENCRYPTED_BET_PK PRIMARY KEY ( ID )
);
ALTER TABLE ENCRYPTED_BET ADD CONSTRAINT ENCRYPTED_BET_FK01 FOREIGN KEY ( GAME_ID ) REFERENCES GAME ( ID );
ALTER TABLE ENCRYPTED_BET ADD CONSTRAINT ENCRYPTED_BET_FK02 FOREIGN KEY ( USER_ID ) REFERENCES ALLOWED_USERS ( ID );
ALTER TABLE ENCRYPTED_BET ADD CONSTRAINT ENCRYPTED_BET_U01 UNIQUE ( GAME_ID, USER_ID );
