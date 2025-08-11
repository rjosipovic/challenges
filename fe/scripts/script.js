const USER_NOT_FOUND_CODE = 'U001';
const USER_EXISTS_CODE = 'U002';
const INVALID_VERIFICATION_CODE = 'A001';

// challenge-manager
const CHALLENGE_API = 'http://localhost:8080/challenges';
const ATTEMPT_API = 'http://localhost:8080/attempts';

// user-manager
const AUTH_API = 'http://localhost:8081/auth';
const USERS_API = 'http://localhost:8081/users';

//gamification-manager
const LEADERBOARD_API = 'http://localhost:8082/leaders';

//analitics-manager
const STATS_API = 'http://localhost:8084/statistics/user';
const HISTORY_API = 'http://localhost:8084/attempts';


// Get the current year for the footer
document.getElementById('current-year').textContent = new Date().getFullYear();

// --- DOM Elements ---
const navHome = document.getElementById('nav-home');
const navRegister = document.getElementById('nav-register');
const navLogin = document.getElementById('nav-login');
const navGame = document.getElementById('nav-game');
const navStatistics = document.getElementById('nav-statistics');
const navHistory = document.getElementById('nav-history');
const navLeaderboard = document.getElementById('nav-leaderboard');
const navAbout = document.getElementById('nav-about');

const homeView = document.getElementById('home-view');
const registerView = document.getElementById('register-view');
const loginView = document.getElementById('login-view');
const gameView = document.getElementById('game-view');
const statisticsView = document.getElementById('statistics-view');
const historyView = document.getElementById('history-view');
const leaderboardView = document.getElementById('leaderboard-view');
const aboutView = document.getElementById('about-view');

const homeToRegisterBtn = document.getElementById('home-to-register');
const homeToLoginBtn = document.getElementById('home-to-login');
const homeToGameBtn = document.getElementById('home-to-game');
const userStatusDisplay = document.getElementById('user-status-display');

// Register elements
const registerForm = document.getElementById('register-form');
const regAliasInput = document.getElementById('reg-alias');
const regEmailInput = document.getElementById('reg-email');
const regBirthdateInput = document.getElementById('reg-birthdate');
const regGenderInputs = document.querySelectorAll('input[name="reg-gender"]');
const registerMessage = document.getElementById('register-message');

// Login elements (UPDATED FOR TWO-STEP)
const loginForm = document.getElementById('login-form');
const loginStep1 = document.getElementById('login-step1'); // New
const loginStep2 = document.getElementById('login-step2'); // New
const loginEmailInput = document.getElementById('login-email');
const generateCodeBtn = document.getElementById('generate-code-btn'); // New
const loginCodeInput = document.getElementById('login-code'); // New
const verifyCodeBtn = document.getElementById('verify-code-btn'); // New
const resendCodeBtn = document.getElementById('resend-code-btn'); // New
const loginMessage = document.getElementById('login-message');

// Game elements
const gameSetupDiv = document.getElementById('game-setup');
const gamePlayAreaDiv = document.getElementById('game-play-area');
const mathOperationSelect = document.getElementById('math-operation');
const difficultySelect = document.getElementById('difficulty');
const startGameBtn = document.getElementById('start-game-btn');
const problemDisplay = document.getElementById('problem-display');
const answerInput = document.getElementById('answer-input');
const submitAnswerBtn = document.getElementById('submit-answer-btn');
const feedbackMessage = document.getElementById('feedback-message');
const nextProblemBtn = document.getElementById('next-problem-btn');
const endGameBtn = document.getElementById('end-game-btn');

// Statistics elements for dynamic rendering
const statsByOperationDiv = document.getElementById('stats-by-operation');
const statsByDifficultyDiv = document.getElementById('stats-by-difficulty'); 
const statisticsDiv = document.getElementById('statistics-view');

// History elements for dynamic rendering
const historyDiv = document.getElementById('history-view');
const historyList = document.getElementById('history-list');

// Leaderboard elements for dynamic rendering
const leaderboardBody = document.getElementById('leaderboard-body');

