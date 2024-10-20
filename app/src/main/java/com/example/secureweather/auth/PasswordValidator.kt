package com.example.secureweather.auth

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import kotlin.reflect.KClass

class PasswordFormState {
    public var password: String = ""
    public var confirmPassword: String = ""
}

abstract class PasswordValidationRule(
    protected var validator: PasswordValidator
) {
    public abstract val title: String
    private var shownInPast: Boolean = false
    public abstract fun rule(passwordForm: PasswordFormState): Boolean
    protected open fun prerequisites(): Boolean { return true }
    protected open fun onValid() {}
    public fun shouldShow(): Boolean {
        val show = shownInPast || prerequisites()
        if (show) shownInPast = true
        return show
    }
    public fun isValid(passwordForm: PasswordFormState): Boolean {
        val passed = rule(passwordForm)
        if (passed && shouldShow()) onValid()
        return passed
    }

    public val feedback: String
        get() {
            val icon = if (validator.ruleResults[this::class] == true) "✔" else "✘"
            return "$icon $title\n"
        }
}

class PasswordLengthRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must be at least 8 characters long"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.length >= 8
    }
}

class PasswordContainsUppercaseRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain an uppercase letter"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.any { it.isUpperCase() }
    }
}

class PasswordContainsLowercaseRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain a lowercase letter"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.any { it.isLowerCase() }
    }
}

class PasswordContainsSpecialCharacterRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain a special character"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.any { !it.isLetterOrDigit() }
    }
}

class PasswordContainsMinDigitRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain at least 3 digits"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.count { it.isDigit() } >= 3
    }
}

class PasswordContainsADigitOnPositionRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must have a digit '0' on 4th position"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.getOrNull(3) == '0'
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordContainsMinDigitRule::class] == true
    }
}


// Crazy absurd password rules:
class PasswordNoSpacesRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "No spaces allowed"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return !passwordForm.password.contains(" ")
    }
}

class PasswordNextLeapYearRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain the next leap year"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        val currentYear = LocalDate.now().year
        val nextLeapYear = getNextLeapYear(currentYear)
        return passwordForm.password.contains(nextLeapYear.toString())
    }

    private fun getNextLeapYear(currentYear: Int): Int {
        var year = currentYear + 1 // Start checking from the next year
        // Loop until we find the next leap year
        while (!(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))) {
            year++
        }
        return year
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordLengthRule::class] == true
    }
}

class PasswordQQRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain exactly 2 letters 'Q'"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.lowercase().count() { it == 'q' } == 2
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordNextLeapYearRule::class] == true
    }

    override fun onValid() {
        validator.removeRule(PasswordContainsUppercaseRule::class)
    }
}

class PasswordNoConsequentQQRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Can't contain the sequence 'qq' and 'QQ'"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return !passwordForm.password.lowercase().contains("qq")
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordQQRule::class] == true
    }

    override fun onValid() {
        validator.removeRule(PasswordContainsLowercaseRule::class)
        validator.removeRule(PasswordLengthRule::class)
    }
}

class PasswordCurrentHourRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain the current hour"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        val currentHour = LocalDateTime.now().hour
        return passwordForm.password.contains(currentHour.toString())
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordNoConsequentQQRule::class] == true &&
               validator.ruleResults[PasswordQQRule::class] == true
    }
}

class PasswordContainsMaxTotalDigitsRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain at most 6 digits"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.count { it.isDigit() } <= 6
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordCurrentHourRule::class] == true
    }

    override fun onValid() {
        validator.removeRule(PasswordContainsMinDigitRule::class)
    }
}

class PasswordOddSpecialCharacterRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain an odd number of special characters"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.count { !it.isLetterOrDigit() } % 2 == 1
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordQQRule::class] == true
    }
}

