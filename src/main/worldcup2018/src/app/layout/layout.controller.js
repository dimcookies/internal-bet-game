import angular from 'angular';

export default class LayoutController {
    constructor($scope, $http, $timeout, $window, $state) {
        this.$state = $state;
        this.$http = $http;
        this.$window = $window;
        this.$timeout = $timeout;
        var self = this;
        self.pollUser();
        $scope.$on('$stateChangeSuccess', (event, toState, toParams, fromState, fromParams) => {
            self.$timeout.cancel(self.mytimeoutPoll);
            self.pollUser();
        });
    }
    pollUser() {
        var self = this;

        self.mytimeoutPoll = self.$timeout(function() {
            self.$http.get("/users/currentUser").then(function(response) {
                if (self.mytimeoutPoll) {
                    self.$timeout.cancel(self.mytimeoutPoll);
                    self.pollUser();
                    if (response.data.username) {
                        // console.log('user    ', response.data.username);
                    } else {
                        self.$window.location.reload();
                    }
                }
            }).catch(function(data) {
                self.$window.location.reload();
            });

        }, 90000);
    }
    stateReload(){
     // this.$state.go(this.$state.current, {}, {reload: true});
     this.$window.scrollTo(0, 0);
    }
}

LayoutController.$inject = ['$scope', '$http', '$timeout', '$window', '$state'];