// Store the generated code for the current login attempt (client-side only)
let currentLoginCode = '';
let currentLoginEmailAttempt = ''; // To remember which email the code was sent to

let userStats = {
    overall: {
        totalAttempts: 0,
        correctAttempts: 0
    },
    byGame: {
        addition: { totalAttempts: 0, correctAttempts: 0 },
        subtraction: { totalAttempts: 0, correctAttempts: 0 },
        multiplication: { totalAttempts: 0, correctAttempts: 0 },
        division: { totalAttempts: 0, correctAttempts: 0, }
    },
    byDifficulty: {
        easy: { totalAttempts: 0, correctAttempts: 0 },
        medium: { totalAttempts: 0, correctAttempts: 0 },
        hard: { totalAttempts: 0, correctAttempts: 0 },
        expert: { totalAttempts: 0, correctAttempts: 0 }
    }
};

let currentUser = null;
let gameHistory = [];

let currentProblem = '';
let num1 = 0;
let num2 = 0;
let selectedOperation = 'addition';
let selectedDifficulty = 'easy';
let gameActive = false;

async function generateProblem() {

    const difficultyValue = difficultySelect.value;
    console.log("Retrieving challenge with difficulty: ", difficultyValue);
    const apiUrl = CHALLENGE_API + '/random?difficulty=' + difficultyValue;
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
            num1 = data.firstNumber;
            num2 = data.secondNumber;
        } else if (response.status === 401) {
            alertMessage("Please log in to play the game.", "text-red-500");
            loginReset();
        } else {
            const errorData = await response.json();
            alertMessage('Something went wrong. Please try again.');
            console.error('Error submitting answer:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('Error getting challenge:', error);
    }


    let problemString = '';

    switch (selectedOperation) {
        case 'addition':
            problemString = `${num1} + ${num2}`;
            break;
        case 'subtraction':
            if (num1 < num2) [num1, num2] = [num2, num1];
            problemString = `${num1} - ${num2}`;
            break;
        case 'multiplication':
            problemString = `${num1} x ${num2}`;
            break;
        case 'division':
            problemString = `${num1} ÷ ${num2}`;
            break;
        default:
            problemString = 'Error';
    }

    currentProblem = problemString;
    problemDisplay.textContent = problemString;
    answerInput.value = '';
    feedbackMessage.textContent = '';
    nextProblemBtn.classList.add('hidden');
    submitAnswerBtn.classList.remove('hidden');
    answerInput.disabled = false;
}

function calculateAccuracy(total, correct) {
    return total > 0 ? ((correct / total) * 100).toFixed(2) : 0;
}

function loginReset() {
    showView(loginView);
    localStorage.removeItem("token");
    currentLoginCode = '';
    currentLoginEmailAttempt = '';
    loginMessage.textContent = '';
    loginEmailInput.value = '';
    loginCodeInput.value = '';
}

async function fetchStatistics() {
    const token = localStorage.getItem("token");
    try {
        const response = await fetch(STATS_API, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.ok) {
            const result = await response.json();
            userStats = result;
            console.log("Fetched statistics:", userStats);
            renderStatistics();
        } else if (response.status === 401) {
            alertMessage("Please log in to play the game.", "text-red-500");
            loginReset();
        } else {
            const errorData = await response.json();
            alertMessage('Something went wrong. Please try again.');
            console.error('Error submitting answer:', errorData);
        }
    } catch (error) {
        alertMessage('Something went wrong. Please try again.');
        console.error('Error submitting answer:', error);
    }
}

