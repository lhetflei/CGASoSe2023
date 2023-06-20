
#version 330 core

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
    vec4 specularTerm = specularCol * lightIntensity * pow(max(dot(viewDir, reflectDir), 0.0), shininess);

    // Calculate spotlight intensity
    vec3 toLight = normalize(spotLight.position - vertexData.viewDir);
    vec3 lightDirection = normalize(spotLight.direction);
    float theta = dot(-toLight, lightDirection);
    float gamma = cos(spotLight.outerConeAngle);
    float phi = cos(spotLight.innerConeAngle);
    float intensity = clamp((theta - gamma) / (phi - gamma), 0.0, 1.0);

    // Apply spotlight intensity to diffuse and specular terms


    color = ambientTerm+ diffuseTerm*intensity+ diffuseTerm + specularTerm+specularTerm*intensity;
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

