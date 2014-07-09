-- Place database initialization here
create table ext_article (
     ID uuid,
     CREATE_TS timestamp,
     CREATED_BY varchar(50),
     VERSION integer,
     UPDATE_TS timestamp,
     UPDATED_BY varchar(50),
     DELETE_TS timestamp,
     DELETED_BY varchar(50),
     PART varchar(10),
     NUMBER varchar(10),
     NAME varchar(100),
     TEMPLATE varchar(1000),
     PENALTY varchar(10),
     primary key (ID)
);

create table ext_natural_person (
     ID uuid,
     CREATE_TS timestamp,
     CREATED_BY varchar(50),
     VERSION integer,
     UPDATE_TS timestamp,
     UPDATED_BY varchar(50),
     DELETE_TS timestamp,
     DELETED_BY varchar(50),
     NAME varchar(50),
     SURNAME varchar(70),
     PATRONYMIC varchar(60),
     FULLNAME varchar(180),
     NAME_OF varchar(50),
     SURNAME_OF varchar(70),
     PATRONYMIC_OF varchar(60),
     FULLNAME_OF varchar(180),
     BIRTHDATE date,
     BIRTHPLACE varchar(200),
     SEX varchar(10),
     RESIDENCE varchar(200),
     DOMICILE varchar(200),
     TELEPHONE varchar(50),
     ORGANIZATION varchar(200),
     TYPE varchar(2),
     DOCUMENT_ID uuid,
     primary key (ID)
);

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
    CATEGORY varchar(10),
    WHOM_GIVE_OUT varchar(100),
    WHEN_GIVE_OUT date,
    EXPIRATION_DATE date,
    OWNER_ID uuid,
    primary key (ID)
)^

alter table ext_document add constraint FK_EXT_DOCUMENT_PERSON
    foreign key (OWNER_ID) references EXT_NATURAL_PERSON(ID);

alter table ext_natural_person add constraint FK_EXT_PERSON_DOCUMENT
    foreign key (DOCUMENT_ID) references EXT_DOCUMENT(ID);

create table ext_testimony (
     ID uuid,
     CREATE_TS timestamp,
     CREATED_BY varchar(50),
     VERSION integer,
     UPDATE_TS timestamp,
     UPDATED_BY varchar(50),
     DELETE_TS timestamp,
     DELETED_BY varchar(50),
     WITNESS_ID uuid,
     TESTIMONY varchar(1000),
     primary key(ID)
)^

alter table ext_testimony add constraint FK_EXT_TESTIMONY_PERSON
    foreign key (WITNESS_ID) references EXT_NATURAL_PERSON(ID);

create table ext_car(
ID uuid,
BRAND varchar(100),               --Марка автомобиля
MODEL varchar(100),               --Модель автомобиля
STATE_LICENSE_TOKEN varchar(100), --Государственный регистрационный знак
OWNER_ID uuid,                    --Владелец
CREATE_TS timestamp,              --Когда создано (системное поле)
CREATED_BY varchar(50),           --Кем  создано (системное поле)
VERSION integer,                  --Версия (системное поле)
UPDATE_TS timestamp,              --Когда было последнее изменение (системное поле)
UPDATED_BY varchar(50),           --Кто последний раз изменил сущность(системное поле)
DELETE_TS timestamp,              --Когда удалено (системное поле)
DELETED_BY varchar(50),           --Кем удалено (системное поле)
primary key (ID),
constraint FK_EXT_CAR foreign key (OWNER_ID)
   references EXT_NATURAL_PERSON(ID)
);

create table ext_cause (
    CARD_ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    OFFENDER_ID uuid,
    POLICEMAN_ID uuid,
    CAR_ID uuid,
    DOCUMENT_ID uuid,
    PETITION boolean,
    WITNESS_ID uuid,
    SERIAL varchar(100),
    VICTIM_ID uuid,
    TESTIMONY_ID uuid,
    SETTLING_DATE date,
    SETTLING_PLACE varchar(100),
    VIOLATION_DATE date,
    VIOLATION_ESSENCE varchar(100),
    VIOLATION_PLACE varchar(100),
    ARTICLE_ID uuid,
    REVIEW_PLACE varchar(100),
    CONDITION varchar(500),
    primary key(CARD_ID)
)^

alter table ext_cause add constraint FK_EXT_CAUSE_DOC
    foreign key (CARD_ID) references DF_DOC(CARD_ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_OFFENDER_PERSON
    foreign key (OFFENDER_ID) references EXT_NATURAL_PERSON(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_POLICEMAN
    foreign key (POLICEMAN_ID) references EXT_POLICEMAN(PERSON_ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_CAR
    foreign key (CAR_ID) references EXT_CAR(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_DOCUMENT
    foreign key (DOCUMENT_ID) references EXT_DOCUMENT(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_WITNESS_PERSON
    foreign key (WITNESS_ID) references EXT_NATURAL_PERSON(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_VICTIM_PERSON
    foreign key (VICTIM_ID) references EXT_NATURAL_PERSON(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_TESTIMONY
    foreign key (TESTIMONY_ID) references EXT_TESTIMONY(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_ARTICLE
    foreign key (ARTICLE_ID) references EXT_ARTICLE(ID)^

insert into DF_DOC_TYPE (ID, CREATE_TS, CREATED_BY, NAME, DISCRIMINATOR)
values ('46ffadc4-3b6a-11e1-9e84-47fae3df0838', current_timestamp, 'admin', 'ext$CauseGIBDD', 130)
^

insert into SYS_CATEGORY (ID, NAME, ENTITY_TYPE, IS_DEFAULT, CREATE_TS, CREATED_BY, VERSION, DISCRIMINATOR)
values ('51bb9e98-3b89-11e1-840f-b76bc93cc547', 'Дело ГИБДД', 'ext$CauseGIBDD', false, now(), USER, 1, 1)
^

insert into DF_DOC_KIND (category_id, create_ts, created_by, version, doc_type_id, fields_xml, numerator_type, category_attrs_place)
values ('51bb9e98-3b89-11e1-840f-b76bc93cc547', '2011-01-10 00:00:00.00', 'admin', 1, '46ffadc4-3b6a-11e1-9e84-47fae3df0838',
'<?xml version="1.0" encoding="UTF-8"?>

<fields>
<field name="number" visible="true" required="false"/>
<field name="parentCard" visible="true" required="false"/>
<field name="comment" visible="true" required="false"/>
<field name="docKind" visible="true" required="false"/>
</fields>'
, null, 1);