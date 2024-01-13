    ════════════════════════════════════════════════════════════════════════════════════════════════      
    Java/Kotlin Database Access Layer Framework                                                                                                          
    ────────────────────────────────────────────────────────────────────────────────────────────────      
                                                                                                            
    DDDDDDDDDDDDD                           kkkkkkkk                              ffffffffffffffff          
    D::::::::::::DDD                        k::::::k                             f::::::::::::::::f         
    D:::::::::::::::DD                      k::::::k                            f::::::::::::::::::f        
    DDD:::::DDDDD:::::D                     k::::::k                            f::::::fffffff:::::f        
      D:::::D    D:::::D     eeeeeeeeeeee    k:::::k    kkkkkkkaaaaaaaaaaaaa    f:::::f       ffffff        
      D:::::D     D:::::D  ee::::::::::::ee  k:::::k   k:::::k a::::::::::::a   f:::::f                     
      D:::::D     D:::::D e::::::eeeee:::::eek:::::k  k:::::k  aaaaaaaaa:::::a f:::::::ffffff               
      D:::::D     D:::::De::::::e     e:::::ek:::::k k:::::k            a::::a f::::::::::::f               
      D:::::D     D:::::De:::::::eeeee::::::ek::::::k:::::k      aaaaaaa:::::a f::::::::::::f               
      D:::::D     D:::::De:::::::::::::::::e k:::::::::::k     aa::::::::::::a f:::::::ffffff               
      D:::::D     D:::::De::::::eeeeeeeeeee  k:::::::::::k    a::::aaaa::::::a  f:::::f                     
      D:::::D    D:::::D e:::::::e           k::::::k:::::k  a::::a    a:::::a  f:::::f                     
    DDD:::::DDDDD:::::D  e::::::::e         k::::::k k:::::k a::::a    a:::::a f:::::::f                    
    D:::::::::::::::DD    e::::::::eeeeeeee k::::::k  k:::::ka:::::aaaa::::::a f:::::::f                    
    D::::::::::::DDD       ee:::::::::::::e k::::::k   k:::::ka::::::::::aa:::af:::::::f                    
    DDDDDDDDDDDDD            eeeeeeeeeeeeee kkkkkkkk    kkkkkkkaaaaaaaaaa  aaaafffffffff                    
                                                                                                            
    ════════════════════════════════════════════════════════════════════════════════════════════════      

Dekaf is Java framework for working with databases, primarily via JDBC. 

Dekaf is NOT an ORM.


Versions
--------

Briefly:
  * The currently stable version is **2.0.0.274** (branch **dekaf-2**).
  * The development version is **3.0.0.X** (branch **revival**). 

Version 2 was successfully completed in mid-2013 and has been used in some applications. 
However, the structure of the modules was so complex and cumbersome that the further it 
went, the more difficult it was to make changes and support new features.

Finally, in January 2024 I decided to rewrite everything again, significantly simplifying the structure and throwing out unnecessary functionality.
I chose Kotlin as the primary language (but some modules remained in Java).


Motivation
----------

Three categories of problems lead me to start authoring a new DB layer framework.
 
JDBC problems:

* too verbose code one have to write for every simple call to a DB procedure and for every simple query
* too many code for data passing and converting
* the damn SQLException that is the same for all kinds of errors coming from DBMS, without possibility to handle same exceptions from different RDBMS by the same way
* JDBC doesn't support named parameters

DB dialect problems:

* different RDBMS have different SQL dialects, and when one wants to support several RDBMS they have to write every query several times (one for every SQL dialect), or to construct queries dynamically (that makes code unreadable)
* different RDBMS reports errors differently that requires different error handling for every query
* different RDBMS requires different data converting, that makes exception handling code overcomplicated
 
Development and debug of applications problem
 
* due to the problems mentioned above, one have to construct their queries dynamically, that obstructs to try and debug these queries right from the code  



Features
--------

R.2.0 features:

Key features to address the problems mentioned above:

* SQL queries are in separated .sql files, that allow to try/debug them right from these files
* retrieve data as commonly used Java structures (arrays, lists, sets)
* unified exceptions hierarchy (partially)

R.2.0 supported RDBMS:

* PostgreSQL     9+
* Oracle         9+
* MS SQL Server  9+
* Sybase ASE     15+
* MySQL          5.1+
* H2             1.4+

Planned features to address the problems mentioned above:

* SQL macros that allow to unify queries and reduce dynamic query text constructions
* named parameter
* typed out parameters
* unified exceptions hierarchy (better hierarchy)

Planned supported RDBMS:

* Azure
* RedShift
* SQLite



The Name
--------

The name 'Dekaf' means that this framework allows to
avoid all pains and problems belonging naturally to pure JDBC API.

A coffee (Java) without headache.

P.S. The former name of this framework is **JDBA** (just Java DataBase Access).
