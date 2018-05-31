export default class BetsController {
	constructor($scope, $http, logger, messageService) {
		// this.templateUrl= './list.html';
		// this.template = `<h3>AngularJS + ES6 boilerplate application using Webpack</h3>`

		// <message-list messages="vm.messages" header="'Messages'"></message-list>`;
		this.$http = $http;
		this.logger = logger;

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
					console.log('savedBets  > ',savedBets);

					self.pointsVisible = false;
					self.commentsVisible = false;
					self.matchesVisible = false;
					self.matchVisible = false;
					self.userVisible = false;
					self.editVisible = true;
					self.userBets = {};
					self.userOverBets = {};
					self.editError = false;
					self.editSuccess = false;
					self.disableSubmit = false;


					for (var idx in savedBets) {
						savedBet = savedBets[idx];
						self.userBets[savedBet.gameId] = savedBet.scoreResult;
						self.userOverBets[savedBet.gameId] = savedBet.overResult;
					}
				});


			});
		});

	}


	loadMessages() {
		return this.messageService.findAll().then(response => {
			this.messages = response;

			return this.messages;
		});
	}
}

BetsController.$inject = ['$scope', '$http', 'logger', 'messageService'];