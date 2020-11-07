[![official project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

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
      
   

Dekaf is Java framework for working with databases, primarily via JDBC. Dekaf is NOT an ORM.

Major Dekaf versions:

| Version                  | Status                            |
| ------------------------ | --------------------------------- |
| 1.x (codename Hydrogen)  | Not supported                     |
| 2.0.0.x                  | The stable version                |
| 3.x                      | This version (under construction) |



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


Version 3
---------

This version is started from scratch, to get the perfect internal architecture
and get rid of legacy code.

In version 3 most of code will be written on Kotlin and just, except two modules:
   * intermediate interface
   * JDBC-specific stuff

Requirements:
   * Java 11+
   * Kotlin 1.3.60+



Features
--------

Key features to address the problems mentioned above:

* SQL queries are in separated .sql files, that allow to try/debug them right from these files
* retrieve data as commonly used Java/Kotlin structures (arrays, lists, sets, data classes)
* unified exceptions hierarchy (partially)


The Name
--------

The name 'Dekaf' means that this framework allows to
avoid all pains and problems belonging naturally to pure JDBC API.

A coffee (Java) without headache.

P.S. The former name of this framework is JDBA (just Java DataBase Access).
