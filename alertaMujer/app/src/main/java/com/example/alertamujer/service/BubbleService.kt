package com.example.alertamujer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import androidx.core.app.NotificationCompat
import com.example.alertamujer.R
import com.example.alertamujer.data.manager.SosManager
import com.example.alertamujer.ui.alerta.AdjuntarActivity
import com.example.alertamujer.ui.contactos.ContactosActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs


class BubbleService : Service() {

    private var windowManager: WindowManager? = null
    private var bubbleView: View? = null
    private lateinit var sosManager: SosManager
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var btnCenter: MaterialButton
    private lateinit var btnCalls: MaterialButton
    private lateinit var btnAudio: MaterialButton
    private lateinit var btnFoto: MaterialButton
    private lateinit var btnVideo: MaterialButton

    private var isMenuExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        sosManager = SosManager.getInstance(this)
        iniciarNotificacionForeground()

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val contextWrapper = ContextThemeWrapper(this, com.google.android.material.R.style.Theme_Material3_DayNight_NoActionBar)
            bubbleView = LayoutInflater.from(contextWrapper).inflate(R.layout.layout_floating_bubble, null)

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 100
                y = 300
            }

            inicializarVistas(bubbleView!!)
            configurarTouchyClicks(layoutParams)
            observarEstadoGlobalSos()

            windowManager?.addView(bubbleView, layoutParams)
        } catch (e: Exception) {
            stopSelf()
        }
    }

    private fun inicializarVistas(view: View) {
        btnCenter = view.findViewById(R.id.btn_center)
        btnCalls = view.findViewById(R.id.btn_opcion_calls)
        btnAudio = view.findViewById(R.id.btn_opcion_audio)
        btnFoto = view.findViewById(R.id.btn_opcion_foto)
        btnVideo = view.findViewById(R.id.btn_opcion_video)
    }

    private fun observarEstadoGlobalSos() {
        serviceScope.launch {
            sosManager.estadoAlerta.collectLatest { estado ->
                when (estado) {
                    is SosManager.EstadoAlerta.Activa -> {
                        btnCenter.text = "ON"
                        btnCenter.setBackgroundColor(android.graphics.Color.RED)
                    }
                    is SosManager.EstadoAlerta.Procesando -> {
                        btnCenter.text = "..."
                    }
                    else -> {
                        btnCenter.text = "SOS"
                        btnCenter.setBackgroundColor(android.graphics.Color.GRAY)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configurarTouchyClicks(layoutParams: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        btnCenter.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(bubbleView, layoutParams)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (abs(event.rawX - initialTouchX) < 10 && abs(event.rawY - initialTouchY) < 10) {
                        // Alternar activación de alerta desde la burbuja
                        if (sosManager.estadoAlerta.value is SosManager.EstadoAlerta.Activa) {
                            sosManager.desactivarAlertaEnServidor()
                        } else {
                            sosManager.procesarAlertaInicial()
                        }
                    }
                    true
                }
                else -> false
            }
        }

        btnFoto.setOnClickListener { abrirCaptura("FOTO") }
        btnVideo.setOnClickListener { abrirCaptura("VIDEO") }
        btnAudio.setOnClickListener { abrirCaptura("AUDIO") }
        btnCalls.setOnClickListener {
            val intent = Intent(this, ContactosActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    private fun abrirCaptura(tipo: String) {
        val activeId = sosManager.idAlertaActual.value ?: -1
        val intent = Intent(this, AdjuntarActivity::class.java).apply {
            putExtra("EXTRA_ID_ALERTA", activeId)
            putExtra("EXTRA_TIPO_AUTO_LAUNCH", tipo)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
    }

    private fun iniciarNotificacionForeground() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("bubble_channel", "Burbuja SOS", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "bubble_channel")
            .setContentTitle("Alerta Mujer")
            .setContentText("Burbuja Activa")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        startForeground(1001, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (bubbleView != null) windowManager?.removeView(bubbleView)
    }
}