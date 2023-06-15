package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

open class PointLight(initialPosition: Vector3f, private val lightColor: Vector3f) : Transformable() , IPointLight {

    init {
        translate(initialPosition)
    }

    override fun bind(shaderProgram: ShaderProgram) {
        val worldPosition = getWorldPosition()
        val lightColorUniform = "lightColor"
        val lightPositionUniform = "lightPosition"

        val color = Vector3f(lightColor)
        val position = Vector3f(worldPosition)

        shaderProgram.use()
        shaderProgram.setUniform(lightColorUniform, color)
        shaderProgram.setUniform(lightPositionUniform, position)
    }
}