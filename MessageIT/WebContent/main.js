// id сессии
var sessionId = '';
 
// имя клиента
var name = '';


// соккет url и port
//var socket_url = 'message-messageit.rhcloud.com';
var socket_url = '192.168.7.106';
var port = '8080';
 
$(document).ready(function() {
 
    $("#form_submit").submit(function(e) {
        e.preventDefault();
        join();
    });
    
    $("#form_send_message").submit(function(e) {
        e.preventDefault();
    });
});
 
var webSocket;
 
/**
 * Соединение с соккетом
 */
function join() {
    // проверка имени
    if ($('#input_name').val().trim().length <= 0) {
        alert('Введите имя!');
    } //else if ($('#input_name').val().trim().toUpperCase() == "ADMIN")
   // {
    	 /*var pass = prompt("Введите пароль админа", '');
    	 if (pass == adminPass){
    		    name = $('#input_name').val().trim().toUpperCase();
    		    $('#prompt_name_container').fadeOut(1000, function() {
    		    // открытие соединения на соккетах
    		    openSocket();
    		    });
    	 } else {
    		 alert ("Неверный пароль");
    	 }*/
//    } 
else {
	    name = $('#input_name').val().trim();
	    $('#prompt_name_container').fadeOut(1000, function() {
	    // открытие соединения на соккетах
	    openSocket();
	    });
    }
 
    return false;
}
 
/**
 * Открытие соединения с соккетом
 */
function openSocket() {
    // проверка того что только одно соединение открыто
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
        return;
    }
 
    // создание нового экземпляра соккета 
    webSocket = new WebSocket("ws://" + socket_url + ":" + port
            + "/MessageIT/chat?name=" + name);
 
    
    
    /**
     * связание функции с соккетом
     */
    webSocket.onopen = function(event) {
        $('#message_container').fadeIn();
 
        if (event.data === undefined)
            return;
 
    };
 
    webSocket.onmessage = function(event) {
 
        // парсим json
        parseMessage(event.data);
    };
 
    webSocket.onclose = function(event) {

    };
}
 
/**
 * Отправка сообщения из чата на сервер
 */
function send() {
    var message = $('#input_message').val();
    
    if (message.trim().length > 0) {
        sendMessageToServer('message', message);
    } else {
        alert('Введите сообщение!');
    }
 
}
 
/**
 * Закрытие соединения
 */
function closeSocket() {
    webSocket.close();
 
    $('#message_container').fadeOut(600, function() {
        $('#prompt_name_container').fadeIn();
        // очиста имени и id сессии
        sessionId = '';
        name = '';
 
        // очистка сообщений
        $('#messages').html('');
        $('p.online_count').hide();
    });
}
 
/**
 * Парсинг сообщения JSON. Тип сообщения обозначается как 'flag'
 * значиение флага может быть self, new, message, exit
 */
function parseMessage(message) {
    var jObj = $.parseJSON(message);
 
    // если флаг это 'self' сообщение содержит id сессии
    if (jObj.flag == 'self') {
 
        sessionId = jObj.sessionId;
 
    } else if (jObj.flag == 'new') {
        // если флаг это 'new', клиент присоединился к чату
        var new_name = 'Вы';
 
        // количество человек онлайн
        var online_count = jObj.onlineCount;
 
        $('p.online_count').html(
                'Здравствуйте, <span class="green">' + name + '</span>. <b>'
                        + online_count + '</b> человек сейчас онлайн')
                .fadeIn();
 
        if (jObj.sessionId != sessionId) {
            new_name = jObj.name;
        }
 
        var li = '<li class="new"><span class="name">' + new_name + '</span> '
                + jObj.message + '</li>';
        $('#messages').append(li);
 
        $('#input_message').val('');
 
    } else if (jObj.flag == 'message') {
        // если флаг это 'message', это значит что кто-то отправил сообщение
 
        var from_name = 'Вы'; //You
 
        if (jObj.sessionId != sessionId) {
            from_name = jObj.name;
        }
       
        var li = '<li><table><tr><td width="100%"><span class="name">' + from_name + '</span> ' + jObj.message + 
        '</td><td><span class="time">' + jObj.time + '</span></td></tr></table></li>';
 
        // добавление сообщения в список
        appendChatMessage(li);
 
        $('#input_message').val('');
 
    } else if (jObj.flag == 'exit') {
        // если флаг это 'exit', это значит что кто-то покинул чат
        var li = '<li class="exit"><span class="name red">' + jObj.name
                + '</span> ' + jObj.message + '</li>';
 
        var online_count = jObj.onlineCount;
 
        $('p.online_count').html(
                'Здравствуй, <span class="green">' + name + '</span>. <b>'
                        + online_count + '</b> человек сейчас онлайн');
 
        appendChatMessage(li);
    }
}
 
/**
 * Добавление сообщения в список
 */
function appendChatMessage(li) {
    $('#messages').append(li);
 
    // пролистывания чата вниз
    $('#messages').scrollTop($('#messages').height());
}
 
/**
 * Отправка сообщения в формате JSON на сервер
 */
function sendMessageToServer(flag, message) {
    var json = '{""}';
 
    // подготовка json объекта
    var myObject = new Object();
    myObject.sessionId = sessionId;
    myObject.message = message;
    myObject.flag = flag;
 
    // конвертирование json объекта в json строку
    json = JSON.stringify(myObject);
 
    // отправка сообщения на сервер
    webSocket.send(json);
}