function renderStatistics() {
    document.getElementById('stat-total').textContent = userStats.overall.totalAttempts;
    document.getElementById('stat-correct').textContent = userStats.overall.correctAttempts;
    document.getElementById('stat-incorrect').textContent = userStats.overall.totalAttempts - userStats.overall.correctAttempts;
    document.getElementById('stat-accuracy').textContent = `${calculateAccuracy(userStats.overall.totalAttempts, userStats.overall.correctAttempts)}%`;

    statsByOperationDiv.innerHTML = '';
    let hasOperationStats = false;
    for (const op in userStats.byGame) {
        const stats = userStats.byGame[op];
        if (stats.totalAttempts > 0) {
            hasOperationStats = true;
            const accuracy = calculateAccuracy(stats.totalAttempts, stats.correctAttempts);
            statsByOperationDiv.innerHTML += `
                <div class="p-2 bg-gray-600 rounded-md">
                    <p class="text-md font-semibold capitalize">${op} Challenges:</p>
                    <p class="text-sm text-gray-300">Attempts: ${stats.totalAttempts}, Correct: ${stats.correctAttempts}, Accuracy: ${accuracy}%</p>
                </div>
            `;
        }
    }
    if (!hasOperationStats) {
        statsByOperationDiv.innerHTML = '<p class="text-gray-400">No operation data yet. Play some games!</p>';
    }

    statsByDifficultyDiv.innerHTML = '';
    let hasDifficultyStats = false;
    for (const diff in userStats.byDifficulty) {
        const stats = userStats.byDifficulty[diff];
        if (stats.totalAttempts > 0) {
            hasDifficultyStats = true;
            const accuracy = calculateAccuracy(stats.totalAttempts, stats.correctAttempts);
            statsByDifficultyDiv.innerHTML += `
                <div class="p-2 bg-gray-600 rounded-md">
                    <p class="text-md font-semibold capitalize">${diff} Difficulty:</p>
                    <p class="text-sm text-gray-300">Attempts: ${stats.totalAttempts}, Correct: ${stats.correctAttempts}, Accuracy: ${accuracy}%</p>
                </div>
            `;
        }
    }
    if (!hasDifficultyStats) {
        statsByDifficultyDiv.innerHTML = '<p class="text-gray-400">No difficulty data yet. Play some games!</p>';
    }
}

async function fetchHistory() {
    const token = localStorage.getItem("token");
    currentUser = extractUserFromToken();
    if (!currentUser) return;

    const apiUrl = HISTORY_API;
    try {
        const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.ok) {
            const result = await response.json();
            gameHistory = result;
            console.log("Fetched history:", gameHistory);
            renderHistory();
        } else if (response.status === 401) {
            alertMessage("Please log in to play the game.", "text-red-500");
            loginReset();
        } else {
            const errorData = await response.json();
            alertMessage('Something went wrong. Please try again.');
            console.error('Error submitting answer:', errorData);
        }
    } catch (error) {
        alertMessage('Something went wrong. Please try again.');
        console.error('Error submitting answer:', error);
    }
}

function renderHistory() {
    historyList.innerHTML = '';
    if (gameHistory.length === 0) {
        historyList.innerHTML = '<p class="text-center text-gray-400" id="no-history-message">No history available yet. Play a game to see your attempts!</p>';
    } else {
        const fragment = document.createDocumentFragment(); // Use a fragment for efficiency
        gameHistory.forEach(attempt => {
            const historyItem = document.createElement('div');
            historyItem.className = `p-3 rounded-lg shadow-md ${attempt.correct ? 'bg-green-800' : 'bg-red-800'}`;

            const problemP = document.createElement('p');
            problemP.className = 'text-lg font-semibold';
            problemP.textContent = `${attempt.firstNumber} ${getOperationFromString(attempt.game)} ${attempt.secondNumber}`;

            const answerP = document.createElement('p');
            answerP.className = 'text-sm';
            answerP.textContent = `Your Answer: ${attempt.guess} ${attempt.correct ? '✅' : '❌'}`;

            const correctP = document.createElement('p');
            correctP.className = 'text-sm';
            correctP.textContent = `Correct Answer: ${attempt.correctResult}`;

            historyItem.append(problemP, answerP, correctP);
            fragment.appendChild(historyItem);
        });
        historyList.appendChild(fragment); // Append all items at once
    }
}

