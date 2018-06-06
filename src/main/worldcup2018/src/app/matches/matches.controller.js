export default class MatchesController {
    constructor($scope, $http, NgTableParams, logger) {
        this.$http = $http;
        this.messages = [];
        this.logger = logger;
        this.NgTableParams = NgTableParams;
        this.activate();
    }

    activate() {
        var self = this;
        self.$http.get("/games/list").then(function(response) {
            self.tableParams = new self.NgTableParams({
                count: response.data.length // hides pager
            }, {
                dataset: response.data,
            });
        });
    }
}

MatchesController.$inject = ['$scope', '$http', 'NgTableParams', 'logger'];