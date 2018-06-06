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
        var self = this;
        var data = [{
            name: 'alpha',
            points: 500,
            prize: 500
        }, {
            name: 'beta',
            points: 1500,
            prize: 500
        }, {
            name: 'gama',
            points: 800,
            prize: 500
        }, {
            name: 'gama',
            points: 800,
            prize: 500
        }, {
            name: 'gama',
            points: 800,
            prize: 500
        }, {
            name: 'gama',
            points: 800,
            prize: 500
        }, {
            name: 'gama',
            points: 800,
            prize: 500
        }, {
            name: 'gama',
            points: 800,
            prize: 500
        }, {
            name: 'gama',
            points: 800,
            prize: 500
        }, {
            name: 'gama',
            points: 800,
            prize: 500
}];

        self.tableParams = new self.NgTableParams({
            count: data.length // hides pager
        }, {
            dataset: data,
            total:1,
            counts: [] // hides page sizes
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