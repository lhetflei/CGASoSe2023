package cga.exercise.game

import SpotLight
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
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
import kotlin.math.cos
import org.joml.Math


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

    val desiredGammaValue = 2.2f // Beispielwert für den gewünschten Gammawert

    val lightPosition = Vector3f(-15f, 5f, 15f) // Anpassen der Lichtposition
    val lightColor = Vector3f(30f, 30f, 30f) // Anpassen der Lichtfarbe (hier: Weiß)

    val pointLight = PointLight(lightPosition, lightColor)

    val pointLight2 = PointLight(Vector3f(-15f, 5f, -15f), Vector3f(30.0f,0.0f,30.0f))
    val pointLight3 = PointLight(Vector3f(15f, 5f, -15f), Vector3f(0f,0.0f,40.0f))
    val pointLight4 = PointLight(Vector3f(15f, 5f, 15f), Vector3f(30.0f,0.0f,0.0f))
    val spotLight = SpotLight(Vector3f(0f,2f,0f),Vector3f(2f,2f,2f),Math.toRadians(20f),org.joml.Math.toRadians(30f))

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




        enableDepthTest(GL_LESS) //Tiefentest, werden Pixel in der richtigen Reihenfolge gerendert

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow() //schwarze hintergrundfarbe, alpha1.0f völlige deckkraft
        //glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow() //schwarz
        //val vertexAttribute = arrayOf<VertexAttribute>(VertexAttribute(3, GL_FLOAT,24,0),VertexAttribute(3, GL_FLOAT,24,12))
        //1.2 mesh
       // simpleMesh = Mesh(vertices, indices,vertexAttribute)


        //renderable2 = Renderable(mutableListOf<Mesh>(groundMesh))
        /*
        renderable.scale(Vector3f(0.5f, 0.5f, 0.5f))
        renderable.rotate(180f,0f,0f)*/











        val spec = Texture2D("assets/textures/ground_spec.png", true)
        val ground = Texture2D("assets/textures/ground_emit.png", true)
        val diff =Texture2D("assets/textures/ground_diff.png", true)

        ground.bind(0)

        ground.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)


        ground.unbind()



        val floorMaterial = Material(

                diff,
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
        renderable.scale(Vector3f(25.7f, 25.7f, 25.7f))
        camera.parent = motorrad

        spotLight.rotate(Math.toRadians(-5f),0f,0f)
        spotLight.parent = motorrad


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
*/  staticShader.setUniform("gammaValue", desiredGammaValue)

        camera.updateViewMatrix()
        camera.updateProjectionMatrix()
        camera.bind(staticShader)

        pointLight2.bind(staticShader,camera.getCalculateViewMatrix(),0)
        pointLight.bind(staticShader,camera.getCalculateViewMatrix(),1)
        pointLight3.bind(staticShader,camera.getCalculateViewMatrix(),2)
        pointLight4.bind(staticShader,camera.getCalculateViewMatrix(),3)

        spotLight.bind(staticShader, camera.getCalculateViewMatrix())

        motorrad.render(staticShader, Vector3f(1f,0f,1f))
        renderable.render(staticShader,Vector3f(0f,1f,0f))


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
            val forward = Vector3f(0f, 0f, -0.1f)
            motorrad.translate(forward)
        }
        if (window.getKeyState(GLFW_KEY_D) == true) {
            motorrad.rotate(0f, -0.03f, 0.0f)
        }
        if (window.getKeyState(GLFW_KEY_S) == true) {
            val backward = Vector3f(0f, 0f, 0.1f)
            motorrad.translate(backward)
        }
        if (window.getKeyState(GLFW_KEY_A) == true) {
            motorrad.rotate(0f, 0.03f, 0.0f)
        }
    }
    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        val x_speed = (xpos - window.windowWidth/ 2.0).toFloat() * 0.002f
        glfwSetCursorPos(window.m_window, window.windowWidth / 2.0, window.windowHeight/ 2.0)

        camera.rotateAroundPoint(0f, -x_speed, 0f, renderable.getWorldPosition())

    }

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