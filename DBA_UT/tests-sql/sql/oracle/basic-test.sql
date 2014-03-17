--=-- CreateTable
create table Basic_Table
(
    Basic_Id number(9) not null primary key,
    Basic_Name varchar(25)
)
/


--=-- InsertIntoTable
insert
    into Basic_Table
    values (44, 'Forty Four')
/

--=-- SelectFromTable
select *
from Basic_Table
order by 1
/

--=-- DropTable
drop table Basic_Table
/




