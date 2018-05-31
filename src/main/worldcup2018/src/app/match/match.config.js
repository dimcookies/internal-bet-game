export default function config($stateProvider) {
    $stateProvider.state('match', {
        url: '/match/:gameId',
        views: {
            main: {
                controller: 'MatchController',
                template: require('./match.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Match'
        }
    });
}

config.$inject = ['$stateProvider'];