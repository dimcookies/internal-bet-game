export default class ChatController {
    constructor($rootScope, $scope, $http, $timeout, logger) {
        this.$rootScope = $rootScope;        
        this.$http = $http;
        this.$timeout = $timeout;
        this.messages = [];
        this.logger = logger;
        this.activate();
    }
    addComment() {
        var self = this;
        // self.$http.post("/comments/add?comment=" + self.new_comment).then(function(response) {
        //     self.$http.get("/comments/list?limit=1000").then(function(response) {
        //         self.allComments = response.data;
        //     });
        //     self.new_comment = "";
        // });
        self.$http.post("/comments/add2", self.new_comment).then(function(response) {
            self.$http.get("/comments/list?limit=1000").then(function(response) {
                self.allComments = response.data;
            });
            self.new_comment = "";
        });         
    };
    fetchComments() {
        var self = this;
        self.$http.get("/comments/list?limit=1000").then(function(response) {
            self.allComments = response.data;
        });
    }
    activate() {
        var self = this;
        self.fetchComments();
        self.mytimeout = self.$timeout(function() {
            self.activate();
        }, 5000);
        this.$rootScope.$on('$locationChangeStart', function() {
            self.$timeout.cancel(self.mytimeout);
        });
    }
}

ChatController.$inject = ['$rootScope', '$scope', '$http', '$timeout', 'logger'];