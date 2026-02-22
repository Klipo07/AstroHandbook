package com.example.astrohandbook

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class TransparentCube {
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ByteBuffer

    private val vertices = floatArrayOf(
        -0.6f, -0.6f, -0.6f,  // 0
        0.6f, -0.6f, -0.6f,  // 1
        0.6f,  0.6f, -0.6f,  // 2
        -0.6f,  0.6f, -0.6f,  // 3
        -0.6f, -0.6f,  0.6f,  // 4
        0.6f, -0.6f,  0.6f,  // 5
        0.6f,  0.6f,  0.6f,  // 6
        -0.6f,  0.6f,  0.6f   // 7
    )

    private val indices = intArrayOf(
        // Рисуем только ребра куба (линии)
        0, 1, 1, 2, 2, 3, 3, 0,  // передняя грань
        4, 5, 5, 6, 6, 7, 7, 4,  // задняя грань
        0, 4, 1, 5, 2, 6, 3, 7   // боковые ребра
    )

    init {
        val vb = ByteBuffer.allocateDirect(vertices.size * 4)
        vb.order(ByteOrder.nativeOrder())
        vertexBuffer = vb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 4)
        indexBuffer.order(ByteOrder.nativeOrder())
        indices.forEach {
            indexBuffer.putInt(it)
        }
        indexBuffer.position(0)
    }

    fun draw(positionHandle: Int) {
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Рисуем линии
        GLES20.glDrawElements(
            GLES20.GL_LINES,
            indices.size,
            GLES20.GL_UNSIGNED_INT,
            indexBuffer
        )
    }
}