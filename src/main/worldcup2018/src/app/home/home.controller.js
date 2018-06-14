export default class HomeController {
    constructor($rootScope, $scope, NgTableParams, $http, $timeout, logger) {
        this.$rootScope = $rootScope;
        this.$http = $http;
        this.$timeout = $timeout;
        this.messages = [];
        this.logger = logger;
        this.NgTableParams = NgTableParams;
        this.activate();
    }
    fetchData() {
        var self = this;
        self.$http.get("/rss/list").then(function(response) {
            self.allRss = response.data;
        });
        self.$http.get("/comments/list?limit=5").then(function(response) {
            self.allComments = response.data;
        });
        self.$http.get("/bets/points").then(function(response) {
            self.tableParams = new self.NgTableParams({
                count: response.data.length // hides pager
            }, {
                dataset: response.data,
                total: 1,
                counts: [] // hides page sizes
            });
        });
    }
    activate() {
        var self = this;
        self.fetchData();
        self.mytimeout = self.$timeout(function() {
            self.activate();
        }, 60000);
        this.$rootScope.$on('$locationChangeStart', function() {
            self.$timeout.cancel(self.mytimeout);
        });
    }
}

HomeController.$inject = ['$rootScope', '$scope', 'NgTableParams', '$http', '$timeout', 'logger'];