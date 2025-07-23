import java.time.LocalDate
import kotlin.random.Random
import java.io.File
import java.io.IOException

open class Account {
    protected var email: String = ""
    protected var parola: String = ""
    
    fun vigenereEncrypt(text: String, key: String): String {
        val encryptedText = StringBuilder()
        var keyIndex = 0

        for (c in text) {
            if (c.isLetter()) {
                val base = if (c.isLowerCase()) 'a' else 'A'
                val encryptedChar = ((c - base + (key[keyIndex % key.length] - base)) % 26 + base.code).toChar()
                encryptedText.append(encryptedChar)
                keyIndex++
            } else {
                encryptedText.append(c)
            }
        }
        println("Debug: Text original: $text, Cheie: $key, Text criptat: $encryptedText")
        return encryptedText.toString()
    }

   open fun genereazaCheie(): String {
        return (1..8).map { 'A' + Random.nextInt(26) }.joinToString("")
    }
    fun verificaSecuritateParola(parola: String): String {
        return try {
            if (parola.length < 6) {
                throw IllegalArgumentException("Parola este prea scurta!")
            }
            val contineLitereMici = parola.any { it.isLowerCase() }
            val contineLitereMari = parola.any { it.isUpperCase() }
            val contineNumar = parola.any { it.isDigit() }
            val contineSpecial = parola.any { !it.isLetterOrDigit() }

            when {
                parola.length > 10 && contineLitereMici && contineLitereMari && contineSpecial && contineNumar -> "Good"
                else -> "Ok"
            }
        } catch (e: Exception) {
            println("Eroare: ${e.message}")
            "Weak"
        }
    }

    fun esteEmailValid(email: String): Boolean {
        if (email.isEmpty()) return false

        val pozitiaLaEticheta = email.indexOf('@')
        if (pozitiaLaEticheta <= 0 || pozitiaLaEticheta == email.length - 1) return false

        val pozitiaLaPunct = email.indexOf('.', pozitiaLaEticheta)
        return !(pozitiaLaPunct <= pozitiaLaEticheta + 1 || pozitiaLaPunct == email.length - 1 || pozitiaLaPunct == -1)
    }

    fun verificaAutentificare(fisier: String, email: String, parola: String): Boolean {
        return try {
            val file = File(fisier)
            if (!file.exists()) throw IOException("Fișierul nu există!")

            // Citim fiecare linie din fișier
            for (linie in file.readLines()) {
                val (emailFisier, parolaCriptata, cheie) = linie.split(",")

                // Compar email-ul
                if (emailFisier == email) {
                    // Criptam parola introdusa
                    val parolaIntroducereCriptata = vigenereEncrypt(parola, cheie.trim())

                    // Compar parola criptata din fisier cu parola introdusa criptata
                    return parolaIntroducereCriptata == parolaCriptata.trim()
                }
            }
            throw IllegalArgumentException("Email-ul nu există în fișier!")
        } catch (e: Exception) {
            println("Eroare: ${e.message}")
            false
        }
    }
}

class Operator(email: String, parola: String) : Account() {
    private var cheie: String = genereazaCheie()
    private var autentificat: Boolean = false

    init {
        val parolaEncriptata = vigenereEncrypt(parola, cheie)
        salveazaOperatorInFisier(email, parolaEncriptata, cheie)
    }
    constructor() : this("", "")

    private fun salveazaOperatorInFisier(email: String, parolaEncriptata: String, cheie: String) {
        try {
            if (!esteEmailValid(email)) {
                throw RuntimeException("Eroare: Email-ul nu este valid!")
            }

            val calitateParola = verificaSecuritateParola(parolaEncriptata)
            when (calitateParola) {
                "Weak" -> {
                    println("Eroare: Parola este prea slaba pentru a salva operatorul!")
                    return
                }
                "Ok" -> println("Parola este acceptabila")
                "Good" -> println("Parola este puternica!")
            }

            val existaDeja = File("operatori.csv").readLines().any {
                it.split(",").first() == email
            }

            if (!existaDeja) {
                File("operatori.csv").appendText("$email,$parolaEncriptata,$cheie\n")
                println("Operatorul a fost salvat cu succes in fisier!")
            } else {
                println("Operatorul exista deja in fisier.")
            }
        } catch (e: RuntimeException) {
            println("A fost aruncata o eroare: ${e.message}")
        }
    }

