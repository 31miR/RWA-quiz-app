$(document).ready(function () {
    const rowsPerPage = 10;
    let currentPage = 1;
    let allUsers = [];

    $('.add-user-button').click(function () {
        window.location.href = 'addNewUser';
    });

    function fetchAndPopulateTable() {
        $.ajax({
            url: '/kviz/api/superadmin/admin?offset=0&limit=1000', 
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                allUsers = data;
                displayTablePage(currentPage);
            },
            error: function (xhr, status, error) {
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
        usersToShow.forEach(function (user) {
            var row = $('<tr></tr>');
            row.append('<td>' + user.id + '</td>');
            row.append('<td>' + user.username + '</td>');
            row.append('<td>' + user.password + '</td>');
            row.append('<td>' + user.role + '</td>');
            row.append('<td><button class="action-button edit-button" data-id="' + user.id + '">Edit</button> <button class="action-button delete-button" data-id="' + user.id + '">Delete</button></td>');
            tableBody.append(row);
        });

        // dodaj event za edit i delete dugmad
        $('.delete-button').click(function () {
            var userId = $(this).data('id');
            deleteUser(userId);
        });

        $('.edit-button').click(function () {
            var userId = $(this).data('id');
            window.location.href = 'addNewUser.html?userId=' + userId;
        });

        createPagination(allUsers.length);
    }

    function createPagination(totalRows) {
        const pageCount = Math.ceil(Math.max(totalRows, 1) / rowsPerPage); // minimum 1 stranica
        const paginationContainer = $('#pagination');
        paginationContainer.empty();

        for (let i = 1; i <= pageCount; i++) {
            const btn = $('<button></button>').text(i);
            btn.addClass('mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect');

            if (i === currentPage) btn.addClass('active');

            btn.click(function () {
                currentPage = i;
                displayTablePage(currentPage);
            });

            paginationContainer.append(btn);
        }

        // MDL inicijalizacija dugmadi
        componentHandler.upgradeDom();
    }

    function deleteUser(userId) {
        $.ajax({
            url: `/kviz/api/superadmin/admin?id=${userId}`,
            type: 'DELETE',
            contentType: 'application/json',
            data: JSON.stringify({id: userId}),
            success: function (response, status, xhr) {
                if (xhr.status === 200) {
                    alert(response.message || 'User deleted successfully');
                    fetchAndPopulateTable();
                } else {
                    alert('Failed to delete user: ' + (response.message || 'Unknown error'));
                }
            },
            error: function (xhr, status, error) {
                console.error('Error deleting user:', status, error);
                alert('An error occurred while deleting the user.');
            }
        });
    }

    fetchAndPopulateTable();
});

