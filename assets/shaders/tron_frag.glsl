#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 color;
    vec2 textureCoordinate;
} vertexData;

uniform sampler2D material_emissive;
//fragment shader output
out vec4 color;

void main(){

       /*     vec3 normalized_normal = normalize(vertexData.color);
                float intensityx = abs(normalized_normal.x);
                float intensityy = abs(normalized_normal.y);
                float intensityz = abs(normalized_normal.z);
                color = vec4(intensityx, intensityy, intensityz, 1.0);*/
    vec4 emissiveColor = texture(material_emissive, vertexData.textureCoordinate);
    color = vec4(emissiveColor.rgb, 1.0);
}