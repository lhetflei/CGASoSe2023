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
import org.joml.*
import org.joml.Math.acos
import org.joml.Math.sqrt
import java.lang.StrictMath.pow
import java.util.Random
import kotlin.math.atan2
import kotlin.math.cos
import org.joml.Vector3f as Vector3f1


/**
 * Created 29.03.2023.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

    private var camera: TronCamera
    //private val simpleMesh: Mesh
    //private val sphereMesh: Mesh
    private val groundMesh: Mesh
    var vmaxa=0.01f
    var shoot2=false
    var cray=0
    var score =0f
    var pause =true
    var rayl= 0
    var rayl2=0
    var speed = -0.1f
    var shoot =false
    private val sphereMatrix = Matrix4f()
    private val groundMatrix = Matrix4f()
    var camselect=0f
    var tempshader=1f
    var asteroidlist = mutableListOf<Renderable>()
    var raylist = mutableListOf<Renderable>()
    var meshlist = mutableListOf<Mesh>()
    var lightlist = mutableListOf<PointLight>()
    var renderable = Renderable(meshlist)
    var renderable2 = Renderable(meshlist)
    var renderable3 = Renderable(meshlist)
    var motorrad = Renderable(meshlist)
    var ray = Renderable(meshlist)
    var ray2 = Renderable(meshlist)
    var ast = Renderable(meshlist)

    val desiredGammaValue = 2.2f // Beispielwert für den gewünschten Gammawert

    val lightPosition = Vector3f1(-15f, 5f, 15f) // Anpassen der Lichtposition
    val lightColor = Vector3f1(20f, 20f, 20f) // Anpassen der Lichtfarbe (hier: Weiß)

    val pointLight = PointLight(lightPosition, lightColor)

    val pointLight2 = PointLight(Vector3f1(0f,1f,0f), Vector3f1(30.0f,0.0f,30.0f))
    val pointLight3 = PointLight(Vector3f1(15f, 5f, -15f), Vector3f1(0f,0.0f,40.0f))
    val pointLight4 = PointLight(Vector3f1(0f, 1f, 0f), Vector3f1(30.0f,0.0f,0.0f))
    val spotLight = SpotLight(Vector3f1(0f,2f,0f), Vector3f1(50f,50f,50f),Math.toRadians(10f),org.joml.Math.toRadians(30f))

    private val initialSpaceshipPosition = Vector3f1(0.0f, 1.0f, 0.0f)
    private var currentSpaceshipPosition = Vector3f1(initialSpaceshipPosition)
    private var collisionCheckTimer: Float = 0f
    private val collisionCheckInterval: Float = 0.1f



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
        camera.rotate(-0.110865f,0f,0f)
        camera.translate(Vector3f1(0.0f, 0.0f, 14.0f))





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
        //enableFaceCulling(GL_CCW, GL_FRONT)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow() //schwarze hintergrundfarbe, alpha1.0f völlige deckkraft
        //glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow() //schwarz
        //val vertexAttribute = arrayOf<VertexAttribute>(VertexAttribute(3, GL_FLOAT,24,0),VertexAttribute(3, GL_FLOAT,24,12))
        //1.2 mesh
       // simpleMesh = Mesh(vertices, indices,vertexAttribute)


        //renderable2 = Renderable(mutableListOf<Mesh>(groundMesh))
        /*
        renderable.scale(Vector3f(0.5f, 0.5f, 0.5f))
        renderable.rotate(180f,0f,0f)*/











        val spec = Texture2D("assets/textures/ground_diff.png", true)
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
        val rayMaterial = Material(

            diff,
            emit = Texture2D("assets/textures/ground_emit.png", true),
            spec,
            60.0f,
            Vector2f(64.0f, 64.0f)
        )
        floorMaterial.bind(staticShader)



        groundMesh = Mesh(objMesh.vertexData, objMesh.indexData, vertexAttributes, floorMaterial)
        //motorrad= ModelLoader.loadModel("assets/newship/AirShip.obj", 0f, Math.toRadians(180f), 0f)!!
        //motorrad= ModelLoader.loadModel("assets/ship/scene.obj", 0f, Math.toRadians(180f), 0f)!!
        //motorrad= ModelLoader.loadModel("assets/xwing/x-wing-flyingv1.obj", 0f, Math.toRadians(180f), 0f)!!
        motorrad= ModelLoader.loadModel("assets/starsparrow/StarSparrow01.obj", 0f, Math.toRadians(180f), 0f)!!

        camera.parent = motorrad
        motorrad.scale(Vector3f1(0.8f, 0.8f, 0.8f))
        motorrad.translate(initialSpaceshipPosition)







        renderable3 = ModelLoader.loadModel("assets/Moon_3D_Model/moon.obj", -1.5708f, 1.5708f, 0f)!!
        renderable2 = ModelLoader.loadModel("assets/10464_Asteroid_L3.123c72035d71-abea-4a34-9131-5e9eeeffadcb/10464_Asteroid_v1_Iterations-2.obj", -1.5708f, 1.5708f, 0f)!!
        renderable2.scale(Vector3f1(0.005f,0.005f,0.005f))
        renderable2.translate(Vector3f1(500f,20f,0f))
        renderable3.scale(Vector3f1(0.05f,0.05f,0.05f))
        renderable3.translate(Vector3f1(-500f,20f,0f))
        renderable = Renderable(mutableListOf<Mesh>(groundMesh))
        renderable.scale(Vector3f1(25.7f, 25.7f, 25.7f))


        var ras = loadOBJ("assets/models/newscene.obj",true,true)
        var raymesh = Mesh(ras.objects[0].meshes[0].vertexData,ras.objects[0].meshes[0].indexData,vertexAttributes,rayMaterial)
        ray = Renderable(mutableListOf(raymesh))
        ray.translate(spotLight.getPosition())
        ray.translate(Vector3f1(0f,-1f,0f))
        ray.rotate(-1.5708f,1.5708f,0f)

        var ras2 = loadOBJ("assets/models/newscene.obj",true,true)
        var raymesh2 = Mesh(ras.objects[0].meshes[0].vertexData,ras.objects[0].meshes[0].indexData,vertexAttributes,rayMaterial)
        ray2 = Renderable(mutableListOf(raymesh2))
        ray2.translate(spotLight.getPosition())
        ray2.translate(Vector3f1(0f,-1f,0f))
        ray2.rotate(-1.5708f,1.5708f,0f)


        //ray.scale(Vector3f(1.1f,2.1f,2.1f))
        spotLight.rotate(Math.toRadians(-5f),0f,0f)
        spotLight.parent = motorrad
        ray.parent = motorrad
        ray2.parent=motorrad
        pointLight4.parent = ray
        pointLight2.parent = ray2

        for(i in 1..25)//random asteroid spawn
        {
            var rendertemp = ModelLoader.loadModel("assets/10464_Asteroid_L3.123c72035d71-abea-4a34-9131-5e9eeeffadcb/10464_Asteroid_v1_Iterations-2.obj", -1.5708f, 1.5708f, 0f)!!
            var ascale=Random().nextFloat(0.005f,0.01f)

            rendertemp.scale(Vector3f1(ascale,ascale,ascale))
            rendertemp.translate(Vector3f1(Random().nextFloat(-10000f,10000f),Random().nextFloat(-10000f,10000f),Random().nextFloat(-10000f,10000f)))
            //rendertemp.rotate(Math.toRadians(Random().nextFloat(0f,360f)),Math.toRadians(Random().nextFloat(0f,360f)),Math.toRadians(Random().nextFloat(0f,360f)) )
            asteroidlist.add(rendertemp)
        }
        ast = ModelLoader.loadModel("assets/10464_Asteroid_L3.123c72035d71-abea-4a34-9131-5e9eeeffadcb/10464_Asteroid_v1_Iterations-2.obj", -1.5708f, 1.5708f, 0f)!!

    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()

        staticShader.setUniform("gammaValue", desiredGammaValue)

        camera.updateViewMatrix()
        camera.updateProjectionMatrix()
        camera.bind(staticShader)


        pointLight.bind(staticShader,camera.getCalculateViewMatrix(),0)
        pointLight3.bind(staticShader,camera.getCalculateViewMatrix(),1)


        spotLight.bind(staticShader, camera.getCalculateViewMatrix())

        motorrad.render(staticShader, Vector3f1(1.2f,1.2f,1.2f))
        renderable.render(staticShader, Vector3f1(0f,1f,0f))
        //renderable2.render(staticShader, Vector3f(0.5f,0.5f,0.5f))
        renderable3.render(staticShader, Vector3f1(0.1f,0.1f,0.1f))
        motorrad.render(staticShader, Vector3f1(1.2f,1.2f,1.2f))
        renderable.render(staticShader,Vector3f1(0f,1f,0f))





       // renderable2.render(staticShader, Vector3f(0.5f,0.5f,0.5f))
        renderable3.render(staticShader, Vector3f1(0.1f,0.1f,0.1f))
        if(shoot2==true){

            ray2.render(staticShader, Vector3f1(10f,0.1f,0.1f))
            pointLight2.bind(staticShader,camera.getCalculateViewMatrix(),1)
            ray2.translate(Vector3f1(0f,3f,0f))
            rayl2++
            println(rayl2)

            if(rayl2>=100){
                ray2.translate(Vector3f1(0f,-300f,0f))
                shoot2= false
                rayl2=0

            }
        }
        if(shoot==true){
            ray.render(staticShader, Vector3f1(10f,0.1f,0.1f))

            pointLight4.bind(staticShader,camera.getCalculateViewMatrix(),3)
            ray.translate(Vector3f1(0f,3f,0f))
            rayl++
            if(rayl==50)
                shoot2=true

            if(rayl>=100){
                ray.translate(Vector3f1(0f,-300f,0f))
                shoot= false
                rayl=0

            }
        }




        for(i in 0..asteroidlist.lastIndex-1)
        {

                asteroidlist[i].translate(motorrad.getWorldPosition().sub(asteroidlist[i].getWorldPosition(),Vector3f1()).mul(Vector3f1(vmaxa,vmaxa,vmaxa)))

            asteroidlist[i].render(staticShader,Vector3f1(0.3f,0.3f,0.3f))
        }

        if(pause)
        {

            score+=1
            if(score.toInt()%100==0)
                vmaxa*=1.01f
        }
        if(score.toInt()%100==0){
            var rendertemp = ModelLoader.loadModel("assets/a2/rock_by_dommk.obj", -1.5708f, 1.5708f, 0f)!!
            var ascale=Random().nextFloat(0.05f,0.1f)

            rendertemp.scale(Vector3f1(ascale,ascale,ascale))
            rendertemp.translate(Vector3f1(Random().nextFloat(-10000f,10000f),Random().nextFloat(-10000f,10000f),Random().nextFloat(-10000f,10000f)))
            //rendertemp.rotate(Math.toRadians(Random().nextFloat(0f,360f)),Math.toRadians(Random().nextFloat(0f,360f)),Math.toRadians(Random().nextFloat(0f,360f)) )
            asteroidlist.add(rendertemp)

        }
    //println(asteroidlist.lastIndex.toString())

    }

    fun update(dt: Float, t: Float) {
        collisionCheckTimer += dt

        if (collisionCheckTimer >= collisionCheckInterval) {
            checkCollisionSpaceship()
            checkCollisionAsteroid()
            collisionCheckTimer = 0f // Setze den Timer zurück
        }


        if (window.getKeyState(GLFW_KEY_W) == true) {
            val forward = Vector3f1(0f, 0f, speed)
            motorrad.translate(forward)
        }
        if (window.getKeyState(GLFW_KEY_D) == true) {
            motorrad.rotate(0f, -0.03f, 0.0f)
        }
        if (window.getKeyState(GLFW_KEY_S) == true) {
            val backward = Vector3f1(0f, 0f, 0.2f)
            motorrad.translate(backward)
        }
        if (window.getKeyState(GLFW_KEY_A) == true) {
            motorrad.rotate(0f, 0.03f, 0.0f)
        }
        if (window.getKeyState(GLFW_KEY_L) == true) {
            tempshader=tempshader+0.1f
            if(tempshader>=3f){
                tempshader=0f}
            staticShader.setUniform("shader",tempshader)
        }
        if (window.getKeyState(GLFW_KEY_ESCAPE) == true) {
            //pause
        }
        if (window.getKeyState(GLFW_KEY_P) == true) {
            shoot=true
            checkCollisionAsteroid()
        }
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT) == true) {

            if(speed>=-0.5f)
            speed-=0.003f

        }
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT) == false) {

            if(speed<=-0.1f)
                speed+=0.01f
        }

        checkCollisionSpaceship()
    }

    private fun checkCollisionSpaceship() {
        val spaceshipPosition = motorrad.getWorldPosition()

        val iterator = asteroidlist.iterator()
        while (iterator.hasNext()) {
            val asteroid = iterator.next()
            val asteroidPosition = asteroid.getWorldPosition()

            val distance = spaceshipPosition.distance(asteroidPosition)

            if (distance < 10.0f) {
                iterator.remove()
                asteroid.cleanup()

                // Setze das Raumschiff an den Punkt des Spielstarts zurück
                setSpaceshipPositionToStart()
            }
        }
    }

    private fun setSpaceshipPositionToStart() {

        motorrad.cleanup()
        motorrad= ModelLoader.loadModel("assets/starsparrow/StarSparrow01.obj", 0f, Math.toRadians(180f), 0f)!!
        camera.parent = motorrad
        motorrad.scale(Vector3f1(0.8f, 0.8f, 0.8f))
        motorrad.translate(initialSpaceshipPosition)
        spotLight.parent = motorrad
        ray.parent = motorrad
        ray2.parent=motorrad
        score=0f
        vmaxa=0.01f
    }

    private fun checkCollisionAsteroid() {
        val shotPosition = ray.getWorldPosition()

        val iterator = asteroidlist.iterator()
        while (iterator.hasNext()) {
            val asteroid = iterator.next()
            val asteroidPosition = asteroid.getWorldPosition()

            val distance = shotPosition.distance(asteroidPosition)

            if (distance < 10.0f) {
                iterator.remove()
                asteroid.cleanup()
                score+=500f
            }
        }
    }
    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        val x_speed = (xpos - window.windowWidth/ 2.0).toFloat() * 0.002f

        val y_speed = (ypos - window.windowHeight/ 2.0).toFloat() * 0.002f

        glfwSetCursorPos(window.m_window, window.windowWidth / 2.0, window.windowHeight/ 2.0)
        motorrad.rotate(-y_speed, 0f, 0f)
        motorrad.rotate(0f, -x_speed, 0f)

    }
    fun onMouseButton(button: Int, action: Int, mode: Int) {
        shoot=true
        checkCollisionAsteroid()
    }

    fun onMouseScroll(xoffset: Double, yoffset: Double) {
        if (yoffset < 0)
        {
            camera.translate(Vector3f1(0.0f, 0.0f, 0.5f))
        }
        if (yoffset > 0)
        {
            camera.translate(Vector3f1(0.0f, 0.0f, -0.5f))
        }
    }



    fun cleanup() {
        for (asteroid in asteroidlist) {
            asteroid.cleanup()
        }


    }

    private fun setSpaceshipPosition(position: Vector3f1) {
        currentSpaceshipPosition.set(position)
        motorrad.translate(currentSpaceshipPosition)
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