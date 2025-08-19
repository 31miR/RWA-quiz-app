import { logoutAndRedirectToLanding } from "./util/backendHelperFuncs.js";

document.addEventListener('DOMContentLoaded', () => {
    const addUserBtn = document.getElementById('add-new-user-btn');
    if (addUserBtn) {
        addUserBtn.classList.add('add-user-button');
        addUserBtn.classList.add('header-button');
        addUserBtn.style.background = '';
        addUserBtn.addEventListener('click', () => {
            window.location.href = 'addNewUser.html';
        });
    }
    const headerCenter = document.querySelector('.header-center');


    function createNavButton(label, target) {
        const form = document.createElement('form');
        form.action = target;
        form.method = 'GET';
        const button = document.createElement('button');
        button.type = 'submit';
        button.className = 'mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect';
        button.textContent = label;
        form.appendChild(button);
        return form;
    }

    headerCenter.appendChild(createNavButton('HOME', '../landingPage.html'));

    headerCenter.appendChild(createNavButton('QUIZZES', '../admin/editorDashboard.html'));

    headerCenter.appendChild(createNavButton('USERS', '../superadmin/manageUsers.html'));

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => logoutAndRedirectToLanding());
    }

    const rowsPerPage = 10;
    let currentPage = 1;
    let allUsers = [];

    function fetchAndPopulateTable() {
        $.ajax({
            url: '/kviz/api/superadmin/admin?offset=0&limit=1000',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                allUsers = data;
                displayTablePage(currentPage);
            },
            error: function(xhr, status, error) {
                console.error('Error fetching data:', status, error);
                allUsers = [];
                displayTablePage(currentPage);
            }
        });
    }

    function displayTablePage(page) {
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        const tableBody = $('#user-table tbody');
        tableBody.empty();

        const usersToShow = allUsers.slice(start, end);
        usersToShow.forEach(user => {
            let row = $('<tr></tr>');
            row.append('<td>' + user.id + '</td>');
            row.append('<td>' + user.username + '</td>');
            row.append('<td>' + user.fullName + '</td>');
            row.append('<td>' + (user.isSuperAdmin ? "super admin" : "editor") + '</td>');
            row.append('<td><button class="action-button edit-button" data-id="' + user.id + '">Edit</button> <button class="action-button delete-button" data-id="' + user.id + '">Delete</button></td>');
            tableBody.append(row);
        });

        $('.delete-button').click(function() {
            let userId = $(this).data('id');
            deleteUser(userId);
        });

        $('.edit-button').click(function() {
            let userId = $(this).data('id');
            window.location.href = 'addNewUser.html?userId=' + userId;
        });

        createPagination(allUsers.length);
    }

    function createPagination(totalRows) {
        const pageCount = Math.ceil(Math.max(totalRows, 1) / rowsPerPage);
        const paginationContainer = $('#pagination');
        paginationContainer.empty();

        for (let i = 1; i <= pageCount; i++) {
            const btn = $('<button></button>').text(i);
            btn.addClass('mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect');

            if (i === currentPage) btn.addClass('active');

            btn.click(() => {
                currentPage = i;
                displayTablePage(currentPage);
            });

            paginationContainer.append(btn);
        }

        componentHandler.upgradeDom();
    }

    function deleteUser(userId) {
        $.ajax({
            url: `/kviz/api/superadmin/admin?id=${userId}`,
            type: 'DELETE',
            contentType: 'application/json',
            data: JSON.stringify({id: userId}),
            success: function(response, status, xhr) {
                if (xhr.status === 200) {
                    alert(response.message || 'User deleted successfully');
                    fetchAndPopulateTable();
                } else {
                    alert('Failed to delete user: ' + (response.message || 'Unknown error'));
                }
            },
            error: function(xhr, status, error) {
                console.error('Error deleting user:', status, error);
                alert('An error occurred while deleting the user.');
            }
        });
    }

    fetchAndPopulateTable();
});

