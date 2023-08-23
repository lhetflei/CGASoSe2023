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
import org.joml.Vector3f
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
    var astmesh: Mesh
    var vmaxa=0.01f
    var vmaxa2=0.0001f
    var shoot2=false
    var cray=0
    var score =0f
    var pause =true
    var rayl= 0
    var rayl2=0
    var speed = -0.1f
    var shoot =false
    var cammode =0
    private val sphereMatrix = Matrix4f()
    private val groundMatrix = Matrix4f()
    var camselect=0f
    var tempshader=1f
    var asteroidlist = mutableListOf<Renderable>()
    var asteroidlist2 = mutableListOf<Renderable>()
    var raylist = mutableListOf<Renderable>()
    var meshlist = mutableListOf<Mesh>()
    var lightlist = mutableListOf<PointLight>()
    //var astmesh = Renderable(meshlist)
    var renderable = Renderable(meshlist)
    var renderable2 = Renderable(meshlist)
    var renderable3 = Renderable(meshlist)
    var motorrad = Renderable(meshlist)
    var skybox = Renderable(meshlist)
    var game_over = Renderable(meshlist)
    var end_game = Renderable(meshlist)
    var reset_game = Renderable(meshlist)
    var challenge = Renderable(meshlist)
    var dif_easy = Renderable(meshlist)
    var dif_mid = Renderable(meshlist)
    var dif_difc = Renderable(meshlist)
    var menu_backg = Renderable(meshlist)

    var skyboxMaterial: Material
    var ray = Renderable(meshlist)
    var ray2 = Renderable(meshlist)
    var ast = Renderable(meshlist)
    val stride = 8 * 4
    val atr1 = VertexAttribute(3, GL_FLOAT, stride, 0)     //position attribute
    val atr2 = VertexAttribute(3, GL_FLOAT, stride, 3 * 4) //texture coordinate attribute
    val atr3 = VertexAttribute(3, GL_FLOAT, stride, 5 * 4) //normal attribute
    val vertexAttributes = arrayOf(atr1, atr2, atr3)
    var astobj =loadOBJ("assets/a2/rock_by_dommk.obj",true,true)
    var astdiff = Texture2D("assets/a2/rock_Base_Color.png",true)
    var astemit = Texture2D("assets/a2/rock_Height.png",true)
    var astspec = Texture2D("assets/a2/rock_by_dommk_nmap.tga",true)
    val astmat=Material(
        astdiff,
        astemit,
        astspec,
        2.0f,
        Vector2f(1.0f, 1.0f)

    )


    val desiredGammaValue = 2.2f // Beispielwert für den gewünschten Gammawert

    val lightPosition = Vector3f1(0f, 5f, 0f) // Anpassen der Lichtposition
    val lightColor = Vector3f1(0.11f, 0.11f, 0.11f) // Anpassen der Lichtfarbe (hier: Weiß)

    var pointLight = PointLight(lightPosition, lightColor)

    var pointLight2 = PointLight(Vector3f1(0f,1f,0f), Vector3f1(0.0f,0.0f,0.0f))
    val pointLight3 = PointLight(Vector3f1(15f, 5f, -15f), Vector3f1(0f,0.0f,40.0f))
    var pointLight4 = PointLight(Vector3f1(0f, 1f, 0f), Vector3f1(0.0f,0.0f,0.0f))
    val spotLight = SpotLight(Vector3f1(0f,2f,0f), Vector3f1(500f,500f,500f),Math.toRadians(1f),org.joml.Math.toRadians(2f))
    val pointLight5 = PointLight(Vector3f1(0f,0f,0f), Vector3f1(500.0f,500.0f,500.0f))

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
        val cu = loadOBJ("assets/models/skybox.obj", true, true)

        //Menu
        val end = loadOBJ("assets/models/menu/beenden.obj", true, true)
        val ga_ov = loadOBJ("assets/models/menu/game_over.obj", true, true)
        val easy = loadOBJ("assets/models/menu/leicht.obj", true, true)
        val mid = loadOBJ("assets/models/menu/mittel.obj", true, true)
        val difc = loadOBJ("assets/models/menu/schwer.obj", true, true)
        val reset = loadOBJ("assets/models/menu/neustart.obj", true, true)
        val chall = loadOBJ("assets/models/menu/schwierigkeitsgrad.obj", true, true)
        val back = loadOBJ("assets/models/menu/menu_background.obj", true, true)


        //Get the first mesh of the first object
        val objMesh = res.objects[0].meshes[0]
        val objres = loadOBJ("assets/models/sphere.obj", true, true)
        val objsphereMesh = objres.objects[0].meshes[0]
        /*val stride = 8 * 4
        val vertexAttributes = arrayOf<VertexAttribute>(
                 VertexAttribute(3,GL_FLOAT, stride, 0)
                ,VertexAttribute(2, GL_FLOAT, stride, (3 * 4).toLong())
                ,VertexAttribute(3, GL_FLOAT, stride, (5 * 4).toLong()))*/



        val atr4 = VertexAttribute(3, GL_FLOAT,12,0)
        val SkyBoxVertexAttribute = arrayOf(atr4, atr2, atr3)


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
        val diff = Texture2D("assets/textures/ground_diff.png", true)
        var skyboxMat = Texture2D("assets/textures/skybox.png", true)
        var raytex = Texture2D("assets/textures/ground_diff.png", true)
        var fontMat = Texture2D("assets/textures/skybox.png", true)
        var backMat = Texture2D("assets/textures/ground_diff.png", true)


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
            raytex,
            raytex,
            600.0f,
            Vector2f(1.0f, 1.0f)
        )
        skyboxMaterial = Material(diff, skyboxMat, spec, 60f, Vector2f(1.0f, 1.0f))

        //floorMaterial.bind(staticShader)



        groundMesh = Mesh(objMesh.vertexData, objMesh.indexData, vertexAttributes, floorMaterial)

        var skyboxmesh = Mesh(cu.objects[0].meshes[0].vertexData,cu.objects[0].meshes[0].indexData,vertexAttributes,skyboxMaterial)
        skybox = Renderable(mutableListOf(skyboxmesh))

        //Menu
        var menu_backMesh = Mesh(back.objects[0].meshes[0].vertexData,back.objects[0].meshes[0].indexData,vertexAttributes, floorMaterial)
        menu_backg = Renderable(mutableListOf(menu_backMesh))
        var menu_overMesh = Mesh(ga_ov.objects[0].meshes[0].vertexData,ga_ov.objects[0].meshes[0].indexData,vertexAttributes, skyboxMaterial)
        game_over = Renderable(mutableListOf(menu_overMesh))




        //var gameovermesh = Mesh(ga_ov.objects[0].meshes[0].vertexData,ga_ov.objects[0].meshes[0].indexData,vertexAttributes, floorMaterial)
        //game_over = Renderable(mutableListOf(gameovermesh))

        //motorrad= ModelLoader.loadModel("assets/newship/AirShip.obj", 0f, Math.toRadians(180f), 0f)!!
        //motorrad= ModelLoader.loadModel("assets/ship/scene.obj", 0f, Math.toRadians(180f), 0f)!!
        //motorrad= ModelLoader.loadModel("assets/xwing/x-wing-flyingv1.obj", 0f, Math.toRadians(180f), 0f)!!
        motorrad= ModelLoader.loadModel("assets/starsparrow/StarSparrow01.obj", 0f, Math.toRadians(180f), 0f)!!

        camera.parent = motorrad
        motorrad.scale(Vector3f1(0.8f, 0.8f, 0.8f))
        motorrad.translate(initialSpaceshipPosition)


        skybox.translate(motorrad.getWorldPosition())
        skybox.scale(Vector3f1(1850f,1850f,1850f))
        //skybox.parent = motorrad

        //Menu
        menu_backg.translate(Vector3f1(-20f,0f,20f))
        menu_backg.scale(Vector3f1(1f,1f,1f))
        game_over.translate(Vector3f1(-20f,0f,20f))
        game_over.scale(Vector3f1(0.06f,0.06f,0.06f))


        renderable3 = ModelLoader.loadModel("assets/Moon_3D_Model/moon.obj", -1.5708f, 1.5708f, 0f)!!
        renderable2 = ModelLoader.loadModel("assets/10464_Asteroid_L3.123c72035d71-abea-4a34-9131-5e9eeeffadcb/10464_Asteroid_v1_Iterations-2.obj", -1.5708f, 1.5708f, 0f)!!
        renderable2.scale(Vector3f1(0.005f,0.005f,0.005f))
        renderable2.translate(Vector3f1(500f,20f,0f))
        renderable3.scale(Vector3f1(0.5f,0.5f,0.5f))
        renderable3.translate(Vector3f1(-500f,1100f,0f))
        renderable = Renderable(mutableListOf<Mesh>(groundMesh))
        renderable.scale(Vector3f1(25.7f, 25.7f, 25.7f))
        pointLight5.parent=renderable3


        //Laser
        var ras = loadOBJ("assets/models/newscene.obj",true,true)
        var raymesh = Mesh(ras.objects[0].meshes[0].vertexData,ras.objects[0].meshes[0].indexData,vertexAttributes,rayMaterial)
        ray = Renderable(mutableListOf(raymesh))
        ray.scale(Vector3f1(5f,5f,5f))
        //ray.translate(spotLight.getPosition())
        ray.translate(Vector3f1(0f,0f,0f))
        ray.rotate(-1.5708f,1.5708f,0f)

        //2.Laser
        var ras2 = loadOBJ("assets/models/newscene.obj",true,true)
        var raymesh2 = Mesh(ras.objects[0].meshes[0].vertexData,ras.objects[0].meshes[0].indexData,vertexAttributes,rayMaterial)
        ray2 = Renderable(mutableListOf(raymesh2))
        ray2.scale(Vector3f1(5f,5f,5f))
        //ray2.translate(spotLight.getPosition())
        ray2.translate(Vector3f1(0f,0f,0f))
        ray2.rotate(-1.5708f,1.5708f,0f)


        //var astmesh= Mesh(astobj.objects[0].meshes[0].vertexData,ras.objects[0].meshes[0].indexData,vertexAttributes,astmat)



        //ray.scale(Vector3f(1.1f,2.1f,2.1f))
        spotLight.rotate(Math.toRadians(-5f),0f,0f)
        spotLight.parent = motorrad
        ray.parent = motorrad
        ray2.parent=motorrad
        pointLight4.parent = ray
        pointLight2.parent = ray2
        pointLight.parent = motorrad

        for(i in 1..25)//random asteroid spawn
        {
            var rendertemp = ModelLoader.loadModel("assets/10464_Asteroid_L3.123c72035d71-abea-4a34-9131-5e9eeeffadcb/10464_Asteroid_v1_Iterations-2.obj", -1.5708f, 1.5708f, 0f)!!
            var ascale=Random().nextFloat(0.005f,0.01f)

            rendertemp.scale(Vector3f1(ascale,ascale,ascale))
            rendertemp.translate(Vector3f1(Random().nextFloat(-10000f,10000f),Random().nextFloat(-10000f,10000f),Random().nextFloat(-10000f,10000f)))
            //rendertemp.rotate(Math.toRadians(Random().nextFloat(0f,360f)),Math.toRadians(Random().nextFloat(0f,360f)),Math.toRadians(Random().nextFloat(0f,360f)) )
            asteroidlist.add(rendertemp)
        }
       // ast = ModelLoader.loadModel("assets/10464_Asteroid_L3.123c72035d71-abea-4a34-9131-5e9eeeffadcb/10464_Asteroid_v1_Iterations-2.obj", -1.5708f, 1.5708f, 0f)!!
        astmesh= Mesh(astobj.objects[0].meshes[0].vertexData,astobj.objects[0].meshes[0].indexData,vertexAttributes,astmat)

    }


    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()

        staticShader.setUniform("gammaValue", desiredGammaValue)

        camera.updateViewMatrix()
        camera.updateProjectionMatrix()
        camera.bind(staticShader)


        pointLight.bind(staticShader,camera.getCalculateViewMatrix(),0)
        //pointLight3.bind(staticShader,camera.getCalculateViewMatrix(),2)
        pointLight5.bind(staticShader,camera.getCalculateViewMatrix(),4)

        spotLight.bind(staticShader, camera.getCalculateViewMatrix())

        skybox.render(staticShader, Vector3f1(1f,1f,1.15f))

        //Menu
        menu_backg.render(staticShader, Vector3f1(1f,1f,2f))
        game_over.render(staticShader, Vector3f1(5f,1f,1f))

        motorrad.render(staticShader, Vector3f1(1.2f,1.2f,1.2f))
        //renderable.render(staticShader, Vector3f1(0f,1f,0f))
        //renderable2.render(staticShader, Vector3f(0.5f,0.5f,0.5f))
        renderable3.render(staticShader, Vector3f1(1f,1f,1f))
        motorrad.render(staticShader, Vector3f1(1.2f,1.2f,1.2f))
        //renderable.render(staticShader,Vector3f1(0f,1f,0f))






       // renderable2.render(staticShader, Vector3f(0.5f,0.5f,0.5f))
        renderable3.render(staticShader, Vector3f1(0.1f,0.1f,0.1f))
        if(shoot2==true){
            pointLight2 = PointLight(Vector3f1(0f,1f,0f), Vector3f1(5.0f,0.0f,5.0f))
            pointLight2.parent=ray2
            ray2.render(staticShader, Vector3f1(10f,0.1f,0.1f))
            pointLight2.bind(staticShader,camera.getCalculateViewMatrix(),1)

            ray2.translate(Vector3f1(0f,1f,0f))
            rayl2++
            //println(rayl2)

            if(rayl2>=100){

                ray2.translate(Vector3f1(0f,-100f,0f))
                //pointLight2.translate(Vector3f1(0f,-300f,0f))
                pointLight2 = PointLight(Vector3f1(0f,1f,0f), Vector3f1(0.0f,0.0f,0.0f))
                pointLight2.parent=ray2
                pointLight2.bind(staticShader,camera.getCalculateViewMatrix(),1)
                shoot2= false
                rayl2=0

            }
        }
        if(shoot==true){
            ray.render(staticShader, Vector3f1(10f,0.1f,0.1f))
            pointLight4 = PointLight(Vector3f1(0f, 1f, 0f), Vector3f1(5.0f,0.0f,0.0f))
            pointLight4.parent=ray
            pointLight4.bind(staticShader,camera.getCalculateViewMatrix(),3)
            ray.translate(Vector3f1(0f,1f,0f))
            rayl++
            if(rayl==50)
                shoot2=true

            if(rayl>=100){
                ray.translate(Vector3f1(0f,-100f,0f))
                //pointLight4.translate(Vector3f1(0f,-300f,0f))
                pointLight4 = PointLight(Vector3f1(0f, 1f, 0f), Vector3f1(0.0f,0.0f,0.0f))
                pointLight4.parent=ray
                pointLight4.bind(staticShader,camera.getCalculateViewMatrix(),3)
                shoot= false
                rayl=0

            }
        }






        if(pause)
        {
            for(i in 0..asteroidlist.lastIndex-1)
            {

                asteroidlist[i].translate(motorrad.getWorldPosition().sub(asteroidlist[i].getWorldPosition(),Vector3f1()).mul(Vector3f1(vmaxa,vmaxa,vmaxa)))

                asteroidlist[i].render(staticShader,Vector3f1(0.2f,0.2f,0.2f))
            }
            for(i in 0..asteroidlist2.lastIndex-1)
            {

                asteroidlist2[i].translate(motorrad.getWorldPosition().sub(asteroidlist2[i].getWorldPosition(),Vector3f1()).mul(Vector3f1(vmaxa2,vmaxa2,vmaxa2)))

                asteroidlist2[i].render(staticShader,Vector3f1(0.2f,0.15f,0.15f))
            }

            println(score)
            println(vmaxa)
            score+=1
            if(score.toInt()%100==0)
                vmaxa*=1.01f
                vmaxa2*=1.0001f

        if(score.toInt()%100==0){

            //var rendertemp = ModelLoader.loadModel("assets/a2/rock_by_dommk.obj", -1.5708f, 1.5708f, 0f)!!
            astmesh= Mesh(astobj.objects[0].meshes[0].vertexData,astobj.objects[0].meshes[0].indexData,vertexAttributes,astmat)
            var rendertemp = Renderable(mutableListOf(astmesh))


            var ascale=Random().nextFloat(6f,10f)

            rendertemp.scale(Vector3f1(ascale,ascale,ascale))
            rendertemp.translate(Vector3f1(Random().nextFloat(-100f,100f),Random().nextFloat(-100f,100f),Random().nextFloat(-100f,100f)))
            //rendertemp.rotate(Math.toRadians(Random().nextFloat(0f,360f)),Math.toRadians(Random().nextFloat(0f,360f)),Math.toRadians(Random().nextFloat(0f,360f)) )
            asteroidlist2.add(rendertemp)

        }
        }
    //println(asteroidlist.lastIndex.toString())
        if(motorrad.getWorldPosition().x>=1700f||motorrad.getWorldPosition().y>=1700f||motorrad.getWorldPosition().z>=1700f||motorrad.getWorldPosition().x<=-1700f||motorrad.getWorldPosition().y<=-1700f||motorrad.getWorldPosition().z<=-1700f) {
            pointLight = PointLight(Vector3f1(0f, 5f, 0f), Vector3f1(1f, 0f, 0f))
            pointLight.parent = motorrad
        }
        else{
            pointLight = PointLight(Vector3f1(0f, 5f, 0f), Vector3f1(0.11f, 0.11f, 0.11f))
            pointLight.parent = motorrad
        }
        if(motorrad.getWorldPosition().x>=1800f||motorrad.getWorldPosition().y>=1800f||motorrad.getWorldPosition().z>=1800f||motorrad.getWorldPosition().x<=-1800f||motorrad.getWorldPosition().y<=-1800f||motorrad.getWorldPosition().z<=-1800f) {
            setSpaceshipPositionToStart()
        }
    }

    fun update(dt: Float, t: Float) {
        collisionCheckTimer += dt
        checkCollisionSpaceship()
        if(shoot==true)
        checkCollisionAsteroid()
        if (collisionCheckTimer >= collisionCheckInterval) {
            checkCollisionSpaceship()
            if(shoot==true)
            checkCollisionAsteroid()
            collisionCheckTimer = 0f // Setze den Timer zurück
        }


        if (window.getKeyState(GLFW_KEY_W) == true) {
            val forward = Vector3f1(0f, 0f, speed)
            motorrad.translate(forward)
        }
        if (window.getKeyState(GLFW_KEY_D) == true) {
            if(cammode==0){
            motorrad.rotate(0.0f, 0.0f, -0.01f)
            //skybox.rotate(0f,0f,0.01f)
                }

            else{
                motorrad.rotate(0.0f, -0.01f, 0.0f)
           // skybox.rotate(0f,0.01f,0f)
            }
        }
        if (window.getKeyState(GLFW_KEY_S) == true) {
            val backward = Vector3f1(0f, 0f, 0.2f)
            motorrad.translate(backward)
        }
        if (window.getKeyState(GLFW_KEY_A) == true) {
            if(cammode==0){
            motorrad.rotate(0.0f, 0.0f, 0.01f)
           // skybox.rotate(0f,0f,-0.01f)
                }
            else{
                motorrad.rotate(0.0f, 0.01f, 0.0f)
                //skybox.rotate(0f,-0.01f,0f)
            }
        }
        if (window.getKeyState(GLFW_KEY_L) == true) {
            tempshader=tempshader+0.1f
            if(tempshader>=3f){
                tempshader=0f
            }
            staticShader.setUniform("shader",tempshader)
        }
        if (window.getKeyState(GLFW_KEY_ESCAPE) == true) {
            //pause
        }
        if (window.getKeyState(GLFW_KEY_P) == true) {
            shoot=true
            checkCollisionAsteroid()
        }
        if (window.getKeyState(GLFW_KEY_C) == true) {
            if(cammode==0)
            {
                cammode=1
            }
            else
                cammode=0
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
        val iterator2 = asteroidlist2.iterator()
        while (iterator.hasNext()) {
            val asteroid = iterator.next()
            val asteroidPosition = asteroid.getWorldPosition().add(Vector3f1(0f,6f,0f))

            val distance = spaceshipPosition.distance(asteroidPosition)

            if (distance < 7.0f) {
                iterator.remove()
                asteroid.cleanup()
                
                checkCollisionGameOver()
            }
        }
        while (iterator2.hasNext()) {
            val asteroid = iterator2.next()
            val asteroidPosition = asteroid.getWorldPosition().add(Vector3f1(0f,6f,0f))

            val distance = spaceshipPosition.distance(asteroidPosition)

            if (distance < 12.0f) {
                iterator2.remove()
                asteroid.cleanup()

                // Setze das Raumschiff an den Punkt des Spielstarts zurück
                //checkCollisionGameOver()
                setSpaceshipPositionToStart()
            }
        }
    }

    private fun checkCollisionGameOver() {
        pause = false

        menu_backg.translate(Vector3f1(20f,0f,-20f))
        menu_backg.translate(Vector3f1(-15f,0f,5f))
        game_over.translate(Vector3f1(20f,0f,-20f))
        game_over.translate(Vector3f1(-15f,0f,5f))
        game_over.parent = motorrad

        cleanup()

        

        // Setze das Raumschiff an den Punkt des Spielstarts zurück
        //setSpaceshipPositionToStart()
        //pause = true
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
        pointLight4.parent = ray
        pointLight2.parent = ray2
        pointLight.parent = motorrad
       // skybox.parent = motorrad
        score=0f
        vmaxa=0.01f
        vmaxa2=0.0001f
    }

    private fun checkCollisionAsteroid() {
        val shotPosition = ray.getWorldPosition()
        val shotPosition2 = ray2.getWorldPosition()
        val iterator = asteroidlist.iterator()
        val iterator2 = asteroidlist2.iterator()
        while (iterator.hasNext()) {
            val asteroid = iterator.next()

            val asteroidPosition = asteroid.getWorldPosition().add(Vector3f1(0f,5f,0f))

            val distance = shotPosition.distance(asteroidPosition)
            val distance2 = shotPosition2.distance(asteroidPosition)
            if (distance < 10.0f||distance2 < 10.0f) {
                iterator.remove()
                asteroid.cleanup()
                score+=500f
            }
        }
        while (iterator2.hasNext()) {
            val asteroid = iterator2.next()

            val asteroidPosition = asteroid.getWorldPosition().add(Vector3f1(0f,5f,0f))

            val distance = shotPosition.distance(asteroidPosition)
            val distance2 = shotPosition2.distance(asteroidPosition)
            if (distance < 10.0f||distance2 < 10.0f) {
                iterator2.remove()
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
        if(cammode==0){
        motorrad.rotate(-y_speed.coerceAtMost(0.015f).coerceAtLeast(-0.015f), 0f, 0f)
        motorrad.rotate(0f, -x_speed.coerceAtMost(0.015f).coerceAtLeast(-0.015f), 0f)
            //skybox.rotate(y_speed.coerceAtMost(0.015f).coerceAtLeast(-0.015f), 0f, 0f)
           // skybox.rotate(0f, x_speed.coerceAtMost(0.015f).coerceAtLeast(-0.015f), 0f)
        }
        else
            camera.rotateAroundPoint(0f, -x_speed, 0f, renderable.getWorldPosition())

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
        for (asteroid in asteroidlist2) {
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