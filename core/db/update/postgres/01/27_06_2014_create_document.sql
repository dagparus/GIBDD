create table ext_document (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    DOCTYPE varchar(30),
    SERIAL integer,
    NUMBER integer,
    WHOM_GIVE_OUT varchar(100),
    WHEN_GIVE_OUT date,
    EXPIRATION_DATE date,
    OWNER_ID uuid,
    primary key (ID)
)^

alter table ext_document add constraint FK_EXT_DOCUMENT_PERSON
    foreign key (OWNER_ID) references EXT_NATURAL_PERSON(ID);
