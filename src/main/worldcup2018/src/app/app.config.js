import seoInterceptor from './common/interceptors/seo-interceptor';

export default  function config($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider) {
    $locationProvider.hashPrefix('!');
    $urlRouterProvider.otherwise('/');

    $httpProvider.interceptors.push(seoInterceptor);
}

config.$inject = ['$stateProvider', '$urlRouterProvider', '$locationProvider', '$httpProvider'];