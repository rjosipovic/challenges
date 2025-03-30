const ERROR_MSG = "Something went wrong. Please try again.";

setUser();

setGame();

setQuestion();

let selectedOperation = null;

function setUser() {
    var user = JSON.parse(localStorage.getItem("alias"));
    document.getElementById("alias").innerHTML = user.alias;
}

function setGame() {
    var selectedOperation = localStorage.getItem("selectedOperation");
    console.log(selectedOperation);
    document.getElementById("game-selected").innerHTML = selectedOperation;
}

async function setQuestion() {
    console.log("About to set question.");

    const difficulty = localStorage.getItem('difficulty');

    console.log("Retrieving challenge with difficulty: ", difficulty);

    const apiUrl = `http://localhost:8080/challenges/random?difficulty=${difficulty}`;

    try {
        const response = await fetch(apiUrl);

        if (response.ok) {
            const data = await response.json();
            console.log('Got challenge:', data);
            document.getElementById("firstNumber").innerHTML = data.firstNumber;
            var selectedOperation = localStorage.getItem("selectedOperation");
            document.getElementById("operator").innerHTML = getOperatorSign(selectedOperation);
            document.getElementById("secondNumber").innerHTML = data.secondNumber;

        } else {
            const errorData = await response.json();
            alert(ERROR_MSG);
            console.error('Getting challenge error:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('There was an error getting challenge', error);
    }
}

async function submitAnswer() {
    console.log("About to submit answer.");
    const firstNumber = document.getElementById("firstNumber").innerHTML;
    const secondNumber = document.getElementById("secondNumber").innerHTML;
    const guess = document.getElementById("guess").value;
    const user = JSON.parse(localStorage.getItem("alias"));
    const game = localStorage.getItem("selectedOperation");

    if (guess.trim() === "") {
        alert("Please enter your answer.");
        return;
    }
    const data = {
        "userId": user.id,
        "firstNumber": parseInt(firstNumber),
        "secondNumber": parseInt(secondNumber),
        "guess": parseInt(guess),
        "game": game
    };

    const apiUrl = `http://localhost:8080/attempts`;

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            if (result.correct) {
                const result = document.getElementById("result");
                result.classList.remove("hidden");
                result.innerHTML = "Correct!";
            } else {
                const result = document.getElementById("result");
                result.classList.remove("hidden");
                result.innerHTML = "Inorrect!";
            }
            const table = document.getElementById("attempts-table");
            table.classList.remove("hidden");
            setQuestion();
            clearAnswer();
            populteAttemptsTable(user.id);
        } else {
            const errorData = await response.json();
            alert(ERROR_MSG);
            console.error('Getting challenge error:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('There was an error getting challenge', error);
    }
}

function clearAnswer() {
    document.getElementById("guess").value = "";
}

function getOperatorSign(selectedOperation) {
    switch (selectedOperation) {
        case "addition":
            return "+";
        case "subtraction":
            return "-";
        case "multiplication":
            return "*";
        case "division":
            return "/";
    }
}

async function populteAttemptsTable(userId) {
    const apiUrl = `http://localhost:8080/attempts?userId=${userId}`;

    try {
        const response = await fetch(apiUrl);

        if (response.ok) {
            const data = await response.json();
            console.log('Got attempts:', data);
            const table = document.getElementById("attempts-table");
            const tableBody = table.querySelector("tbody");
            tableBody.innerHTML = "";
            data.forEach(attempt => {
                const operator = getOperatorSign(attempt.game);
                const row = document.createElement("tr");
                row.innerHTML = `
                <td>${attempt.firstNumber} ${operator} ${attempt.secondNumber}</td>
                <td>${attempt.guess}</td>
                <td>${attempt.correct ? 'Correct' : 'Incorrect (' + attempt.correctResult + ')'}</td>
                <td>${attempt.game}</td>
                `;
                tableBody.appendChild(row);
            });
        } else {
            const errorData = await response.json();
            console.error('Getting challenge error:', errorData);
        }

    } catch (error) {
        console.warn('There was an error getting challenge', error);
    }
}
