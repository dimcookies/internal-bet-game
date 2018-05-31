import angular from 'angular';
import uirouter from 'angular-ui-router';

import config from './leaderboard.config';

import LeaderboardController from './leaderboard.controller.js';

export default angular.module('espackApp.leaderboard', [uirouter])
    .config(config)
    .controller('LeaderboardController', LeaderboardController)
    .name;