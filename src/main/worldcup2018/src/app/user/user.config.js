export default function config($stateProvider) {
    $stateProvider.state('user', {
        url: '/user/:userName',
        views: {
            main: {
                controller: 'UserController',
                template: require('./user.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Home'
        }
    });
}

config.$inject = ['$stateProvider'];