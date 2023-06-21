
#version 330 core

// Input from vertex shader
in struct VertexData
{
    vec3 color;
    vec2 tc;
    vec3 lightDirpoint;
    vec3 lightDirspot;
    vec3 viewDir;
    vec3 normal;
} vertexData;

struct PointLight {
    vec3 position;
    vec3 lightColor;
};

struct SpotLight {
    vec3 direction;
    vec3 position;
    vec3 lightColor;
    float innerConeAngle;
    float outerConeAngle;
} ;
uniform SpotLight spotLight;
uniform PointLight pointLight;
uniform sampler2D material_emissive;
uniform sampler2D material_diffuse;
uniform sampler2D material_specular;
uniform float shininess;



out vec4 color;

vec3 brdf(vec3 n,vec3 l, vec3 v,vec3 ms,vec3 md,float k )
{
    vec3 r=reflect(-l, n);
    vec3 c=max(0.0,dot(n,l))*md;
    vec3 d =pow(max(0.0,dot(v,r)),k)*ms;

    return c+d;
}

void main()
{
    vec3 viewDir = normalize(vertexData.viewDir);
    vec3 normal = normalize(vertexData.normal);
    vec3 lightDirpoint = normalize(vertexData.lightDirpoint);
    vec3 lightDirspot = normalize(vertexData.lightDirspot);

    vec4 emissiveCol = texture(material_emissive, vertexData.tc);
    vec4 diffuseCol = texture(material_diffuse, vertexData.tc);
    vec4 specularCol = texture(material_specular, vertexData.tc);
    color= vec4(0,0,0,1);
    vec4 ambientCol = vec4(0.04, 0.04, 0.04, 1.0);
    //ambient color
    color.xyz +=vec3(ambientCol);
    //emissive
    color.xyz += vec3(emissiveCol);




    //pointlight
    color.xyz += brdf(normal,lightDirpoint,viewDir,specularCol.xyz,diffuseCol.xyz,shininess)*pointLight.lightColor;


    vec3 lightDirection = normalize(spotLight.direction);
    float theta = dot(-lightDirspot, lightDirection);
    float gamma = spotLight.outerConeAngle;
    float phi = spotLight.innerConeAngle;
    float intensity = clamp((theta - gamma) / (phi - gamma), 0.0, 1.0);
    color.xyz += brdf(normal,lightDirspot,viewDir,specularCol.xyz,diffuseCol.xyz,shininess) * spotLight.lightColor * intensity;



    //color = ambientTerm+ diffuseTerm*intensity+ diffuseTerm + specularTerm+specularTerm*intensity;

}


/*#version 330 core

// Input from vertex shader
in struct VertexData
{
    vec3 color;
    vec2 tc;
    vec3 lightDir;
    vec3 viewDir;
    vec3 normal;
} vertexData;

uniform struct PointLight {
    vec3 position;
} pointLight;

uniform struct SpotLight {
    float innerConeAngle;
    float outerConeAngle;
    vec3 direction;
    vec3 position;
} spotLight;

uniform sampler2D material_emissive;
uniform sampler2D material_diffuse;
uniform sampler2D material_specular;
uniform float shininess;
uniform vec3 lightColor;

// Fragment shader output
out vec4 color;

void main()
{
    vec3 normal = normalize(vertexData.normal);
    vec3 lightDir = normalize(vertexData.lightDir);



    vec3 viewDir = normalize(vertexData.viewDir);
    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = pow(max(dot(viewDir, reflectDir), 0.0), shininess);

    vec4 emissiveCol = texture(material_emissive, vertexData.tc);
    vec4 diffuseCol = texture(material_diffuse, vertexData.tc);
    vec4 specularCol = texture(material_specular, vertexData.tc);

    vec4 ambientCol = vec4(1.0, 1.0, 1.0, 1.0); // Ambient color

    vec4 lightIntensity = vec4(lightColor, 1.0); // Light intensity

    vec4 ambientTerm = emissiveCol * ambientCol;
    vec4 diffuseTerm = diffuseCol * lightIntensity * max(dot(normal, lightDir), 0.0);
    vec4 specularTerm = specularCol * lightIntensity  * pow(max(dot(viewDir, reflectDir), 0.0), shininess);

    color = ambientTerm + diffuseTerm + specularTerm;
}

/*#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 color;
    vec2 tc;
    vec3 lightDir;
    vec3 viewDir;
    vec3 normal;
} vertexData;

uniform struct SpotLight {

    float innerConeAngle;
    float outerConeAngle;
    vec3 direction;

} spotLight;

uniform sampler2D material_emissive;
uniform sampler2D material_diffuse;
uniform sampler2D material_specular;
float shininess;
uniform vec3 lightColor;
//fragment shader output
out vec4 color;

void main(){



    vec3 normal = normalize(vertexData.normal);
    vec3 lightDir = normalize(vertexData.lightDir);
    float diffuse = max(dot(normal,lightDir),0.0);

    vec3 viewDir = normalize(vertexData.viewDir);
    vec3 reflectDir = normalize(reflect(-lightDir,normal));
    float scpecular = pow(max(dot(viewDir,reflectDir),0.0),shininess);

    vec4 emissiveCol = texture(material_emissive,vertexData.tc);
    vec4 diffuseCol = texture(material_diffuse,vertexData.tc);
    vec4 specularCol = texture(material_specular,vertexData.tc);

    color = emissiveCol * (diffuse*diffuseCol+scpecular*specularCol)* vec4(lightColor,1.0);

    //color = vec4(emissiveColor.rgb, 1.0);
}*/

