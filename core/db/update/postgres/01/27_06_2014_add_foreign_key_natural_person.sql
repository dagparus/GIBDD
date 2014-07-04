alter table ext_natural_person add constraint FK_EXT_PERSON_DOCUMENT
    foreign key (DOCUMENT_ID) references EXT_DOCUMENT(ID);