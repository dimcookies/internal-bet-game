import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './home.config';

import HomeController from './home.controller.js';

export default angular.module('espackApp.home', [uirouter])
    .config(config)
    .controller('HomeController', HomeController)
    .name;