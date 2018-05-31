export default class MatchesController {
    constructor($scope, logger, messageService) {
        // this.templateUrl= './list.html';
        this.template = `<h3>AngularJS + ES6 boilerplate application using Webpack</h3>`

// <message-list messages="vm.messages" header="'Messages'"></message-list>`;      
        this.messages = [];
        this.messageService = messageService;
        this.logger = logger;

        this.activate();
    }

    activate() {
        // return this.loadMessages().then(()=> {
        //     this.logger.info('init Home View');
        // });
    }


    loadMessages() {
        return this.messageService.findAll().then(response=> {
            this.messages = response;

            return this.messages;
        });
    }
}

MatchesController.$inject = ['$scope', 'logger', 'messageService'];
