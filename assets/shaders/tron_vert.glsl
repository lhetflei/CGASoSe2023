

#version 330 core

// todo 2.1.2
layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;
layout(location = 1) in vec2 textureCoordinate;

// --- uniforms
// object to world (the default value will be used unless you upload some other matrix using glUniformMatrix4fv)
uniform mat4 model_matrix = mat4(1.0);

// world to camera (2.4.2)
uniform mat4 view_matrix;
// camera to clipping (2.4.2)
uniform mat4 proj_matrix;
uniform vec2 tcMultiplier;
uniform vec3 lightPosition;

// Hint: Packing your data passed to the fragment shader into a struct like this helps to keep the code readable!
out struct VertexData
{
    vec3 color;
    vec2 tc;
    vec3 lightDir;
    vec3 viewDir;
    vec3 normal;
} vertexData;

void main()
{
    // Change to homogeneous coordinates
    vec4 objectSpacePos = vec4(position, 1.0);
    // Calculate world space position by applying the model matrix
    vec4 worldSpacePos = model_matrix * objectSpacePos;

    // Calculate the view space position by applying the view matrix
    vec4 viewSpacePos = view_matrix * worldSpacePos;

    // Calculate the clip space position by applying the projection matrix
    gl_Position = proj_matrix * viewSpacePos;

    // Pass the transformed normal to the fragment shader
    vertexData.normal = transpose(inverse(mat3(view_matrix * model_matrix))) * normal;

    // Pass the transformed texture coordinates to the fragment shader
    vertexData.tc = textureCoordinate * tcMultiplier;

    // Calculate the light direction vector in view space
    vertexData.lightDir = (view_matrix * vec4(lightPosition, 1.0) - viewSpacePos).xyz;

    // Calculate the view direction vector in view space
    vertexData.viewDir = -viewSpacePos.xyz;


}



/*#version 330 core

// todo 2.1.2
layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;
layout(location = 1) in vec2 textureCoordinate;

// --- uniforms
// object to world (the default value will be used unless you upload some other matrix using glUniformMatrix4fv)
uniform mat4 model_matrix = mat4(1.0, 0.0, 0.0, 0.0,
                                 0.0, 1.0, 0.0, 0.0,
                                 0.0, 0.0, 1.0, 0.0,
                                 0.0, 0.0, 0.0, 1.0);


// world to camera (2.4.2)
 uniform mat4 view_matrix;
// camera to clipping (2.4.2)
 uniform mat4 proj_matrix;
 uniform vec2 tcMultiplier;
uniform vec3 lightPosition;


// Hint: Packing your data passed to the fragment shader into a struct like this helps to keep the code readable!
out struct VertexData

{
    vec3 color;
    vec2 tc;
    vec3 lightDir;
    vec3 viewDir;
    vec3 normal;

} vertexData;

void main(){
    // This code should output something similar to Figure 1 in the exercise sheet.
    // TODO Modify this to solve the remaining tasks (2.1.2 and 2.4.2).
    // Change to homogeneous coordinates
    vec4 objectSpacePos = vec4(position, 1.0);
    // Calculate world space position by applying the model matrix
    vec4 worldSpacePos = model_matrix * objectSpacePos;
    // Write result to gl_Position
    // Note: z-coordinate must be flipped to get valid NDC coordinates. This will later be hidden in the projection matrix.
    gl_Position = worldSpacePos * vec4(1.0, 1.0, -1.0, 1.0);
    // Green color with some variation due to z coordinate
    //vertexData.color = vec3(0.0, worldSpacePos.z + 0.5, 0.0);
    //vertexData.color = mat3(transpose(inverse(view_matrix * model_matrix))) * normal;
    gl_Position = proj_matrix * view_matrix * model_matrix * vec4(position, 1.0); //gl_Position =  model_matrix * vec4(position, 1.0);
    vertexData.normal = transpose(inverse(mat3(view_matrix*model_matrix)))*normal;
    vertexData.tc = textureCoordinate *  tcMultiplier ;
    vertexData.lightDir = (view_matrix * vec4(lightPosition,1.0)-(view_matrix*worldSpacePos)).xyz;
    vertexData.viewDir = -(view_matrix*worldSpacePos).xyz;

}*/