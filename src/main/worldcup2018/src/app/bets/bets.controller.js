export default class BetsController {
	constructor($scope, $http, logger, messageService) {
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
					console.log('savedBets  > ',self.savedBets);

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


					for (var idx in self.savedBets) {
						savedBet = self.savedBets[idx];
						self.userBets[savedBet.gameId] = savedBet.scoreResult;
						self.userOverBets[savedBet.gameId] = savedBet.overResult;
					}
				});


			});
		});
	}
            saveBets() {
                this.disableSubmit = true;


                const isPlayoffStage = this.allowedMatchDays.split(',') < 3;
                console.log(isPlayoffStage);
                this.editError = false;
                this.editSuccess = false;
                for (var i = 0; i < this.selectedGames.length; i++) {
                    const game = this.selectedGames[i];
                    if(this.userBets[game.game.id] == null) {
                      this.editError = true;
                      return;
                    }

                    if(isPlayoffStage && this.userOverBets[game.game.id] == null) {
                      this.editError = true;
                      return;
                    }
                }

                const ar = [];
                for(var key in this.userBets) {
                  var value = this.userBets[key];
                  const dct = { "gameId": key, "overResult": this.userOverBets[key], "scoreResult": this.userBets[key] }
                  ar.push(dct);
                }

                var parameter = JSON.stringify(ar);
                $http.post("/bets/encrypted/add", parameter).then(function (response) {
                    this.editSuccess = true;
                    this.disableSubmit = true;
                });
            };
}

BetsController.$inject = ['$scope', '$http', 'logger', 'messageService'];