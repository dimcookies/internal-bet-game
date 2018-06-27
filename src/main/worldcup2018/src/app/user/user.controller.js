export default class UserController {
    constructor($scope, $stateParams, $http, NgTableParams, logger) {
        this.$http = $http;
        this.logger = logger;
        this.NgTableParams = NgTableParams;
        this.userName = $stateParams.userName; //getting userName
        var self =this;
        this.$http.get("/bets/currentMatchDays").then(function(response) {
            self.allowedMatchDays = response.data;
            self.activate();
        });
    }
    fetchBets() {
        var self = this;

        this.$http.get("/bets/list?userName=" + self.userName).then(function(response) {
            self.selectedUser = self.userName;

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
                self.tableParamsP7 = new self.NgTableParams({
                    count: groupedByMatchDay["7"].length // hides pager
                }, {
                    dataset: groupedByMatchDay["7"],
                    total: 1,
                    counts: [] // hides page sizes                   
                });
            }

            if (_.last(self.allowedMatchDays) >= '8') {
                self.tableParamsP8 = new self.NgTableParams({
                    count: groupedByMatchDay["8"].length // hides pager
                }, {
                    dataset: groupedByMatchDay["8"],
                    total: 1,
                    counts: [] // hides page sizes                   
                });
            }
        
        });

    }
    activate() {
        var self = this;
        if (this.userName) {
            self.fetchBets();
        } else {
            this.$http.get("/users/currentUser").then(function(response) {
                // console.log('response///...  ', response);
                self.userName = response.data.username
                self.fetchBets();
            });
        }
    }
}

UserController.$inject = ['$scope', '$stateParams', '$http', 'NgTableParams', 'logger'];