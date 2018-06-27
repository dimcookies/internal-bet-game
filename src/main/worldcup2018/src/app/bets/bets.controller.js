export default class BetsController {
	constructor($scope, $rootScope, $http, $window, logger, NgTableParams) {
		this.$http = $http;
		this.logger = logger;
		this.$window = $window;
		this.NgTableParams = NgTableParams;
		this.activate();
	}
	activate() {
		var self = this;
		self.$http.get("/bets/betDeadline").then(function(response) {
			self.betDeadline = response.data;
			// console.log('betDeadline    >   ', response.data );
			self.$http.get("/bets/allowedMatchDays").then(function(response) {
				// console.log('allowedMatchDays    >   ', response.data );
				self.allowedMatchDays = response.data;
				self.isPlayoffStage = _.includes(self.allowedMatchDays, '4');
				self.$http.get("/games/list?matchDays=" + self.allowedMatchDays).then(function(response) {
					self.selectedGames = response.data;
					self.$http.get("/bets/encrypted/list").then(function(response) {
						self.savedBets = response.data;
						self.userBets = {};
						self.userOverBets = {};
						self.editError = false;
						self.editSuccess = false;
						self.disableSubmit = false;
						for (var idx in self.savedBets) {
							const savedBet = self.savedBets[idx];
							self.userBets[savedBet.gameId] = savedBet.scoreResult;
							self.userOverBets[savedBet.gameId] = savedBet.overResult;
						}
						self.tableParams = new self.NgTableParams({
							count: self.selectedGames.length // hides pager
						}, {
							dataset: self.selectedGames,
							total: 1,
							counts: [] // hides page sizes						
						});
					});
				});
			});
		});
	}
	saveBets() {
		var self = this;
		self.disableSubmit = true;
		self.enableSubmit = true;
		self.editError = false;
		self.editSuccess = false;

		const ar = [];
		for (var key in self.userBets) {
			var value = self.userBets[key];
			const dct = {
				"gameId": key,
				"overResult": self.userOverBets[key],
				"scoreResult": self.userBets[key]
			}
			ar.push(dct);
		}

		var parameter = JSON.stringify(ar);
			self.$http.post("/bets/encrypted/add", parameter).then(function(response) {
				self.enableSubmit = false;
				self.editSuccess = true;
				self.disableSubmit = true;
				if (response.data.includes("DOCTYPE html") && typeof response.data === 'string') {
					self.$window.location.reload();
				}

			}).catch(function(data) {
				alert("Opps! Something went wrong");
			});
	};
}
BetsController.$inject = ['$scope', '$rootScope', '$http', '$window', 'logger', 'NgTableParams'];