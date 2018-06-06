export default class HomeController {
    constructor($rootScope, $scope, NgTableParams, $http, $timeout, logger) {
        // console.log('angulardataTables  ', angulardataTables);
        this.$rootScope = $rootScope;
        this.$http = $http;
        this.$timeout = $timeout;
        this.messages = [];
        this.logger = logger;
        this.NgTableParams = NgTableParams;

        // this.options = {
        //   scrollbarV: false
        // };

        // this.allLeaders  = [
        //   { name: 'alpha', points: 500, prize: 500},
        //   { name: 'beta', points: 1500, prize: 500},
        //   { name: 'gama', points: 800, prize: 500}
        // ];
        // self.allLeaders = [
        //     [
        //         "Tiger Nixon",
        //         "System Architect",
        //         "Edinburgh",
        //         "5421",
        //         "2011\/04\/25",
        //         "$320,800"
        //     ],
        //     [
        //         "Tiger Nixon",
        //         "System Architect",
        //         "Edinburgh",
        //         "5421",
        //         "2011\/04\/25",
        //         "$320,800"
        //     ]
        // ]
        this.activate();
    }
    fetchData() {
        var self = this;
        self.$http.get("/rss/list").then(function(response) {
            self.allRss = response.data;
        });
        self.$http.get("/comments/list?limit=5").then(function(response) {
            self.allComments = response.data;
            //             $('#example').DataTable( {
            //     data: response.data
            // } );
            // self.allLeaders = [{
            //     name: 'alpha',
            //     points: 500,
            //     prize: 500
            // }, {
            //     name: 'beta',
            //     points: 1500,
            //     prize: 500
            // }, {
            //     name: 'gama',
            //     points: 800,
            //     prize: 500
            // }];
            // $('#example').DataTable({
            //     "paging": false,
            //     "ordering": false,
            //     "info": false
            // })
        });
        // var vm = this;
        //  vm.dtOptions = DTOptionsBuilder.fromFnPromise(function() {
        //      var defer = $q.defer();
        //      $http.get("/comments/list?limit=5").then(function(result) {
        //          defer.resolve(result.data);
        //      });
        //      return defer.promise;
        //  }).withPaginationType('full_numbers');

        //  vm.dtColumns = [
        //      DTColumnBuilder.newColumn('id').withTitle('ID'),
        //      DTColumnBuilder.newColumn('firstName').withTitle('First name'),
        //      DTColumnBuilder.newColumn('lastName').withTitle('Last name').notVisible()
        //  ];       
        var self = this;
        var data = [
            { name: "Moroni", age: 50 },
            { name: "Tiancum", age: 43 },
            { name: "Jacob", age: 27 },
            { name: "Nephi", age: 29 },
            { name: "Enos", age: 34 },
            { name: "Tiancum", age: 43 },
            { name: "Jacob", age: 27 },
            { name: "Nephi", age: 29 },
            { name: "Enos", age: 34 },
            { name: "Tiancum", age: 43 }];
        self.tableParams = new self.NgTableParams({
            sorting: { name: "asc" } 
        }, {
            dataset: data
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