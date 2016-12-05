---- CreateOperator ----

create function EQ_F(a varchar, b varchar) return number
as
begin
   if a = b then return 1;
   else return 0;
   end if;
end;
/

create operator EQ_OP binding (varchar, varchar) return number using EQ_F
/


---- CreateMaterView ----

create table X_Order
(
  Id number(9) not null primary key,
  City_id number(9) not null,
  Note varchar(1000)
)
/

create materialized view log on X_Order
  with sequence, rowid (Id, City_Id)
  including new values
/

create materialized view X_Order_Stat
  build immediate refresh fast
  as
  select City_Id, count(*)
    from X_Order
    group by City_Id
/


---- CreateIndexedCluster ----

create cluster project_data
(
    tm_id number(6),
    pro_id number(6)
)
    size 256
/

create index project_data_i
    on cluster project_data
/

create table project
(
    tm_id number(6),
    pro_id number(6),
    pro_name varchar(60),
    primary key (tm_id,pro_id)
)
    cluster project_data(tm_id,pro_id)
/

create table project_property
(
    tm_id number(6),
    pro_id number(6),
    prop_code varchar(26),
    prop_name varchar(60),
    prop_value varchar(60),
    primary key (tm_id,pro_id,prop_code)
)
    cluster project_data(tm_id,pro_id)
/

insert into project
       values (111111, 222222, 'Y')
/

insert into project_property
       values (111111, 222222, 'X', 'Y', 'Z')
/

commit
/


---- CreateHashCluster ----

create cluster dump
(
    x number(9),
    y number(9)
)
    hash is x*13 + y*7 hashkeys 129
/

create table capping
(
    coord_x number(9),
    coord_y number(9),
    amount number(10,4),
    primary key (coord_x,coord_y)
)
    cluster dump(coord_x,coord_y)
/

create table kern_rock
(
    coord_x number(9),
    coord_y number(9),
    rock_type char(1),
    amount number(10,4),
    primary key (coord_x,coord_y,rock_type)
)
    cluster dump(coord_x,coord_y)
/

insert into capping
       values (-1, +1, 1000)
/

insert into kern_rock
       values (-1, +1, 'F', 670)
/

commit
/
