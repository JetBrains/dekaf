create table T1
(
    F1 number(9),
    F2 varchar(60)
)
;

insert into T1 (F1,F2)
       values (44, 'A string with slash / and colon semicolon;')
;

commit
;

drop table T1
;

