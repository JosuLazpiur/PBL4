// cliente-select2.js

// Espera a que el DOM esté listo
$(document).ready(function () {
    $('#clienteUsername').select2({
        width: '100%',                            // Ancho completo
        placeholder: $('#clienteUsername option:first').text(),  // Placeholder desde la primera opción
        allowClear: true,                         // Permite limpiar la selección
        minimumResultsForSearch: 0,               // Siempre muestra el buscador, aunque pocas opciones
        language: document.documentElement.lang || 'es'  // Detecta idioma del HTML o usa español por defecto
    });
});
