package com.example.astrohandbook

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube {
    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer

    private val vertices = floatArrayOf(
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f,  0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f
    )

    private val indices = intArrayOf(
        0, 1, 2, 0, 2, 3,
        4, 6, 5, 4, 7, 6,
        0, 3, 7, 0, 7, 4,
        1, 5, 6, 1, 6, 2,
        3, 2, 6, 3, 6, 7,
        0, 4, 5, 0, 5, 1
    )

    private val colors = floatArrayOf(
        1f, 0f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 0f, 1f, 1f,
        1f, 1f, 0f, 1f,
        1f, 0f, 1f, 1f,
        0f, 1f, 1f, 1f
    )

    init {
        val vb = ByteBuffer.allocateDirect(vertices.size * 4)
        vb.order(ByteOrder.nativeOrder())
        vertexBuffer = vb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val cb = ByteBuffer.allocateDirect(colors.size * 4)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)
    }

    fun draw(positionHandle: Int, colorHandle: Int) {
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(
            colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer
        )
        GLES20.glEnableVertexAttribArray(colorHandle)

        val indexBuffer = ByteBuffer.allocateDirect(indices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_INT, indexBuffer)
    }
}