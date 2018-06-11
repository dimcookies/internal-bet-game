export default class SettingsController {
	constructor($scope, $http, logger, NgTableParams) {
		this.$http = $http;
		this.logger = logger;
		this.NgTableParams = NgTableParams;
		var self = this;
		self.optOut = false;
		self.$http.get("/users/currentUser").then(function(response) {
			self.optOut = response.data.optOut;
		});
	}
	resetPass() {
		var self = this;
		if (self.password && self.passwordConfirm && self.password != self.passwordConfirm) {
			alert("Passwords must match!")
		}
		if (self.password && self.passwordConfirm && self.password === self.passwordConfirm) {
			self.$http.post("/users/modify2", {
				password: self.password
			}).then(function(response) {
				alert("Password altered succesfully!");
			});
			// self.$http.post("/users/modify?password=" + self.password).then(function(response) {
			// 	alert("Password altered succesfully!");
			// });
		}
	};
	setOptOut() {
		var self = this;
			self.$http.post("/users/modify2", {
				optOut: self.optOut
			}).then(function(response) {
				alert("Opt out option saved succesfully!");
			});
		// self.$http.post("/users/modify?optOut=" + self.optOut).then(function(response) {
		// 	alert("Opt out option saved succesfully!");
		// });
	};
}
SettingsController.$inject = ['$scope', '$http', 'logger', 'NgTableParams'];