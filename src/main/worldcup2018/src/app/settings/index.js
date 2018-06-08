import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './settings.config';

import SettingsController from './settings.controller.js';

export default angular.module('espackApp.settings', [uirouter])
    .config(config)
    .controller('SettingsController', SettingsController)
    .name;