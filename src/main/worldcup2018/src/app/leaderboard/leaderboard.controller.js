export default class LeaderboardController {
    constructor($scope, $http, logger) {
        this.$http = $http;
        this.messages = [];
        this.logger = logger;

        this.activate();
    }

    activate() {
    var self =this;
        this.$http.get("/bets/points").then(function(response) {
            self.allPoints = response.data;
        });

    }
}

LeaderboardController.$inject = ['$scope', '$http', 'logger'];