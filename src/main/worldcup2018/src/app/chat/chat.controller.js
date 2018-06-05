export default class ChatController {
    constructor($scope, $http, $timeout, logger) {
        this.$http = $http;
        this.$timeout = $timeout;
        this.messages = [];
        this.logger = logger;
        this.activate();
    }
    addComment() {
        var self = this;
        self.$http.post("/comments/add?comment=" + self.new_comment).then(function(response) {
            self.$http.get("/comments/list?limit=1000").then(function(response) {
                self.allComments = response.data;
            });
            self.new_comment = "";
        });
    };
    activate() {
        // @TODO check polling
        var self = this;
        (function tick2() {
            self.$http.get("/comments/list?limit=1000").then(function(response) {
                self.allComments = response.data;
                self.$timeout(tick2, 60000);
            });
        })();
    }
}

ChatController.$inject = ['$scope', '$http', '$timeout', 'logger'];