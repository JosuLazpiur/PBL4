function mostrarNombreArchivo() {
  const input = document.getElementById('imagen');
  const texto = document.getElementById('nombreArchivo');
  if (input.files.length > 0) {
    texto.textContent = input.files[0].name;
  } else {
    // El valor por defecto en caso de reset o no selección
    texto.textContent = texto.getAttribute('data-default') || 'No file selected';
  }
}
