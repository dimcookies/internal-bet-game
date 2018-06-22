export default class AnalyticsController {
    constructor($scope, $http, $timeout, logger) {
        this.$http = $http;
        this.logger = logger;
        this.activate();
        this.usersIdx = [];
        //http://jtblin.github.io/angular-chart.js/#getting_started
        this.userRankHistory = {};
        this.userRankHistory.selection = 'Rank';
        this.userDropdown = {};
        this.userDropdown.settings = {
            scrollable: true,
            scrollableHeight: '350px',
            selectionLimit: 5
        };
        var self = this;
        this.userDropdown.events = {
            onDeselectAll: function(item) {
                self.fetchUserRank(self.activeUser);
            },
            onItemDeselect: function(item) {
                if (item.id === self.activeUser) return;
                const index = _.findIndex(self.usersIdx, {
                    'id': item.id
                }) + 1;
                self.usersIdx.splice(index, 1);
                self.userRankHistory.data.splice(index, 1);
                self.userRankHistory.series.splice(index, 1);
            },
            onItemSelect: function(item) {
                if (item.id === self.activeUser) return;

                self.usersIdx.push(item);
                self.fetchOtherUsersRank(item.id);
            }
        };

    }
    userRankHistoryClick() {
        if (this.userRankHistory.selection === 'Points') {
            this.userRankHistory.selection = 'Rank';
        } else {
            this.userRankHistory.selection = 'Points';
        }
        this.fetchUserRank(this.activeUser);

    }
    fetchOtherUsersRank(username) {
        var self = this;
        self.$http.get("/analytics/rankHistory?userName=" + username).then(function(response) {
            self.userRankHistory.series.push(username);
            if (self.userRankHistory.selection === 'Points') {
                self.userRankHistory.data.push(_.map(response.data, 'points'));
                self.userRankHistory.options = {
                    scales: {
                        yAxes: [{
                            ticks: {
                                reverse: false,
                            },
                            display: true,

                        }]
                    }
                };
            } else {
                self.userRankHistory.data.push(_.map(response.data, 'rank'));
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
            }

            self.userRankHistory.datasetOverride = [];
        });
    }

    fetchUserRank(username) {
        var self = this;
        self.$http.get("/analytics/rankHistory?userName=" + username).then(function(response) {
            self.userRankHistory.series = [username];
            self.userRankHistory.labels = _.map(response.data, 'rankDate');
            if (self.userRankHistory.selection === 'Points') {
                self.userRankHistory.data = [_.map(response.data, 'points')];
                self.userRankHistory.options = {
                    scales: {
                        yAxes: [{
                            ticks: {
                                reverse: false,
                            },
                            display: true,

                        }]
                    }
                };
            } else {
                self.userRankHistory.data = [_.map(response.data, 'rank')];
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
            }

            self.userRankHistory.datasetOverride = [];
        });

    }
    activate() {

        var self = this;
        self.$http.get("/users/list").then(function(response) {
            const formated = _.map(_.toPairs(response.data), (value) => {
                return {
                    id: value[0],
                    label: value[0]
                };
            });
            self.userDropdown.model = [];
            self.userDropdown.data = formated;
        });
        self.$http.get("/analytics/lastupdate").then(function(response) {
            self.lastUpdateDate = response.data;
        });
        self.$http.get("/users/currentUser").then(function(responseUser) {
            self.activeUser = responseUser.data.username;
            self.fetchUserRank(responseUser.data.username);
        });
        self.$http.get("/analytics/riskIndex").then(function(response) {
            self.riskIndex = {};
            self.riskIndex.labels = _.reverse(_.map(response.data, 'username'));
            self.riskIndex.data = _.reverse(_.map(response.data, 'riskIndex'));
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
            self.topRanked = _.orderBy(formated, ['value'], ['desc'])
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