async function fetchLeaderboard() {
    const token = localStorage.getItem("token");
    if (!token) return; // Should be caught by the view gate, but good practice

    try {
        // Step 1: Fetch the core leaderboard data (scores, badges)
        const leaderboardResponse = await fetch(LEADERBOARD_API, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });
        if (!leaderboardResponse.ok) {
            // Handle errors from the first API call
            if (leaderboardResponse.status === 401) loginReset();
            console.error('Failed to fetch leaderboard stats:', await leaderboardResponse.text());
            alertMessage("Could not load leaderboard data.", "text-red-500");
            return;
        }

        const leaderboardStats = await leaderboardResponse.json();
        if (leaderboardStats.length === 0) {
            renderLeaderboard([]); // Render an empty board if no data
            return;
        }

        // Step 2: Extract user IDs to fetch their aliases
        const userIds = leaderboardStats.map(stat => stat.userId);

        // Step 3: Fetch the aliases from the dedicated endpoint
        const apiUrl = USERS_API + `?userIds=${userIds.join(",")}`;
        const aliasesResponse = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        if (!aliasesResponse.ok) {
            console.error('Failed to fetch user aliases:', await aliasesResponse.text());
            alertMessage("Could not load user names for the leaderboard.", "text-red-500");
            // Fallback: render with a placeholder if aliases fail
            renderLeaderboard(leaderboardStats.map(stat => ({ ...stat, alias: 'Unknown' })));
            return;
        }

        const aliasesArray = await aliasesResponse.json(); // Expects an array like [{id: "...", alias: "..."}]

        // Convert the array to a map for efficient lookup
        const aliasesMap = aliasesArray.reduce((map, user) => {
            map[user.id] = user.alias;
            return map;
        }, {});

        // Step 4: Combine the data and render
        const enrichedLeaderboardData = leaderboardStats.map(stat => ({
            ...stat,
            alias: aliasesMap[stat.userId] || 'Unknown User' // Add alias with a fallback
        }));

        renderLeaderboard(enrichedLeaderboardData);

    } catch (error) {
        console.error('Error during leaderboard fetch process:', error);
        alertMessage("An unexpected error occurred while loading the leaderboard.", "text-red-500");
    }
}

const badgeIcons = {
    BRONZE: { icon: '🥉', title: 'Bronze Medal' },
    SILVER: { icon: '🥈', title: 'Silver Medal' },
    GOLD: { icon: '🥇', title: 'Gold Medal' },
    FIRST_WON: { icon: '🎉', title: 'First Win' },
    LUCKY_NUMBER: { icon: '🍀', title: 'Lucky Number' }
};

