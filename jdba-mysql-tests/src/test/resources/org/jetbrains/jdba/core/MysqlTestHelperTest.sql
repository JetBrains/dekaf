---- SimpleTableMyIsam ----
create table simple_table_1
(
  column1 char(1),
  column2 char(2)
) engine MyIsam
;

---- SimpleTableInnoDB ----
create table simple_table_2
(
  column1 char(1),
  column2 char(2)
) engine InnoDB
;


---- CyclicFK ----
create table Tab_A
(
  column_A int not null primary key,
  column_B int
) engine InnoDB
;

create table Tab_B
(
  column_B int not null primary key,
  column_A int
) engine InnoDB
;

alter table Tab_A
  add constraint Tab_A_ref_B_fk
    foreign key (column_B) references Tab_B (column_B)
;

alter table Tab_B
  add constraint Tab_B_ref_A_fk
    foreign key (column_A) references Tab_A (column_A)
;


---- CheckTableExists ----
select 1
from information_schema.tables
where table_schema = schema()
  and lower(table_name) = lower(?)
;
