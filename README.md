# Secure Notes App
Secure Notes App with 2FA, allowing user to write, publish and encrypt notes

## Main Functionality
- 2FA with TOTP
- Notes publication
- Notes encryption
- Notes creation
- Notes sanitization
- Password strength checker
- HTTPS

## Main Dependencies
- zxing _(qr code generation)_
- zxcvbn _(password entropy check)_
- httpclient _(easier url building)_
- bouncycastle _(argon2)_
- owasp java html sanitizer
- spring data jpa
- spring security
- thymeleaf

## Examples

### Login Page
![login](img/login.png)
### Signup Page
![signup](img/signup.png)
### Totp Qr Code
![totp qr code](img/totp.png)
### Notes List
![notes list](img/notes-list.png)
### New Note
![new note](img/new-note.png)
### Note Example #1
![note #1](img/note-1.png)
### Note Example #2
![note #2](img/note-2.png)
### Note Example #3
![note #3](img/note-4.png)

## Features WIP
- Email verification for registration
- CSRF
- Password Change
- Localization
- Tests

