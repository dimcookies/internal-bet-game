import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './rules.config';

import RulesController from './rules.controller.js';

export default angular.module('espackApp.rules', [uirouter])
    .config(config)
    .controller('RulesController', RulesController)
    .name;