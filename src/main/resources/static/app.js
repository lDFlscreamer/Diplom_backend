var stompClient = null;
var adr='/'.concat($("#name").val())
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/diplom');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        adr='/'.concat($("#name").val())
        console.log("adr=".concat(adr));
        stompClient.subscribe(adr, function (greeting) {
            console.log('typeof greeting: ' + typeof(greeting.body));
            showGreeting(greeting.body);
        });
        stompClient.subscribe(adr.concat("/logs"), function (logs) {
            console.log('typeof logs: ' + typeof(logs.body));
            showGLogs(logs.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    adr='/'.concat($("#name").val()).concat("/input")
    stompClient.send(adr, {}, $("#textInput").val());
}

function showGreeting(message) {
    $("#outputText").append( message.replace("\n","\\n"));
}

function showGLogs(message) {
 //   $("#logs").append( message.replace("\n","<br>"));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});