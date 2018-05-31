import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './match.config';

import MatchController from './match.controller.js';

export default angular.module('espackApp.match', [uirouter])
    .config(config)
    .controller('MatchController', MatchController)
    .name;