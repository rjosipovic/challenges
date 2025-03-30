const USER_EXISTS_CODE = 'NE-002';
const USER_EXISTS_MSG = 'User already exists';
const UNABLE_TO_RESISTER_MSG = 'Unable to register user. Please try again later.';
const LOGIN = 'login.html';

async function register() {
    const email = document.getElementById('email').value;

    if (!email) {
        alert('Please enter an email address.');
        return;
    }

    const alias = document.getElementById('alias').value;

    if (!alias) {
        alert('Please enter an alias.');
        return;
    }

    const birthdate = document.getElementById('birthdate').value  ?? null;
    const genderCheckbox = document.getElementById('male');
    const gender = genderCheckbox.checked ? genderCheckbox.value : null;
    const userData = {
        alias: alias,
        email: email,
        birthdate: birthdate,
        gender: gender
    };
    console.log("About to register user:", userData);

    const apiUrl = `http://localhost:8081/users`;

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            const data = await response.json();
            console.log('Registration successful:', data);
            window.location.href = LOGIN;
        } else {
            const errorData = await response.json();
            const code = errorData.error.code;
            if (code === USER_EXISTS_CODE) {
                alert(USER_EXISTS_MSG);
                
            } else {
                alert(UNABLE_TO_RESISTER_MSG);
            }
            console.error('Registration failed:', errorData);
        }
    } catch (error) {
        alert(UNABLE_TO_RESISTER_MSG);
        console.error('There was an error!', error);
    }
}