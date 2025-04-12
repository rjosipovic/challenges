const ERROR_MSG = "Something went wrong. Please try again.";
const USER_DETAILS_API = 'http://localhost:8081/users';
const USER_NOT_FOUND = 'NE-001';

async function login() {
    const alias = document.getElementById('alias').value;

    if (alias === "") {
        alert('Please enter an alias.');
        return;
    }

    const apiUrl = USER_DETAILS_API + '/alias/' + alias;

    try {
        const response = await fetch(apiUrl);

        if (response.ok) {
            const data = await response.json();
            console.log('User details:', data);
            localStorage.setItem('alias', JSON.stringify(data));
            window.location.href = "challenges.html";
        } else if (response.status === 404) {
            const errorData = await response.json();
            const code = errorData.error.code;
            if (code === USER_NOT_FOUND) {
                alert('Please register first.');
                window.location.href = "register.html";
            } else {
                alert(ERROR_MSG);
                console.error('Error getting user details:', errorData);
            }
        } else {
            const errorData = await response.json();
            alert(ERROR_MSG);
            console.error('Error getting user details:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('Error getting user details:', error);
    }
}