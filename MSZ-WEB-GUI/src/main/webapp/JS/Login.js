// ===============================
// DOM ELEMENTS
// ===============================

const loginForm =
        document.getElementById('loginForm');

const usernameInput =
        document.getElementById('username');

const passwordInput =
        document.getElementById('password');

const errorMsgDiv =
        document.getElementById('errorMsg');

const errorTextSpan =
        document.getElementById('errorText');


// ===============================
// GET CONTEXT PATH
// ===============================

function getContextPath()
{
    const path = window.location.pathname;

    return path.substring(
            0,
            path.indexOf('/', 1)
            );
}


// ===============================
// SHOW FIELD ERROR
// ===============================

function showFieldError(inputElement)
{
    inputElement.classList.add('input-error');

    inputElement.style.borderColor = '#FF6B6B';

    inputElement.style.backgroundColor =
            'rgba(255, 107, 107, 0.05)';

    inputElement.style.boxShadow =
            '0 0 0 3px rgba(255, 107, 107, 0.15)';
}


// ===============================
// REMOVE FIELD ERROR
// ===============================

function removeFieldError(inputElement)
{
    inputElement.classList.remove('input-error');

    inputElement.style.borderColor = '';

    inputElement.style.backgroundColor = '';

    inputElement.style.boxShadow = '';
}


// ===============================
// VALIDATE FIELDS
// ===============================

function validateRequiredFields()
{
    let isValid = true;

    // USERNAME
    if (usernameInput.value.trim() === '')
    {
        showFieldError(usernameInput);

        isValid = false;
    }
    else
    {
        removeFieldError(usernameInput);
    }

    // PASSWORD
    if (passwordInput.value.trim() === '')
    {
        showFieldError(passwordInput);

        isValid = false;
    }
    else
    {
        removeFieldError(passwordInput);
    }

    return isValid;
}


// ===============================
// SHOW ERROR
// ===============================

function showError(message)
{
    errorTextSpan.innerText = message;

    errorMsgDiv.classList.add('show');

    setTimeout(() =>
    {
        errorMsgDiv.classList.remove('show');

    }, 4000);
}


// ===============================
// LOGIN
// ===============================

loginForm.addEventListener('submit', function (e)
{
    e.preventDefault();

    // VALIDATION
    if (!validateRequiredFields())
    {
        showError(
                'Please fill in all required fields.'
                );

        return;
    }

    // CONTEXT PATH
    const contextPath = getContextPath();

    // SERVLET URL
    const url =
            contextPath + '/LoginServlet';

    console.log("LOGIN URL : ", url);

    // FETCH
    fetch(url,
            {
                method: 'POST',

                headers:
                        {
                            'Content-Type':
                                    'application/x-www-form-urlencoded'
                        },

                body:
                        'username='
                        + encodeURIComponent(usernameInput.value)
                        + '&password='
                        + encodeURIComponent(passwordInput.value)
            })

            .then(response =>
            {
                if (!response.ok)
                {
                    throw new Error(
                            'HTTP ERROR : '
                            + response.status
                            );
                }

                return response.json();
            })

            .then(data =>
            {
                console.log(data);

                // SUCCESS LOGIN
                if (data.success)
                {
                    // SAVE USERNAME
                    sessionStorage.setItem(
                            'username',
                            usernameInput.value
                            );

                    // REDIRECT
                    window.location.href =
                            'Dashboard.html';
                }
                // INVALID LOGIN
                else
                {
                    showError(data.message);

                    showFieldError(usernameInput);

                    showFieldError(passwordInput);
                }
            })

            .catch(error =>
            {
                console.error(error);

                showError(
                        'Server Connection Error'
                        );
            });
});


// ===============================
// REMOVE ERROR ON INPUT
// ===============================

usernameInput.addEventListener('input', function ()
{
    removeFieldError(usernameInput);

    errorMsgDiv.classList.remove('show');
});

passwordInput.addEventListener('input', function ()
{
    removeFieldError(passwordInput);

    errorMsgDiv.classList.remove('show');
});


// ===============================
// AUTO FOCUS
// ===============================

window.addEventListener('load', function ()
{
    usernameInput.focus();
});