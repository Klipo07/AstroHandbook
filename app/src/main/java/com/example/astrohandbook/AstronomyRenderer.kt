package com.example.astrohandbook

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AstronomyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val square = Square()
    private val cube = Cube()

    private var program = 0
    private var textureId = 0

    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var positionHandle = 0
    private var texCoordHandle = 0
    private var colorHandle = 0
    private var mvpMatrixHandle = 0

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.1f, 1.0f)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader())
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader())

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        colorHandle = GLES20.glGetAttribLocation(program, "aColor")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        // Загружаем текстуру из ресурсов (твоя картинка)
        textureId = loadTextureFromResource(R.drawable.galaxy_texture)
    }

    // Функция для загрузки картинки из ресурсов
    private fun loadTextureFromResource(resourceId: Int): Int {
        // Создаем текстуру
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])

        // Настройки текстуры
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        // Загружаем картинку
        val options = BitmapFactory.Options().apply {
            inScaled = false // Не масштабировать автоматически
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

        // Активируем текстуру
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        val textureUniform = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(textureUniform, 0)

        Matrix.setLookAtM(viewMatrix, 0,
            0f, 0f, 5f,  // позиция камеры
            0f, 0f, 0f,  // точка, куда смотрим
            0f, 1f, 0f   // вектор "вверх"
        )

        drawSquare()
        drawCube()

        // Запрашиваем перерисовку на следующем кадре
        GLES20.glFinish()
    }

    private fun drawSquare() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -8f)  // Отодвигаем назад
        Matrix.scaleM(modelMatrix, 0, 8f, 8f, 1f)       // Увеличиваем размер

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        square.draw(positionHandle, texCoordHandle)
    }

    private fun drawCube() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, 0f)    // Центр

        // ВРАЩЕНИЕ КУБА - используем время для непрерывного вращения
        val angle = System.currentTimeMillis() / 20f % 360f
        Matrix.rotateM(modelMatrix, 0, angle, 1f, 1f, 0.5f) // Вращаем по всем осям

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        cube.draw(positionHandle, colorHandle)
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
            attribute vec4 aColor;
            
            uniform mat4 uMVPMatrix;
            
            varying vec2 vTexCoord;
            varying vec4 vColor;
            
            void main() {
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
                vTexCoord = aTexCoord;
                vColor = aColor;
            }
        """.trimIndent()
    }

    private fun getFragmentShader(): String {
        return """
            precision mediump float;
            
            varying vec2 vTexCoord;
            varying vec4 vColor;
            
            uniform sampler2D uTexture;
            
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord) * vColor;
            }
        """.trimIndent()
    }
}