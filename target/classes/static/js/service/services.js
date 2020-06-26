var app = angular.module("main")

app.service("MessageService", messageService)
app.service('UserService', userService)


function userService($http){

    function findAll (){
        return $http.get('users').then(function(response){
            return response.data;
        })
    }

    return{
        findAll : findAll
    }

}

function messageService ($http){

    function getAllMessages(){
        return $http.get('messages').then(function(response){
            return response.data;
        })
    }

    function getMessageById(id){
        return $http.get(`message/${id}`).then(function(response){
            return response.data;
        })
    }

    function saveMessage(username, message){
        return $http.post(`message/${username}`, message).then(function(response){
            return response.data;
        })
    }

    function updateMessage(id, data){
        return $http.put(`message/{id}`, data).then(function(response){
            return response.data;
        })
    }

    function deleteMessageById(id){
        return $http.delete(`message/${id}`).then(function(response){
            return response.data;
        })
    }

    return{
        getAllMessages : getAllMessages,
        getMessageById : getMessageById,
        saveMessage : saveMessage,
        updateMessage : updateMessage,
        deleteMessageById : deleteMessageById
    }


}