function renderLeaderboard(leaderboardData) {
    leaderboardBody.innerHTML = ''; // Clear existing leaderboard
    const currentUser = extractUserFromToken();
    const currentUserId = currentUser ? currentUser.userId : null;

    leaderboardData.forEach((user, index) => {
        const row = document.createElement('tr'); // Create a new row for each user

        let rowClass = 'border-b border-gray-700 hover:bg-gray-700 transition-colors duration-200';
        // Highlight the current user's row in the leaderboard
        if (currentUserId && user.userId === currentUserId) {
            rowClass = 'border-b border-green-600 bg-emerald-800 font-bold text-white';
        }
        row.className = rowClass;

        const badgesHtml = user.badges && user.badges.length > 0
            ? user.badges.map(badgeName => {
                const badge = badgeIcons[badgeName];
                return badge ? `<span title="${badge.title}" class="cursor-help">${badge.icon}</span>` : '';
            }).join(' ')
            : '<span class="text-sm text-gray-400">-</span>';

        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap font-medium">${index + 1}</td>
            <td class="px-6 py-4 whitespace-nowrap">${user.alias}</td>
            <td class="px-6 py-4 whitespace-nowrap text-center">${user.totalScore}</td>
            <td class="px-6 py-4 whitespace-nowrap text-center text-xl">${badgesHtml}</td>
        `;
        leaderboardBody.appendChild(row);
    });
}



function getOperationFromString(game) {
    switch (game) {
        case 'addition':
            return '+';
        case 'subtraction':
            return '-';
        case 'multiplication':
            return 'x';
        case 'division':
            return '÷';
        default:
            return '?';
    }
}

// --- Game Logic ---
function startGame() {
    console.log("Starting game.");
    currentUser = extractUserFromToken();
    if (!currentUser) {
        alertMessage("Please log in to play the game.", "text-red-500");
        return;
    }
    gameActive = true;
    gameSetupDiv.classList.add('hidden');
    gamePlayAreaDiv.classList.remove('hidden');
    selectedOperation = mathOperationSelect.value;
    selectedDifficulty = difficultySelect.value;
    generateProblem();
}

async function submitAnswer() {
    console.log("About to submit answer.");
    const userAnswer = parseFloat(answerInput.value);

    if (isNaN(userAnswer)) {
        alertMessage("Please enter a valid number.", "text-yellow-500");
        return;
    }
    const data = {
        "firstNumber": num1,
        "secondNumber": num2,
        "guess": userAnswer,
        "game": selectedOperation
    };
    const token = localStorage.getItem("token");
    let isCorrect;
    let correctAnswer;

    const apiUrl = ATTEMPT_API;
    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            isCorrect = result.correct;
            correctAnswer = result.correctResult;
        } else if (response.status === 401) {
            alertMessage("Please log in to play the game.", "text-red-500");
            loginReset();
        } else {
            const errorData = await response.json();
            alertMessage('Something went wrong. Please try again.');
            console.error('Error submitting answer:', errorData);
        }
    } catch (error) {
        alertMessage('Something went wrong. Please try again.');
        console.error('Error submitting answer:', error);
    }

    if (isCorrect) {
        feedbackMessage.textContent = "Correct! 🎉";
        feedbackMessage.className = "text-lg mt-4 text-green-400";
    } else {
        feedbackMessage.textContent = `Incorrect. The answer was ${correctAnswer}. 😔`;
        feedbackMessage.className = "text-lg mt-4 text-red-400";
    }

    answerInput.disabled = true;
    submitAnswerBtn.classList.add('hidden');
    nextProblemBtn.classList.remove('hidden');
}

function nextProblem() {
    generateProblem();
}

function endGame() {
    gameActive = false;
    gameSetupDiv.classList.remove('hidden');
    gamePlayAreaDiv.classList.add('hidden');
    problemDisplay.textContent = '?';
    answerInput.value = '';
    feedbackMessage.textContent = '';
    nextProblemBtn.classList.add('hidden');
    submitAnswerBtn.classList.remove('hidden');
    answerInput.disabled = false;
    alertMessage("Game Over! Check your statistics and history.", "text-blue-400");
}

// --- View Management ---
function showView(viewToShow) {
    // --- Authentication Gate for Protected Views ---
    const token = localStorage.getItem("token");
    const isProtectedView = viewToShow === gameView || viewToShow === statisticsView || viewToShow === historyView || viewToShow === leaderboardView;

    if (isProtectedView && !token) {
        alertMessage("You need to be logged in to access this page.", "text-yellow-500");
        // Redirect to the login view instead of showing the protected view
        showView(loginView);
        return; // Stop execution to prevent showing the protected view
    }
    // --- End of Authentication Gate ---

    document.querySelectorAll('.view-section').forEach(view => {
        view.classList.remove('active');
    });
    viewToShow.classList.add('active');

    // Reset forms and messages when switching views
    registerForm.reset();
    loginForm.reset(); // Reset login form for two-step process
    registerMessage.textContent = '';
    loginMessage.textContent = '';

    // Always show step 1 of login when navigating to login view
    if (viewToShow === loginView) {
        loginStep1.classList.remove('hidden');
        loginStep2.classList.add('hidden');
        loginEmailInput.value = ''; // Clear email input
        loginCodeInput.value = ''; // Clear code input
        currentLoginCode = ''; // Clear stored code
        currentLoginEmailAttempt = ''; // Clear stored email attempt
    }


    if (viewToShow === gameView) {
        if (gameActive) {
            gameSetupDiv.classList.add('hidden');
            gamePlayAreaDiv.classList.remove('hidden');
        } else {
            gameSetupDiv.classList.remove('hidden');
            gamePlayAreaDiv.classList.add('hidden');
        }
    } else {
        gameSetupDiv.classList.remove('hidden');
        gamePlayAreaDiv.classList.add('hidden');
        gameActive = false;
    }

    if (viewToShow === statisticsView) {
        fetchStatistics();
    } else if (viewToShow === historyView) {
        fetchHistory();
    } else if (viewToShow === leaderboardView) {
        fetchLeaderboard();
    } else if (viewToShow === homeView) {
        currentUser = extractUserFromToken();
        if (currentUser) {
            userStatusDisplay.textContent = `Status: Logged in as ${currentUser.alias} (${currentUser.email})`; // Display alias
        } else {
            userStatusDisplay.textContent = "Status: Not logged in";
        }
    }
}

// Shows a temporary banner message at the top of the screen for non-critical info.
function showBannerMessage(message, colorClass) {
    const bannerBox = document.createElement('div');
    // Styling for the banner, similar to the previous alert implementation
    bannerBox.className = `fixed top-5 left-1/2 -translate-x-1/2 p-4 text-lg rounded-lg shadow-lg z-50 ${colorClass} text-white font-bold`;
    bannerBox.textContent = message;
    document.body.appendChild(bannerBox);

    // Remove the banner after a few seconds
    setTimeout(() => {
        bannerBox.remove();
    }, 3000);
}

// Custom alert message instead of window.alert
// Updated to show a modal dialog window for better user interaction.
function alertMessage(message, colorClass) {
    // Create a container for the dialog to ensure it's removed correctly
    const dialogContainer = document.createElement('div');
    dialogContainer.id = 'custom-dialog-container';

    // Create overlay
    const overlay = document.createElement('div');
    overlay.className = 'fixed inset-0 bg-black bg-opacity-60 z-40';

    // Create dialog box
    const dialogBox = document.createElement('div');
    dialogBox.className = 'fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-gray-800 p-8 rounded-xl shadow-2xl z-50 w-full max-w-md text-center';

    // Create message content
    const messageP = document.createElement('p');
    messageP.className = `text-xl mb-6 font-semibold ${colorClass}`;
    messageP.textContent = message;

    // Create close button
    const closeButton = document.createElement('button');
    closeButton.className = 'bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded-full focus:outline-none focus:shadow-outline transition duration-300';
    closeButton.textContent = 'OK';

    // Assemble the dialog
    dialogBox.appendChild(messageP);
    dialogBox.appendChild(closeButton);
    dialogContainer.appendChild(overlay);
    dialogContainer.appendChild(dialogBox);

    // Add to DOM
    document.body.appendChild(dialogContainer);

    // --- Event Listeners for Closing ---
    const closeDialog = () => {
        dialogContainer.remove();
        document.removeEventListener('keydown', handleEsc); // Clean up listener
    };
    const handleEsc = (e) => { if (e.key === 'Escape') closeDialog(); };

    overlay.addEventListener('click', closeDialog);
    closeButton.addEventListener('click', closeDialog);
    document.addEventListener('keydown', handleEsc);

    // Focus the button for immediate interaction and accessibility
    closeButton.focus();
}

// --- Registration and Login Functions ---
async function handleRegister(e) {
    e.preventDefault();
    const alias = regAliasInput.value.trim(); // Get alias
    const email = regEmailInput.value.trim(); // Get email
    const birthdate = regBirthdateInput.value ?? null; // Get birthdate

    // Get selected gender from radio buttons
    let gender = null;
    for (const radio of regGenderInputs) {
        if (radio.checked) {
            gender = radio.value;
            break;
        }
    }

    if (!alias || !email) {
        registerMessage.textContent = "Please fill in all registration fields.";
        registerMessage.className = "text-red-500 mt-4";
        return;
    }

    const user = {
        alias: alias,
        email: email,
        birthdate: birthdate,
        gender: gender
    };

    console.log("About to register user:", user);
    const registrationApi = AUTH_API + '/register';
        try {
        const response = await fetch(registrationApi, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(user)
        });

        if (response.ok) {
            // Create new user object with updated fields
            const newUser = { alias, email, birthdate, gender };
            registerMessage.textContent = "Registration successful! Please proceed to login.";
            registerMessage.className = "text-green-500 mt-4";
            showBannerMessage("Registration successful! Please login to continue.", "text-green-500");
            showView(loginView); // Go to login after successful registration
            console.log("User registered:", newUser);
        } else {
            const errorData = await response.json();
            const message = errorData.message;
            const code = errorData.code;
            const reason = errorData.reason;
            console.log("Unable to register user: {}, reason: {} ", message, reason);
            if (code === USER_EXISTS_CODE) {
                registerMessage.textContent = "Registration failed: User with this email/alias already exists.";
                registerMessage.className = "text-red-500 mt-4";
            } else {
                registerMessage.textContent = "Registration failed.";
                registerMessage.className = "text-red-500 mt-4";
            }
        }
    } catch (error) {
        registerMessage.textContent = "Registration failed.";
        registerMessage.className = "text-red-500 mt-4";
        console.log("Registration failed. ", error);
    }
}
// New function to handle generating the login code
async function handleGenerateCode() {
    const email = loginEmailInput.value.trim();
    loginMessage.textContent = ''; // Clear previous messages

    if (!email) {
        loginMessage.textContent = "Please enter your email.";
        loginMessage.className = "text-yellow-500 mt-4";
        return;
    }

    const apiUrl = AUTH_API + '/request-code';
    const codeGenerationRequest = {
        "email": email
    };

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(codeGenerationRequest)
        });

        if (response.ok) {
            showBannerMessage(`Code sent to ${email}!`, "text-blue-400");
            //Transition to step 2
            loginStep1.classList.add('hidden');
            loginStep2.classList.remove('hidden');
            loginCodeInput.focus(); // Focus on the code input
            loginMessage.textContent = `A 6-digit code has been sent to ${email}.`;
            loginMessage.className = "text-gray-300 mt-4";
        
        } else {
            const errorDate = await response.json();
            const message = errorDate.message;
            const code = errorDate.code;
            const reason = errorDate.reason;
            console.log("Unable to generate code: {}, reason: {} ", message, reason);
            if (response .status === 404 && code === USER_NOT_FOUND_CODE) {
                loginMessage.innerHTML = 'User not found with this email. <a href="#" id="login-to-register-link" class="text-green-400 hover:underline">Please register.</a>';
                loginMessage.className = "text-red-500 mt-4";
                // Add event listener to the newly created link
                document.getElementById('login-to-register-link').addEventListener('click', (e) => {
                    e.preventDefault();
                    showView(registerView);
                });
            } else {
                loginMessage.textContent = "Error generating code.";
                loginMessage.className = "text-red-500 mt-4";
            }
        }
    } catch (error) {
        loginMessage.textContent = "Error generating code.";
        loginMessage.className = "text-red-500 mt-4";
        console.error('Error generating code:', error);
    }
}

// New function to handle verifying the login code
async function handleVerifyCode() {
    const enteredCode = loginCodeInput.value.trim();
    const email = loginEmailInput.value.trim();
    loginMessage.textContent = ''; // Clear previous messages

    if (!enteredCode) {
        loginMessage.textContent = "Please enter the verification code.";
        loginMessage.className = "text-yellow-500 mt-4";
        return;
    }


    const codeVerificationRequest = {
        "email": email,
        "code": enteredCode
    };

    const apiUrl = AUTH_API + '/verify-code'

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(codeVerificationRequest)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            currentUser = extractUserFromToken();
            userStatusDisplay.textContent = `Status: Logged in as ${currentUser.alias} (${currentUser.email})`;
            loginMessage.textContent = "Login successful! Welcome back.";
            loginMessage.className = "text-green-500 mt-4";
            showBannerMessage("Login successful! Welcome back.", "text-green-500");
            showView(homeView); // Redirect to home for a better landing experience
        } else {
            const errorData = await response.json();
            const message = errorData.message;
            const code = errorData.code;
            const reason = errorData.reason;
            console.log("Unable to verify code: {}, reason: {} ", message, reason);
            if (response.status === 404 && code === USER_NOT_FOUND_CODE) {
                loginMessage.textContent = "Login failed: User data not found after code verification.";
                loginMessage.className = "text-red-500 mt-4";
            } else if (response.status === 400 && code === INVALID_VERIFICATION_CODE) {
                loginMessage.textContent = "Verification failed: Invalid code.";
                loginMessage.className = "text-red-500 mt-4";
            } else {
                loginMessage.textContent = "Verification failed.";
                loginMessage.className = "text-red-500 mt-4";
            }
        }
    } catch (error) {
        loginMessage.textContent = "Verification failed: Invalid code.";
        loginMessage.className = "text-red-500 mt-4";
        console.error('Login failed:', error);
    }
}

function extractUserFromToken() {
    const token = localStorage.getItem("token");

    if (token) {
        const base64Url = token.split(".")[1];//get the payload part of the JWT
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/"); //convert base64url to base64
        const payload = atob(base64);
        const userExtended = JSON.parse(payload);
        const user = {
            alias: userExtended.alias,
            userId: userExtended.userId,
            email: userExtended.sub
        };
        return user;
    }
}

// --- Event Listeners Setup ---
document.addEventListener('DOMContentLoaded', () => {
    showView(homeView);

    navHome.addEventListener('click', (e) => { e.preventDefault(); showView(homeView); });
    navRegister.addEventListener('click', (e) => { e.preventDefault(); showView(registerView); });
    navLogin.addEventListener('click', (e) => { e.preventDefault(); showView(loginView); });
    navGame.addEventListener('click', (e) => { e.preventDefault(); showView(gameView); });
    navStatistics.addEventListener('click', (e) => { e.preventDefault(); showView(statisticsView); });
    navHistory.addEventListener('click', (e) => { e.preventDefault(); showView(historyView); });
    navLeaderboard.addEventListener('click', (e) => { e.preventDefault(); showView(leaderboardView); });
    navAbout.addEventListener('click', (e) => { e.preventDefault(); showView(aboutView); });

    homeToRegisterBtn.addEventListener('click', (e) => { e.preventDefault(); showView(registerView); });
    homeToLoginBtn.addEventListener('click', (e) => { e.preventDefault(); showView(loginView); });
    homeToGameBtn.addEventListener('click', (e) => { e.preventDefault(); showView(gameView); });

    registerForm.addEventListener('submit', handleRegister);

    // Login button event listeners (UPDATED)
    generateCodeBtn.addEventListener('click', handleGenerateCode);
    verifyCodeBtn.addEventListener('click', handleVerifyCode);
    resendCodeBtn.addEventListener('click', handleGenerateCode); // Resend uses the same logic as generate

    // Allow pressing Enter in login-email to trigger generateCode
    loginEmailInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            generateCodeBtn.click(); // Simulate click on generate code button
        }
    });

    // Allow pressing Enter in login-code to trigger verifyCode
    loginCodeInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            verifyCodeBtn.click(); // Simulate click on verify code button
        }
    });


    startGameBtn.addEventListener('click', startGame);
    submitAnswerBtn.addEventListener('click', submitAnswer);
    nextProblemBtn.addEventListener('click', nextProblem);
    endGameBtn.addEventListener('click', endGame);

    answerInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !submitAnswerBtn.classList.contains('hidden')) {
            e.preventDefault();
            submitAnswer();
        } else if (e.key === 'Enter' && !nextProblemBtn.classList.contains('hidden')) {
            e.preventDefault();
            nextProblem();
        }
    });
});
