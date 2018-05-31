export default function config($stateProvider) {
    $stateProvider.state('home', {
        url: '/',
        views: {
            main: {
                controller: 'HomeController',
                template: require('./home.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Home'
        }
    });
}

config.$inject = ['$stateProvider'];