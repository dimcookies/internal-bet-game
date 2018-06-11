export default class BetsController {
	constructor($scope, $http, logger, NgTableParams) {
		this.$http = $http;
		this.logger = logger;
		this.NgTableParams = NgTableParams;
		this.activate();
	}
	activate() {
		var self = this;
		self.$http.get("/bets/allowedMatchDays").then(function(response) {
			self.allowedMatchDays = response.data;
			self.$http.get("/games/list?matchDays=" + self.allowedMatchDays).then(function(response) {
				self.selectedGames = response.data;
				self.$http.get("/bets/encrypted/list").then(function(response) {
					self.savedBets = response.data;
					// console.log('savedBets  > ', self.savedBets);
					// self.pointsVisible = false;
					// self.commentsVisible = false;
					// self.matchesVisible = false;
					// self.matchVisible = false;
					// self.userVisible = false;
					// self.editVisible = true;
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
	}
	saveBets() {
		var self = this;
		self.disableSubmit = true;
		self.enableSubmit = true;
		const isPlayoffStage = self.allowedMatchDays.split(',') < 3;
		console.log(isPlayoffStage);
		self.editError = false;
		self.editSuccess = false;
		// for (var i = 0; i < self.selectedGames.length; i++) {
		// 	const game = self.selectedGames[i];
		// 	if (self.userBets[game.game.id] == null) {
		// 		self.editError = true;
		// 		self.enableSubmit = false;
		// 		return;
		// 	}
		// 	if (isPlayoffStage && self.userOverBets[game.game.id] == null) {
		// 		self.editError = true;
		// 		self.enableSubmit = false;				
		// 		return;
		// 	}
		// }
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
		});
	};
}
BetsController.$inject = ['$scope', '$http', 'logger', 'NgTableParams'];