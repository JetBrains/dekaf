---- NoRows ----
declare
  x natural;
begin
  select 1
    into x
    from dual
    where 42 is null;
end;
/

