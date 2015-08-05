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
