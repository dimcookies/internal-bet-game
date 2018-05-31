export default class LeaderboardController {
    constructor($scope, $http, logger, messageService) {
        this.$http = $http;
        this.messages = [];
        this.messageService = messageService;
        this.logger = logger;

        this.activate();
    }

    activate() {
        this.$http.get("/bets/points").then(function(response) {
            this.allPoints = response.data;
        });

    }
}

LeaderboardController.$inject = ['$scope', '$http', 'logger', 'messageService'];