    fun loginOperator(): Boolean {
        return try {
            print("Email: ")
            val emailInput = readLine() ?: ""
            print("Parola: ")
            val parolaInput = readLine() ?: ""

            val autentificareReusita = verificaAutentificare("operatori.csv", emailInput, parolaInput)
            autentificat = autentificareReusita

            if (autentificareReusita) {
                println("Autentificare reusita!")
                true
            } else {
                throw IllegalArgumentException("Eroare: Autentificare esuata! Datele introduse sunt incorecte.")
            }
        } catch (e: IllegalArgumentException) {
            println(e.message)
            autentificat = false
            false
        } catch (e: Exception) {
            println("Eroare necunoscuta: ${e.message}")
            autentificat = false
            false
        }
    }

    fun esteAutentificat() = autentificat

    fun creazaOperatorNou() {
        print("Introduceti emailul operatorului: ")
        val email = readLine() ?: ""
        print("Introduceti parola operatorului: ")
        val parola = readLine() ?: ""
        Operator(email, parola)
    }

    fun autentificaOperator() {
        loginOperator()
    }

    private fun dataCurenta(data: String): Boolean {
        if (data.length != 10 || data[4] != '-' || data[7] != '-') {
            throw RuntimeException("Data trebuie sa fie in formatul YYYY-MM-DD!")
        }

        return try {
            val dataIntroducere = LocalDate.parse(data)
            val dataCurenta = LocalDate.now()
            dataIntroducere < dataCurenta
        } catch (e: Exception) {
            throw RuntimeException("Format de dată invalid!")
        }
    }

    fun adaugaCursa() {
        if (!esteAutentificat()) {
            println("Nu sunteti autentificat ca operator!")
            return
        }

        try {
            print("Introduceti ID-ul cursei: ")
            val id = readLine() ?: ""
            print("Introduceti locatia de plecare: ")
            val loc_plecare = readLine() ?: ""
            print("Introduceti destinatia: ")
            val destinatie = readLine() ?: ""
            print("Introduceti data plecarii (YYYY-MM-DD): ")
            val plecare = readLine() ?: ""

            if (dataCurenta(plecare)) {
                throw RuntimeException("Data introdusa este in trecut! Nu se poate adauga cursa.")
            }

            if (!loc_plecare.all { it.isLetter() || it.isWhitespace() }) {
                throw RuntimeException("Locatia de plecare nu poate contine caractere nepermise!")
            }

            if (!destinatie.all { it.isLetter() || it.isWhitespace() }) {
                throw RuntimeException("Destinatia nu poate contine caractere nepermise!")
            }

            print("Introduceti ora plecarii: ")
            val ora_plecare = readLine() ?: ""
            print("Introduceti pretul: ")
            val pret = readLine()?.toDoubleOrNull() ?: 0.0

            File("curse.csv").appendText("$id,$loc_plecare,$destinatie,$plecare,$ora_plecare,$pret\n")
            println("Cursa a fost adaugata cu succes!")

        } catch (e: RuntimeException) {
            println("Eroare: ${e.message}")
        } catch (e: Exception) {
            println("A aparut o eroare necunoscuta!")
        }
    }

    fun stergeCursa() {
        if (!esteAutentificat()) {
            println("Nu sunteti autentificat ca operator!")
            return
        }

        print("Introduceti ID-ul cursei de sters: ")
        val id = readLine() ?: ""

        val linii = File("curse.csv").readLines().filter {
            !it.startsWith(id)
        }

        val gasit = linii.size < File("curse.csv").readLines().size

        if (gasit) {
            File("curse.csv").writeText(linii.joinToString("\n"))
            println("Cursa a fost stearsa cu succes!")
        } else {
            println("Cursa cu ID-ul $id nu a fost gasita!")
        }
    }
}

class Utilizator(private val nume: String = "", private val prenume: String = "",  email: String = "", parola: String = "") : Account() {
    private val cheie: String = genereazaCheie()

    init {
        if (email.isNotEmpty() && parola.isNotEmpty()) {
            val parolaEncriptata = vigenereEncrypt(parola, cheie)
            salveazaUtilizatorInFisier(nume, prenume, email, parolaEncriptata, cheie)
        }
    }

    override fun genereazaCheie(): String {
        return (1..8)
            .map { ('A'.code + Random.nextInt(26)).toChar() }
            .joinToString("")
    }

