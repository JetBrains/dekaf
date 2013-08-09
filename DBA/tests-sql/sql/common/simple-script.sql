-- A simple script that can be executable on all supported RDBMS.

create table Simple_Table
(
    Prop_Code varchar(6) not null primary key,
    Prop_Value nvarchar(80)
)
;

insert into Simple_Table values ('P1', 'Aaa')
;

insert into Simple_Table values ('P2', 'Bbb')
;

commit
;

drop table Simple_Table
;

