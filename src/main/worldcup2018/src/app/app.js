import './layout/styles'

import angular from 'angular';
import uirouter from 'angular-ui-router';

import 'lodash';
import 'angular-animate';

import config from './app.config';
import LayoutController from './layout/layout.controller.js';

import commonModule from './common';
import homeModule from './home';
import leaderboardModule from './leaderboard';
import matchModule from './match';
import matchesModule from './matches';
import betsModule from './bets';
import chatModule from './chat';

import componentsModule from './components';

import filtersModule from './common/filters';
import servicesModule from './common/services';

angular.module('espackApp', [
		uirouter,
		commonModule, componentsModule, filtersModule, servicesModule, homeModule, leaderboardModule, matchModule, matchesModule, betsModule, chatModule
	])
	.config(config)
	.controller('LayoutController', LayoutController);