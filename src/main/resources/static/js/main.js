'use strict';

var usernamePage = document.querySelector('#username-page');
var roomPage = document.querySelector('#room');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var typing = document.querySelector('#typing');
var userlist =document.querySelector('#user-list')

var stompClient = null;
var username = null;
var room =null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
    room = document.querySelector('#room').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}



function keypress(event){


        var chatMessage = {
            sender: username,
            content: messageInput.value,
            room: room,
            typeOn: 'true',
            type: 'TYPE'


        };

    stompClient.send("/app/chat.type", {}, JSON.stringify(chatMessage));
    //event.preventDefault();
}






function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public/'+room, onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, room: room ,type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            room: room,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onKeyPressed(payload,username){
    var message = JSON.parse(payload.body);

    if(message.typeOn === 'true'){
        if(message.sender !== username) {
            console.log(message.type);
            message.content = message.sender + ' is typing.....';
            typing.innerHTML = message.content;
            setTimeout(() => {
                typing.innerHTML = ''
            }, 5000)

        }
    }
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    if(message.typeOn === 'true' && message.type==='TYPE'){
        onKeyPressed(payload,username);
        return;
    }

    var messageElement = document.createElement('li');
    console.log("In REsponse");

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined! ' + message.room + '  '
        //var a = message.userList;
        //for(var i in a){
            var li = document.createElement("li");
            li.setAttribute("id", message.sender);
            li.appendChild(document.createTextNode(message.sender));
            userlist.appendChild(li);
        //}
        //var li = document.createElement("li");
        //li.setAttribute('id',"sender");
        //li.appendChild(document.createTextNode(message.sender));
        //userlist.appendChild(li);
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
        var sender = document.getElementById(message.sender);
        userlist.removeChild(sender);


    }else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
        typing.innerHTML = "";
    }



    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content + "  " + message.time);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
messageInput.addEventListener("keypress", keypress, true)
