console.log("Page is loaded");

getChallenge();

function evaluateGuess() {
    const factorA = document.getElementById("factorA").innerHTML;
    const factorB = document.getElementById("factorB").innerHTML;
    const guess = document.getElementById("guess").value;

    const guessData = {
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
            alert(`Incorrect! The correct answer is ${data.correctResult}`);
        }
    })
    .catch(error => console.error(error));
    
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
