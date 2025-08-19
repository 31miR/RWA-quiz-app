import { getProfileDataById, createNewProfile, updateExistingProfile, redirectToPage } from "./util/backendHelperFuncs.js";

document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId');

    const form = document.getElementById('user-form');
    const submitButton = document.querySelector('.submit-buttons button[type="submit"]');
    const cancelButton = document.querySelector('.submit-buttons button[type="button"]');
    const errorMessageParagraph = document.getElementById('error-message');

    if (userId != null) {
        const user = await getProfileDataById(userId);
        form.querySelector('input[name="name"]').value = user.fullName || '';
        form.querySelector('input[name="username"]').value = user.username || '';
        form.querySelector('input[name="password"]').value = user.password || '';
    }

    cancelButton.addEventListener('click', function () {
        window.location.href = 'manageUsers.html';
    });

    submitButton.addEventListener('click', async (event) => {
        event.preventDefault();

        const name = form.querySelector('input[name="name"]').value.trim();
        const username = form.querySelector('input[name="username"]').value.trim();
        const password = form.querySelector('input[name="password"]').value.trim();

        if (!name || !username || !password) {
            alert('Molimo popunite sva polja prije slanja.');
            return;
        }

        const user = {
            id: userId,
            fullName: name,
            username: username,
            password: password,
        };
        
        let response = null;

        if (userId == null) {
            response = await createNewProfile(user);
        } else {
            response = await updateExistingProfile(user);
        }
        
        if (response.ok) {
            redirectToPage('/kviz/superadmin/manageUsers.html')
        } else {
            const responseBody = await response.json();
            errorMessageParagraph.innerText = responseBody.error;
        }
    });
});

