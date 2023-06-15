#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 color;
    vec2 tc;
    vec3 lightDir;
    vec3 viewDir;
    vec3 normal;
} vertexData;

uniform sampler2D material_emissive;
uniform sampler2D material_diffuse;
uniform sampler2D material_specular;
float shininess;
uniform vec3 lightColor;
//fragment shader output
out vec4 color;

void main(){

       /*     vec3 normalized_normal = normalize(vertexData.color);
                float intensityx = abs(normalized_normal.x);
                float intensityy = abs(normalized_normal.y);
                float intensityz = abs(normalized_normal.z);
                color = vec4(intensityx, intensityy, intensityz, 1.0);*/
    //vec4 emissiveColor = texture2D(material_emissive, vertexData.tc);

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
}

