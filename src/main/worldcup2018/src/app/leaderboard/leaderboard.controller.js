export default class LeaderboardController {
    constructor($scope, $http, NgTableParams, logger) {
        this.$http = $http;
        this.logger = logger;
        this.NgTableParams = NgTableParams;        
        this.activate();
    }

    activate() {
        var self = this;
        this.$http.get("/bets/points").then(function(response) {
            self.tableParams = new self.NgTableParams({
                count: response.data.length // hides pager
            }, {
                dataset: response.data,
            });            
        });

    }
}

LeaderboardController.$inject = ['$scope', '$http', 'NgTableParams', 'logger'];