    private fun salveazaUtilizatorInFisier(
        nume: String,
        prenume: String,
        email: String,
        parolaEncriptata: String,
        cheie: String
    ) {
        val emailTrim = email.trim()

        if (!esteEmailValid(emailTrim)) {
            throw RuntimeException("Eroare: Email-ul nu este valid!")
        }

        val utilizatori = File("utilizatori.csv").readLines()
        if (utilizatori.any { it.startsWith("$emailTrim,") }) {
            throw RuntimeException("Eroare: Utilizatorul cu acest email exista deja in fisier!")
        }

        val calitateParola = verificaSecuritateParola(parolaEncriptata)
        when (calitateParola) {
            "Weak" -> throw RuntimeException("Eroare: Parola este prea slaba pentru a salva utilizatorul!")
            "Ok" -> println("Parola este acceptabila.")
            "Good" -> println("Parola este puternica!")
        }

        try {
            File("utilizatori.csv").appendText(
                "$emailTrim,$parolaEncriptata,$cheie,$nume,$prenume\n"
            )
            println("Utilizatorul a fost salvat cu succes in fisier!")
        } catch (e: IOException) {
            throw RuntimeException("Eroare: Nu se poate deschide fisierul utilizatori.csv pentru scriere!")
        }
    }

    fun loginUtilizator(): Boolean {
        return try {
            print("Introduceti email-ul: ")
            val emailInput = readLine() ?: ""
            print("Introduceti parola: ")
            val parolaInput = readLine() ?: ""

            verificaAutentificare("utilizatori.csv", emailInput, parolaInput)
        } catch (e: Exception) {
            println("Eroare la autentificare: ${e.message}")
            false
        }
    }

    fun creeazaContUtilizator() {
        try {
            print("Introduceti numele: ")
            val nume = readLine() ?: ""
            print("Introduceti prenumele: ")
            val prenume = readLine() ?: ""
            print("Introduceti email-ul: ")
            val email = readLine() ?: ""
            print("Introduceti parola: ")
            val parola = readLine() ?: ""

            require(email.contains("@") && email.contains(".")) {
                "Email-ul trebuie sa contina @ si ."
            }

            Utilizator(nume, prenume, email, parola)
        } catch (e: Exception) {
            println("Eroare: ${e.message}")
        }
    }

    fun autentificaUtilizator() {
        val utilizator = Utilizator()
        if (utilizator.loginUtilizator()) {
            println("Autentificare reusita!")
        } else {
            println("Autentificare esuata!")
        }
    }

    fun cautaCursa() {
        try {
            print("Introduceti locatia de plecare: ")
            val loc_plecare = readLine() ?: ""
            print("Introduceti destinatia: ")
            val destinatie = readLine() ?: ""
            print("Introduceti data plecarii (YYYY-MM-DD): ")
            val plecare = readLine() ?: ""

            val curse = File("curse.csv").readLines()
            val curseGasite = curse.filter { linie ->
                val (_, loc_plecareFisier, destinatieFisier, plecareFisier) = linie.split(",")
                loc_plecareFisier == loc_plecare &&
                        destinatieFisier == destinatie &&
                        plecareFisier == plecare
            }

            if (curseGasite.isEmpty()) {
                throw RuntimeException("Cursa nu a fost gasita!")
            }
            println("Curse gasite:")
            curseGasite.forEachIndexed { index, linie ->
                val parts = linie.split(",")
                val id = parts[0]
                val loc_plecareFisier = parts[1]
                val destinatieFisier = parts[2]
                val plecareFisier = parts[3]
                val ora_plecare = parts[4]
                val pret = parts[5]

                println("Cursa ${index + 1}:")
                println("ID: $id")
                println("Locatie de plecare: $loc_plecareFisier")
                println("Destinatie: $destinatieFisier")
                println("Data plecarii: $plecareFisier")
                println("Ora plecarii: $ora_plecare")
                println("Pret: $pret")
                println("----------------------")
            }

        } catch (e: Exception) {
            println("Eroare: ${e.message}")
        }
    }

