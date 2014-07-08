create table ext_policeman (
    PERSON_ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    POST varchar(50),
    POST_OF varchar(50),
    AREA varchar(50),
    ADDRESS_ORGAN varchar(100),
    CITY varchar(50),
    BADGE_NUMBER integer,
    TYPE varchar(2),
    primary key (PERSON_ID)
)^

alter table ext_policeman add constraint FK_EXT_POLICEMAN_PERSON
    foreign key (PERSON_ID) references EXT_NATURAL_PERSON(ID);

