package com.example.astrohandbook

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Sphere(
    private val radius: Float,
    private val stacks: Int = 36,
    private val slices: Int = 36
) {
    private val vertexBuffer: FloatBuffer
    private val texCoordBuffer: FloatBuffer
    private val indexBuffer: ByteBuffer

    private val indexCount: Int

    init {
        // Генерируем вершины сферы
        val vertices = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()
        val indices = mutableListOf<Int>()

        for (i in 0..stacks) {
            val phi = PI * i / stacks
            val sinPhi = sin(phi)
            val cosPhi = cos(phi)

            for (j in 0..slices) {
                val theta = 2 * PI * j / slices
                val sinTheta = sin(theta)
                val cosTheta = cos(theta)

                val x = (radius * sinPhi * cosTheta).toFloat()
                val y = (radius * cosPhi).toFloat()
                val z = (radius * sinPhi * sinTheta).toFloat()

                vertices.add(x)
                vertices.add(y)
                vertices.add(z)

                texCoords.add((j.toFloat() / slices))
                texCoords.add((i.toFloat() / stacks))
            }
        }

        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = (i * (slices + 1)) + j
                val second = first + slices + 1

                indices.add(first)
                indices.add(second)
                indices.add(first + 1)

                indices.add(second)
                indices.add(second + 1)
                indices.add(first + 1)
            }
        }

        indexCount = indices.size

        val vb = ByteBuffer.allocateDirect(vertices.size * 4)
        vb.order(ByteOrder.nativeOrder())
        vertexBuffer = vb.asFloatBuffer()
        vertexBuffer.put(vertices.toFloatArray())
        vertexBuffer.position(0)

        val tb = ByteBuffer.allocateDirect(texCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        texCoordBuffer = tb.asFloatBuffer()
        texCoordBuffer.put(texCoords.toFloatArray())
        texCoordBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 4)
        indexBuffer.order(ByteOrder.nativeOrder())
        indices.forEach {
            indexBuffer.putInt(it)
        }
        indexBuffer.position(0)
    }

    fun draw(positionHandle: Int, texCoordHandle: Int) {
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(
            texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer
        )
        GLES20.glEnableVertexAttribArray(texCoordHandle)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indexCount,
            GLES20.GL_UNSIGNED_INT,
            indexBuffer
        )
    }
}