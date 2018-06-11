import angular from 'angular';

export default class LayoutController {
    // constructor() {
    constructor($scope, $http, $timeout, $window) {
        this.$http = $http;
        this.$window = $window;
        this.$timeout = $timeout;
        this.pollUser();
        var self = this;
        self.pollUser();

        $scope.$on('$stateChangeSuccess', (event, toState, toParams, fromState, fromParams) => {
            self.pollUser();
        });
    }
    pollUser() {
        var self = this;
        if(self.mytimeoutPoll){
            self.$timeout.cancel(self.mytimeoutPoll);            
        }

        self.mytimeoutPoll = self.$timeout(function() {
            self.$http.get("/users/currentUser").then(function(response) {
                // console.log('response    ', response);
            }).catch(function(data) {
                self.$window.location.reload();
                // Handle error here
            });

        }, 90000);
    }
}

LayoutController.$inject = ['$scope', '$http', '$timeout', '$window'];