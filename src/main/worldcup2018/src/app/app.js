import './layout/styles'

// import 'datatables';

import angular from 'angular';
import uirouter from 'angular-ui-router';

import 'lodash';
import 'angular-animate';

import config from './app.config';
import LayoutController from './layout/layout.controller.js';

import commonModule from './common';
import homeModule from './home';
import userModule from './user';
import leaderboardModule from './leaderboard';
import matchModule from './match';
import matchesModule from './matches';
import betsModule from './bets';
import chatModule from './chat';
import analyticsModule from './analytics';
import rulesModule from './rules';
import settingsModule from './settings';

import componentsModule from './components';

import filtersModule from './common/filters';
import servicesModule from './common/services';
import angularChart from 'angular-chart.js';
import 'angularjs-dropdown-multiselect/dist/angularjs-dropdown-multiselect.min.js'
import 'angularjs-dropdown-multiselect/dist/index.css'

import { ngTableModule } from 'ng-table/bundles/ng-table';

angular.module('espackApp', [
		angularChart,
		uirouter,
		commonModule,
		componentsModule,
		filtersModule,
		servicesModule,
		homeModule,
		leaderboardModule,
		matchModule,
		matchesModule,
		analyticsModule,
		userModule,
		betsModule,
		chatModule,
		rulesModule,
		settingsModule,
		ngTableModule.name,
		'angularjs-dropdown-multiselect'
	])
	.config(config)
	.controller('LayoutController', LayoutController);