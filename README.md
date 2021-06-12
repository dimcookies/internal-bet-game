# internal-bet-game

Implementation of the custom bet game

* Users selects 1/X/2 in all matches (not the exact score)
  * If you miss the pick, you get 0 points.
  * If you make the pick, you get the "base points" multiplied by the "betting factor".
    * What is "base points"? Base points = 100 for all Group Stage matches, 200 for all the rest of the matches except for the Final &3rd place play-off that gives 300.
    * What is the "betting factor"? It's the factor rewarding risky bets vs safe ones? Example:
      * Let's say England vs Italy gives 1.5 for "1", 2.5 for "X" and 3.5 for "2".
      * If you've selected "1" and you guessed it right you'll get Base Points * 1.5
      * If you've selected "X" and you guessed it right you'll get Base Points * 2.5
      * If you've selected "2" and you guessed it right you'll get Base Points * 3.5
      * Again: if you've guessed it wrong you get 0.
* Users will also select Over/Under in all matches except for the Group Stage.
  * This gives extra points ONLY if the 1/X/2 pick was correct.
  * Extra points given by guessing right is: 0.5 * Base Points * "O/U betting factor"
* Note: All results are "90-min period" results (any overtime will be recorded as a tie = "X"); applies both on 1/x/2 and O/U
* Most points win!

The implementation is for World Cup 2018, but can easily be adapted to other tournaments. Live score 
feed is implemented based on football-api.org (and there is a backup option on fifa.com live scores)

## Prerequisites

* Java 8
* maven version 3.x
* Postgres 9.3+
* (Node 12.X for testing gui)

## Init the module database / user /schema:

    mvn clean install -DskipTests -Dinit.database.skip=false -Ddb.host=rdbms -Ddb.port=5432 -Ddb.root.password=postgres -Ddb.module.password=postgres flyway:migrate

## Frontend:

    npm install
    npm run build (copies frontend in resources folder and served by tomcat)
    npm start (for dev, changes ports in webpack.config.js)

## Steps to init project

* Check that livefeed implementation works (activated by spring profile). Check if tokens work
* Download matches, add in classpath (resources) and fix GameInitializer 
  to import them in database  (dto is based on api v1 response from http://api.football-data.
  org/v2/competitions/2001/matches?status=SCHEDULED)
* Need to import odds some days before each round starts 
* Create deadlines in database based on tournament schedule (current days and allowed days for betting. Use 
  array for multiple values, empty deadline message does not show bets in gui)
  * "currentMatchDays": "1,2,3", "allowedMatchDays": "1,2,3", "betDeadlineText": "date text"  
  * "currentMatchDays": "1,2,3", "allowedMatchDays": null, "betDeadlineText": null
  * "currentMatchDays": "1,2,3,4", "allowedMatchDays": "4", "betDeadlineText": "date text"
  * "currentMatchDays": "1,2,3,4,5,6,7,8", "allowedMatchDays": "8,7", "betDeadlineText": "date text"
* Fix email credentials
* Change encryption key  
* After all bets have been set, decrypt them
* Check rss feeds
* Upload odds.csv using the management endpoint for each stage
* Change in leaderboard.html the condition to show different color for winners
* The implementation is based on world cup 2018 with 8 rounds, some tweaks 
might be needed for tournaments with more/less rounds or different format
* Inititial status of games should be SCHEDULED