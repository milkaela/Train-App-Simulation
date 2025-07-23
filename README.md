# Train-App-Simulation
The `Account` class provides functionalities for managing user accounts, including email validation, password security checks, encryption, and authentication. Below is a detailed description of the class and its methods:

### Class: Account

#### Protected Members:
- `string email`: Stores the email address of the account.
- `string parola`: Stores the password of the account.

#### Public Methods:

1. **vigenereEncrypt(const string &text, const string &key)**:
    - Encrypts a given text using the Vigenère cipher with the provided key.
    - Parameters:
      - `text`: The plaintext to be encrypted.
      - `key`: The encryption key.
    - Returns:
      - The encrypted text.

2. **genereazaCheie()**:
    - Generates a random encryption key of 8 uppercase alphabetic characters.
    - Returns:
      - A randomly generated key.

3. **verificaSecuritateParola(const string &parola)**:
    - Checks the security level of a given password based on its length and composition.
    - Parameters:
      - `parola`: The password to be checked.
    - Returns:
      - `"Good"`: If the password is strong (length > 10, contains lowercase, uppercase, numbers, and special characters).
      - `"Ok"`: If the password is acceptable.
      - `"Weak"`: If the password is too short or does not meet security criteria.

4. **esteEmailValid(const string &email)**:
    - Validates the format of an email address.
    - Parameters:
      - `email`: The email address to be validated.
    - Returns:
      - `true`: If the email is valid.
      - `false`: If the email is invalid.

5. **verificaAutentificare(const string &fisier, const string &email, const string &parola)**:
    - Verifies user authentication by checking the email and password against stored data in a file.
    - Parameters:
      - `fisier`: The file path containing user data.
      - `email`: The email address to authenticate.
      - `parola`: The password to authenticate.
    - Returns:
      - `true`: If authentication is successful.
      - `false`: If authentication fails.
    - Exceptions:
      - Throws runtime errors for issues such as file access failure, email not found, or password mismatch.

### Usage Notes:
- Ensure the file specified in `verificaAutentificare` exists and follows the expected format (CSV with email, encrypted password, and key).
- Use `genereazaCheie` to create encryption keys for storing passwords securely.
- Validate email addresses using `esteEmailValid` before storing or processing them.
- Check password security using `verificaSecuritateParola` to encourage strong passwords.

### Example Workflow:
1. Generate a random key using `genereazaCheie`.
2. Encrypt a password using `vigenereEncrypt`.
3. Validate the email format using `esteEmailValid`.
4. Store the email, encrypted password, and key in a file.
5. Authenticate users using `verificaAutentificare`.

This class is designed to enhance account security and streamline authentication processes.
*/
