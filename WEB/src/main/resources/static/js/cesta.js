document.addEventListener('DOMContentLoaded', function() {
    const addButton = document.getElementById('add-producto-btn');
    const container = document.getElementById('productos-container');
    const template = document.getElementById('producto-template');

    if (addButton && container && template) {
        addButton.addEventListener('click', function() {
            // Clonar el contenido del template
            const clone = template.content.cloneNode(true);
            
            // Obtener el nuevo índice basado en los productos existentes
            const nextIndex = container.querySelectorAll('.row.mb-2').length;
            
            // Actualizar los nombres de los campos con el nuevo índice
            const select = clone.querySelector('select');
            const input = clone.querySelector('input[type="number"]');
            
            select.name = select.name.replace('__index__', nextIndex);
            input.name = input.name.replace('__index__', nextIndex);
            
            // Añadir el nuevo producto al contenedor
            container.appendChild(clone);
        });
    } else {
        console.error('No se encontraron todos los elementos necesarios');
    }
});