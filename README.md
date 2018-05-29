# internal-bet-game

## Prerequisites

* Java 8
* maven version 3.x
* Postgres 9.3+

## Init the module database / user /schema:

    mvn clean install -DskipTests -Dinit.database.skip=false -Ddb.host=rdbms -Ddb.port=5432 -Ddb.root.password=postgres -Ddb.module.password=postgres flyway:migrate

