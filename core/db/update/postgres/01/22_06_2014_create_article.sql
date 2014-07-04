create table ext_article (
     ID uuid,
     CREATE_TS timestamp,
     CREATED_BY varchar(50),
     VERSION integer,
     UPDATE_TS timestamp,
     UPDATED_BY varchar(50),
     DELETE_TS timestamp,
     DELETED_BY varchar(50),
     NUMBER varchar(50),
     NAME varchar(100),
     TEMPLATE varchar(1000),
     PENALTY varchar(25),
     primary key (ID)
)