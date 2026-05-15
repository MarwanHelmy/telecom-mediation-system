// Get form elements
const loginForm = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const errorMsgDiv = document.getElementById('errorMsg');
const errorTextSpan = document.getElementById('errorText');

// Function to add red border error style
function showFieldError(inputElement) {
    inputElement.classList.add('input-error');
    inputElement.style.borderColor = '#FF6B6B';
    inputElement.style.backgroundColor = 'rgba(255, 107, 107, 0.05)';
    inputElement.style.boxShadow = '0 0 0 3px rgba(255, 107, 107, 0.15)';
}

// Function to remove red border error style
function removeFieldError(inputElement) {
    inputElement.classList.remove('input-error');
    inputElement.style.borderColor = '';
    inputElement.style.backgroundColor = '';
    inputElement.style.boxShadow = '';
}

// Function to validate required fields are not empty
function validateRequiredFields() {
    let isValid = true;

    // Validate username
    if (usernameInput.value.trim() === '') {
        showFieldError(usernameInput);
        isValid = false;
    } else {
        removeFieldError(usernameInput);
    }

    // Validate password
    if (passwordInput.value.trim() === '') {
        showFieldError(passwordInput);
        isValid = false;
    } else {
        removeFieldError(passwordInput);
    }

    return isValid;
}

// Function to show error message
function showError(message) {
    errorTextSpan.innerText = message;
    errorMsgDiv.classList.add('show');

    // Auto-hide error after 4 seconds
    setTimeout(() => {
        errorMsgDiv.classList.remove('show');
    }, 4000);
}

// Form submission handler
loginForm.addEventListener('submit', function (e) {
    e.preventDefault(); // Prevent default form submission

    // Validate required fields are not empty
    if (!validateRequiredFields()) {
        showError('Please fill in all required fields.');
        return;
    }

    // If validation passes, redirect to dashboard
    errorMsgDiv.classList.remove('show');
    
    // Redirect immediately to dashboard
    window.location.href = 'Dashboard.html';
});

// Remove error styles when user starts typing
usernameInput.addEventListener('focus', function () {
    removeFieldError(usernameInput);
    errorMsgDiv.classList.remove('show');
});

usernameInput.addEventListener('input', function () {
    removeFieldError(usernameInput);
    errorMsgDiv.classList.remove('show');
});

passwordInput.addEventListener('focus', function () {
    removeFieldError(passwordInput);
    errorMsgDiv.classList.remove('show');
});

passwordInput.addEventListener('input', function () {
    removeFieldError(passwordInput);
    errorMsgDiv.classList.remove('show');
});

// Auto-focus on username field when page loads
window.addEventListener('load', function () {
    usernameInput.focus();
});