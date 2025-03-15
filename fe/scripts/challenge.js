console.log("Page is loaded");

getChallenge();
setGameName();
setOperator();

function evaluateGuess() {
    const firstNumber = document.getElementById("firstNumber").innerHTML;
    const secondNumber = document.getElementById("secondNumber").innerHTML;
    const guess = document.getElementById("guess").value;
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId');
    const game = urlParams.get('game');

    if (guess.trim() === "") {
        alert("Please enter a number");
        return;
    }

    console.log(`User ID: ${userId}`);
    const guessData = {
        "userId": userId,
        "firstNumber": parseInt(firstNumber),
        "secondNumber": parseInt(secondNumber),
        "guess": parseInt(guess),
        "game": game
    };

    fetch(`http://localhost:8080/attempts`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(guessData)
    })
    .then(response => response.json())
    .then(data => {
        setAttemptsHeading();
        setResponseMessage(data);
        populateTable(userId);
        getChallenge();
    })
    .catch(error => console.error(error));  
}

function setAttemptsHeading() {
    const attempts = document.getElementById("attempts");
    attempts.classList.remove("hidden");
}

function setResponseMessage(data) {
    const result = document.getElementById("result");
    let response;
    if(data.correct) {
        response = `Congrats! Your guess ${data.guess} is correct!`;
    } else {
        response = `Oops! Your guess ${data.guess} is wrong, but keep trying!`;
    }
    result.innerHTML = response;
    result.classList.remove("hidden");
}

function populateTable(userId) {
    fetch(`http://localhost:8080/attempts?userId=${userId}`)
    .then(response => response.json())
    .then(data => {
        const tableBody = document.getElementById('attemptsTableBody');
        tableBody.innerHTML = '';
        data.forEach(attempt => {
            const operator = getOperator(attempt.game);
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${attempt.firstNumber} ${operator} ${attempt.secondNumber}</td>
                <td>${attempt.guess}</td>
                <td>${attempt.correct ? 'Correct' : 'Incorrect (' + attempt.correctResult + ')'}</td>
                <td>${attempt.game}</td>
            `;
            tableBody.appendChild(row);
        });
        const table = document.getElementById('attemptsTable');
        table.classList.remove('hidden');
    })
    .catch(error => console.error(error));
}


function getOperator(game) {
    let operator;
    if (game === "multiplication") {
        operator = "x";
    } else if (game === "subtraction") {
        operator = "-";
    } else if (game === "addition") {
        operator = "+";
    } else if (game === "division") {
        operator = "/";
    }
    return operator;
}

async function createUser(alias) {
    const apiUrl = `http://localhost:8081/users`;
    const userData = { alias: alias};
    const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    });
    const data = await response.json();
    return data;
}

  
function getChallenge() {
    const urlParams = new URLSearchParams(window.location.search);
    const difficulty = urlParams.get('difficulty');
    return fetch(`http://localhost:8080/challenges/random?difficulty=${difficulty}`)
    .then(response => response.json())
    .then(data => {
        document.getElementById("firstNumber").innerHTML = data.firstNumber;
        document.getElementById("secondNumber").innerHTML = data.secondNumber;
    })
    .catch(error => console.error(error));
}

function setGameName() {
    const urlParams = new URLSearchParams(window.location.search);
    const game = urlParams.get('game');
    document.getElementById("game").innerHTML = game;
}

function setOperator() {
    const urlParams = new URLSearchParams(window.location.search);
    const game = urlParams.get('game');
    let operator;
    if (game === "multiplication") {
        operator = "x";
    } else if (game === "subtraction") {
        operator = "-";
    } else if (game === "addition") {
        operator = "+";
    } else if (game === "division") {
        operator = "/";
    }
    document.getElementById("operator").innerHTML = operator;
}
