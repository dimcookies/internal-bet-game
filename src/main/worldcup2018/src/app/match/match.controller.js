export default class MatchController {
    constructor($scope, $http, $stateParams, NgTableParams, logger) {

        this.$http = $http;
        this.NgTableParams = NgTableParams;
        this.gameId = $stateParams.gameId; //getting gameId
        this.logger = logger;
        this.activate();
    }

    activate() {
        var self = this;
        self.$http.get("/games/list?matchId=" + self.gameId).then(function(response) {
            self.selectedGame = response.data[0];
            self.$http.get("/bets/list?gameId=" + self.gameId).then(function(response) {
                self.selectedGameId = self.gameId;
                self.tableParams = new self.NgTableParams({
                    count: response.data.length // hides pager
                }, {
                    dataset: response.data,
                    total: 1,
                    counts: [] // hides page sizes                
                });
            });
        });

        self.$http.get("/bets/gameStats?gameId=" + self.gameId).then(function(response) {
            self.labels = ["HOME", "DRAW", "AWAY", "OVER", "UNDER"];
            self.data = [response.data.HOME_1, response.data.DRAW_X, response.data.AWAY_2, response.data.OVER, response.data.UNDER];
        });

    }
}

MatchController.$inject = ['$scope', '$http', '$stateParams', 'NgTableParams', 'logger'];