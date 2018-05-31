import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './bets.config';

import BetsController from './bets.controller.js';

export default angular.module('espackApp.bets', [uirouter])
    .config(config)
    .controller('BetsController', BetsController)
    .name;