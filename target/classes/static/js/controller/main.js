(function(){
var app = angular.module("main", ['ui.router'])
    app.controller('MessageCtrl', function(dataMessages, $scope, $state, MessageService){
        $scope.messages = dataMessages
        $scope.getMessageById = function(id){
             MessageService.getMessageById(id).then(function(data){
                console.log(data)
             })
        }

        $scope.deleteMessageById = function(id, index){
            swal({
                title : 'Are you sure',
                text: "This message will be delete permanently",
                type : "warning",
                showCancelButton : true,
                confirmButtonClass : "btn-danger",
                confirmButtonText : "Yes" ,
                closeConfirm: false,
            },
                function(isConfirm){
                    if(isConfirm){
                        MessageService.deleteMessageById(id).then(function(data){
                            $scope.messages.splice(index, 1);
                        })
                    }
                }
            )
        }
    })

    app.controller('NewMessageCtrl', function ($scope, $state, data, MessageService){
        console.log(data);
        $scope.users = data;

         $scope.addMessage = function(){
         console.log($scope.selected)
                    console.log($scope.message);
                    MessageService.saveMessage($scope.selected.username,$scope.message).then(function(data){
                        console.log(data);
                        $state.go('messageState')
                        $scope.message = undefined;
            })
         }

    })
})()
