const ERROR_MSG = "Something went wrong. Please try again.";

async function login() {
    const alias = document.getElementById('alias').value;

    if (alias === "") {
        alert('Please enter an alias.');
        return;
    }

    const apiUrl = `http://localhost:8081/users/alias/${alias}`;

    try {
        const response = await fetch(apiUrl);

        if (response.ok) {
            const data = await response.json();
            console.log('User details:', data);
            localStorage.setItem('alias', JSON.stringify(data));

        } else {
            const errorData = await response.json();
            alert(ERROR_MSG);
            console.error('Getting user details error:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('There was an error getting user details', error);
    }
    window.location.href = "challenges.html";
}