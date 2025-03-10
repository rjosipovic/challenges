console.log("Page is loaded");

getChallenge();

function evaluateGuess() {
    const factorA = document.getElementById("factorA").innerHTML;
    const factorB = document.getElementById("factorB").innerHTML;
    const guess = document.getElementById("guess").value;
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId');

    if (guess.trim() === "") {
        alert("Please enter a number");
        return;
    }
    
    console.log(`User ID: ${userId}`);
    const guessData = {
        "userId": userId,
        "factorA": parseInt(factorA),
        "factorB": parseInt(factorB),
        "guess": parseInt(guess)
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
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${attempt.factorA} x ${attempt.factorB}</td>
                <td>${attempt.guess}</td>
                <td>${attempt.correct ? 'Correct' : 'Incorrect (' + attempt.correctResult + ')'}</td>
            `;
            tableBody.appendChild(row);
        });
        const table = document.getElementById('attemptsTable');
        table.classList.remove('hidden');
    })
    .catch(error => console.error(error));
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
    return fetch(`http://localhost:8080/challenges/random`)
    .then(response => response.json())
    .then(data => {
        document.getElementById("factorA").innerHTML = data.factorA;
        document.getElementById("factorB").innerHTML = data.factorB;
    })
    .catch(error => console.error(error));
}
