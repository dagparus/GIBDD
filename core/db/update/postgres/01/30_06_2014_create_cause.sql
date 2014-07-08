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
    ARTICLE_ID uuid,
    REVIEW_PLACE varchar(100),
    CONDITION varchar(500),
    primary key(CARD_ID)
)^

alter table ext_cause add constraint FK_EXT_CAUSE_DOC
    foreign key (CARD_ID) references DF_DOC(CARD_ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_PERSON
    foreign key (OFFENDER_ID) references EXT_NATURAL_PERSON(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_POLICEMAN
    foreign key (POLICEMAN_ID) references EXT_POLICEMAN(PERSON_ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_CAR
    foreign key (CAR_ID) references EXT_CAR(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_DOCUMENT
    foreign key (DOCUMENT_ID) references EXT_DOCUMENT(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_PERSON
    foreign key (WITNESS_ID) references EXT_NATURAL_PERSON(ID)^

alter table ext_cause add constraint FK_EXT_CAUSE_PERSON
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
