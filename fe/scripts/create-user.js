
function createUser() {
    const alias = document.getElementById("alias").value;
    const email = document.getElementById("email").value;
    const birthdate = document.getElementById("birthdate").value;
    const gender = document.querySelector('input[name="gender"]:checked').value;

    const userData = { 
        alias: alias,
        email: email,
        birthdate: birthdate,
        gender: gender
    };

    const apiUrl = `http://localhost:8081/users`;
    fetch(apiUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);
        window.location.href = "index.html";
    })
    .catch(error => console.error(error));
}