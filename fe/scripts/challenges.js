setUser();

let selectedOperation = null;

function setUser() {
    var user = JSON.parse(localStorage.getItem("alias"));
    document.getElementById("alias").innerHTML = user.alias;
}

function selectOperation(operation, element) {
    selectedOperation = operation;

    // Remove "selected" class from all
    document.querySelectorAll('.operation').forEach(op => op.classList.remove('selected'));

    // Highlight selected one
    element.classList.add('selected');
}

function confirmSelection() {
    let difficulty = document.getElementById('difficulty').value;

    if (!selectedOperation) {
        alert("Please select a math operation.");
        return;
    }

    localStorage.setItem('selectedOperation', selectedOperation);
    localStorage.setItem('difficulty', difficulty);
    window.location.href = "game.html";
}