class PasswordsMustMatch(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Passwords must match"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password == passwordForm.confirmPassword &&
               passwordForm.password.isNotEmpty()
    }

    override fun prerequisites(): Boolean {
        val ruleResults = validator.ruleResults.filter {
            (rule, _) -> when (rule) {
                PasswordsMustMatch::class -> false
                PasswordContainsClownEmojiRule::class -> false
                PasswordContainsClown2EmojiRule::class -> false
                PasswordContainsClown3EmojiRule::class -> false
                else -> true
            }

        }
        return ruleResults.all { (_, passed) -> passed }
    }

    override fun onValid() {
        if (validator.ruleResults[PasswordContainsClown3EmojiRule::class] == true) {
            validator.removeRule(PasswordContainsClown3EmojiRule::class)
            validator.removeRule(PasswordContainsClown2EmojiRule::class)
            validator.removeRule(PasswordContainsClownEmojiRule::class)
        }
        validator.removeRule(PasswordContainsMaxTotalDigitsRule::class)
    }
}

class PasswordContainsClownEmojiRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain the 🤡 emoji"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.contains("🤡")
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordsMustMatch::class] == true
    }
}

class PasswordContainsClown2EmojiRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain the sequence '\uD83E\uDD21\uD83E\uDD21'"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.contains("\uD83E\uDD21\uD83E\uDD21")
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordContainsClownEmojiRule::class] == true
    }
}

class PasswordContainsClown3EmojiRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Must contain the sequence '\uD83E\uDD21\uD83E\uDD21\uD83E\uDD21'"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return passwordForm.password.contains("\uD83E\uDD21\uD83E\uDD21\uD83E\uDD21")
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordContainsClown2EmojiRule::class] == true
    }
}

class PasswordNoClownEmojiRule(validator: PasswordValidator) : PasswordValidationRule(validator) {
    override val title: String = "Can't contain the 🤡 emoji"
    override fun rule(passwordForm: PasswordFormState): Boolean {
        return !passwordForm.password.contains("🤡")
    }

    override fun prerequisites(): Boolean {
        return validator.ruleResults[PasswordsMustMatch::class] == true &&
               validator.ruleResults[PasswordContainsClownEmojiRule::class] == null
    }
}

class PasswordValidator {
    private val passwordForm: PasswordFormState = PasswordFormState()

    var password: String
        get() = passwordForm.password
        set(value) { passwordForm.password = value; update() }

    var confirmPassword: String
        get() = passwordForm.confirmPassword
        set(value) { passwordForm.confirmPassword = value; update() }

    private var rules: List<PasswordValidationRule> = listOf()
    var ruleResults: Map<KClass<out PasswordValidationRule>, Boolean> = mapOf()
    private var _feedback: String = ""
    var isValid: Boolean = false
    var feedback: String
        get() = _feedback
        set(value) {}

    fun removeRule(rule: KClass<out PasswordValidationRule>) {
        val oldRules = rules
        rules = rules.filter { it::class != rule }
        if (oldRules.size != rules.size) update()
    }

    init {
        rules = listOf(
            PasswordLengthRule(this),
            PasswordContainsUppercaseRule(this),
            PasswordContainsLowercaseRule(this),
            PasswordContainsSpecialCharacterRule(this),
            PasswordContainsMinDigitRule(this),
            PasswordContainsADigitOnPositionRule(this),
            PasswordNextLeapYearRule(this),
            PasswordQQRule(this),
            PasswordNoConsequentQQRule(this),
            PasswordCurrentHourRule(this),
            PasswordContainsMaxTotalDigitsRule(this),
            PasswordNoSpacesRule(this),
            PasswordsMustMatch(this),
            PasswordContainsClownEmojiRule(this),
            PasswordContainsClown2EmojiRule(this),
            PasswordContainsClown3EmojiRule(this),
            PasswordNoClownEmojiRule(this),
            PasswordOddSpecialCharacterRule(this)
        )
    }

    fun update(){
        ruleResults = rules.associate { rule -> rule::class to rule.isValid(passwordForm) }

        _feedback = rules.filter { it.shouldShow() }.reversed().joinToString("") { it.feedback }
        isValid = ruleResults.all { (_, passed) -> passed }
    }
}