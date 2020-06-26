var app = angular.module("main");

app.config(function ($stateProvider, $urlRouterProvider){

    var messageState = {
        name : 'messageState',
        url : '/message',
        templateUrl : 'home',
        controller : 'MessageCtrl',
        resolve:{
            dataMessages : function(MessageService){
                //return service
                return MessageService.getAllMessages();
            }
        }
    }

    var newMessage = {
        name : 'newMessage',
        url: '/new',
        templateUrl : 'new',
        controller : 'NewMessageCtrl',
        resolve:{
            data : function (UserService){
                return UserService.findAll();
            }
        }
    }

    $urlRouterProvider.otherwise('/message')
    $stateProvider.state(messageState)
    $stateProvider.state(newMessage)

})