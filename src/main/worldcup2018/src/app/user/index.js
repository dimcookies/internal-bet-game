import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './user.config';

import UserController from './user.controller.js';

export default angular.module('espackApp.user', [uirouter])
    .config(config)
    .controller('UserController', UserController)
    .name;