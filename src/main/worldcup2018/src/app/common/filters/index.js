import angular from 'angular';
import WhiteSpaceFilter from './whitespace.filter.js';
import SafeHTMLFilter from './safeHTML.filter.js';

export default angular.module('espackApp.filters', [])
    .filter('whiteSpace',WhiteSpaceFilter)
    .filter('safeHtml',SafeHTMLFilter)
    .name;