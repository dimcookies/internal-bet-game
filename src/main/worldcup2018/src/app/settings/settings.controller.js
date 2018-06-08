export default class SettingsController {
	constructor($scope, $http, logger, NgTableParams) {
		this.$http = $http;
		this.logger = logger;
		this.NgTableParams = NgTableParams;
		// this.activate();
		this.optOut= false;
	}
	resetPass() {
		var self = this;
		console.log('self.password  ', self.password);
		console.log('self.passwordConfirm  ', self.passwordConfirm);
		console.log('self.optOut  ', self.optOut);
		if (self.password && self.passwordConfirm && self.password != self.passwordConfirm) {
			alert("Passwords must match!")
		}
		if (self.password && self.passwordConfirm && self.password === self.passwordConfirm) {
			self.$http.post("/users/modify?password=" + self.password + "&optOut=" + self.optOut).then(function(response) {
				alert("Password altered succesfully!");
			});
		}

	};
}
SettingsController.$inject = ['$scope', '$http', 'logger', 'NgTableParams'];