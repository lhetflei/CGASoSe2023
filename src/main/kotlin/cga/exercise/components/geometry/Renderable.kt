package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class Renderable(private val meshes: MutableList<Mesh>) : Transformable() , IRenderable {

    override fun render(shaderProgram: ShaderProgram) {

        shaderProgram.use()
        shaderProgram.setUniform("modelMatrix", getModelMatrix())

        for (mesh in meshes) {

            mesh.render()
        }

    }
}