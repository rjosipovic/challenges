populateUsers();


function populateUsers() {

    fetch(`http://localhost:8081/users`)
    .then(response => response.json())
    .then(data => {
        data.forEach(user => {
            const option = document.createElement("option");
            option.value = user.id;
            option.text = user.alias;
            document.getElementById("user-select").appendChild(option);
        })

    })
    .catch(error => console.error(error));
}

function startChallenge() {
    const userId = document.getElementById("user-select").value;
    const game = document.getElementById("game-select").value;
    var difficulty = document.getElementById("difficulty-select").value;


    if (userId === "") {
        alert("Please select a user");
        return;
    }

    if (game === "") {
        alert("Please select a game");
        return;
    }

    if (difficulty === "") {
        difficulty = "medium";
    }

    window.location.href = "challenge.html?userId=" + userId + "&difficulty=" + difficulty + "&game=" + game;

}

