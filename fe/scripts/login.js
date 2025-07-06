const AUTH_API = 'http://localhost:8081/auth';

document.getElementById('email-input').style.display = 'block';
document.getElementById('code-input').style.display = 'none';


async function getCode() {
    const email = document.getElementById('email').value;

    if (email === "") {
        alert('Please enter an email address.');
        return;
    }

    const codeGenerationRequest = {
        "email": email
    };

    const apiUrl = AUTH_API + '/request-code';

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(codeGenerationRequest)
        });

        if (response.ok) {
            alert('Code generated successfully. Please check your email.');
            // Toggle visibility of input fields
            document.getElementById('email-input').style.display = 'none';
            document.getElementById('code-input').style.display = 'block';
        } else if (response.status === 404) {
            window.location.href = "register.html";
        } else {
            const errorData = await response.json();
            alert(ERROR_MSG);
            console.error('Error generating code:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('Error generating code:', error);
    }
}

async function login() {
    const email = document.getElementById('email').value;
    const code = document.getElementById('code').value;

    if (email === "") {
        alert('Something went wrong. Please try again.');
        return;
    }


    if (code === "") {
        alert('Please enter code to login.');
        return;
    }

    const codeVerificationRequest = {
        "email": email,
        "code": code
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
            console.log('Token:', data);
            localStorage.setItem('token', data.token);
            setUser();
            alert('Login successful.');
            window.location.href = "challenges.html";
        } else if (response.status === 404) {
            const errorData = await response.json();
            const code = errorData.error.code;
            if (code === USER_NOT_FOUND) {
                alert('Please register first.');
                window.location.href = "register.html";
            } else {
                alert(ERROR_MSG);
                console.error('Login failed:', errorData);
            }
        } else {
            const errorData = await response.json();
            alert(ERROR_MSG);
            console.error('Login failed:', errorData);
        }
    } catch (error) {
        alert(ERROR_MSG);
        console.error('Login failed:', error);
    }
}

function setUser() {
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
        localStorage.setItem("alias", JSON.stringify(user));
    }

}