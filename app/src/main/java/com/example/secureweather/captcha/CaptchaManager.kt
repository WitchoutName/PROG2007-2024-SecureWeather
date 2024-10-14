import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.example.secureweather.auth.AuthService
import com.example.secureweather.captcha.CaptchaChatbotActivity
import com.example.secureweather.captcha.CaptchaCompassActivity
import com.example.secureweather.captcha.CaptchaFindColorActivity
import com.example.secureweather.weather.WeatherActivity

class CaptchaManager {
    // List of available CAPTCHA tasks
    private val captchaTasks: MutableList<Class<*>> = ArrayList()
    private var currentTaskIndex = 0

    companion object {
        @Volatile
        private var INSTANCE: CaptchaManager? = null

        // Lazy initialization of the singleton instance
        fun getInstance(): CaptchaManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CaptchaManager().also { INSTANCE = it }
            }
        }
    }

    // Constructor to initialize the task list and shuffle them
    init {
        captchaTasks.add(CaptchaChatbotActivity::class.java)
        captchaTasks.add(CaptchaCompassActivity::class.java)
        captchaTasks.add(CaptchaFindColorActivity::class.java)

        // Shuffle the tasks to ensure random order
        captchaTasks.shuffle()
    }

    val nextCaptchaTask: Class<*>?
        // Get the next CAPTCHA task in sequence
        get() = if (currentTaskIndex < captchaTasks.size) {
            captchaTasks[currentTaskIndex++]
        } else {
            null // All tasks are completed
        }

    // Check if there are more tasks to complete
    fun hasMoreTasks(): Boolean {
        return currentTaskIndex < captchaTasks.size
    }

    fun next(context: Context) {
        var intent: Intent
        if (hasMoreTasks()){
            val nextTask = nextCaptchaTask
            intent = Intent(context, nextTask)
        } else {
            intent = Intent(context, WeatherActivity::class.java)
        }
        startActivity(context, intent, null)
    }
}