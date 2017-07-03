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


---- CreateMaterView1 ----

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


---- CreateMaterViewDependence ----

create materialized view owners
    refresh complete
as
select distinct owner
  from sys.all_objects
/

create materialized view schemas
    refresh complete with rowid
as
select owner as name
  from owners
  where owner not in ('SYS','SYSTEM','PUBLIC','XDB','CTXSYS','EXFSYS','WMSYS','DBSNMP')
/


---- CreateMaterViewPrebuilt ----

create table One
(
    digit char(1)
)
/

create materialized view One
    on prebuilt table
    refresh complete
as
select cast('1' as char(1)) as digit
from dual
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


---- Quotation ----

create cluster "The Cluster"
(
    "Id" number(9)
)
    size 64
/

create table "The Table"
(
    "My Id"   number(9) constraint "The Primary Key" primary key,
    "My Name" varchar(26) constraint "The Alternative Key" unique,
    "My DOB"  date
)
    cluster "The Cluster" ("My Id")
/

create index "The Index"
    on "The Table" ("My DOB")
/

create view "The View" as
select *
from "The Table"
/

create package "The Package" as
    function "Fun";
    pragma restrict_references ("Fun", rnds, wnds);
end "The Package";
/

create package body "The Package" as
    function "Fun" is
    begin
      null;
    end;
end "The Package";
/

create type "The Object Type" as object ("ha-ha" varchar(4))
/

create procedure "The Procedure" is
begin
    null;
end;
/

create function "The Fun" (a varchar, b varchar) return number as
begin
    return 42;
end;
/

create or replace operator "The Operator"
    binding (varchar, varchar)
    return number
    using "The Fun"
/
