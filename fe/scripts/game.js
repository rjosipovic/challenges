const ERROR_MSG = "Something went wrong. Please try again.";
const CHALLENGE_API = 'http://localhost:8080/challenges';
const ATTEMPT_API = 'http://localhost:8080/attempts';
const STATISTICS_API = 'http://localhost:8084/statistics/user';
const STATISTICS_EMITTER_API = 'http://localhost:8084/statistics/user/subscribe';

setUser();

setGame();

setQuestion();

fetchStatistics();

statsSubscribe()

let selectedOperation = null;

function setUser() {
    var user = JSON.parse(localStorage.getItem("alias"));
    document.getElementById("alias").innerHTML = user.alias;
    document.getElementById("stat-alias").innerHTML = user.alias;
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

    const apiUrl = CHALLENGE_API + '/random?difficulty=' + difficulty;
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(apiUrl, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.ok) {
            const data = await response.json();
            console.log('Got challenge:', data);
            document.getElementById("firstNumber").innerHTML = data.firstNumber;
            var selectedOperation = localStorage.getItem("selectedOperation");
            document.getElementById("operator").innerHTML = getOperatorSign(selectedOperation);
            document.getElementById("secondNumber").innerHTML = data.secondNumber;
        } else if (response.status === 401) {
            alert("Access denied. Please log in to play the game.");
            window.location.href = "login.html";
        } else {
            const errorData = await response.json();
            alert(ERROR_MSG);
            console.error('Error getting challenge:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('Error getting challenge:', error);
    }
}

async function submitAnswer() {
    console.log("About to submit answer.");
    const firstNumber = document.getElementById("firstNumber").innerHTML;
    const secondNumber = document.getElementById("secondNumber").innerHTML;
    const guess = document.getElementById("guess").value;
    const user = JSON.parse(localStorage.getItem("alias"));
    const game = localStorage.getItem("selectedOperation");
    const token = localStorage.getItem("token");

    if (guess.trim() === "") {
        alert("Please enter your answer.");
        return;
    }
    const data = {
        "firstNumber": parseInt(firstNumber),
        "secondNumber": parseInt(secondNumber),
        "guess": parseInt(guess),
        "game": game
    };

    try {
        const response = await fetch(ATTEMPT_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            const resultElement = document.getElementById("result");
            resultElement.classList.remove("hidden");
            if (result.correct) {
                resultElement.innerHTML = "Correct!";
            } else {
                resultElement.innerHTML = "Inorrect!";
            }
            const table = document.getElementById("attempts-table");
            table.classList.remove("hidden");
            setQuestion();
            clearAnswer();
            populateAttemptsTable(user.id);
        } else if (response.status === 401) {
            alert("Access denied. Please log in to play the game.");
            window.location.href = "login.html";
        } else {
            const errorData = await response.json();
            alert(ERROR_MSG);
            console.error('Error submitting answer:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('Error submitting answer:', error);
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

async function fetchStatistics() {
    const user = JSON.parse(localStorage.getItem("alias"));
    console.log("About to fetch statistics for user:" + user.alias);

    const apiUrl = STATISTICS_API;
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(apiUrl, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.ok) {
            const data = await response.json();
            console.log('Got statistics:', data);
            localStorage.setItem('statistics', JSON.stringify(data));
            showStatistics();

        } else if (response.status === 401) {
            alert("Access denied. Please log in to view the scoreboard.");
            window.location.href = "login.html";
        } else {
            const errorData = await response.json();
            console.error('Error fetching statistics:', errorData);
        }

    } catch (error) {
        console.error('Error fetching statistics:', error);
    }
}

function showStatistics() {
    const stats = JSON.parse(localStorage.getItem('statistics'));
    showOverallStatistics(stats.overall);
    showGameStatistics(stats.byGame);
    showByDifficultyStatistics(stats.byDifficulty);
}

function showOverallStatistics(overallStats) {
    buildStatTable(overallStats, 'overall-stats-table', 'OVERALL', 'overall-head-name');
}

function showGameStatistics(byGameStats) {
    const additionStats = byGameStats['addition'];
    const subtractionStats = byGameStats['subtraction'];
    const multiplicationStats = byGameStats['multiplication'];
    const divisionStats = byGameStats['division'];

    if (additionStats != null) {
        const additionTable = document.getElementById('addition-stats-table');
        additionTable.classList.remove('hidden');
        buildStatTable(additionStats, 'addition-stats-table', 'ADDITION', 'addition-head-name');
    } else {
        const additionTable = document.getElementById('addition-stats-table');
        additionTable.classList.add('hidden');
    }
    if (subtractionStats != null) {
        const subtractionTable = document.getElementById('subtraction-stats-table');
        subtractionTable.classList.remove('hidden');
        buildStatTable(subtractionStats, 'subtraction-stats-table', 'SUBTRACTION', 'subtraction-head-name');
    } else {
        const subtractionTable = document.getElementById('subtraction-stats-table');
        subtractionTable.classList.add('hidden');
    }
    if (multiplicationStats != null) {
        const multiplicationTable = document.getElementById('multiplication-stats-table');
        multiplicationTable.classList.remove('hidden');
        buildStatTable(multiplicationStats, 'multiplication-stats-table', 'MULTIPLICATION', 'multiplication-head-name');
    } else {
        const multiplicationTable = document.getElementById('multiplication-stats-table');
        multiplicationTable.classList.add('hidden');
    }
    if (divisionStats != null) {
        const divisionTable = document.getElementById('division-stats-table');
        divisionTable.classList.remove('hidden');
        buildStatTable(divisionStats, 'division-stats-table', 'DIVISION', 'division-head-name');
    } else {
        const divisionTable = document.getElementById('division-stats-table');
        divisionTable.classList.add('hidden');
    }
}

function showByDifficultyStatistics(byDifficultyStats) {
    const easyStats = byDifficultyStats['easy'];
    const mediumStats = byDifficultyStats['medium'];
    const hardStats = byDifficultyStats['hard'];
    const expertStats = byDifficultyStats['expert'];

    if (easyStats != null) {
        const easyTable = document.getElementById('easy-stats-table');
        easyTable.classList.remove('hidden');
        buildStatTable(easyStats, 'easy-stats-table', 'EASY', 'easy-head-name');
    } else {
        const easyTable = document.getElementById('easy-stats-table');
        easyTable.classList.add('hidden');
    }
    if (mediumStats != null) {
        const mediumTable = document.getElementById('medium-stats-table');
        mediumTable.classList.remove('hidden');
        buildStatTable(mediumStats, 'medium-stats-table', 'MEDIUM', 'medium-head-name');
    } else {
        const mediumTable = document.getElementById('medium-stats-table');
        mediumTable.classList.add('hidden');
    }
    if (hardStats != null) {
        const hardTable = document.getElementById('hard-stats-table');
        hardTable.classList.remove('hidden');
        buildStatTable(hardStats, 'hard-stats-table', 'HARD', 'hard-head-name');
    } else {
        const hardTable = document.getElementById('hard-stats-table');
        hardTable.classList.add('hidden');
    }
    if (expertStats != null) {
        const expertTable = document.getElementById('expert-stats-table');
        expertTable.classList.remove('hidden');
        buildStatTable(expertStats, 'expert-stats-table', 'EXPERT', 'expert-head-name');
    } else {
        const expertTable = document.getElementById('expert-stats-table');
        expertTable.classList.add('hidden');
    }
}

function buildStatTable(stat, tableName, headerName, headerId) {
    const table = document.getElementById(tableName);
    const overallHeadName = document.getElementById(headerId);
    overallHeadName.innerHTML = headerName;

    const tableBody = table.querySelector("tbody");
    tableBody.innerHTML = "";
    const totalAttempts = stat.totalAttempts;
    const correctAttempts = stat.correctAttempts;
    const successRate = correctAttempts / totalAttempts * 100;

    const totalRow = document.createElement("tr");
    totalRow.innerHTML = `
    <td class="border border-gray-300 px-4 py-2">TOTAL</td>
    <td class="border border-gray-300 px-4 py-2"><span id="total-attempts">${totalAttempts}</span></td>
    `;

    const correctRow = document.createElement("tr");
    correctRow.innerHTML = `
    <td class="border border-gray-300 px-4 py-2">CORRECT</td>
    <td class="border border-gray-300 px-4 py-2"><span id="total-attempts">${correctAttempts}</span></td>
    `;

    const successRateRow = document.createElement("tr");
    successRateRow.innerHTML = `
    <td class="border border-gray-300 px-4 py-2">RATE</td>
    <td class="border border-gray-300 px-4 py-2"><span id="total-attempts">${successRate.toFixed(2)}%</span></td>
    `;

    tableBody.appendChild(totalRow);
    tableBody.appendChild(correctRow);
    tableBody.appendChild(successRateRow);
}

async function populateAttemptsTable(userId) {
    console.log("About to populate attempts table.");
    const token = localStorage.getItem("token");

    const apiUrl = ATTEMPT_API;
    try {
        const response = await fetch(apiUrl, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

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
        } else if (response.status === 401) {
            alert("Access denied. Please log in to play the game.");
            window.location.href = "login.html";
        } else {
            const errorData = await response.json();
            console.error('Error getting attempts history:', errorData);
        }

    } catch (error) {
        console.warn('Error getting attempts history:', error);
    }
}

function statsSubscribe() {
    const token = localStorage.getItem("token");
    const eventSourceUrl = new URL(STATISTICS_EMITTER_API);
    eventSourceUrl.searchParams.append("access_token", token);

    const eventSource = new EventSource(eventSourceUrl.toString(), { withCredentials: true });
    eventSource.onmessage = function (event) {
        const update = JSON.parse(event.data);
        const stats = JSON.parse(localStorage.getItem('statistics'));
        console.log('Received update:', update);
        updateOverallStats(stats, update);
        updateOnDifficultyStats(stats, update);
        updateByGameStats(stats, update);
        localStorage.setItem('statistics', JSON.stringify(stats));
        showStatistics();
    }
    eventSource.onerror = function (err) {
        console.error("SSE error:", err);
    }
}
function updateOverallStats(stats, update) {
    stats.overall.totalAttempts += 1;
    if (update.success) {
        stats.overall.correctAttempts += 1;
    }
}

function updateOnDifficultyStats(stats, update) {
    var difficultyStats = stats.byDifficulty[update.difficulty];
    if (difficultyStats == null) {
        stats.byDifficulty[update.difficulty] = {
            totalAttempts: 0,
            correctAttempts: 0
        }
        difficultyStats = stats.byDifficulty[update.difficulty];
    }
    difficultyStats.totalAttempts += 1;
    if (update.success) {
        difficultyStats.correctAttempts += 1;
    }
}

function updateByGameStats(stats, update) {
    var game = update.game;
    var gameStats = stats.byGame[game];
    if (gameStats == null) {
        stats.byGame[game] = {
            totalAttempts: 0,
            correctAttempts: 0
        }
        gameStats = stats.byGame[game];
    }
    gameStats.totalAttempts += 1;
    if (update.success) {
        gameStats.correctAttempts += 1;
    }
}

