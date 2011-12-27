DROP TABLE IF EXISTS USERS;
CREATE TABLE USERS(
 USER_ID INTEGER PRIMARY KEY,
 USER_NAME VARCHAR (75),
 PASSWORD VARCHAR (75));

DROP TABLE IF EXISTS ROLES;
CREATE TABLE ROLES(
 ROLE_ID INTEGER PRIMARY KEY,
 ROLE_NAME VARCHAR (20));

DROP TABLE IF EXISTS USER_ROLES;
CREATE TABLE USER_ROLES(
 USER_ID_FK INTEGER,
 ROLE_ID_FK INTEGER);

INSERT INTO USERS
 VALUES(1,'admin','password');
INSERT INTO USERS
 VALUES(2,'csr','password');
INSERT INTO USERS
 VALUES(3,'guest',
 '{SSHA}zEWG/X8AzSdkHEFXE8pyCt0ddA321ktZz6bx1to9bFikZBS5wlAw3g==');
INSERT INTO ROLES
 VALUES(1,'everyone');
INSERT INTO ROLES
 VALUES(2,'csr');
INSERT INTO ROLES
 VALUES(3,'administrator');
INSERT INTO USER_ROLES
 VALUES(1,1);
INSERT INTO USER_ROLES
 VALUES(1,2);
INSERT INTO USER_ROLES
 VALUES(1,3);
INSERT INTO USER_ROLES
 VALUES(2,1);
INSERT INTO USER_ROLES
 VALUES(2,2);
INSERT INTO USER_ROLES
 VALUES(3,1);