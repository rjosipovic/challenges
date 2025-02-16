console.log("Page is loaded");

getChallenge();

function evaluateGuess() {
    const factorA = document.getElementById("factorA").innerHTML;
    const factorB = document.getElementById("factorB").innerHTML;
    const guess = document.getElementById("guess").value;
    const alias = document.getElementById("alias").value;

    if (alias.trim() === "") {
        alert("Please enter an alias");
        return;
    }

    if (guess.trim() === "") {
        alert("Please enter a number");
        return;
    }


    getUserIdFromAlias(alias).then(userId => {
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
            if(data.correct) {
                alert("Correct!");
                getChallenge
            } else {
                alert(`Incorrect!`);
            }
        })
        .catch(error => console.error(error));    
    
    })
}

function getUserIdFromAlias(alias) {
    const apiUrl = `http://localhost:8081/users/alias/${alias}`;
    return fetch(apiUrl)
    .then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 404) {
            return createUser(alias)
        } else {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
    })
    .then(data => {
        if (data.id) {
            return data.id;
        } else {
            throw new Error(`User not found with alias ${alias}`);
        }        
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
