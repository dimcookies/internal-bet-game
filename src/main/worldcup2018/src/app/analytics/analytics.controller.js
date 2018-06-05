export default class AnalyticsController {
    constructor($scope, $http, logger) {

        this.$http = $http;
        this.messages = [];
        this.logger = logger;

        this.activate();
    }

    activate() {
        var self = this;
        self.$http.get("/games/list").then(function(response) {
            self.allMatches = response.data;
        });
    }
}

AnalyticsController.$inject = ['$scope', '$http', 'logger'];