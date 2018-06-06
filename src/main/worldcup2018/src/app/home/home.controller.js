export default class HomeController {
    constructor($rootScope, $scope, $http, $timeout, logger) {
        this.$rootScope = $rootScope;
        this.$http = $http;
        this.$timeout = $timeout;
        this.messages = [];
        this.logger = logger;
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
    }
    activate() {
        var self = this;
        self.fetchData();
        self.mytimeout = self.$timeout(function() {
            self.activate();
        }, 5000);
        this.$rootScope.$on('$locationChangeStart', function() {
            self.$timeout.cancel(self.mytimeout);
        });
    }
}

HomeController.$inject = ['$rootScope', '$scope', '$http', '$timeout', 'logger'];