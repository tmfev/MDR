
// DIMENSIONALITY.coordinateIndicator
alter table DIMENSIONALITY add column new_coordinateIndicator boolean not null default false
update DIMENSIONALITY set new_coordinateIndicator=(coordinateIndicator=0)
alter table DIMENSIONALITY drop column coordinateIndicator
rename column DIMENSIONALITY.new_coordinateIndicator to coordinateIndicator

// NAMESPACE.mandatoryNamingConvention
alter table NAMESPACE add column new_mandatoryNamingConvention boolean default false
update NAMESPACE set new_mandatoryNamingConvention=(mandatoryNamingConvention=0)
alter table NAMESPACE drop column mandatoryNamingConvention
rename column NAMESPACE.new_mandatoryNamingConvention to mandatoryNamingConvention

// NAMESPACE.oneItemPerName
alter table NAMESPACE add column new_oneItemPerName boolean default false
update NAMESPACE set new_oneItemPerName=(oneItemPerName=0)
alter table NAMESPACE drop column oneItemPerName
rename column NAMESPACE.new_oneItemPerName to oneItemPerName

// NAMESPACE.oneNamePerItem
alter table NAMESPACE add column new_oneNamePerItem boolean default false
update NAMESPACE set new_oneNamePerItem=(oneNamePerItem=0)
alter table NAMESPACE drop column oneNamePerItem
rename column NAMESPACE.new_oneNamePerItem to oneNamePerItem

delete from SESSION
alter table SESSION drop constraint FKA11C0E76BF611DA1 
drop table SUBJECT

create table USER_GROUP (id bigint not null,primary key (id))
create table USER_GROUP_USR (USER_GROUP_id bigint not null,subjects_id bigint not null,primary key (USER_GROUP_id, subjects_id))
create table USR (id bigint not null,email varchar(255),passwordHash varchar(255),primary key (id))

alter table USER_GROUP_USR add constraint FKC4A0C20E9D7C957 foreign key (subjects_id) references USR
alter table USER_GROUP_USR add constraint FKC4A0C20EA3E1FEA foreign key (USER_GROUP_id) references USER_GROUP

alter table SESSION add constraint FKA11C0E76EB8D2ED2 foreign key (subject_id) references USR
