let stompClient = null;

const wrapper = document.querySelector('.chat-wrapper');
const ACTUAL_ID = +wrapper.getAttribute('data-actual-id');
const destino = wrapper.getAttribute('data-destino');
const chatId = +wrapper.getAttribute('data-chat-id');

function conectar() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        stompClient.subscribe('/user/queue/messages', function (mensajeRecibido) {
            const msg = JSON.parse(mensajeRecibido.body);

            if (msg.chat.chatId !== chatId) {
                return; // No mostrarlo, es de otro chat
            }

            mostrarMensaje(msg);
        });

        stompClient.subscribe('/user/queue/leidos', function (message) {
            const msgId = message.body;
            const msgDiv = document.querySelector(`.mensaje[data-id='${msgId}']`);
            if (msgDiv) {
                const span = msgDiv.querySelector('.leido-indicador');
                if (span) {
                    span.textContent = '✔✔';
                    span.classList.add('leido');
                }
            }
        });

        // Marcar los mensajes como leídos al conectar (por si han llegado antes)
        stompClient.send('/app/chat.leido.' + chatId, {}, {});
    });
}

function mostrarMensaje(msg) {
    const contenedor = document.getElementById("mensajes");

    const div = document.createElement("div");
    div.className = 'mensaje ' + (msg.remitente.usuarioId === ACTUAL_ID ? 'yo' : 'el');
    div.setAttribute("data-id", msg.mensajeId);

    const textoDiv = document.createElement("div");
    textoDiv.className = "texto";
    textoDiv.textContent = msg.texto;

    const fechaDiv = document.createElement("div");
    fechaDiv.className = "fecha";
    fechaDiv.textContent = new Date(msg.fechaEnvio).toLocaleString();

    // Si el mensaje es mío, añado el tick
    if (msg.remitente.usuarioId === ACTUAL_ID) {
        const tickSpan = document.createElement("span");
        tickSpan.className = "leido-indicador";
        tickSpan.textContent = msg.leido ? '✔✔' : '✔';
        if (msg.leido) tickSpan.classList.add("leido");
        fechaDiv.appendChild(tickSpan);
    }

    div.appendChild(textoDiv);
    div.appendChild(fechaDiv);
    contenedor.appendChild(div);
    contenedor.scrollTop = contenedor.scrollHeight;

    // Si el mensaje es de otro, márcalo como leído
    if (msg.remitente.usuarioId !== ACTUAL_ID) {
        stompClient.send('/app/chat.leido.' + chatId, {}, {});
    }
}

function enviarMensaje(texto) {
    if (!stompClient || !texto.trim()) return;

    const mensaje = {
        texto: texto,
        chat: { chatId: chatId }
    };

    stompClient.send('/app/chat.send.' + destino, {}, JSON.stringify(mensaje));
}

document.getElementById('chatForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const input = document.getElementById('mensajeInput');
    enviarMensaje(input.value);
    input.value = '';
});

window.addEventListener('load', function () {
    conectar();

    const contenedor = document.getElementById("mensajes");
    contenedor.scrollTop = contenedor.scrollHeight;
});
