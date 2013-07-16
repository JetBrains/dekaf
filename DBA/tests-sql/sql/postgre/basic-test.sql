--=-- CreateTable
create table Basic_Table
(
    Basic_Id int not null primary key,
    Basic_Name varchar(25)
);


--=-- InsertIntoTable
insert
    into Basic_Table
    values (44, 'Forty Four');


--=-- SelectFromTable
select *
from Basic_Table;


--=-- DropTable
drop table Basic_Table;
