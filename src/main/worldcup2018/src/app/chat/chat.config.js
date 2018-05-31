export default function config($stateProvider) {
    $stateProvider.state('chat', {
        url: '/chat',
        views: {
            main: {
                controller: 'ChatController',
                template: require('./chat.html'),
                controllerAs: 'vm'
            }
        },
        data: {
            pageTitle: 'Matches'
        }
    });
}

config.$inject = ['$stateProvider'];