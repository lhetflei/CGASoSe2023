package cga.exercise.game

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import cga.framework.GameWindow
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

/**
 * Created 29.03.2023.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")

    private val simpleMesh: Mesh


    //scene setup
    init {
        /*val vertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f
        )

        val indices = intArrayOf(
            0, 1, 2,
            0, 2, 4,
            4, 2, 3
        )*/
        val vertices = floatArrayOf(
                //L
                -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.4f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.4f, 0.4f, 0.0f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                -0.4f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.1f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.1f, -0.4f, 0.0f, 1.0f, 0.0f, 0.0f,
                -0.5f, -0.4f, 0.0f, 1.0f, 0.0f, 0.0f, //7
                //H
                0.4f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.4f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.7f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.8f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.7f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.8f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,//15
                0.5f, 0.4f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.7f, 0.4f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.3f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.7f, 0.3f, 0.0f, 1.0f, 0.0f, 0.0f,

        )

        val indices = intArrayOf(
                //L
                0, 2, 1,
                0, 3, 2,
                3, 4, 2,
                4, 5, 6,
                7, 4, 6,
                //H
                8, 10, 9,
                9, 10 ,11,
                12,14,13,
                13,14,15,
                16,18,17,
                17,18,19
        )
        // todo
        enableFaceCulling(GL_CW, GL_FRONT)
        //initial opengl state
        glClearColor(0.0f, 0.533f, 1.0f, 1.0f); GLError.checkThrow()
        val vertexAttribute =arrayOf<VertexAttribute>(VertexAttribute(3, GL_FLOAT,24,0),VertexAttribute(3, GL_FLOAT,24,12))
        simpleMesh = Mesh(vertices, indices,vertexAttribute)
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()

        simpleMesh.render()

    }

    fun update(dt: Float, t: Float) {}

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {
        simpleMesh.cleanup()
    }

    /**
     * enables culling of specified faces
     * orientation: ordering of the vertices to define the front face
     * faceToCull: specifies the face, that will be culled (back, front)
     * Please read the docs for accepted parameters
     */
    fun enableFaceCulling(orientation: Int, faceToCull: Int){
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(orientation); GLError.checkThrow()
        glCullFace(faceToCull); GLError.checkThrow()
    }

    /**
     * enables depth test
     * comparisonSpecs: specifies the comparison that takes place during the depth buffer test
     * Please read the docs for accepted parameters
     */
    fun enableDepthTest(comparisonSpecs: Int){
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(comparisonSpecs); GLError.checkThrow()
    }
}