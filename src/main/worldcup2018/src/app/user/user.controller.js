export default class UserController {
    constructor($scope, $stateParams, $http, NgTableParams, logger) {
        this.$http = $http;
        this.logger = logger;
        this.NgTableParams = NgTableParams;
        this.userName = $stateParams.userName; //getting userName
        this.activate();

    }
    fetchBets() {
        var self = this;

        this.$http.get("/bets/list?userName=" + self.userName).then(function(response) {
            // self.selectedBets = response.data;
            self.selectedUser = self.userName;
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