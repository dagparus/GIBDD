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