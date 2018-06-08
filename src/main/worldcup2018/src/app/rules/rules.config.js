export default function config($stateProvider) {
    $stateProvider.state('rules', {
        url: '/rules',
        views: {
            main: {
                controller: 'RulesController',
                template: require('./rules.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Matches'
        }
    });
}

config.$inject = ['$stateProvider'];