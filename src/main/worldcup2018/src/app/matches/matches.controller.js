export default class MatchesController {
    constructor($scope, $rootScope, $http, NgTableParams, logger) {
        this.$http = $http;
        this.$rootScope = $rootScope;
        this.messages = [];
        this.logger = logger;
        this.NgTableParams = NgTableParams;
        var self = this;
        this.$http.get("/bets/allowedMatchDays").then(function(response) {
            self.allowedMatchDays = response.data;
            self.activate();
        });
    }

    activate() {
        var self = this;
        self.$http.get("/games/list").then(function(response) {
            const groupedByMatchDay = _.groupBy(response.data, function(o) {
                return o.game.matchDay;
            });

            if (_.last(self.allowedMatchDays) >= '3') {
                const p123 = _.concat(groupedByMatchDay["1"], groupedByMatchDay["2"], groupedByMatchDay["3"]);
                self.tableParamsP123 = new self.NgTableParams({
                    count: p123.length // hides pager
                }, {
                    dataset: p123,
                    total: 1,
                    counts: [] // hides page sizes                   
                });
            }

            if (_.last(self.allowedMatchDays) >= '4') {
                self.tableParamsP4 = new self.NgTableParams({
                    count: groupedByMatchDay["4"].length // hides pager
                }, {
                    dataset: groupedByMatchDay["4"],
                    total: 1,
                    counts: [] // hides page sizes                   
                });
            }

            if (_.last(self.allowedMatchDays) >= '5') {
                self.tableParamsP5 = new self.NgTableParams({
                    count: groupedByMatchDay["5"].length // hides pager
                }, {
                    dataset: groupedByMatchDay["5"],
                    total: 1,
                    counts: [] // hides page sizes                   
                });
            }

            if (_.last(self.allowedMatchDays) >= '6') {
                self.tableParamsP6 = new self.NgTableParams({
                    count: groupedByMatchDay["6"].length // hides pager
                }, {
                    dataset: groupedByMatchDay["6"],
                    total: 1,
                    counts: [] // hides page sizes                   
                });
            }

            if (_.last(self.allowedMatchDays) >= '8') {
                const p78 = _.concat(groupedByMatchDay["7"], groupedByMatchDay["8"]);
                self.tableParamsP78 = new self.NgTableParams({
                    count: p78.length // hides pager
                }, {
                    dataset: p78,
                    total: 1,
                    counts: [] // hides page sizes                   
                });
            }
        });

    }
}

MatchesController.$inject = ['$scope', '$rootScope', '$http', 'NgTableParams', 'logger'];