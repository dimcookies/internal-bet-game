export default class UserController {
    constructor($scope, $stateParams, $http, logger) {
        this.$http = $http;
        this.logger = logger;
        this.userName = $stateParams.userName; //getting userName
        this.activate();
    }

    activate() {
        var self = this;
        this.$http.get("/bets/list?userName=" + self.userName).then(function(response) {
            self.selectedBets = response.data;
            self.selectedUser = self.userName;
        });
    }
}

UserController.$inject = ['$scope', '$stateParams', '$http', 'logger'];