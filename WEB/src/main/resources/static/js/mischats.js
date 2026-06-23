let stompClientMisChats = null;
let reconnectAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 5;
const audioMisChats = new Audio('/sounds/notificacion.mp3');

function conectarMischats() {
    const socket = new SockJS('/ws');
    stompClientMisChats = Stomp.over(socket);

    // Opcional: silenciar logs
    stompClientMisChats.debug = () => {};

    stompClientMisChats.connect({}, function () {
        reconnectAttempts = 0;
        console.log("✅ Conexión WebSocket establecida para /mischats");

        // ✅ Suscripción para actualizar doble tick
        stompClientMisChats.subscribe('/user/queue/leidos-mischats', function (message) {
            const msgId = message.body;
            console.log("📩 Notificación de mensaje leído:", msgId);
            actualizarTickEnLista(msgId);
        });

        // ✅ Suscripción para actualización de mensajes nuevos en /mischats
        stompClientMisChats.subscribe('/user/queue/contador-mischats', function(message) {
            const data = JSON.parse(message.body);
            const chatId = data.chatId;
            const noLeidos = data.noLeidos;

            const chatItem = document.querySelector('.chat-item[data-chat-id="${chatId}"]');
            if (chatItem) {
                const header = chatItem.querySelector('.chat-header');
                let badge = chatItem.querySelector('.unread-badge');

                if (noLeidos > 0) {
                    if (!badge) {
                        badge = document.createElement('span');
                        badge.classList.add('unread-badge');
                        header.appendChild(badge); // Coloca debajo de hora
                    }
                    badge.textContent = noLeidos;
                    badge.style.display = 'inline-block';
                    audio.play().catch(e => {
                        console.warn("No se pudo reproducir el sonido (probablemente por falta de interacción):", e);
                    });
                } else {
                    if (badge) {
                        badge.remove(); // Elimina si llega a 0
                    }
                }

                // Actualizar texto del último mensaje
                const mensajeTexto = chatItem.querySelector('.chat-last-message span:last-child');
                if (mensajeTexto && data.texto) {
                    mensajeTexto.textContent = data.texto;
                }

                // Mover arriba
                const lista = document.getElementById('chat-list');
                lista.prepend(chatItem);
            }
        });
    }, function (error) {
        console.error("❌ Error en conexión WebSocket:", error);

        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;
            console.log("!🔄 Intentando reconexión (${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS})...");
            setTimeout(conectarMischats, 5000);
        } else {
            console.error("⚠️ Máximo de intentos de reconexión alcanzado");
        }
    });
}

function actualizarTickEnLista(msgId) {
    const chatItem = document.querySelector('.chat-item[data-ultimo-mensaje-id="${msgId}"]');

    if (chatItem) {
        const tickSpan = chatItem.querySelector('.message-prefix');
        if (tickSpan) {
            tickSpan.textContent = '✔✔';
            tickSpan.classList.add('leido');
        }
    }
}

// Iniciar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function () {
    conectarMischats();

    // Debug: Mostrar IDs
    document.querySelectorAll('.chat-item').forEach(item => {
        console.log(`Chat ID: ${item.getAttribute('data-chat-id')}, ` +
            "Último mensaje ID: ${item.getAttribute('data-ultimo-mensaje-id')}");
    });
});