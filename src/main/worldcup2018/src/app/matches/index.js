import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './matches.config';

import MatchesController from './matches.controller.js';

export default angular.module('espackApp.matches', [uirouter])
    .config(config)
    .controller('MatchesController', MatchesController)
    .name;