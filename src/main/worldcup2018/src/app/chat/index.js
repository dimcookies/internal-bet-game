import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './chat.config';

import ChatController from './chat.controller.js';

export default angular.module('espackApp.chat', [uirouter])
    .config(config)
    .controller('ChatController', ChatController)
    .name;