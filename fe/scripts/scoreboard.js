const SCOREBOARD_API = 'http://localhost:8082/leaders'; // replace with your actual API URL
const USER_DETAILS_API = 'http://localhost:8081/users';

populateScoreboardTable();


async function populateScoreboardTable() {
    try {
        const response = await fetch(SCOREBOARD_API);
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
        } else {
            console.error("Error fetching scoreboard data");
        }
    } catch (error) {
        console.error("Error fetching scoreboard data", error);
    }
}

async function getUserAliasFromId(ids) {
    try {
        const response = await fetch(USER_DETAILS_API + '?ids=' + ids);
        if (response.ok) {
            const data = await response.json();
            return data.reduce((acc, user) => {
                acc[user.id] = user.alias;
                return acc;
            }, {});
        } else {
            console.error("Error fetching user details");
        }
    } catch (error) {
        console.error("Error fetching user details", error);
    }
    
}