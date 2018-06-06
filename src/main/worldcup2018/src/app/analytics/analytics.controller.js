export default class AnalyticsController {
    constructor($scope, $http, $timeout, logger) {
        this.$http = $http;
        this.logger = logger;
        this.activate();
        //http://jtblin.github.io/angular-chart.js/#getting_started
    }

    activate() {
        var self = this;
        self.$http.get("/games/list").then(function(response) {
            // self.allMatches = response.data;
            self.labels = ["January", "February", "March", "April", "May", "June", "July"];
            self.series = ['Series A', 'Series B'];
            self.data = [
                [65, 59, 80, 81, 56, 55, 40],
                [28, 48, 40, 19, 86, 27, 90]
            ];
        });
    }
}

AnalyticsController.$inject = ['$scope', '$http', '$timeout', 'logger'];