export default function config($stateProvider) {
    $stateProvider.state('analytics', {
        url: '/analytics',
        views: {
            main: {
                controller: 'AnalyticsController',
                template: require('./analytics.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Analytics'
        }
    });
}

config.$inject = ['$stateProvider'];