create table T1
(
    Id number(9) not null unique
)
/

insert into T1 values (111);
insert into T1 values (222);
insert into T1 values (333);

commit
/

create view My_Tabs
as
select Table_Name from Tabs
/

declare
    --
    tr My_Tabs%rowtype;
    --
begin
    --
    for tr in (select * from My_Tabs)
        loop
            dbms_Output.put_line(tr.Table_Name);
        end loop;
    --
end;
/