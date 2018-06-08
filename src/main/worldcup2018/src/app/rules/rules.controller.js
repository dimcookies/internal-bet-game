export default class RulesController {
	constructor($scope, $http, logger, NgTableParams) {
		this.$http = $http;
		this.logger = logger;
		this.NgTableParams = NgTableParams;
	}
}
RulesController.$inject = ['$scope', '$http', 'logger', 'NgTableParams'];