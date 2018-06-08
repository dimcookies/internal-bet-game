export default function config($stateProvider) {
    $stateProvider.state('settings', {
        url: '/settings',
        views: {
            main: {
                controller: 'SettingsController',
                template: require('./settings.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Matches'
        }
    });
}

config.$inject = ['$stateProvider'];