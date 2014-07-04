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
)^