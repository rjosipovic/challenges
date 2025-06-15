const SCOREBOARD_API = 'http://localhost:8082/leaders';
const USER_DETAILS_API = 'http://localhost:8081/users';

populateScoreboardTable();

async function populateScoreboardTable() {
    const token = localStorage.getItem("token");
    try {
        const response = await fetch(SCOREBOARD_API, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
        if (response.ok) {
            const data = await response.json();
            const ids = data.map(row => row.userId);
            const usersMap = await getUserAliasFromId(ids);

            const tableBody = document.getElementById("scoreboard-table").querySelector("tbody");
            tableBody.innerHTML = "";
            data.forEach((row, index) => {
                const rowHtml = `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${usersMap[row.userId]}</td>
                        <td>${row.totalScore}</td>
                        <td>${row.badges}</td>
                    </tr>
                `;
                tableBody.innerHTML += rowHtml;
            });
        } else if (response.status === 403) {
            alert("Access denied. Please log in to view the scoreboard.");
            window.location.href = "login.html";
        } else {
            console.error("Error fetching scoreboard data");
        }
    } catch (error) {
        console.error("Error fetching scoreboard data", error);
    }
}

async function getUserAliasFromId(ids) {
    const token = localStorage.getItem("token");
    try {
        const response = await fetch(USER_DETAILS_API + '?ids=' + ids, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
        if (response.ok) {
            const data = await response.json();
            return data.reduce((acc, user) => {
                acc[user.id] = user.alias;
                return acc;
            }, {});
        } else if (response.status === 403) {
            alert("Access denied. Please log in to view the scoreboard.");
            window.location.href = "login.html";
        } else {
            console.error("Error fetching user details");
        }
    } catch (error) {
        console.error("Error fetching user details", error);
    }
}