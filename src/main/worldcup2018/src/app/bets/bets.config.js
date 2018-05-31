export default function config($stateProvider) {
    $stateProvider.state('bets', {
        url: '/bets',
        views: {
            main: {
                controller: 'BetsController',
                template: require('./bets.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Matches'
        }
    });
}

config.$inject = ['$stateProvider'];