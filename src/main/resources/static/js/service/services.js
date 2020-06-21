var app = angular.module("main")

app.service("MessageService", messageService)

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

    function saveMessage(message){
        return $http.post('message', message).then(function(response){
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