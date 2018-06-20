export default class AnalyticsController {
    constructor($scope, $http, $timeout, logger) {
        this.$http = $http;
        this.logger = logger;
        this.activate();
        //http://jtblin.github.io/angular-chart.js/#getting_started
    }
    activate() {
        var self = this;
        self.$http.get("/users/currentUser").then(function(responseUser) {
            self.$http.get("/analytics/rankHistory?userName=" + responseUser.data.username).then(function(response) {
                self.userRankHistory = {};
                self.userRankHistory.labels = _.map(response.data, 'rankDate');
                self.userRankHistory.data = _.map(response.data, 'rank');
                self.userRankHistory.options = {
                    scales: {
                        yAxes: [{
                            ticks: {
                                reverse: true,
                            },
                            display: true,

                        }]
                    }
                };
            });

        });
        self.$http.get("/analytics/riskIndex").then(function(response) {
            self.riskIndex = {};
            self.riskIndex.labels = _.map(response.data, 'username');
            self.riskIndex.data = _.map(response.data, 'riskIndex');
        });
        self.$http.get("/analytics/userStreak").then(function(response) {
            self.userStreak = {};
            self.userStreak.labels = _.map(response.data, 'user.username');
            self.userStreak.data = _.map(response.data, 'streak');
        });
        self.$http.get("/analytics/topRanked").then(function(response) {
            const formated = _.map(_.toPairs(response.data), (value) => {
                return {
                    name: value[0],
                    value: value[1]
                };
            });
            self.topRanked = _.orderBy(formated,['value'], ['desc'])
        });
        self.$http.get("/analytics/userStreakHistory").then(function(response) {
            self.userStreakHistory = {};
            self.userStreakHistory.labels = _.map(response.data, 'user.username');
            self.userStreakHistory.data = [_.map(response.data, 'maxStreak'), _.map(response.data, 'minStreak')];
            self.userStreakHistory.options = {
                scales: {
                    xAxes: [{
                        ticks: {
                            stepSize: 1,
                            min: 0,
                            autoSkip: false
                        }

                    }]
                }
            };

        });
        self.$http.get("/analytics/userStreakHistoryLimits").then(function(response) {
            self.userStreakHistoryLimits = response.data;
        });
    }
}

AnalyticsController.$inject = ['$scope', '$http', '$timeout', 'logger'];