export default function config($stateProvider) {
    $stateProvider.state('matches', {
        url: '/matches',
        views: {
            main: {
                controller: 'MatchesController',
                template: require('./matches.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Matches'
        }
    });
}

config.$inject = ['$stateProvider'];