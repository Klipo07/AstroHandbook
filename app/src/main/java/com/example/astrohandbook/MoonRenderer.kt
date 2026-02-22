package com.example.astrohandbook

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MoonRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var moonSphere: MoonSphere
    private var moonTextureId = 0

    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    // Для освещения
    private val lightPos = floatArrayOf(5.0f, 5.0f, 5.0f, 1.0f) // Позиция источника света
    private val lightAmbient = floatArrayOf(0.2f, 0.2f, 0.2f, 1.0f)
    private val lightDiffuse = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    private val lightSpecular = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    private var program = 0

    private var positionHandle = 0
    private var normalHandle = 0
    private var texCoordHandle = 0
    private var mvpMatrixHandle = 0
    private var mvMatrixHandle = 0
    private var lightPosHandle = 0
    private var lightAmbientHandle = 0
    private var lightDiffuseHandle = 0
    private var lightSpecularHandle = 0

    private var rotationAngle = 0f

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
        normalHandle = GLES20.glGetAttribLocation(program, "aNormal")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")

        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        mvMatrixHandle = GLES20.glGetUniformLocation(program, "uMVMatrix")
        lightPosHandle = GLES20.glGetUniformLocation(program, "uLightPos")
        lightAmbientHandle = GLES20.glGetUniformLocation(program, "uLightAmbient")
        lightDiffuseHandle = GLES20.glGetUniformLocation(program, "uLightDiffuse")
        lightSpecularHandle = GLES20.glGetUniformLocation(program, "uLightSpecular")

        // Создаем сферу Луны
        moonSphere = MoonSphere(1.5f)

        // Загружаем текстуру Луны
        moonTextureId = loadTexture(R.drawable.moon)
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

        GLES20.glUseProgram(program)

        // Камера
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 0f, 8f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        // Модель (Луна вращается)
        Matrix.setIdentityM(modelMatrix, 0)
        rotationAngle += 0.5f
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 0f, 1f, 0f)

        // Вычисляем матрицы
        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        // Передаем матрицы в шейдер
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0)

        // Передаем параметры освещения
        GLES20.glUniform3f(lightPosHandle, lightPos[0], lightPos[1], lightPos[2])
        GLES20.glUniform4fv(lightAmbientHandle, 1, lightAmbient, 0)
        GLES20.glUniform4fv(lightDiffuseHandle, 1, lightDiffuse, 0)
        GLES20.glUniform4fv(lightSpecularHandle, 1, lightSpecular, 0)

        // Активируем текстуру
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, moonTextureId)
        val textureUniform = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(textureUniform, 0)

        // Рисуем Луну
        moonSphere.draw(positionHandle, normalHandle, texCoordHandle)
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
            attribute vec3 aNormal;
            attribute vec2 aTexCoord;
            
            uniform mat4 uMVPMatrix;
            uniform mat4 uMVMatrix;
            uniform vec3 uLightPos;
            
            varying vec2 vTexCoord;
            varying vec3 vLightDir;
            varying vec3 vViewDir;
            varying float vDistance;
            
            void main() {
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
                
                // Позиция вершины в пространстве камеры
                vec3 position = (uMVMatrix * vec4(aPosition, 1.0)).xyz;
                
                // Нормаль в пространстве камеры
                vec3 normal = normalize(mat3(uMVMatrix) * aNormal);
                
                // Направление света
                vec3 lightPosCamera = (uMVMatrix * vec4(uLightPos, 1.0)).xyz;
                vLightDir = normalize(lightPosCamera - position);
                
                // Направление к камере
                vViewDir = normalize(-position);
                
                // Расстояние до источника света (для затухания)
                vDistance = length(lightPosCamera - position);
                
                vTexCoord = aTexCoord;
            }
        """.trimIndent()
    }

    private fun getFragmentShader(): String {
        return """
            precision mediump float;
            
            varying vec2 vTexCoord;
            varying vec3 vLightDir;
            varying vec3 vViewDir;
            varying float vDistance;
            
            uniform sampler2D uTexture;
            uniform vec4 uLightAmbient;
            uniform vec4 uLightDiffuse;
            uniform vec4 uLightSpecular;
            
            void main() {
                vec4 texColor = texture2D(uTexture, vTexCoord);
                
                // Нормализуем векторы
                vec3 L = normalize(vLightDir);
                vec3 V = normalize(vViewDir);
                vec3 N = vec3(0.0, 0.0, 1.0); // Нормаль в пространстве камеры
                
                // Модель освещения Фонга
                // Ambient (фоновое)
                vec4 ambient = uLightAmbient * texColor;
                
                // Diffuse (рассеянный свет)
                float diff = max(dot(N, L), 0.0);
                vec4 diffuse = uLightDiffuse * texColor * diff;
                
                // Specular (зеркальный свет)
                vec3 R = reflect(-L, N);
                float spec = pow(max(dot(R, V), 0.0), 32.0);
                vec4 specular = uLightSpecular * spec;
                
                // Затухание света с расстоянием
                float attenuation = 1.0 / (1.0 + 0.1 * vDistance + 0.01 * vDistance * vDistance);
                
                vec4 finalColor = ambient + (diffuse + specular) * attenuation;
                gl_FragColor = finalColor;
            }
        """.trimIndent()
    }
}