    fun rezervareLoc() {
        try {
            print("Introduceti ID-ul cursei (3 cifre): ")
            val idCursa = readLine() ?: ""

            val curse = File("curse.csv").readLines()
            val cursaGasita = curse.find { it.startsWith("$idCursa,") }
                ?: throw RuntimeException("Cursa nu a fost gasita!")

            print("Introduceti clasa (Clasa I/Clasa a II-a, introduceti I sau II): ")
            val clasaAlesa = readLine() ?: ""
            require(clasaAlesa in listOf("I", "II")) { "Clasa invalida!" }

            print("Introduceti identificatorul utilizatorului (nume/email/ID): ")
            val utilizator = readLine() ?: ""

            val locuriFile = File("locuri.csv")
            val locuriRezervate = mutableMapOf<Pair<Int, String>, MutableSet<Int>>()

            if (locuriFile.exists()) {
                locuriFile.readLines().forEach { linie ->
                    val parts = linie.split(",")
                    if (parts.size >= 5) {
                        val idCursaFisier = parts[0]
                        val idVagon = parts[1]
                        val clasaFisier = parts[2]
                        val locuriOcupate = parts[3]
                        val utilizatorFisier = parts[4]

                        if (idCursaFisier == idCursa && clasaFisier == clasaAlesa) {
                            locuriOcupate.split(";").forEach { loc ->
                                try {
                                    val locNumar = loc.toInt()
                                    locuriRezervate
                                        .getOrPut(Pair(idVagon.toInt(), clasaFisier)) { mutableSetOf() }
                                        .add(locNumar)
                                } catch (e: NumberFormatException) {
                                    println("Eroare la conversia locului $loc: ${e.message}")
                                }
                            }
                        }
                    } else {
                        println("Linie invalidă în locuri.csv: $linie")
                    }
                }
            }

            println("Locuri disponibile pentru Clasa $clasaAlesa:")
            for (vagon in 1..3) {
                print("Vagon $vagon: ")
                (1..200).filter { loc ->
                    !locuriRezervate.getOrDefault(Pair(vagon, clasaAlesa), mutableSetOf()).contains(loc)
                }.forEach { print("$it ") }
                println()
            }

            print("Introduceti numarul vagonului (1-3): ")
            val vagonAles = readLine()?.toIntOrNull()
                ?: throw IllegalArgumentException("Vagon invalid!")
            require(vagonAles in 1..3) { "Vagon invalid!" }

            print("Introduceti numarul locului (1-200): ")
            val locAles = readLine()?.toIntOrNull()
                ?: throw IllegalArgumentException("Loc invalid!")

            require(locAles in 1..200) { "Loc invalid!" }

            val locuriVagon = locuriRezervate.getOrDefault(Pair(vagonAles, clasaAlesa), mutableSetOf())
            require(!locuriVagon.contains(locAles)) { "Loc deja ocupat!" }

            // Add reservation to file
            locuriFile.appendText("$idCursa,$vagonAles,$clasaAlesa,$locAles;$utilizator\n")

            println("Rezervarea a fost efectuata cu succes!")
            println("Vagon: $vagonAles, Loc: $locAles, Clasa: $clasaAlesa, Utilizator: $utilizator")

        } catch (e: Exception) {
            println("Eroare: ${e.message}")
        }
    }
}

fun main() {
    val operator = Operator()
    val utilizator = Utilizator()
    var optiune: Int = -1

    do {
        println("=== Bine ati venit in aplicatia CFR! ===")
        println("Alegeti: 1. Operator 2. Utilizator (Introduceti 1 sau 2):")
        var raspuns: Int

        val input = readlnOrNull()  // Citim ceea ce scrie utilizatorul

        if (input != null && input.toIntOrNull() != null) {
            raspuns = input.toInt()  // pt raspuns valid
        } else {
            raspuns = -1  // daca numarul introdus nu este valid, raspuns va fi -1
        }


        if (raspuns == 1) {
            println("Operator")
            println("1. Creeaza operator nou")
            println("2. Autentifica operator")
            println("3. Adauga o cursa noua")
            println("4. Sterge o cursa")
            println("0. Iesire")
        } else if (raspuns == 2) {
            println("Utilizator")
            println("5. Creeaza cont utilizator")
            println("6. Autentifica utilizator")
            println("7. Cauta cursa")
            println("8. Rezerva loc")
        } else {
            println("Optiune invalida")
            continue
        }
        optiune = readlnOrNull()?.toIntOrNull() ?: -1

        when (optiune) {
            1 -> operator.creazaOperatorNou()
            2 -> operator.autentificaOperator()
            3 -> operator.adaugaCursa()
            4 -> operator.stergeCursa()
            5 -> utilizator.creeazaContUtilizator()
            6 -> utilizator.autentificaUtilizator()
            7 -> utilizator.cautaCursa()
            8 -> utilizator.rezervareLoc()
            0 -> println("La revedere!")
            else -> println("Optiune invalida. Va rugam sa incercati din nou.")
        }
    } while (optiune != 0)
}
