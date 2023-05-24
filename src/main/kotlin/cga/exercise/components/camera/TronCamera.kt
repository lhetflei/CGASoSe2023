package cga.exercise.components.camera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram


import org.joml.Matrix4f
import org.joml.Vector3f


class TronCamera : Transformable(), ICamera {
    private var fieldOfView: Float = Math.toRadians(90.0).toFloat()
    private var aspectRatio: Float = 16.0f / 9.0f
    private var nearPlane: Float = 0.1f
    private var farPlane: Float = 100.0f



    override fun getCalculateViewMatrix(): Matrix4f {
        val viewMatrix = Matrix4f()
        viewMatrix.lookAt(getPosition(), getPosition().add(getFront(), Vector3f()), getUp())
        return viewMatrix
    }

    fun getFront(): Vector3f {
        return getZAxis().negate()
    }
    fun getUp(): Vector3f {
        return getYAxis().negate()
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        val projectionMatrix = Matrix4f()
        projectionMatrix.perspective(fieldOfView, aspectRatio, nearPlane, farPlane)
        return projectionMatrix
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniform("projectionMatrix", getCalculateProjectionMatrix())
        shader.setUniform("viewMatrix", getCalculateViewMatrix())
    }
}