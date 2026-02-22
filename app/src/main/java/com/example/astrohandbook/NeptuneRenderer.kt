package com.example.astrohandbook

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NeptuneRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var neptuneSphere: Sphere
    private var waterTextureId = 0

    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var program = 0
    private var positionHandle = 0
    private var texCoordHandle = 0
    private var mvpMatrixHandle = 0
    private var timeHandle = 0

    private var startTime = System.currentTimeMillis()
    private var currentTexture: Bitmap? = null

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader())
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader())

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        timeHandle = GLES20.glGetUniformLocation(program, "uTime")

        // Создаем сферу для Нептуна
        neptuneSphere = Sphere(1.5f, 64, 64)

        // Создаем начальную текстуру воды
        updateWaterTexture(0f)
    }

    private fun updateWaterTexture(time: Float) {
        // Генерируем новую текстуру воды с текущим временем
        currentTexture?.recycle()
        currentTexture = WaterTexture.generateWaterTexture(512, time)

        // Загружаем в OpenGL
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        waterTextureId = textures[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, waterTextureId)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, currentTexture, 0)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val aspectRatio = if (height > 0) width.toFloat() / height.toFloat() else 1f
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 1f, 100f)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val currentTime = (System.currentTimeMillis() - startTime).toFloat() / 1000f

        // Обновляем текстуру каждые 100мс для анимации волн
        if ((currentTime * 10).toInt() % 2 == 0) {
            updateWaterTexture(currentTime)
        }

        GLES20.glUseProgram(program)

        // Камера
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 2f, 5f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        // Вращение Нептуна
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, currentTime * 10f, 0f, 1f, 0f)

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform1f(timeHandle, currentTime)

        // Активируем текстуру воды
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, waterTextureId)
        val textureUniform = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(textureUniform, 0)

        // Рисуем Нептун
        neptuneSphere.draw(positionHandle, texCoordHandle)
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
            uniform float uTime;
            
            void main() {
                // Смещаем текстурные координаты для эффекта движения волн
                vec2 distortedCoord = vTexCoord;
                distortedCoord.x += sin(vTexCoord.y * 10.0 + uTime * 2.0) * 0.03;
                distortedCoord.y += cos(vTexCoord.x * 8.0 + uTime * 1.5) * 0.03;
                
                vec4 color = texture2D(uTexture, distortedCoord);
                gl_FragColor = color;
            }
        """.trimIndent()
    }
}