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
  build immediate refresh fast enable query rewrite
  as
  select City_Id, count(*)
    from X_Order
    group by City_Id
/

