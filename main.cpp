#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <cstdlib>
#include <regex>
#include <set>
using namespace std;

// Class for common methods that will be needed by both operator and user classes
/*
README - Account Class Documentation

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

class Account {
protected:
    string email;
    string parola;

public:
    // Function for Vigenere encryption
    string vigenereEncrypt(const string &text, const string &key) {
        string encryptedText;
        int keyIndex = 0;

        for (char c: text) {
            if (isalpha(c)) {
                char base = islower(c) ? 'a' : 'A';
                encryptedText += (c - base + key[keyIndex % key.length()] - base) % 26 + base;
                keyIndex++;
            } else {
                encryptedText += c;
            }
        }

        return encryptedText;
    }

    string genereazaCheie() {
        srand(time(0));
        string cheie = "";
        for (int i = 0; i < 8; i++) { // The key is 8 characters long
            cheie += char('A' + rand() % 26);
        }
        return cheie;
    }

    // Function to check password security
    string verificaSecuritateParola(const string &parola) {
        try {
            // First, check the length of the password
            if (parola.length() < 6) {
                throw runtime_error("Password is too short!");
            }

            bool contineLitereMici = false, contineLitereMari = false, contineNumar = false, contineSpecial = false;

            // Then check if the password contains lowercase letters, uppercase letters, numbers, and special characters
            for (char c: parola) {
                if (islower(c)) contineLitereMici = true;
                if (isupper(c)) contineLitereMari = true;
                if (isdigit(c)) contineNumar = true;
                if (ispunct(c)) contineSpecial = true;
            }

            if (parola.length() > 10 && contineLitereMici && contineLitereMari && contineSpecial && contineNumar) {
                return "Good"; // The password is strong
            } else {
                return "Ok"; // The password is acceptable
            }
        } catch (const runtime_error &e) {
            cerr << "Error: " << e.what() << endl;
            return "Weak";  // The password is weak
        }
    }

    bool esteEmailValid(const string &email) {
        if (email.empty()) {
            return false; // An empty email is not valid
        }

        size_t pozitiaLaEticheta = email.find('@'); // Use size_t to store the position (using find) of the '@' character to ensure it exists in the email

        if (pozitiaLaEticheta == string::npos || pozitiaLaEticheta == 0 || pozitiaLaEticheta == email.length() - 1) {
            return false; // Using npos, check if the '@' character exists in the substring
        }

        // Check if there is a single dot after '@'
        size_t pozitiaLaPunct = email.find('.', pozitiaLaEticheta);

        // Check if the dot exists and is not immediately after '@'
        if (pozitiaLaPunct == string::npos || pozitiaLaPunct <= pozitiaLaEticheta + 1) {
            return false;
        }

        // If the dot exists, it should not be at the end of the email
        if (pozitiaLaPunct == email.length() - 1) {
            return false;
        }

        return true;
    }


    // Common function for authentication verification
    bool verificaAutentificare(const string &fisier, const string &email, const string &parola) {
        try {
            // First, check the password security
            string calitateParola = verificaSecuritateParola(parola);
            ifstream file(fisier);
            if (!file.is_open()) {
                throw runtime_error("Error: Cannot open file " + fisier + "!"); // Exception if the file cannot be opened
            }

            string linie, emailFisier, parolaCriptata, cheie;
            getline(file, linie); // For header

            while (emailFisier != email && getline(file, linie)) {
                stringstream ss(linie);
                getline(ss, emailFisier, ',');
                getline(ss, parolaCriptata, ',');
                getline(ss, cheie, ',');
            }

            if (emailFisier != email) {
                throw runtime_error("Email does not exist in the file!"); // Exception if the email does not exist in the file
            }

            // Decrypt the stored password
            string parolaScrisaCriptata = vigenereEncrypt(parola, cheie);

            // Check if the passwords match
            if (parolaScrisaCriptata == parolaCriptata) {
                return true;
            } else {
                throw runtime_error("Password does not match!");
            }

        } catch (const runtime_error &e) { // Exception if an error occurs, by reference to avoid copying
            cerr << "Error: " << e.what() << endl; // Use e.what() to display the error message
            return false;
        }
    }
};
