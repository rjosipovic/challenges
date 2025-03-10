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
    const selectedUserId = document.getElementById("user-select").value;
    const selectedChallenge = document.getElementById("challenge-select").value;

    console.log("Selected user ID: " + selectedUserId);
    console.log("Selected challenge: " + selectedChallenge);

    if (selectedUserId === "") {
        alert("Please select a user");
        return;
    }

    if (selectedChallenge === "") {
        alert("Please select a challenge");
        return;
    }

    if (selectedChallenge === "multiplication") {
        window.location.href = "multiplication.html?userId=" + selectedUserId;
    } else {
        window.location.href = "index.html";
    }

}

