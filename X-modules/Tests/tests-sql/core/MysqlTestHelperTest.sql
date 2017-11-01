---- SimpleTableMyIsam ----
create table simple_table_1
(
  column1 char(1),
  column2 char(2)
) engine MyIsam
/

---- SimpleTableInnoDB ----
create table simple_table_2
(
  column1 char(1),
  column2 char(2)
) engine InnoDB
/


---- CyclicFK ----
create table Tab_A
(
  column_A int not null primary key,
  column_B int
) engine InnoDB
/

create table Tab_B
(
  column_B int not null primary key,
  column_A int
) engine InnoDB
/

alter table Tab_A
  add constraint Tab_A_ref_B_fk
    foreign key (column_B) references Tab_B (column_B)
/

alter table Tab_B
  add constraint Tab_B_ref_A_fk
    foreign key (column_A) references Tab_A (column_A)
/


---- Pro42 ----
create procedure Pro42()
begin
  select 42;
end
/


---- Fun33 ----
create function Fun33() returns int
begin
  return 33;
end
/


---- TableFunView ----
create table T1 (c1 int)
/

create function fun_t1(x int) returns int
begin
  declare zz int;
  select count(*) into zz from T1 where T1.c1 = x;
  return zz;
end
/

create view V1
as
select fun_t1(99)
/


---- CheckTableExists ----
select 1
from information_schema.tables
where table_schema = schema()
  and lower(table_name) = lower(?)
/

---- CheckRoutineExists ----
select 1
from information_schema.routines
where routine_schema = schema()
  and routine_name = ?
/