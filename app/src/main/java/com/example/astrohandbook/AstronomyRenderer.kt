package com.example.astrohandbook

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class AstronomyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val square = Square()

    // Сферы планет
    private lateinit var sunSphere: Sphere
    private lateinit var mercurySphere: Sphere
    private lateinit var venusSphere: Sphere
    private lateinit var earthSphere: Sphere
    private lateinit var marsSphere: Sphere
    private lateinit var moonSphere: Sphere

    // Данные планет
    private val planets = listOf(
        Planet(0.8f, 0f, 0f, R.drawable.sun, "Солнце"),
        Planet(0.15f, 2.0f, 0.5f, R.drawable.mercury, "Меркурий"),
        Planet(0.18f, 2.8f, 0.35f, R.drawable.venus, "Венера"),
        Planet(0.2f, 3.6f, 0.25f, R.drawable.earth, "Земля"),
        Planet(0.17f, 4.4f, 0.2f, R.drawable.mars, "Марс"),
        Planet(0.06f, 0.5f, 1.2f, R.drawable.moon, "Луна")
    )

    // Текстуры
    private val textureIds = mutableMapOf<Int, Int>()

    private var program = 0
    private var backgroundTextureId = 0

    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var positionHandle = 0
    private var texCoordHandle = 0
    private var mvpMatrixHandle = 0

    private var startTime = System.currentTimeMillis()

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader())
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader())

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        backgroundTextureId = loadTexture(R.drawable.galaxy_texture)

        // Создаем сферы
        sunSphere = Sphere(planets[0].radius)
        mercurySphere = Sphere(planets[1].radius)
        venusSphere = Sphere(planets[2].radius)
        earthSphere = Sphere(planets[3].radius)
        marsSphere = Sphere(planets[4].radius)
        moonSphere = Sphere(planets[5].radius)

        // Загружаем текстуры
        planets.forEach { planet ->
            if (planet.textureResId != 0) {
                textureIds[planet.textureResId] = loadTexture(planet.textureResId)
            }
        }
    }

    private fun loadTexture(resourceId: Int): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

        val options = BitmapFactory.Options().apply {
            inScaled = false
        }

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        if (bitmap != null) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }

        return textures[0]
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val aspectRatio = if (height > 0) width.toFloat() / height.toFloat() else 1f
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 1f, 100f)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        GLES20.glUseProgram(program)

        Matrix.setLookAtM(viewMatrix, 0,
            0f, 3f, 12f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        drawBackground()
        drawSolarSystem()
    }

    private fun drawBackground() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -20f)
        Matrix.scaleM(modelMatrix, 0, 20f, 20f, 1f)

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backgroundTextureId)
        val textureUniform = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(textureUniform, 0)

        square.draw(positionHandle, texCoordHandle)
    }

    private fun drawSolarSystem() {
        val time = (System.currentTimeMillis() - startTime).toFloat() * 0.002f

        // Солнце
        drawPlanetAtPosition(sunSphere, planets[0], 0f, 0f, 0f)

        // Меркурий
        val mercuryAngle = time * planets[1].speed
        drawPlanetAtAngle(mercurySphere, planets[1], mercuryAngle, 0f)

        // Венера
        val venusAngle = time * planets[2].speed
        drawPlanetAtAngle(venusSphere, planets[2], venusAngle, 0f)

        // Земля
        val earthAngle = time * planets[3].speed
        val earthX = planets[3].orbitRadius * cos(earthAngle)
        val earthZ = planets[3].orbitRadius * sin(earthAngle)
        drawPlanetAtPosition(earthSphere, planets[3], earthX, 0f, earthZ)

        // Луна (вокруг Земли, перпендикулярно)
        val moonAngle = time * planets[5].speed * 3f
        val moonX = earthX + planets[5].orbitRadius * cos(moonAngle)
        val moonY = planets[5].orbitRadius * 0.8f * sin(moonAngle) // Перпендикулярно!
        val moonZ = earthZ + planets[5].orbitRadius * sin(moonAngle) * 0.5f
        drawPlanetAtPosition(moonSphere, planets[5], moonX, moonY, moonZ)

        // Марс
        val marsAngle = time * planets[4].speed
        drawPlanetAtAngle(marsSphere, planets[4], marsAngle, 0f)
    }

    // Функция для рисования планеты по углу (круговая орбита)
    private fun drawPlanetAtAngle(sphere: Sphere, planet: Planet, angle: Float, verticalOffset: Float) {
        if (planet.orbitRadius == 0f) return

        val x = planet.orbitRadius * cos(angle)
        val z = planet.orbitRadius * sin(angle)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, verticalOffset, z)

        // Вращение вокруг своей оси
        Matrix.rotateM(modelMatrix, 0, angle * 10f, 0f, 1f, 0f)

        applyTextureAndDraw(sphere, planet)
    }

    // Функция для рисования планеты по конкретным координатам
    private fun drawPlanetAtPosition(sphere: Sphere, planet: Planet, x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)

        // Вращение вокруг своей оси
        val time = (System.currentTimeMillis() - startTime).toFloat() * 0.001f
        Matrix.rotateM(modelMatrix, 0, time * 20f, 0f, 1f, 0f)

        applyTextureAndDraw(sphere, planet)
    }

    // Общая функция для применения текстуры и рисования
    private fun applyTextureAndDraw(sphere: Sphere, planet: Planet) {
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        if (planet.textureResId != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[planet.textureResId] ?: 0)
            val textureUniform = GLES20.glGetUniformLocation(program, "uTexture")
            GLES20.glUniform1i(textureUniform, 0)
        }

        sphere.draw(positionHandle, texCoordHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun getVertexShader(): String {
        return """
            attribute vec3 aPosition;
            attribute vec2 aTexCoord;
            
            uniform mat4 uMVPMatrix;
            
            varying vec2 vTexCoord;
            
            void main() {
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
                vTexCoord = aTexCoord;
            }
        """.trimIndent()
    }

    private fun getFragmentShader(): String {
        return """
            precision mediump float;
            
            varying vec2 vTexCoord;
            
            uniform sampler2D uTexture;
            
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """.trimIndent()
    }

    private fun cos(angle: Float): Float {
        return kotlin.math.cos(angle.toDouble()).toFloat()
    }

    private fun sin(angle: Float): Float {
        return kotlin.math.sin(angle.toDouble()).toFloat()
    }
}