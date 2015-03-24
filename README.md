JDBA
====

Java Database Access Layer Framework

Allows to access database easily and safely. 




BUILD
-----

JDBA requires JDK 1.6 or newer.

Build a development package:

    mvn package
    
Build a package with build number:
     
    mvn versions:set -DnewVersion=2.0.0-B.1234 versions:commit 
    mvn package -Djdba.build.nr=1234
    
