package com.example.astrohandbook

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*

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

        // Уменьшил планету с 1.0f до 0.7f
        neptuneSphere = Sphere(0.7f, 180, 180)  // Увеличил детализацию сферы
        waterTextureId = createDetailedWaterTexture()
    }

    private fun createDetailedWaterTexture(): Int {
        val size = 1024  // Увеличил размер текстуры для большей детализации
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (x in 0 until size) {
            for (y in 0 until size) {
                val u = x.toFloat() / size
                val v = y.toFloat() / size

                // МАТЕМАТИЧЕСКИЕ ФОРМУЛЫ ДЛЯ ДЕТАЛЬНЫХ ВОЛН

                // 1. Базовые синусоидальные волны (океанская зыбь)
                val baseWave1 = sin(u * 10f * PI.toFloat()) * cos(v * 8f * PI.toFloat())
                val baseWave2 = sin(u * 15f * PI.toFloat() + 2f) * cos(v * 12f * PI.toFloat() + 1f)

                // 2. Высокочастотные волны (рябь)
                val ripple1 = sin(u * 40f * PI.toFloat()) * sin(v * 35f * PI.toFloat())
                val ripple2 = cos(u * 50f * PI.toFloat() + 3f) * cos(v * 45f * PI.toFloat() + 2f)

                // 3. Микро-рябь (очень мелкие детали)
                val microRipple1 = sin(u * 120f * PI.toFloat() + v * 80f * PI.toFloat())
                val microRipple2 = cos(u * 150f * PI.toFloat() - v * 130f * PI.toFloat())

                // 4. Круговые волны (эффект брошенного камня)
                val centerX1 = 0.3f
                val centerY1 = 0.7f
                val dist1 = sqrt((u - centerX1).pow(2) + (v - centerY1).pow(2))
                val circular1 = sin(dist1 * 30f * PI.toFloat()) * exp(-dist1 * 5f)

                val centerX2 = 0.7f
                val centerY2 = 0.3f
                val dist2 = sqrt((u - centerX2).pow(2) + (v - centerY2).pow(2))
                val circular2 = cos(dist2 * 25f * PI.toFloat()) * exp(-dist2 * 4f)

                // 5. Турбулентность (шумоподобный эффект)
                val turbulence = sin(u * 200f * PI.toFloat() + v * 150f * PI.toFloat()) *
                        cos(v * 180f * PI.toFloat() - u * 160f * PI.toFloat())

                // Комбинируем все волны с разными весами
                val combinedWave = (
                        baseWave1 * 0.4f +
                                baseWave2 * 0.3f +
                                ripple1 * 0.2f +
                                ripple2 * 0.15f +
                                microRipple1 * 0.1f +
                                microRipple2 * 0.1f +
                                circular1 * 0.25f +
                                circular2 * 0.2f +
                                turbulence * 0.08f
                        ) * 0.5f + 0.5f  // Нормализация в диапазон 0-1

                // Добавляем градиент глубины (центр темнее, края светлее)
                val depthFactor = 1f - sqrt((u - 0.5f).pow(2) + (v - 0.5f).pow(2)) * 1.2f
                val finalHeight = (combinedWave * 0.7f + depthFactor * 0.3f).coerceIn(0f, 1f)

                // Сложная цветовая схема для водной поверхности
                val red = (0.05f + finalHeight * 0.15f + sin(finalHeight * 10f) * 0.03f).coerceIn(0f, 1f)
                val green = (0.2f + finalHeight * 0.4f + cos(finalHeight * 8f) * 0.05f).coerceIn(0f, 1f)
                val blue = (0.6f + finalHeight * 0.4f + sin(finalHeight * 12f) * 0.07f).coerceIn(0f, 1f)

                // Добавляем белую пену на пиках волн
                var finalRed = red
                var finalGreen = green
                var finalBlue = blue

                if (finalHeight > 0.8f) {
                    val foam = (finalHeight - 0.8f) * 5f
                    finalRed = (red + foam * 0.5f).coerceIn(0f, 1f)
                    finalGreen = (green + foam * 0.5f).coerceIn(0f, 1f)
                    finalBlue = (blue + foam).coerceIn(0f, 1f)
                }

                val color = Color.rgb(
                    (finalRed * 255).toInt(),
                    (finalGreen * 255).toInt(),
                    (finalBlue * 255).toInt()
                )
                bitmap.setPixel(x, y, color)
            }
        }

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)  // Генерируем мип-карты для лучшего качества
        bitmap.recycle()

        return textures[0]
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspectRatio = if (height > 0) width.toFloat() / height.toFloat() else 1f
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 1f, 100f)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        val time = (System.currentTimeMillis() - startTime).toFloat() / 1000f

        // Камера чуть ближе из-за меньшего размера планеты
        Matrix.setLookAtM(viewMatrix, 0,
            1.2f, 0.8f, 2.8f,  // Приблизил камеру
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, time * 8f, 0f, 1f, 0f)  // Немного замедлил вращение

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform1f(timeHandle, time)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, waterTextureId)
        val textureUniform = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(textureUniform, 0)

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
            varying vec3 vPosition;
            void main() {
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
                vTexCoord = aTexCoord;
                vPosition = aPosition;
            }
        """.trimIndent()
    }

    private fun getFragmentShader(): String {
        return """
            precision highp float;  // Повысил точность
            varying vec2 vTexCoord;
            varying vec3 vPosition;
            uniform sampler2D uTexture;
            uniform float uTime;
            
            void main() {
                // Более сложное искажение текстурных координат для эффекта движущихся волн
                vec2 coord = vTexCoord;
                
                // Множество слоев искажения с разными частотами
                float distortion1 = sin(coord.y * 25.0 + uTime * 3.0) * 0.03;
                float distortion2 = cos(coord.x * 30.0 - uTime * 2.5) * 0.02;
                float distortion3 = sin(coord.x * 60.0 + coord.y * 40.0 + uTime * 5.0) * 0.015;
                float distortion4 = cos(coord.x * 100.0 - coord.y * 80.0 + uTime * 4.0) * 0.01;
                
                coord.x += distortion1 + distortion2 + distortion3;
                coord.y += distortion2 + distortion3 + distortion4;
                
                // Получаем цвет из текстуры с искаженными координатами
                vec4 color = texture2D(uTexture, coord);
                
                // Улучшенное освещение (учет позиции источника света)
                vec3 normal = normalize(vPosition);
                vec3 lightDir = normalize(vec3(0.5, 1.0, 0.5));  // Направление света
                float diff = max(dot(normal, lightDir), 0.2);
                
                // Добавляем блеск на освещенной стороне
                float specular = pow(max(dot(normal, lightDir), 0.0), 32.0) * 0.3;
                
                color.rgb *= (diff + specular);
                
                gl_FragColor = color;
            }
        """.trimIndent()
    }
}