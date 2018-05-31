export default function config($stateProvider) {
    $stateProvider.state('leaderboard', {
        url: '/leaderboard',
        views: {
            main: {
                controller: 'LeaderboardController',
                template: require('./leaderboard.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Home'
        }
    });
}

config.$inject = ['$stateProvider'];