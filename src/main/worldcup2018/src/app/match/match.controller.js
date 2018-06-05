export default class MatchController {
    constructor($scope, $http, $stateParams, logger) {

        this.$http = $http;
        this.messages = [];
        this.gameId = $stateParams.gameId; //getting gameId
        this.logger = logger;
        this.activate();
    }

    activate() {
        var self = this;
        self.$http.get("/games/list?matchId=" + self.gameId).then(function(response) {
            self.selectedGame = response.data[0];
            self.$http.get("/bets/list?gameId=" + self.gameId).then(function(response) {
                self.allBets = response.data;
                self.selectedGameId = self.gameId;
            });
        });
    }
}

MatchController.$inject = ['$scope', '$http', '$stateParams', 'logger'];