package com.example.alertamujer.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.alertamujer.R
import com.example.alertamujer.data.manager.SosManager
import com.example.alertamujer.ui.alerta.AdjuntarActivity
import com.example.alertamujer.ui.alerta.AutoridadesActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs
import kotlin.math.hypot

class BubbleService : Service() {

    private var windowManager: WindowManager? = null
    private var bubbleView: View? = null
    private var trashView: View? = null
    private lateinit var trashParams: WindowManager.LayoutParams

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
            val contextWrapper = ContextThemeWrapper(
                this,
                R.style.Theme_AlertaMujer // 🟢 Usa el tema principal de tu app (o R.style.Theme_AppCompat_DayNight_NoActionBar)
            )

            // 1. Inflar la burbuja
            bubbleView = LayoutInflater.from(contextWrapper).inflate(R.layout.layout_floating_bubble, null)

            // 2. Inflar la papelera con la "X" de forma segura
            trashView = LayoutInflater.from(this).inflate(R.layout.layout_trash, null)
            trashView?.visibility = View.GONE

            val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 100
                y = 300
            }

            // Parámetros de la papelera (centrada abajo)
            trashParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                y = 120
            }

            inicializarVistas(bubbleView!!)
            configurarTouchyClicks(layoutParams)
            observarEstadoGlobalSos()

            // Añadir vistas al WindowManager
            windowManager?.addView(trashView, trashParams)
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
                        if (!isMenuExpanded) {
                            btnCenter.text = "ON"
                            btnCenter.setBackgroundColor(Color.RED)
                        }
                    }
                    is SosManager.EstadoAlerta.Procesando -> {
                        btnCenter.text = "..."
                        btnCenter.setBackgroundColor(Color.GRAY)
                    }
                    is SosManager.EstadoAlerta.Inactiva -> {
                        if (isMenuExpanded) {
                            toggleMenuExpansion()
                        }
                        btnCenter.text = "SOS"
                        btnCenter.setBackgroundColor(Color.parseColor("#D32F2F"))
                    }
                    is SosManager.EstadoAlerta.Error -> {
                        if (isMenuExpanded) {
                            toggleMenuExpansion()
                        }
                        btnCenter.text = "SOS"
                        btnCenter.setBackgroundColor(Color.parseColor("#D32F2F"))
                        Toast.makeText(applicationContext, estado.mensaje, Toast.LENGTH_LONG).show()
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
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            val bubbleWidth = bubbleView?.width ?: 150
            val bubbleHeight = bubbleView?.height ?: 150
            val margin = 30 // Margen de seguridad con los bordes

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    // Mostrar la papelera cuando empieza el arrastre
                    trashView?.visibility = View.VISIBLE
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val rawNewX = initialX + (event.rawX - initialTouchX).toInt()
                    val rawNewY = initialY + (event.rawY - initialTouchY).toInt()

                    // Aplicar límites de pantalla para respetar márgenes
                    layoutParams.x = maxOf(margin, minOf(rawNewX, screenWidth - bubbleWidth - margin))
                    layoutParams.y = maxOf(margin, minOf(rawNewY, screenHeight - bubbleHeight - margin))

                    windowManager?.updateViewLayout(bubbleView, layoutParams)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Ocultar la papelera al soltar
                    trashView?.visibility = View.GONE

                    val movedX = abs(event.rawX - initialTouchX)
                    val movedY = abs(event.rawY - initialTouchY)

                    // 1. Evaluar si se soltó sobre la papelera inferior para eliminar la burbuja
                    val trashCenterX = screenWidth / 2f
                    val trashCenterY = screenHeight - 150f
                    val bubbleCenterX = layoutParams.x + (bubbleWidth / 2f)
                    val bubbleCenterY = layoutParams.y + (bubbleHeight / 2f)
                    val distance = hypot((trashCenterX - bubbleCenterX).toDouble(), (trashCenterY - bubbleCenterY).toDouble())

                    if (distance < 150.0) {
                        stopSelf()
                        return@setOnTouchListener true
                    }

                    // 2. Si el desplazamiento fue mínimo, se toma como un Clic limpio
                    if (movedX < 10 && movedY < 10) {
                        val estadoActual = sosManager.estadoAlerta.value

                        when (estadoActual) {
                            is SosManager.EstadoAlerta.Activa -> {
                                toggleMenuExpansion()
                            }
                            is SosManager.EstadoAlerta.Procesando -> {
                                // No hacer nada mientras procesa
                            }
                            else -> {
                                sosManager.procesarAlertaInicial()
                            }
                        }
                    }
                    true
                }
                else -> false
            }
        }

        btnFoto.setOnClickListener { abrirCaptura("FOTO"); toggleMenuExpansion() }
        btnVideo.setOnClickListener { abrirCaptura("VIDEO"); toggleMenuExpansion() }
        btnAudio.setOnClickListener { abrirCaptura("AUDIO"); toggleMenuExpansion() }
        btnCalls.setOnClickListener {
            val intent = Intent(this, AutoridadesActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            toggleMenuExpansion()
        }
    }

    private fun toggleMenuExpansion() {
        isMenuExpanded = !isMenuExpanded

        val targetVisibility = if (isMenuExpanded) View.VISIBLE else View.GONE
        val distance = 160f

        btnCalls.visibility = targetVisibility
        btnAudio.visibility = targetVisibility
        btnFoto.visibility = targetVisibility
        btnVideo.visibility = targetVisibility

        val layoutParams = bubbleView?.layoutParams as? WindowManager.LayoutParams
        if (layoutParams != null) {
            if (isMenuExpanded) {
                layoutParams.width = 500
                layoutParams.height = 500
            } else {
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            windowManager?.updateViewLayout(bubbleView, layoutParams)
        }

        if (isMenuExpanded) {
            btnCalls.animate().translationX(-distance).translationY(0f).setDuration(200).start()
            btnAudio.animate().translationX(distance).translationY(0f).setDuration(200).start()
            btnFoto.animate().translationX(0f).translationY(-distance).setDuration(200).start()
            btnVideo.animate().translationX(0f).translationY(distance).setDuration(200).start()

            btnCenter.text = "✕"
            btnCenter.setBackgroundColor(Color.DKGRAY)
        } else {
            btnCalls.animate().translationX(0f).translationY(0f).setDuration(150).start()
            btnAudio.animate().translationX(0f).translationY(0f).setDuration(150).start()
            btnFoto.animate().translationX(0f).translationY(0f).setDuration(150).start()
            btnVideo.animate().translationX(0f).translationY(0f).setDuration(150).start()

            val estadoActual = sosManager.estadoAlerta.value
            if (estadoActual is SosManager.EstadoAlerta.Activa) {
                btnCenter.text = "ON"
                btnCenter.setBackgroundColor(Color.RED)
            } else {
                btnCenter.text = "SOS"
                btnCenter.setBackgroundColor(Color.parseColor("#D32F2F"))
            }
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
            .setContentText("Burbuja Flotante Activa")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(1001, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        // Actualiza las preferencias para que la SettingsActivity apague el switch automáticamente
        val prefs = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("bubble_active", false).apply()

        if (bubbleView != null) {
            windowManager?.removeView(bubbleView)
        }
        if (trashView != null) {
            windowManager?.removeView(trashView)
        }
    }
}