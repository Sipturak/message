(function(){
var app = angular.module("main", ['ui.router'])
    app.controller('MessageCtrl', function(dataMessages, $scope, $state, MessageService){
        $scope.messages = dataMessages

        $scope.getMessageById = function(id){
             MessageService.getMessageById(id).then(function(data){
                console.log(data)
             })
        }

        $scope.addMessage = function(){
            console.log($scope.message);
            MessageService.saveMessage($scope.message).then(function(data){
                console.log(data);
                $state.go('messageState')
                $scope.message = undefined;
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
                function(){
                    MessageService.deleteMessageById(id).then(function(data){
                        swal("Success" , "You have delete message" , "success")
                        $scope.messages.splice(index, 1);

                    })

                }
            )
        }
    })
})()
