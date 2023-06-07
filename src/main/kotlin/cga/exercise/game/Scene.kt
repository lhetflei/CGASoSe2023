package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader.loadOBJ
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer
import cga.framework.ModelLoader.loadModel


/**
 * Created 29.03.2023.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

    private var camera: TronCamera
    //private val simpleMesh: Mesh
    //private val sphereMesh: Mesh
    private val groundMesh: Mesh

    private val sphereMatrix = Matrix4f()
    private val groundMatrix = Matrix4f()

    var meshlist = mutableListOf<Mesh>()
    var renderable = Renderable(meshlist)
    var renderable2 = Renderable(meshlist)
    var motorrad = Renderable(meshlist)

    //scene setup
    init {
        //1.2.3 haus
        val vertices = floatArrayOf(
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
        )

        camera = TronCamera()
        camera.rotate(-0.610865f,0f,0f)
        camera.translate(Vector3f(0.0f, 0.0f, 4.0f))


/*
        //Initialien DP
         private val vertices = floatArrayOf(
// Buchstabe D:
        -0.75f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
        -0.75f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
        -0.25f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
        -0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        -0.25f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
// Buchstabe P:
        0.25f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
        0.25f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.75f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.75f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f

    )
    val indices = intArrayOf(
        // Indizes für den Buchstaben D:
        0,1,2,
        0,4,5,
        2,3,4,

        //Indizes für P:
        6,7,8,
        8,6,12,
        8,9,11,
        8,9,10

    )
    val vertices = floatArrayOf(
                //P
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.1f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.1f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.1f, 0.9f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 0.9f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.4f, 0.9f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.4f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.1f, 0.6f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 0.6f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.1f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                //K
                0.7f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.7f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.8f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.8f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.8f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.9f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.9f, 0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.1f, 0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

        )
        val indices = intArrayOf(
                //P
                0, 1, 2,
                2, 3, 0,
                4,5,3,
                6,3,5,
                6,8,10,
                6,7,8,
                10,11,12,
                12,11,9,
                //K
                16,14,13,
                16,15,14,
                20,17,18,
                20,19,17,
                21,18,17,
                17,22,21
        )



        */
        //1.2.4 Initialien
        /*
        val vertices = floatArrayOf(
                //L
                -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.4f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                -0.4f, 0.4f, 0.0f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                -0.4f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.1f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.1f, -0.4f, 0.0f, 1.0f, 0.0f, 0.0f,
                -0.5f, -0.4f, 0.0f, 1.0f, 0.0f, 0.0f,
                //H
                0.4f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.4f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.7f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.8f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.7f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.8f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
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
*/

        val res = loadOBJ("assets/models/ground.obj", true, true)

        //Get the first mesh of the first object

        val objMesh = res.objects[0].meshes[0]
        val objres = loadOBJ("assets/models/sphere.obj", true, true)
        val objsphereMesh = objres.objects[0].meshes[0]
        val stride = 8 * 4

        val vertexAttributes = arrayOf<VertexAttribute>(
                 VertexAttribute(3,GL_FLOAT, stride, 0)
                ,VertexAttribute(2, GL_FLOAT, stride, (3 * 4).toLong())
                ,VertexAttribute(3, GL_FLOAT, stride, (5 * 4).toLong()))

        //1.3 mesh

        //sphereMesh = Mesh(objsphereMesh.vertexData, objsphereMesh.indexData, vertexAttributes, materials[model.meshes[i].materialIndex])

        //sphereMatrix.scale(0.5f)
        //groundMatrix.rotation(90f,Vector3f(1f,0f,0f)).scale(0.7f)



        //enableFaceCulling(GL_CW, GL_FRONT)
        enableDepthTest(GL_LESS)

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow() //blau
        //glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow() //schwarz
        //val vertexAttribute = arrayOf<VertexAttribute>(VertexAttribute(3, GL_FLOAT,24,0),VertexAttribute(3, GL_FLOAT,24,12))
        //1.2 mesh
       // simpleMesh = Mesh(vertices, indices,vertexAttribute)


        //renderable2 = Renderable(mutableListOf<Mesh>(groundMesh))
        /*
        renderable.scale(Vector3f(0.5f, 0.5f, 0.5f))
        renderable.rotate(180f,0f,0f)*/
        //renderable.scale(Vector3f(0.7f, 0.7f, 0.7f))










        val spec = Texture2D("assets/textures/ground_spec.png", true)
        val ground = Texture2D("assets/textures/ground_emit.png", true)


        ground.bind(1)
        ground.setTexParams(1,1,1,1)

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D,EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,16.0f)

        ground.unbind()



        val floorMaterial = Material(

                Texture2D("assets/textures/ground_diff.png", true),
                ground,
                spec,
                60.0f,
                Vector2f(64.0f, 64.0f)
        )
        floorMaterial.bind(staticShader)



        groundMesh = Mesh(objMesh.vertexData, objMesh.indexData, vertexAttributes, floorMaterial)
        motorrad= ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", -1.5708f, 1.5708f, 0f)!!

        motorrad.scale(Vector3f(0.8f, 0.8f, 0.8f))



        renderable2 = motorrad
        renderable = Renderable(mutableListOf<Mesh>(groundMesh))

        camera.parent = motorrad
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()

/*
        staticShader.use()

        val viewMatrix = camera.getCalculateViewMatrix()
        staticShader.setUniform("view_matrix", viewMatrix)

        val projectionMatrix = camera.getCalculateProjectionMatrix()
        staticShader.setUniform("proj_matrix", projectionMatrix)

        // Normalenmatrix berechnen
        val normalMatrix = Matrix4f(viewMatrix).invert().transpose()
        staticShader.setUniform("normalMatrix", normalMatrix)
*/

        camera.updateViewMatrix()
        camera.updateProjectionMatrix()
        camera.bind(staticShader)



        renderable.render(staticShader)
        motorrad.render(staticShader)
        //renderable2.render(staticShader)


        //sphereMesh.render()
        //groundMesh.render(staticShader)


        //staticShader.use()
        //staticShader.setUniform("model_matrix", sphereMatrix)
        //sphereMesh.render()
        //staticShader.setUniform("model_matrix", renderable2.getModelMatrix())
        //groundMesh.render()


        //simpleMesh.render()

    }

    fun update(dt: Float, t: Float) {
    if (window.getKeyState(GLFW_KEY_W) == true) {
        motorrad.scale(Vector3f(1.02f, 1.02f, 1.02f))
        //camera.translate(Vector3f(0.0f,0.0f,-2.0f))
    }
    if (window.getKeyState(GLFW_KEY_A) == true) {
        motorrad.rotate(0f,-0.05f,0.00f)
        //camera.translate(Vector3f(-2.0f,0.0f,0.0f))
    }
    if (window.getKeyState(GLFW_KEY_S) == true) {
        motorrad.scale(Vector3f(0.98f, 0.98f, 0.98f))
        //camera.translate(Vector3f(0.0f,0.0f,2.0f))
    }
    if (window.getKeyState(GLFW_KEY_D) == true) {
        motorrad.rotate(0f,0.05f,0.0f)
        //camera.translate(Vector3f(2.0f,0.0f,0.0f))
    }
    }
    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {
        //simpleMesh.cleanup()


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