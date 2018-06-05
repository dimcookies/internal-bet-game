import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './analytics.config';

import AnalyticsController from './analytics.controller.js';

export default angular.module('espackApp.analytics', [uirouter])
    .config(config)
    .controller('AnalyticsController', AnalyticsController)
    .name;