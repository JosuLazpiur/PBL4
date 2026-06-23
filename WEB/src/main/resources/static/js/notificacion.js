// Inicializamos stmpClient
let stmpClient = null;
let socket = null;

const audio = new Audio('/sounds/notificacion.mp3');

function connect() {
  socket = new SockJS('/ws'); // Cambia '/ws-endpoint' por tu endpoint real
  stmpClient = Stomp.over(socket);

  stmpClient.connect({}, function (frame) {
    console.log('Conectado: ' + frame);

    // Suscribirse al canal de notificaciones para el usuario
    stmpClient.subscribe('/user/queue/notificaciones', function (message) {
      handleNotification(message);
    });
  }, function (error) {
    console.error('Error en conexión STOMP:', error);
    // Reintentar conexión en 5 segundos
    setTimeout(connect, 5000);
  });
}

function handleNotification(message) {
  try {
    const data = JSON.parse(message.body);
    const pathname = window.location.pathname;
    const estaEnChat = pathname.startsWith("/chat");
    const estaEnMisChats = pathname === "/chats";

    if (!estaEnChat && !estaEnMisChats) {
      // Animar icono
      const icono = document.getElementById("mensaje-icono");
      if (icono && !icono.classList.contains("vibrar")) {
        icono.classList.add("vibrar");
        setTimeout(() => icono.classList.remove("vibrar"), 2000);
        audio.play().catch(e => {
          console.warn("No se pudo reproducir el sonido (probablemente por falta de interacción):", e);
        });
      }

      // Mostrar badge
      const badge = document.getElementById("badge-notificacion");
      if (badge) {
        badge.style.display = "inline";
      }
    }
  } catch (e) {
    console.error('Error al procesar notificación:', e);
  }
}

// Desconectar cuando el usuario abandona la página
window.addEventListener('beforeunload', () => {
  if (stmpClient !== null) {
    stmpClient.disconnect(() => {
      console.log('Desconectado de STOMP');
    });
  }
});

// Iniciar conexión
connect();